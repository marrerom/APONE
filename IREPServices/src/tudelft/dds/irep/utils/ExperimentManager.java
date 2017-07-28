package tudelft.dds.irep.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.glassdoor.planout4j.Namespace;
import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.Action;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JDistribution;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;

public class ExperimentManager {

	public static final int TOTALSEGMENTS = 100;
	
	private Database db;
	
	private RunningExperiments re;
	
	public ExperimentManager(Database db, RunningExperiments re) throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		this.db = db;
		this.re = re;
		dbSynchronize(); //save in re running/paused experiments in db
	}
	
	private void dbSynchronize() throws JsonParseException, JsonMappingException, IOException, ValidationException, ParseException {
		ImmutableList<Status> conditions = ImmutableList.of(Status.ON, Status.PAUSED);
		for (JConfiguration conf: db.getConfigurations(conditions)) {
			JExperiment exp = db.getExpFromConfiguration(conf.get_id());
			NamespaceConfig ns = createNamespace(exp, conf);
			re.setExperiment(conf.get_id(), ns, conf.getRunEnum());
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
	
	public NamespaceConfig createNamespace(JExperiment exp, JConfiguration config) throws ValidationException {
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
	
	public void start(JConfiguration conf, NamespaceConfig ns) {
		Status st = re.getStatus(conf.get_id());
		if (st != Status.ON) {
			db.addExpConfigDateStart(conf);
		}
		re.setExperiment(conf.get_id(), ns, Status.ON);
		db.setExpConfigRunStatus(conf, Status.ON);
	}
	
	public void stop(JConfiguration conf) {
		if (re.getStatus(conf.get_id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		re.setExperiment(conf.get_id(), ns, Status.OFF); 
		db.setExpConfigRunStatus(conf, Status.OFF);
	}

	public void pause(JConfiguration conf) {
		if (re.getStatus(conf.get_id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		NamespaceConfig ns = re.getNsConfig(conf.get_id());
		re.setExperiment(conf.get_id(), ns, Status.PAUSED);
		db.setExpConfigRunStatus(conf, Status.PAUSED);
	}
	
	public String saveEvent(JEvent event, String idconf) {
		Status st = re.getStatus(idconf);
		String result = null;
		if (st == Status.ON || st == Status.PAUSED) {
			result = db.addEvent(event);
		} else if (st == Status.OFF) {
			throw new javax.ws.rs.BadRequestException("The experiment is not running");
		}
		return result;

	}
	
	
	
}
