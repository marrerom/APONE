package tudelft.dds.irep.lifecycle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.BSON;
import org.bson.Transformer;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.glassdoor.planout4j.config.ValidationException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.database.MongoDB;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExperiments;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.Security;

/**
 * Application Lifecycle Listener implementation class ServerListener
 *
 */
@WebListener
public class ServerListener implements ServletContextListener {

	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
        try {
        	Database db = (Database) sce.getServletContext().getAttribute("DBManager");
        	db.close();
         
        	Channel channel = (Channel) sce.getServletContext().getAttribute("MsgChannel");
			Connection con = channel.getConnection();
			
			//channel.close();
			//con.close();
			channel.abort();
			con.abort();
			
		} catch (IOException  e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
    }
    
	public Properties readProperties(ServletContext sc) throws IOException {
		InputStream is = sc.getResourceAsStream("/WEB-INF/config.properties");
		Properties p = new Properties();
		p.load(is);
		return p;
		//return p.getProperty("EXPERIMENT_ID");
	}

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	
    	try {
    		//PROPERTIES FILE
    		Properties prop = readProperties(sce.getServletContext());
    		
    		//RABBITMQ
			ConnectionFactory rabbitFactory = new ConnectionFactory();
			rabbitFactory.setHost(prop.getProperty("RABBITHOST"));
			rabbitFactory.setPort(Integer.parseInt(prop.getProperty("RABBITPORT")));
			Connection rabbitConnection = rabbitFactory.newConnection();
			log.log(Level.INFO, "RabbitMQ Connection Created");
			Channel channel = rabbitConnection.createChannel();
			log.log(Level.INFO, "RabbitMQ Channel Created");
			String exchangeName = prop.getProperty("RABBITEXCHANGE");
			channel.exchangeDeclare(exchangeName, "direct", true);
			log.log(Level.INFO, "RabbitMQ Channel binded to "+exchangeName+" exchange");
			sce.getServletContext().setAttribute("MsgChannel", channel);
			
			channel.addShutdownListener(new ShutdownListener() {
		        public void shutdownCompleted (ShutdownSignalException cause) {
		        	if (cause.isInitiatedByApplication()) {
		        		log.log(Level.SEVERE, "RabbitMQ closed by error in the app");
		        		throw new RuntimeException(cause);
		        	} else {
		        		log.log(Level.SEVERE, "Connectivity to RabbitMQ has failed.  Reason received " + cause.getMessage(), cause);
		        		throw new RuntimeException(cause); //TODO: create new channel and rebind all existing queues?
		        	}
		        }
		      });
			
    		//MONGODB
    		MongoDB db = new MongoDB(prop.getProperty("MONGOHOST"),Integer.parseInt(prop.getProperty("MONGOPORT")), prop.getProperty("MONGODB"), prop.getProperty("MONGOUSER"), prop.getProperty("MONGOUSERPWD").toCharArray());
    		sce.getServletContext().setAttribute("DBManager", db);
			
    		//EXPERIMENT MANAGER & JSON VALIDATOR
    		ExperimentManager em = new ExperimentManager(db,new RunningExperiments(), channel);
    		sce.getServletContext().setAttribute("ExperimentManager", em);
    		sce.getServletContext().setAttribute("JsonValidator", new JsonValidator());
    		sce.getServletContext().setAttribute("limitedAccess", Boolean.parseBoolean(prop.getProperty("LIMITEDACCESS")));
    		
    		//CREATE PREDEFINED USERS IN DATABASE
    		JUser master = Security.getMasterUser();
    		JUser anonymous = Security.getAnonymousUser();
    		JUser client = Security.getClientUser();
    		if (em.getUsers(master, master).isEmpty()) {
    				em.createUser(master.getIdTwitter(), master.getName(), master);
    			}
    		if (em.getUsers(client, master).isEmpty()) {
				em.createUser(client.getIdTwitter(), client.getName(), master);
			}
    		if (em.getUsers(anonymous, master).isEmpty()) {
				em.createUser(anonymous.getIdTwitter(), anonymous.getName(), master);
			}

    	} catch (IOException | ValidationException | ParseException | TimeoutException e) {
    		log.log(Level.SEVERE, e.getMessage(), e);
    		throw new RuntimeException(e);
		}
    }
	
}
