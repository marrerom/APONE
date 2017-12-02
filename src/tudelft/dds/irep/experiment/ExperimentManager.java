package tudelft.dds.irep.experiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.Timer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.glassdoor.planout4j.Experiment;
import com.glassdoor.planout4j.Namespace;
import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.rabbitmq.client.Channel;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.Action;
import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JDistribution;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.User;
import tudelft.dds.irep.utils.Utils;

public class ExperimentManager {

	public int TOTALSEGMENTS = 100;
	
	public static final String prefixEventRegister = "REG_";
	
	public static final String prefixEventMonitoring = "MON_";
	
	private Database db;
	
	private RunningExperiments re;

	private Channel channel;

	
	public Collection<RunningExpInfo> getRunningExp(User authuser) {
		return re.getRunningExp(authuser);
	}
	
	public RunningExpInfo getRunningExp(String idconf, User authuser) {
		return re.getExpInfo(idconf, authuser);
	}
	
	public Collection<String> getExperiments(List<Status> status, User authuser) {
		Collection<RunningExpInfo> experiments = re.getRunningExp(authuser);
		Set<String> result = new HashSet<String>();
		for (RunningExpInfo exp: experiments) {
		if (status.contains(exp.getStatus())){
			result.add(exp.getIdconfig());
		}
	}
	return result;
}
	
	public ExperimentManager(Database db, RunningExperiments re, Channel channel) throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		this.db = db;
		this.re = re;
		this.channel = channel;
		User authuser = Security.getMasterUser();
		dbSynchronize(authuser); //save in re running/paused experiments in db
		int delay = 10000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  for (String idconf:getExperiments(Arrays.asList(Status.ON, Status.PAUSED), authuser)) {
		    		  try {
		    		  if (matchStopConditions(idconf, authuser)) 
							stop(idconf, null); //TODO: what if I stop and there is a service currently running related to the experiment?
						} catch (IOException | ParseException e) {
							e.printStackTrace();
							Thread.currentThread().interrupt();
							//TODO: handle error properly
						}
		    		  }
		      }
		  
		};
		Timer timer = new Timer(delay, taskPerformer);
		timer.start();
	}
	
	//even if the experiment if OFF (in order to decide if we can start it again or not)
	private boolean matchStopConditions(String idconf, User authuser)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		Date dateToEnd;
		Integer maxExposures;
		Integer actualExposures;
		EventMonitoringConsumer emc;
		if (re.getExpInfo(idconf, authuser).getStatus() == Status.OFF) {
			JConfiguration jconf = db.getConfiguration(idconf, authuser);
			dateToEnd = jconf.getDate_to_end();
			if (dateToEnd != null && dateToEnd.compareTo(new Date()) < 0) {
				return true;
			}
			maxExposures = jconf.getMax_exposures();
			if (maxExposures != null) {
				emc = createMonitoringConsumer(createMonitoringQueue(idconf), Optional.of(getExposureEvents(idconf, authuser)));
				actualExposures = emc.getTotalCount();
				if (actualExposures >= maxExposures)
					return true;
			}
		} else {
			dateToEnd = re.getExpInfo(idconf, authuser).getDateToEnd();
			if (dateToEnd != null && dateToEnd.compareTo(new Date()) < 0) {
				return true;
			}
			maxExposures = re.getExpInfo(idconf, authuser).getMaxExposures();
			if (maxExposures != null) {
				emc = re.getExpInfo(idconf, authuser).getMonConsumer();
				actualExposures = emc.getTotalCount();
				if (actualExposures >= maxExposures)
					return true;
			}
		}

		return false;
	}
	
	
	
	private void dbSynchronize(User authuser) throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		ImmutableList<Status> conditions = ImmutableList.of(Status.ON, Status.PAUSED);
		for (JConfiguration conf: db.getConfigurations(conditions, authuser)) {
			JExperiment exp = db.getExpFromConfiguration(conf.get_id(), authuser);
			load(exp,conf, authuser);
		}
	}
	
	//removes exp only if there are no more configurations
	public void deleteConfig(String idconf, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		db.deleteConfig(idconf, authuser);
	}
	
	public void deleteEvent(String idevent, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		db.deleteEvent(idevent, authuser);
	}
	
	public String addConfig(String idexp, JConfiguration config, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.addExpConfig(idexp, config, authuser);
	}
	
	public String addExperiment(JExperiment exp, User authuser) throws ParseException, IOException {
		return db.addExperiment(exp, authuser);
	}
	
	public JExperiment getExperiment(String idexp, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getExperiment(idexp, authuser);
	}
	
	public JExperiment getExperimentFromConf(String idconf,  User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getExpFromConfiguration(idconf, authuser);
	}
	
	public JConfiguration getConfiguration(String idconf, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getConfiguration(idconf, authuser);
	}
	
	public JEvent getEvent(String idevent, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getEvent(idevent, authuser);
	}
	
	public List<JExperiment> getExperiments(JExperiment filter, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		return db.getExperiments(filter, authuser);
	}
	
	public List<JEvent> getEvents(JEvent filter, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		return db.getEvents(filter, authuser);
	}
		
	public Map<String,?> treatment_to_json(JTreatment treatment) throws ValidationException {
		String dsl = treatment.getDefinition();
		return PlanoutDSLCompiler.dsl_to_json(dsl); 
	}
	
	public Map<String, ?> getParams(String unitExp, String idconf, String idunit, Map<String,?> overrides, User authuser) throws javax.ws.rs.BadRequestException {
		Status st = re.getExpInfo(idconf, authuser).getStatus();
		NamespaceConfig nsConfig = re.getExpInfo(idconf, authuser).getConf();
		Map<String, ?> result = new HashMap<>();
		if (st == Status.ON) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit), overrides);
			result = ns.getParams();
		} else if (st == Status.PAUSED) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit, Namespace.BASELINE_KEY, true), overrides);
			result = ns.getParams();
		} else if (st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;
	}
	
	public String getTreatment(String unitExp, String idconf, String idunit, User authuser) throws javax.ws.rs.BadRequestException {
		Status st = re.getExpInfo(idconf, authuser).getStatus();
		NamespaceConfig nsConfig = re.getExpInfo(idconf, authuser).getConf();
		String result = null;
		if (st == Status.ON) {
			Experiment planoutExp = nsConfig.getExperiment(ImmutableMap.of(unitExp,idunit));
			if (planoutExp != null)
				result = planoutExp.name;
			else result = nsConfig.getDefaultExperiment().name;
			//Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit), null);
			//result = ns.getExperiment().name;
		} else if (st == Status.PAUSED) {
			result = nsConfig.getDefaultExperiment().name;
			//Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit, Namespace.BASELINE_KEY, true), null);
			//result = ns.getExperiment().name; //should always be the default experiment
			//result = nsConfig.getExperiment(ImmutableMap.of(unitExp, idunit, Namespace.BASELINE_KEY, true)).name;
		} else if (st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;
	}
	
	public JTreatment getTreatment(JExperiment exp, String name) {  		
	for (JTreatment treat: exp.getTreatment()) {
		if (treat.getName().equals(name)) {
			return treat;
			}
		}
	return null;
	}

	public void load(JExperiment exp, JConfiguration conf, User authuser) throws ValidationException, IOException,  ParseException {
		Status dbstatus =Status.valueOf(conf.getRun());
		Date last_started = conf.getDate_started()[conf.getDate_started().length-1]; //There is at least one value
		NamespaceConfig ns  = createNamespace(exp,conf);
		EventRegisterConsumer erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id(), authuser)));
		re.setExperiment(conf, ns, dbstatus, erc, emc, last_started, exp.getExperimenter(), authuser);
	}
	
	private List<JEvent> getExposureEvents(String idconf, User authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		JEvent filter = new JEvent();
		filter.setIdconfig(idconf);
		filter.setEname(JEvent.EXPOSURE_ENAME);
		return db.getEvents(filter, authuser);
	}
	
//	private Map<String, Integer> getExposures(JExperiment exp, JConfiguration conf) throws JsonParseException, JsonMappingException, IOException, ParseException{
//		Map<String, Integer> expcount = new HashMap<String, Integer>();
//		ObjectMapper mapper = new ObjectMapper();
//		List<JTreatment> treatments = db.getTreatments(exp.get_id());
//		for (JTreatment treat:treatments) {
//			expcount.put(treat.getName(), 0);
//		}
//		JEvent filter = new JEvent();
//		filter.setIdconfig(conf.get_id());
//		filter.setEname(JExposureBody.EVENT_ENAME);
//		List<JEvent> expevents = db.getEvents(filter);
//		for (JEvent event: expevents) {
//			String body = event.getEvalue();
//			JExposureBody expbody = mapper.readValue(new StringReader(body),JExposureBody.class);
//			//JExposureBody expbody = mapper.convertValue(body, JExposureBody.class); //It does not work
//			String treatment = expbody.getTreatment();
//			Integer count = expcount.get(treatment);
//			expcount.put(treatment, ++count); //Throws exception if treatment does not exist, but if that happens, something went very wrong in the registering process
//		}
//		return expcount;
//	}
	
	//It should do nothing if the current status is on or paused
	public boolean start(JExperiment exp, JConfiguration conf, User authuser) throws IOException, ValidationException, ParseException {
		Status st = re.getExpInfo(conf.get_id(), authuser).getStatus();
		Date last_started = null;
		if (st == Status.OFF) {
			if (matchStopConditions(conf.get_id(),authuser))
				return false;
			last_started = new Date();
			db.addExpConfigDateStart(conf.get_id(), last_started, authuser);
		}
		NamespaceConfig ns = re.getExpInfo(conf.get_id(), authuser).getConf();
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getExpInfo(conf.get_id(), authuser).getRegConsumer();
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getExpInfo(conf.get_id(), authuser).getMonConsumer(); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id(), authuser)));
		if (last_started == null) last_started = re.getExpInfo(conf.get_id(), authuser).getLastStarted();
		re.setExperiment(conf, ns, Status.ON, erc, emc, last_started, exp.getExperimenter(), authuser);
		db.setExpConfigRunStatus(conf.get_id(), Status.ON, authuser);
		return true;
	}
	
	public void stop(String idconf, User authuser) throws IOException {
		if (re.getExpInfo(idconf, authuser).getStatus() == Status.ON || re.getExpInfo(idconf, authuser).getStatus() == Status.PAUSED) {
			db.addExpConfigDateEnd(idconf, new Date(), authuser); 
		}
		deleteRegisterQueue(idconf); //TODO: the list may not be empty, the events will be destroyed
		deleteMonitoringQueue(idconf);
		re.setExperimentOFF(idconf, authuser); 
		db.setExpConfigRunStatus(idconf, Status.OFF, authuser);
	}

	//all the users receives the control, instead of the corresponding treatment while paused
	public void pause(JExperiment exp, JConfiguration conf, User authuser) throws IOException, ValidationException, ParseException {
		Date last_started = null;
		if (re.getExpInfo(conf.get_id(), authuser).getStatus() == Status.OFF) {
			last_started = new Date();
			db.addExpConfigDateStart(conf.get_id(), last_started, authuser);
		}
		NamespaceConfig ns = re.getExpInfo(conf.get_id(), authuser).getConf();
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getExpInfo(conf.get_id(), authuser).getRegConsumer();
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getExpInfo(conf.get_id(), authuser).getMonConsumer(); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id(), authuser)));
		if (last_started == null) last_started = re.getExpInfo(conf.get_id(), authuser).getLastStarted();
		re.setExperiment(conf, ns, Status.PAUSED, erc, emc, last_started, exp.getExperimenter(), authuser);
		db.setExpConfigRunStatus(conf.get_id(), Status.PAUSED, authuser);
	}
	
	public String saveEvent(JEvent event, User authuser) throws javax.ws.rs.BadRequestException, ParseException, JsonProcessingException, IOException {
		String idconf = event.getIdconfig();
		Status st = re.getExpInfo(idconf, authuser).getStatus();
		String result = null;
		if (st == Status.ON) {
			result = db.addEvent(event, authuser);
		} else if (st == Status.PAUSED || st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;

	}
	
	public void registerEvent(String idconf, JEvent event, User authuser) throws IOException {
		Status st = re.getExpInfo(idconf, authuser).getStatus();
		if (st != Status.ON && st != Status.PAUSED) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		String queue = createRegisterQueue(event.getIdconfig());
		byte[] body = Utils.serialize(event);
		channel.basicPublish("", queue, null, body);
	}
	
	public void monitorEvent(JEvent event) throws IOException {
		String queue = createMonitoringQueue(event.getIdconfig());
		byte[] body = Utils.serialize(event);
		channel.basicPublish("", queue, null, body);
	}
	
	public Map<String, Set<String>> getUnitCount(String idconfig, User authuser) throws javax.ws.rs.BadRequestException {
		EventMonitoringConsumer emc = re.getExpInfo(idconfig, authuser).getMonConsumer();
		if (emc == null) throw new javax.ws.rs.BadRequestException("The experiment is not running/paused");
		return emc.getTreatmentCount();
	}
	
	public JEvent createEvent(String idconf, String unitid, String ename, EventType etype, InputStream evalue, String timestamp, 
			String treatment, JParamValues params, String useragent, String expowner) throws IOException, ParseException {
		JEvent event = new JEvent();
		event.setEtype(etype.toString());
		event.setEname(ename);
		event.setIdconfig(idconf);
		event.setIdunit(unitid);
		event.setTreatment(treatment);
		event.setParamvalues(params);
		event.setTimestamp(Utils.getDate(timestamp));
		event.setUseragent(useragent);
		event.setExperimenter(expowner);
		String valuestr;
		if (etype == EventType.BINARY) {
			byte[] valuebin = ByteStreams.toByteArray(evalue);
			valuestr = Utils.encodeBinary(valuebin);
		} else {
			valuestr = CharStreams.toString(new InputStreamReader(evalue));
		}
		event.setEvalue(valuestr);
		return event;
	}
	
//	public JExposureBody createExposureBody(String treatment, Map<String, ?> params) {
//		JExposureBody expbody = new JExposureBody();
//		expbody.setTreatment(treatment);
//		expbody.setParamvalues(params);
//		return expbody;
//	}
//	
//	public JEvent createExposureEvent(String idconfig, String idunit, String timestamp, JExposureBody expbody) throws IOException, ParseException {
//		ObjectMapper mapper = new ObjectMapper();
//		//InputStream is = new ByteArrayInputStream(mapper.convertValue(expbody, Map.class).toString().getBytes());
//		InputStream is = new ByteArrayInputStream(mapper.writeValueAsString(expbody).getBytes());
//		return createEvent(idconfig, idunit, JExposureBody.EVENT_ENAME, false, is, timestamp);
//	}
	
	private NamespaceConfig createNamespace(JExperiment exp, JConfiguration config) throws ValidationException {
		String nsName = exp.getName()+"@"+exp.getExperimenter()+"."+config.getName();
		//String salt = nsName; //TODO: check conf. names are different for same experiment
		String salt = config.get_id();
		NamespaceConfig ns = new NamespaceConfig(nsName,TOTALSEGMENTS, exp.getUnit(), salt);
		
		
		for (JTreatment treat: exp.getTreatment()) {
			Map<String,?> dsl = PlanoutDSLCompiler.dsl_to_json(treat.getDefinition());
			ns.defineExperiment(treat.getName(), dsl);
			if (treat.isControl())
				ns.setDefaultExperiment(treat.getName());
		}
		
		//TODO: you can add/remove several times the same experiment, but you need a different name for each action: addExperiment(name, treatment, segments)
		for (JDistribution dist: config.getDistribution()) {
			if (dist.getActionEnum() == Action.ADD)
				ns.addExperiment(dist.getTreatment(), dist.getTreatment(), dist.getSegments());
			if (dist.getActionEnum() == Action.REMOVE)
				ns.removeExperiment(dist.getTreatment());
		}
		
		return ns;
	}
	
	private String createRegisterQueue(String idconf) throws IOException {
		String regQ = prefixEventRegister+idconf;
		channel.queueDeclare(regQ,true, false, false, null); //persistent. Idempotent
		return regQ;
	}
	
	private String createMonitoringQueue(String idconf) throws IOException {
		String monQ = prefixEventMonitoring+idconf;
		channel.queueDeclare(monQ,true, false, false, null); //persistent. Idempotent
		return monQ;
	}
	
	private EventRegisterConsumer createRegisterConsumer(String queue) throws IOException {
		EventRegisterConsumer regConsumer = new EventRegisterConsumer(channel, this);
		channel.basicConsume(queue, regConsumer);
		return regConsumer;
	}
	
	private EventMonitoringConsumer createMonitoringConsumer(String queue, Optional<Collection<JEvent>> exposureEvents) throws IOException {
		EventMonitoringConsumer monConsumer;
		if (exposureEvents.isPresent())
			monConsumer = new EventMonitoringConsumer(channel, exposureEvents.get());
		else
			monConsumer = new EventMonitoringConsumer(channel);
		channel.basicConsume(queue, monConsumer);
		return monConsumer;
	}
	
	private void deleteRegisterQueue(String idconf) throws IOException {
		String regQ = prefixEventRegister+idconf;
		channel.queueDelete(regQ);
	}
	
	private void deleteMonitoringQueue(String idconf) throws IOException {
		String monQ = prefixEventMonitoring+idconf;
		channel.queueDelete(monQ);
	}
	

	
	
	
}
