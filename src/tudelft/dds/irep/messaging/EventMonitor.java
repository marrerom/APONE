package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.services.Experiment;

public class EventMonitor {
	Map<String, List<String>> treatcount;
	Map<String, Map<Map<String, ?>, List<String>>> subtreatmentcount; //treatment - set<unitid>
	ObjectMapper mapper = new ObjectMapper();
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	public EventMonitor() {
		treatcount = new HashMap<String, List<String>>();
		subtreatmentcount = new HashMap<String, Map<Map<String,?>, List<String>>>(); //treatment - set<unitid>
	}
	
//	public EventMonitor(Collection<JEvent> events) {
//		this();
//		for (JEvent exp:events)
//			try {
//				loadEvent(exp);
//			} catch (IOException e) {
//				log.log(Level.SEVERE, "IO ERROR in EventMonitoringConsumer while loading events",e);
//			}
//	}
	
//	public Map<String, Map<Map<String, ?>, List<String>>> getSubtreatmentCount() { //treatment - params -set<unitid>
//		return subtreatmentcount;
//	}
//
//	public Map<String, List<String>> getTreatmentCount() {
//		return treatcount;
//	}
	
	
	protected synchronized void loadEvent(JEvent exp) throws JsonProcessingException, IOException {
		updateTreatmentCount(exp);
		updateSubtreatmentCount(exp);
	}
	
	public Long getTotalCount(boolean differentuser) {
		if (treatcount.values().isEmpty()) 
			return (long)0;
		if (!differentuser)
			return treatcount.values().stream().mapToLong(p -> p.size()).sum();
		else
			return	treatcount.values().stream().mapToLong(p -> p.stream().distinct().count()).sum();
//		int count = 0;
//		for (Set<String> value:treatcount.values())
//			count += value.size();
//		return count;
	}
	
	public Set<String> getTreatments(){
		return treatcount.keySet();
	}
	
	public Set<Map<String, ?>> getSubtreatments(String treatment){
		if (subtreatmentcount.get(treatment) == null)
			return new HashSet<Map<String, ?>>();
		return subtreatmentcount.get(treatment).keySet();
	}
	
	public Long getTreatmentCount(String treatment, boolean differentuser) {
		if (treatcount.get(treatment) == null)
			return (long) 0;
		if (!differentuser)
			return treatcount.get(treatment).stream().count();
		else
			return	treatcount.get(treatment).stream().distinct().count();
	}
	
	public Long getSubTreatmentCount(String treatment, Map<String,?> params, boolean differentuser) {
		Map<Map<String,?>, List<String>> subt = subtreatmentcount.get(treatment);
		if (subt == null || subt.get(params) == null)
			return (long) 0;
		if (!differentuser)
			return subtreatmentcount.get(treatment).get(params).stream().count();
		else
			return	subtreatmentcount.get(treatment).get(params).stream().distinct().count();
	}
	
	private void updateTreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment(); 
		List<String> unitids = treatcount.get(treatment);
		if (unitids == null) {
			unitids = new ArrayList<String>();
			treatcount.put(treatment, unitids);
		}
		unitids.add(jevent.getIdunit());
	}
	
	private void updateSubtreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment();
		Map<String,?> params =mapper.convertValue(jevent.getParamvalues(), Map.class);
		Set<String> toremove = params.keySet().stream().filter(p->p.startsWith("_")).collect(Collectors.toSet());
		toremove.stream().forEach(p->params.remove(p));
		Map<Map<String, ?>, List<String>> maptreatment = subtreatmentcount.get(treatment);
		List<String> unitids=null;
		if (maptreatment == null) {
			maptreatment = new HashMap<Map<String,?>, List<String>>();
			unitids = new ArrayList<String>();
			maptreatment.put(params, unitids);
			subtreatmentcount.put(treatment, maptreatment);
		} else {
			for (Map<String,?> mapparams: maptreatment.keySet()) {
				if (params.equals(mapparams)) {  //function equals in Map compares the entries of both Maps (key-value). TODO: another more efficient approach?
					unitids = maptreatment.get(mapparams);
					break;
				}
			}
			if (unitids == null) {
				unitids = new ArrayList<String>();
				maptreatment.put(params, unitids);
			}
		}
		unitids.add(jevent.getIdunit());
	}
	


}
