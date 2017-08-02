package tudelft.dds.irep.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.glassdoor.planout4j.Namespace;
import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.Action;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JDistribution;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
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

	public ExperimentManager(Database db, RunningExperiments re, Channel channel) throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException, TimeoutException {
		this.db = db;
		this.re = re;
		this.channel = channel;
		dbSynchronize(); //save in re running/paused experiments in db
	}
	
	private void dbSynchronize() throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException, TimeoutException {
		ImmutableList<Status> conditions = ImmutableList.of(Status.ON, Status.PAUSED);
		for (JConfiguration conf: db.getConfigurations(conditions)) {
			JExperiment exp = db.getExpFromConfiguration(conf.get_id());
			load(exp,conf,Status.valueOf(conf.getRun()));
		}
	}
	
	public String addConfig(String idexp, JConfiguration config) throws JsonParseException, JsonMappingException, IOException {
		return db.addExpConfig(idexp, config);
	}
	
	public String addExperiment(JExperiment exp) {
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
		
	public Map<String,?> treatment_to_json(JTreatment treatment) throws ValidationException {
		String dsl = treatment.getDefinition();
		return PlanoutDSLCompiler.dsl_to_json(dsl); 
	}
	
	public Map<String, ?> getParams(String unitExp, String idconf, String idunit) {
		Status st = re.getStatus(idconf);
		NamespaceConfig nsConfig = re.getNsConfig(idconf);
		Map<String, ?> result = new HashMap<>();
		if (st == Status.ON) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit), null);
			result = ns.getParams();
		} else if (st == Status.PAUSED) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit, Namespace.BASELINE_KEY, true), null);
			result = ns.getParams();
		} else if (st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;
	}
	
	public String getTreatment(String unitExp, String idconf, String idunit) {
		Status st = re.getStatus(idconf);
		NamespaceConfig nsConfig = re.getNsConfig(idconf);
		String result = null;
		if (st == Status.ON) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit), null);
			result = ns.getExperiment().name;
		} else if (st == Status.PAUSED) {
			Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitExp, idunit, Namespace.BASELINE_KEY, true), null);
			result = ns.getExperiment().name; //should always be the default experiment
		} else if (st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;
	}

	public void load(JExperiment exp, JConfiguration conf, Status dbstatus) throws ValidationException, IOException, TimeoutException {
		NamespaceConfig ns  = createNamespace(exp,conf);
		EventRegisterConsumer erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()));
		re.setExperiment(conf.get_id(), ns, dbstatus, erc, emc);
	}
	
	//It should do nothing if the current status is on
	public void start(JExperiment exp, JConfiguration conf) throws IOException, TimeoutException, ValidationException {
		Status st = re.getStatus(conf.get_id());
		if (st == Status.OFF || st == Status.PAUSED) {
			db.addExpConfigDateStart(conf);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getEventRegisterConsumer(conf.get_id());
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getEventMonitoringConsumer(conf.get_id()); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()));
		re.setExperiment(conf.get_id(), ns, Status.ON, erc, emc);
		db.setExpConfigRunStatus(conf, Status.ON);
	}
	
	public void stop(JConfiguration conf) throws IOException, TimeoutException {
		if (re.getStatus(conf.get_id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		deleteRegisterQueue(conf.get_id());
		deleteMonitoringQueue(conf.get_id());
		re.setExperiment(conf.get_id(), null, Status.OFF,null,null); 
		db.setExpConfigRunStatus(conf, Status.OFF);
	}

	public void pause(JExperiment exp, JConfiguration conf) throws IOException, TimeoutException, ValidationException {
		if (re.getStatus(conf.get_id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		if (ns == null) ns = createNamespace(exp,conf);
		EventRegisterConsumer erc = re.getEventRegisterConsumer(conf.get_id());
		if (erc == null) erc = createRegisterConsumer(createRegisterQueue(conf.get_id()));
		EventMonitoringConsumer emc = re.getEventMonitoringConsumer(conf.get_id()); 
		if (emc == null) emc = createMonitoringConsumer(createMonitoringQueue(conf.get_id()));
		re.setExperiment(conf.get_id(), ns, Status.PAUSED, erc, emc);
		db.setExpConfigRunStatus(conf, Status.PAUSED);
	}
	
	public String saveEvent(JEvent event) {
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
	
	public void registerEvent(JEvent event) throws IOException {
		String queue = createRegisterQueue(event.getIdconfig());
		byte[] body = Utils.serialize(event);
		channel.basicPublish("", queue, null, body);
	}
	
	public JEvent createEvent(String idconf, String unitid, String ename, boolean isBinary, InputStream evalue, Date timestamp) throws IOException {
		JEvent event = new JEvent();
		event.setBinary(false);
		event.setEname(ename);
		event.setIdconfig(idconf);
		event.setUnitid(unitid);
		event.setTimestamp(timestamp);
		event.setBinary(isBinary);
		String valuestr;
		if (isBinary) {
			byte[] valuebin = ByteStreams.toByteArray(evalue);
			valuestr = java.util.Base64.getEncoder().encodeToString(valuebin);
		} else {
			valuestr = CharStreams.toString(new InputStreamReader(evalue));
		}
		event.setEvalue(valuestr);
		return event;
	}
	
	private NamespaceConfig createNamespace(JExperiment exp, JConfiguration config) throws ValidationException {
		String nsName = exp.getName()+"."+config.getName();
		String salt = nsName; //TODO: except if we specify a random key
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
	
	private EventMonitoringConsumer createMonitoringConsumer(String queue) throws IOException {
		EventMonitoringConsumer monConsumer = new EventMonitoringConsumer(channel, this);
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
