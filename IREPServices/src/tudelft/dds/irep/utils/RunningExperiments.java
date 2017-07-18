package tudelft.dds.irep.utils;

import java.util.HashMap;
import java.util.Map;

import com.glassdoor.planout4j.NamespaceConfig;

public class RunningExperiments {
	
	private Map<String, NamespaceConfig> idRunConfig;
	
	public RunningExperiments() {
		idRunConfig = new HashMap<String, NamespaceConfig>();
	}
	
	public NamespaceConfig getConf(String idrun){
		return idRunConfig.get(idrun);
	}
	
	public boolean exist(String idrun){
		return idRunConfig.containsKey(idrun);
	}
	
	public NamespaceConfig put(java.util.Map.Entry<String, NamespaceConfig> pair){
		return idRunConfig.put(pair.getKey(),pair.getValue());
	}
}
