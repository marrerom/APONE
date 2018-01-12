package tudelft.dds.irep.messaging;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.Utils;




public class EventMonitorConsumer extends DefaultConsumer {
	static public final int MAXATTEMPTS = 3;
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());

	Map<String, EventMonitor> mapMonitor = new HashMap<String, EventMonitor>();

	public EventMonitorConsumer(Channel channel) {
		super(channel);
	}
	
	public EventMonitorConsumer(Channel channel, Collection<JEvent> events) {
		super(channel);
		events.stream().forEach(p -> {
			EventMonitor em = getEventMonitor(p.getEname()); 
			try {
				em.loadEvent(p);
			} catch (JsonProcessingException e) {
				log.log(Level.WARNING, "JSONProcessingExceptoin at saving event "+p.get_id(), e);
			} catch (IOException e) {
				log.log(Level.WARNING, "IO ERROR  at saving event "+p.get_id(), e);
			}
		});
	}
	
	
	public EventMonitor getEventMonitor(String ename) {
		if (mapMonitor.containsKey(ename))
			return mapMonitor.get(ename);
		EventMonitor emonitor = new EventMonitor();
		mapMonitor.put(ename, emonitor);
		return emonitor;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
		JEvent event;
		int attempts = MAXATTEMPTS;
		while (attempts > 0) {
			try {
				event = (JEvent) Utils.deserialize(body);
				getEventMonitor(event.getEname()).loadEvent(event);
				this.getChannel().basicAck(envelope.getDeliveryTag(), true);
				attempts = 0;
			} catch (ClassNotFoundException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				attempts = attempts -1 ;
				log.log(Level.WARNING, "IO ERROR. Attempt "+attempts+"/"+MAXATTEMPTS+". "+e.getMessage(), e);
				if (attempts <= 0) {
					log.log(Level.SEVERE, "IO ERROR. Attempt "+attempts+"/"+MAXATTEMPTS+". Monitoring Event message not processed for monitoring. "+e.getMessage(), e);
				}
			}
		}
	}
	
	public void handleShutdownSignal​(String consumerTag, ShutdownSignalException sig) {
		try {
			this.getChannel().basicCancel(consumerTag);
		} catch (IOException e) {
			log.log(Level.SEVERE, "IO ERROR. EventMonitoringConsumer basic cancel error", sig.getCause());
		}
	}

}
