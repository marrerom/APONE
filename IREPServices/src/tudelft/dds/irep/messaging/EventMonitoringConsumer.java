package tudelft.dds.irep.messaging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import tudelft.dds.irep.experiment.ExperimentManager;

public class EventMonitoringConsumer extends DefaultConsumer {
	private ExperimentManager em;

	public EventMonitoringConsumer(Channel channel, ExperimentManager em) {
		super(channel);
		this.em = em;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		//TODO
	}

}
