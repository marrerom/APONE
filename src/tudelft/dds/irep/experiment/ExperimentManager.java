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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JDistribution;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;
import tudelft.dds.irep.utils.Utils;

public class ExperimentManager {

	public int TOTALSEGMENTS = 100;
	
	public static final String prefixEventRegister = "REG_";
	
	public static final String prefixEventMonitoring = "MON_";
	
	private Database db;
	
	private RunningExperiments re;

	private Channel channel;

	
	public Collection<RunningExpInfo> getRunningExp() {
		return re.getRunningExp();
	}
	
	public RunningExpInfo getRunningExp(String idconf) {
		return re.getRunningExp(idconf);
	}
	
	public ExperimentManager(Database db, RunningExperiments re, Channel channel) throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		this.db = db;
		this.re = re;
		this.channel = channel;
		dbSynchronize(); //save in re running/paused experiments in db
		int delay = 10000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  for (String idconf:re.getExperiments(Arrays.asList(Status.ON, Status.PAUSED))) {
		    		  try {
		    		  if (matchStopConditions(idconf)) 
							stop(idconf); //TODO: what if I stop and there is a service currently running related to the experiment?
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
	private boolean matchStopConditions(String idconf)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		Date dateToEnd;
		Integer maxExposures;
		Integer actualExposures;
		EventMonitoringConsumer emc;
		if (re.getStatus(idconf) == Status.OFF) {
			JConfiguration jconf = db.getConfiguration(idconf);
			dateToEnd = jconf.getDate_to_end();
			if (dateToEnd != null && dateToEnd.compareTo(new Date()) < 0) {
				return true;
			}
			maxExposures = jconf.getMax_exposures();
			if (maxExposures != null) {
				emc = createMonitoringConsumer(createMonitoringQueue(idconf), Optional.of(getExposureEvents(idconf)));
				actualExposures = emc.getExposurecount().values().stream().mapToInt(Integer::intValue).sum();
				if (actualExposures >= maxExposures)
					return true;
			}
		} else {
			dateToEnd = re.getDateToEnd(idconf);
			if (dateToEnd != null && dateToEnd.compareTo(new Date()) < 0) {
				return true;
			}
			maxExposures = re.getMaxExposures(idconf);
			if (maxExposures != null) {
				emc = re.getEventMonitoringConsumer(idconf);
				actualExposures = emc.getExposurecount().values().stream().mapToInt(Integer::intValue).sum();
				if (actualExposures >= maxExposures)
					return true;
			}
		}

		return false;
	}
	
	
	
	private void dbSynchronize() throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		ImmutableList<Status> conditions = ImmutableList.of(Status.ON, Status.PAUSED);
		for (JConfiguration conf: db.getConfigurations(conditions)) {
			JExperiment exp = db.getExpFromConfiguration(conf.get_id());
			load(exp,conf);
		}
	}
	
	//removes exp only if there are no more configurations
	public void deleteConfig(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		db.deleteConfig(idconf);
	}
	
	public void deleteEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException {
		db.deleteEvent(idevent);
	}
	
	public String addConfig(String idexp, JConfiguration config) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.addExpConfig(idexp, config);
	}
	
	public String addExperiment(JExperiment exp) throws ParseException, JsonProcessingException {
		return db.addExperiment(exp);
	}
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getExperiment(idexp);
	}
	
	public JExperiment getExperimentFromConf(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getExpFromConfiguration(idconf);
	}
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getConfiguration(idconf);
	}
	
	public JEvent getEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return db.getEvent(idevent);
	}
	
	public List<JExperiment> getExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException{
		return db.getExperiments(filter);
	}
	
	public List<JEvent> getEvents(JEvent filter) throws JsonParseException, JsonMappingException, IOException, ParseException{
		return db.getEvents(filter);
	}
		
	public Map<String,?> treatment_to_json(JTreatment treatment) throws ValidationException {
		String dsl = treatment.getDefinition();
		return PlanoutDSLCompiler.dsl_to_json(dsl); 
	}
	
	public Map<String, ?> getParams(String unitExp, String idconf, String idunit, Map<String,?> overrides) throws javax.ws.rs.BadRequestException {
		Status st = re.getStatus(idconf);
		NamespaceConfig nsConfig = re.getNsConfig(idconf);
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
	
	public String getTreatment(String unitExp, String idconf, String idunit) throws javax.ws.rs.BadRequestException {
		Status st = re.getStatus(idconf);
		NamespaceConfig nsConfig = re.getNsConfig(idconf);
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

	public void load(JExperiment exp, JConfiguration conf) throws ValidationException, IOException,  ParseException {
		Status dbstatus =Status.valueOf(conf.getRun());
		Date last_started = conf.getDate_started()[conf.getDate_started().length-1]; //There is at least one value
		NamespaceConfig ns  = createNamespace(exp,conf);
		EventRegisterConsumer erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id())));
		re.setExperiment(conf, ns, dbstatus, erc, emc, last_started);
	}
	
	private List<JEvent> getExposureEvents(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException{
		JEvent filter = new JEvent();
		filter.setIdconfig(idconf);
		filter.setEname(JEvent.EXPOSURE_ENAME);
		return db.getEvents(filter);
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
	public boolean start(JExperiment exp, JConfiguration conf) throws IOException, ValidationException, ParseException {
		Status st = re.getStatus(conf.get_id());
		Date last_started = null;
		if (st == Status.OFF) {
			if (matchStopConditions(conf.get_id()))
				return false;
			last_started = new Date();
			db.addExpConfigDateStart(conf.get_id(), last_started);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getEventRegisterConsumer(conf.get_id());
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getEventMonitoringConsumer(conf.get_id()); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id())));
		if (last_started == null) last_started = re.getLastStarted(conf.get_id());
		re.setExperiment(conf, ns, Status.ON, erc, emc, last_started);
		db.setExpConfigRunStatus(conf.get_id(), Status.ON);
		return true;
	}
	
	public void stop(String idconf) throws IOException {
		if (re.getStatus(idconf) == Status.ON || re.getStatus(idconf) == Status.PAUSED) {
			db.addExpConfigDateEnd(idconf, new Date()); 
		}
		deleteRegisterQueue(idconf); //TODO: the list may not be empty, the events will be destroyed
		deleteMonitoringQueue(idconf);
		re.setExperimentOFF(idconf); 
		db.setExpConfigRunStatus(idconf, Status.OFF);
	}

	//all the users receives the control, instead of the corresponding treatment while paused
	public void pause(JExperiment exp, JConfiguration conf) throws IOException, ValidationException, ParseException {
		Date last_started = null;
		if (re.getStatus(conf.get_id()) == Status.OFF) {
			last_started = new Date();
			db.addExpConfigDateStart(conf.get_id(), last_started);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getEventRegisterConsumer(conf.get_id());
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getEventMonitoringConsumer(conf.get_id()); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()), Optional.of(getExposureEvents(conf.get_id())));
		if (last_started == null) last_started = re.getLastStarted(conf.get_id());
		re.setExperiment(conf, ns, Status.PAUSED, erc, emc, last_started);
		db.setExpConfigRunStatus(conf.get_id(), Status.PAUSED);
	}
	
	public String saveEvent(JEvent event) throws javax.ws.rs.BadRequestException, ParseException {
		String idconf = event.getIdconfig();
		Status st = re.getStatus(idconf);
		String result = null;
		if (st == Status.ON) {
			result = db.addEvent(event);
		} else if (st == Status.PAUSED || st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;

	}
	
	public void registerEvent(String idconf, JEvent event) throws IOException {
		Status st = re.getStatus(idconf);
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
	
	public Map<String, Integer> getExposures(String idconfig) throws javax.ws.rs.BadRequestException {
		EventMonitoringConsumer emc = re.getEventMonitoringConsumer(idconfig);
		if (emc == null) throw new javax.ws.rs.BadRequestException("The experiment is not running/paused");
		return emc.getExposurecount();
	}
	
	public JEvent createEvent(String idconf, String unitid, String ename, boolean isBinary, InputStream evalue, String timestamp, String treatment, JParamValues params) throws IOException, ParseException {
		JEvent event = new JEvent();
		event.setBinary(false);
		event.setEname(ename);
		event.setIdconfig(idconf);
		event.setUnitid(unitid);
		event.setTreatment(treatment);
		event.setParamvalues(params);
		event.setTimestamp(Utils.getDate(timestamp));
		event.setBinary(isBinary);
		String valuestr;
		if (isBinary) {
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
