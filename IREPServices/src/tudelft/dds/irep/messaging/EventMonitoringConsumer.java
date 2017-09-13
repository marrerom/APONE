package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExposureBody;
import tudelft.dds.irep.utils.Utils;

public class EventMonitoringConsumer extends DefaultConsumer {
	private Map<String, Integer> exposurecount;
	ObjectMapper mapper = new ObjectMapper();
	
	private Map<String, Map<Map<String, ?>, Integer>> subtreatmentcount; 

	public Map<String, Map<Map<String, ?>, Integer>> getSubtreatmentcount() {
		return subtreatmentcount;
	}

	public Map<String, Integer> getExposurecount() {
		return exposurecount;
	}

	public EventMonitoringConsumer(Channel channel) {
		super(channel);
		exposurecount = new HashMap<String, Integer>(); //pairs treatment-counter
		subtreatmentcount = new HashMap<String, Map<Map<String,?>, Integer>>();  //pairs treatment - <map pararms, count>
	}
	
	public EventMonitoringConsumer(Channel channel, Collection<JEvent> expEvents) {
		super(channel);
		exposurecount = new HashMap<String, Integer>(); //pairs treatment-counter
		subtreatmentcount = new HashMap<String, Map<Map<String,?>, Integer>>();  //pairs treatment - <map pararms, count>
		for (JEvent exp:expEvents)
			try {
				loadEvent(exp);
			} catch (IOException e) {
				//TODO: handle error properly -log
				e.printStackTrace();
			}
	}
	
	private void loadEvent(JEvent exp) throws JsonProcessingException, IOException {
//		String expbody = exp.getEvalue();
//		JsonNode jnode = mapper.readTree(expbody);
//		JExposureBody jexpbody = mapper.convertValue(jnode, JExposureBody.class);
		updateExposureCount(exp);
		updateSubtreatmentCount(exp);
	}
	
	private void updateExposureCount(JEvent jevent) {
		String treatment = jevent.getTreatment(); 
		Integer counter = exposurecount.get(treatment);
		if (counter == null) counter = 1; else counter++;
		exposurecount.put(treatment, counter);
	}
	
	private void updateSubtreatmentCount(JEvent jevent) {
		String treatment = jevent.getTreatment();
		Map<String,?> params =mapper.convertValue(jevent.getParamvalues(), Map.class);
		Map<Map<String, ?>, Integer> maptreatment = subtreatmentcount.get(treatment);
		if (maptreatment == null) {
			maptreatment = new HashMap<Map<String,?>, Integer>();
			maptreatment.put(params, 1);
			subtreatmentcount.put(treatment, maptreatment);
		} else {
			Integer counter = null;
			for (Map<String,?> mapparams: maptreatment.keySet()) {
				if (params.equals(mapparams)) {  //function equals in Map compares the entries of both Maps (key-value). TODO: another more efficient approach?
					counter = maptreatment.get(mapparams);
					maptreatment.put(mapparams, ++counter);
					break;
				}
			}
			if (counter == null) {
				maptreatment.put(params, 1);
			}
		}
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
