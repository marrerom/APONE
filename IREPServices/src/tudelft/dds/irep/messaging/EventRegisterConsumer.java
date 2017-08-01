package tudelft.dds.irep.messaging;

import java.io.IOException;

import javax.servlet.ServletContext;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.util.Utils;

public class EventRegisterConsumer extends DefaultConsumer {
	private ExperimentManager em;

	public EventRegisterConsumer(Channel channel, ExperimentManager em) {
		super(channel);
		this.em = em;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		JEvent event;
		try {
			event = (JEvent) Utils.deserialize(body);
			em.saveEvent(event);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			//TODO: HANDLE ERROR 
		}
		
	}

}
