package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.Utils;

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
			this.getChannel().basicAck(envelope.getDeliveryTag(), true);
		} catch (ClassNotFoundException | IOException | BadRequestException | ParseException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			//TODO: handle error properly 
		}
		
	}

}
