package tudelft.dds.irep.messaging;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.Utils;

public class EventRegisterConsumer extends DefaultConsumer {
	static public final int MAXATTEMPTS = 3;
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	private ExperimentManager em;

	public EventRegisterConsumer(Channel channel, ExperimentManager em) {
		super(channel);
		this.em = em;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		JEvent event;
		int attempts = MAXATTEMPTS;
		boolean done= false;
		while (attempts > 0) {
			try {
				event = (JEvent) Utils.deserialize(body);
				em.saveEvent(event, Security.getClientUser());
				this.getChannel().basicAck(envelope.getDeliveryTag(), true);
				attempts = 0;
				done = true;
			} catch (ClassNotFoundException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
			} catch (ParseException e) {
				log.log(Level.WARNING, "Parse error. Event message lost. "+e.getMessage(), e);
				attempts = 0;
			} catch (Exception e) {
				attempts =  attempts -1;
				log.log(Level.WARNING, "IO ERROR. Attempt "+attempts+"/"+MAXATTEMPTS+". "+e.getMessage(), e);
				if (attempts <= 0) {
					log.log(Level.SEVERE, "IO ERROR. Attempt "+attempts+"/"+MAXATTEMPTS+". Event message lost. "+e.getMessage(), e);
					
				}
			}
			finally {
				if (attempts <= 0 && !done) {
					try {
						this.getChannel().basicNack(envelope.getDeliveryTag(), true, false);
					} catch(IOException e) {
						log.log(Level.SEVERE, "RabbitMQ: basic Ack error "+e.getMessage(), e);
						throw new tudelft.dds.irep.utils.InternalServerException("RabbitMQ: basic Ack error "+e.getMessage());
					}
					//throw new tudelft.dds.irep.utils.BadRequestException("Message lost: the event could not be registered in the database. Please, check the contents are correct");
				}

			}
		}
		
	}
	
	
	public void handleShutdownSignal​(String consumerTag, ShutdownSignalException sig) {
		try {
			this.getChannel().basicCancel(consumerTag);
		} catch (IOException e) {
			log.log(Level.SEVERE, "IO ERROR. EventRegisterConsumer basic cancel error", sig.getCause());
		}
	}
}
