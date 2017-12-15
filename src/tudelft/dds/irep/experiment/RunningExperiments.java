package tudelft.dds.irep.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotAuthorizedException;

import com.glassdoor.planout4j.NamespaceConfig;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.Security;

public class RunningExperiments {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	private Map<String, RunningExpInfo> idconfConfig;
	

	public RunningExperiments() {
		idconfConfig = new HashMap<String, RunningExpInfo>();
	}
	
	public Collection<RunningExpInfo> getRunningExp(JUser authuser){
		return filterByOwnership(authuser, idconfConfig.values());
	}
	
//	public RunningExpInfo getRunningExp(String idconf, JUser authuser) {
//		RunningExpInfo runningExp = idconfConfig.get(idconf);
//		Authorization.checkAuthorized(authuser, runningExp.getExperimenter());
//		return runningExp;	
//	}
	
	public RunningExpInfo getExpInfo(String idconf, JUser authuser){
		RunningExpInfo runningExp = idconfConfig.get(idconf);
		if (runningExp != null)
			Security.checkAuthorized(authuser, runningExp.getExperimenter());
		return runningExp;
	}
	
	public Status getExpStatus(String idconf) {
		RunningExpInfo runningExp = idconfConfig.get(idconf);
		if (runningExp == null)
			return Status.OFF;
		return runningExp.getStatus();
	}
	
	public synchronized void setExperiment(JConfiguration conf, NamespaceConfig nsconf, Status targetStatus, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date lastStarted, String expowner, JUser authuser) throws IOException {
		String idconf = conf.get_id();
		Date dateToEnd = conf.getDate_to_end();
		Integer maxExposures = conf.getMax_exposures();
		if (targetStatus == Status.OFF)
			setExperimentOFF(idconf, authuser);
		else {
			if (targetStatus == Status.PAUSED) {
				put(idconf, nsconf, targetStatus, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted, expowner, authuser);
			} else if (targetStatus == Status.ON){
				put(idconf, nsconf, targetStatus, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted, expowner, authuser);
			}
		}
	}

	public synchronized void setExperimentOFF(String idconf, JUser authuser) throws IOException {
		remove(idconf, authuser);
	}

	
	private Collection<RunningExpInfo> filterByOwnership(JUser authuser, Collection<RunningExpInfo> runningExp) {
		Collection<RunningExpInfo> results = new HashSet<RunningExpInfo>();
		for (RunningExpInfo exp:runningExp) {
			if (Security.isAuthorized(authuser, exp.getExperimenter())) {
				results.add(exp);
			}
		}
		return results;
	}
	
	
//	private RunningExpInfo authorize(JUser authuser, RunningExpInfo runningExp) {
//		if (runningExp != null && !Authorization.isAuthorized(authuser,runningExp.getExperimenter())) {
//			String msg = "Access to experiment "+runningExp.getIdconfig()+" not authorized";
//			NotAuthorizedException e = new NotAuthorizedException(msg);
//			log.log(Level.WARNING, msg, e);
//			throw e;
//		}
//		return runningExp;
//	}
	
	
	private RunningExpInfo put(String idconf, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date dateToEnd, Integer maxExposures, Date lastStarted, String expowner, JUser authuser){
		RunningExpInfo newExp = new RunningExpInfo(idconf,conf,status, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted, expowner);
		Security.checkAuthorized(authuser, newExp.getExperimenter());
		return idconfConfig.put(idconf,newExp);
	}
	
	private RunningExpInfo remove(String idconf, JUser authuser) throws IOException {
		RunningExpInfo toremove = idconfConfig.get(idconf);
		if (toremove != null) {
			Security.checkAuthorized(authuser, toremove.getExperimenter());
			return idconfConfig.remove(toremove.getIdconfig());
		}
		return null;
	}
	

	
//	public Status getStatus(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null) {
//			return ei.getStatus();
//		}
//		return Status.OFF;
//	}
//	
//	public Date getDateToEnd(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null) {
//			return ei.getDateToEnd();
//		}
//		return null;
//	}
//	
//	public Integer getMaxExposures(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null) {
//			return ei.getMaxExposures();
//		}
//		return null;
//	}
	

	
//	public NamespaceConfig getNsConfig(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null)
//			return ei.getConf();
//		return null;
//	}
//	
//	public EventMonitoringConsumer getEventMonitoringConsumer(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null)
//			return ei.getMonConsumer();
//		return null;
//	}
//	
//	public EventRegisterConsumer getEventRegisterConsumer(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null)
//			return ei.getRegConsumer();
//		return null;
//	}
//	
//	public Date getLastStarted(String idconf, JUser authuser) {
//		RunningExpInfo ei = getExpInfo(idconf, authuser);
//		if (ei != null)
//			return ei.getLastStarted();
//		return null;
//	}

	


}
