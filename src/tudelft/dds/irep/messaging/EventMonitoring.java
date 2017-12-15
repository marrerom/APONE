package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.services.Experiment;

abstract class EventMonitoring {
	protected Map<String, Set<String>> treatcount = new HashMap<String, Set<String>>();
	protected Map<String, Map<Map<String, ?>, Set<String>>> subtreatmentcount = new HashMap<String, Map<Map<String,?>, Set<String>>>();; //treatment - set<unitid>
	ObjectMapper mapper = new ObjectMapper();
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	public EventMonitoring() {	}
	
	public EventMonitoring(Collection<JEvent> events) {
		for (JEvent exp:events)
			try {
				loadEvent(exp);
			} catch (IOException e) {
				log.log(Level.SEVERE, "IO ERROR in EventMonitoringConsumer while loading events",e);
			}
	}
	
	public Map<String, Map<Map<String, ?>, Set<String>>> getSubtreatmentCount() { //treatment - params -set<unitid>
		return subtreatmentcount;
	}

	public Map<String, Set<String>> getTreatmentCount() {
		return treatcount;
	}
	
	protected void loadEvent(JEvent exp) throws JsonProcessingException, IOException {
		updateTreatmentCount(exp);
		updateSubtreatmentCount(exp);
	}
	
	public Integer getTotalCount() {
		int count = 0;
		for (Set<String> value:treatcount.values())
			count += value.size();
		return count;
	}
	
	private void updateTreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment(); 
		Set<String> unitids = treatcount.get(treatment);
		if (unitids == null) {
			unitids = new HashSet<String>();
			treatcount.put(treatment, unitids);
		}
		unitids.add(jevent.getIdunit());
	}
	
	private void updateSubtreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment();
		Map<String,?> params =mapper.convertValue(jevent.getParamvalues(), Map.class);
		Map<Map<String, ?>, Set<String>> maptreatment = subtreatmentcount.get(treatment);
		Set<String> unitids=null;
		if (maptreatment == null) {
			maptreatment = new HashMap<Map<String,?>, Set<String>>();
			unitids = new HashSet<String>();
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
				unitids = new HashSet<String>();
				maptreatment.put(params, unitids);
			}
		}
		unitids.add(jevent.getIdunit());
	}
	


}
