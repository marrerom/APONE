package tudelft.dds.irep.test;
import java.util.HashMap;
import java.util.Map;

import com.glassdoor.planout4j.NamespaceConfig;

public class ConfigRepository {
	static private Map<Integer, NamespaceConfig> confObjects = new HashMap<Integer,NamespaceConfig>();

	public static void addConf(Integer hash, NamespaceConfig nsConf){
		confObjects.put(hash, nsConf);
	}
	
	
	public static NamespaceConfig getConf(Integer hash){
		return confObjects.get(hash);
	}
	
	public static boolean exist(Integer hash){
		return confObjects.containsKey(hash);
	}

}
