package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.utils.Utils;

public class EventMonitoringConsumer extends DefaultConsumer {
	private Map<String, Set<String>> treatcount;
	ObjectMapper mapper = new ObjectMapper();
	
	private Map<String, Map<Map<String, ?>, Set<String>>> subtreatmentcount; //treatment - set<unitid>

	public Map<String, Map<Map<String, ?>, Set<String>>> getSubtreatmentCount() { //treatment - params -set<unitid>
		return subtreatmentcount;
	}

	public Map<String, Set<String>> getTreatmentCount() {
		return treatcount;
	}
	
	public Integer getTotalCount() {
		int count = 0;
		for (Set<String> value:treatcount.values())
			count += value.size();
		return count;
	}

	public EventMonitoringConsumer(Channel channel) {
		super(channel);
		treatcount = new HashMap<String, Set<String>>(); //pairs treatment-set unitids
		subtreatmentcount = new HashMap<String, Map<Map<String,?>, Set<String>>>();  //pairs treatment - <map pararms, count>
	}
	
	public EventMonitoringConsumer(Channel channel, Collection<JEvent> expEvents) {
		super(channel);
		treatcount = new HashMap<String, Set<String>>(); //pairs treatment-counter
		subtreatmentcount = new HashMap<String, Map<Map<String,?>, Set<String>>>();  //pairs treatment - <map pararms, count>
		for (JEvent exp:expEvents)
			try {
				loadEvent(exp);
			} catch (IOException e) {
				//TODO: handle error properly -log
				e.printStackTrace();
			}
	}
	
	private void loadEvent(JEvent exp) throws JsonProcessingException, IOException {
		updateTreatmentCount(exp);
		updateSubtreatmentCount(exp);
	}
	
	private void updateTreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment(); 
		Set<String> unitids = treatcount.get(treatment);
		if (unitids == null) {
			unitids = new HashSet<String>();
			treatcount.put(treatment, unitids);
		}
		unitids.add(jevent.getUnitid());
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
		unitids.add(jevent.getUnitid());
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		JEvent event;
		try {
			event = (JEvent) Utils.deserialize(body);
			if (event.getEname().equals(JEvent.EXPOSURE_ENAME)) {
				loadEvent(event);
			}
			this.getChannel().basicAck(envelope.getDeliveryTag(), true);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			//TODO: handle error properly 
		}
	}

}
