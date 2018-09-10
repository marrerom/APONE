package tudelft.dds.irep.lifecycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.UserRol;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExperiments;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.InternalServerException;
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
   	
		
		
//    	try {
//    		FileInputStream streamIn = new FileInputStream("/home/mmarrero/Downloads/msg-0000");
//    	     ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
//    	     JEvent readCase = (JEvent) objectinputstream.readObject();
//    	     readCase.setIdconfig("5a8d9b9bda0ed1063b9303f5");
//    	     System.out.println(readCase);
//    	 } catch (Exception e) {
//    	     e.printStackTrace();
//    	 }
    	
    	
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
    		
    		String adminName = prop.getProperty("ADMINTWITTERNAME");
    		sce.getServletContext().setAttribute("adminTwitterName", adminName);
    		String adminID = prop.getProperty("ADMINTWITTERID");
    		//JUser admin = new JUser("socialdatadelft",  "937708183979773955", UserRol.ADMIN);
    		JUser admin = new JUser(adminName,  adminID, UserRol.ADMIN);

	
    		if (em.getUsers(master, master).isEmpty()) {
    			JUser masteruser = em.createMasterUser(master.getIdTwitter(), master.getName(), master);
    			if (!masteruser.getIdname().equals(master.getIdname()))
    				throw new InternalServerException("Master (MASTER) user name already existing in the database for other user");
    		}
    		if (em.getUsers(client, master).isEmpty()) {
    			JUser clientuser = em.createMasterUser(client.getIdTwitter(), client.getName(),  master);
    			if (!clientuser.getIdname().equals(client.getIdname()))
    				throw new InternalServerException("Client (CLIENT) user name already existing in the database for other user");
    		}
    		
    		if (em.getUsers(admin, master).isEmpty()) {
    			JUser sdduser = em.createMasterUser(admin.getIdTwitter(), admin.getName(), master);
    			if (!sdduser.getIdname().equals(admin.getIdname()))
    				throw new InternalServerException("User name '"+ adminName +"' already existing in the database for other user");
    		}

    		if (em.getUsers(anonymous, master).isEmpty()) {
				JUser anonuser = em.createRegularUser(anonymous.getIdTwitter(), anonymous.getName(), master);
    			if (!anonuser.getIdname().equals(anonymous.getIdname()))
    				throw new InternalServerException("Anonymous (ANONYMOUS) user name already existing in the database for other user");
    		}
    		
    		
   		
    		
    	} catch (Exception  e) {
    		log.log(Level.SEVERE, e.getMessage(), e);
    		throw new RuntimeException(e);
		}
    }
	
}
