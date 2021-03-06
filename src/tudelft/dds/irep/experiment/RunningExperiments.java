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
import tudelft.dds.irep.messaging.EventMonitorConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.Security;

public class RunningExperiments {
	
	private Map<String, RunningExpInfo> idconfConfig;
	

	public RunningExperiments() {
		idconfConfig = new HashMap<String, RunningExpInfo>();
	}
	
	public Collection<RunningExpInfo> getRunningExp(JUser authuser){
		synchronized(this) {
			return filterByOwnership(authuser, idconfConfig.values());
		}
	}
	
	public RunningExpInfo getExpInfo(String idconf, JUser authuser){
		synchronized(this) {
			RunningExpInfo runningExp = idconfConfig.get(idconf);
			if (runningExp != null)
				Security.checkAuthorized(authuser, runningExp.getExperimenter(), Security.Useraction.READ);
			return runningExp;
		}
	}
	
	public Status getExpStatus(String idconf) {
		synchronized(this) {
			RunningExpInfo runningExp = idconfConfig.get(idconf);
			if (runningExp == null)
				return Status.OFF;
			
			return runningExp.getStatus();
		}
	}
	
	public void setExperiment(JConfiguration conf, NamespaceConfig nsconf, Status targetStatus, EventRegisterConsumer regConsumer, EventMonitorConsumer monConsumer, Date lastStarted, String expowner, JUser authuser) throws IOException {
		synchronized(this) {
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
	}

	public void setExperimentOFF(String idconf, JUser authuser) throws IOException {
		synchronized(this) {
			remove(idconf, authuser);
		}
	}

	
	private Collection<RunningExpInfo> filterByOwnership(JUser authuser, Collection<RunningExpInfo> runningExp) {
		Collection<RunningExpInfo> results = new HashSet<RunningExpInfo>();
		for (RunningExpInfo exp:runningExp) {
			if (Security.isAuthorized(authuser, exp.getExperimenter(), Security.Useraction.READ)) {
				results.add(exp);
			}
		}
		return results;
	}
	
	
	private RunningExpInfo put(String idconf, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitorConsumer monConsumer, Date dateToEnd, Integer maxExposures, Date lastStarted, String expowner, JUser authuser){
		RunningExpInfo newExp = new RunningExpInfo(idconf,conf,status, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted, expowner);
		Security.checkAuthorized(authuser, newExp.getExperimenter(), Security.Useraction.WRITE);
		return idconfConfig.put(idconf,newExp);
	}
	
	private RunningExpInfo remove(String idconf, JUser authuser) throws IOException {
		RunningExpInfo toremove = idconfConfig.get(idconf);
		if (toremove != null) {
			Security.checkAuthorized(authuser, toremove.getExperimenter(), Security.Useraction.WRITE);
			return idconfConfig.remove(toremove.getIdconfig());
		}
		return null;
	}
	
}
