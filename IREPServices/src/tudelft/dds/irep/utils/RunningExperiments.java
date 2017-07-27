package tudelft.dds.irep.utils;

import java.util.HashMap;
import java.util.Map;

import com.glassdoor.planout4j.NamespaceConfig;

import jersey.repackaged.com.google.common.collect.Maps;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.Status;

class ExpInfo {
	public NamespaceConfig conf;
	public Status status;
	
	public ExpInfo(NamespaceConfig conf, Status status) {
		this.conf = conf;
		this.status = status;
	}
}

public class RunningExperiments {
	
	private Map<String, ExpInfo> idconfConfig;
	
	
	public RunningExperiments() {
		idconfConfig = new HashMap<String, ExpInfo>();
	}
	
	public Status getStatus(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null) {
			return ei.status;
		}
		return Status.OFF;
	}
	
	public void setExperiment(String idconf, NamespaceConfig conf, Status status) {
		if (status == Status.OFF)
			remove(idconf);
		else {
			put(idconf, conf, status);
		}
	}
	
	public NamespaceConfig getNsConfig(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.conf;
		return null;
	}


	private ExpInfo getExpInfo(String idconf){
		return idconfConfig.get(idconf);
	}
	
	private ExpInfo put(String idconf, NamespaceConfig conf, Status status){
		return idconfConfig.put(idconf,new ExpInfo(conf,status));
	}
	
	private ExpInfo remove(String idconf) {
		return idconfConfig.remove(idconf);
	}
	

}
