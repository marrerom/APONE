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
	
	private Map<String, ExpInfo> idRunConfig;
	
	
	public Status getStatus(String idrun) {
		ExpInfo ei = getExpInfo(idrun);
		if (ei != null) {
			return ei.status;
		}
		return Status.OFF;
	}
	
	public void setExperiment(String idExp, NamespaceConfig conf, Status status) {
		if (status == Status.OFF)
			remove(idExp);
		else {
			put(idExp, conf, status);
		}
	}

	private RunningExperiments() {
		idRunConfig = new HashMap<String, ExpInfo>();
	}
	
	private ExpInfo getExpInfo(String idrun){
		return idRunConfig.get(idrun);
	}
	
	private ExpInfo put(String idExp, NamespaceConfig conf, Status status){
		return idRunConfig.put(idExp,new ExpInfo(conf,status));
	}
	
	private ExpInfo remove(String idrun) {
		return idRunConfig.remove(idrun);
	}
	

}
