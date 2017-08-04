package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExposureBody;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.Utils;

public class EventMonitoringConsumer extends DefaultConsumer {
	private ExperimentManager em;
	private Map<String, Integer> exposurecount;

	public Map<String, Integer> getExposurecount() {
		return exposurecount;
	}

	public EventMonitoringConsumer(Channel channel, ExperimentManager em) {
		super(channel);
		this.em = em;
		exposurecount = new HashMap<String, Integer>(); //pairs treatment-counter
	}
	
	public EventMonitoringConsumer(Channel channel, ExperimentManager em, Map<String, Integer> status) {
		super(channel);
		this.em = em;
		this.exposurecount = status;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		JEvent event;
		try {
			event = (JEvent) Utils.deserialize(body);
			if (event.getEname().equals(JExposureBody.EVENT_ENAME)) {
				String expbody = event.getEvalue();
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jnode = mapper.readTree(expbody);
				JExposureBody jexpbody = mapper.convertValue(jnode, JExposureBody.class);
				String treatment = jexpbody.getTreatment(); 
				Integer counter = exposurecount.get(treatment);
				if (counter == null) counter = 1; else counter++;
				exposurecount.put(treatment, counter);
			}
			this.getChannel().basicAck(envelope.getDeliveryTag(), true);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			//TODO: handle error properly 
		}
	}

}
