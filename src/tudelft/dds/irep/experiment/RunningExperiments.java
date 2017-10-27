package tudelft.dds.irep.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.glassdoor.planout4j.NamespaceConfig;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;

public class RunningExperiments {
	
	private Map<String, RunningExpInfo> idconfConfig;
	

	public RunningExperiments() {
		idconfConfig = new HashMap<String, RunningExpInfo>();
	}
	
	public Collection<RunningExpInfo> getRunningExp(){
		return idconfConfig.values();
	}
	
	public RunningExpInfo getRunningExp(String idconf) {
		return idconfConfig.get(idconf);
	}
	
	public List<String> getExperiments(List<Status> status) {
		List<String> result = new ArrayList<String>();
		for (String idconfig: idconfConfig.keySet()) {
			if (status.contains(getStatus(idconfig))){
				result.add(idconfig);
			}
		}
		return result;
	}
	
	public Status getStatus(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null) {
			return ei.getStatus();
		}
		return Status.OFF;
	}
	
	public Date getDateToEnd(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null) {
			return ei.getDateToEnd();
		}
		return null;
	}
	
	public Integer getMaxExposures(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null) {
			return ei.getMaxExposures();
		}
		return null;
	}
	
	public synchronized void setExperiment(JConfiguration conf, NamespaceConfig nsconf, Status targetStatus, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date lastStarted) throws IOException {
		String idconf = conf.get_id();
		Date dateToEnd = conf.getDate_to_end();
		Integer maxExposures = conf.getMax_exposures();
		if (targetStatus == Status.OFF)
			setExperimentOFF(idconf);
		else {
			if (targetStatus == Status.PAUSED) {
				put(idconf, nsconf, targetStatus, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted);
			} else if (targetStatus == Status.ON){
				put(idconf, nsconf, targetStatus, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted);
			}
		}
	}

	public synchronized void setExperimentOFF(String idconf) throws IOException {
		remove(idconf);
	}
	
	public NamespaceConfig getNsConfig(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.getConf();
		return null;
	}
	
	public EventMonitoringConsumer getEventMonitoringConsumer(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.getMonConsumer();
		return null;
	}
	
	public EventRegisterConsumer getEventRegisterConsumer(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.getRegConsumer();
		return null;
	}
	
	public Date getLastStarted(String idconf) {
		RunningExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.getLastStarted();
		return null;
	}

	public RunningExpInfo getExpInfo(String idconf){
		return idconfConfig.get(idconf);
	}
	
	private RunningExpInfo put(String idconf, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date dateToEnd, Integer maxExposures, Date lastStarted){
		return idconfConfig.put(idconf,new RunningExpInfo(idconf,conf,status, regConsumer, monConsumer, dateToEnd, maxExposures, lastStarted));
	}
	
	private RunningExpInfo remove(String idconf) throws IOException {
		return idconfConfig.remove(idconf);
	}

}
