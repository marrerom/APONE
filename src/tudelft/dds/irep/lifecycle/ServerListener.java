package tudelft.dds.irep.lifecycle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

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


import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.database.MongoDB;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExperiments;
import tudelft.dds.irep.utils.JsonValidator;

/**
 * Application Lifecycle Listener implementation class ServerListener
 *
 */
@WebListener
public class ServerListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ServerListener() {
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
        try {
        	Database db = (Database) sce.getServletContext().getAttribute("DBManager");
        	db.close();
         
        	Channel channel = (Channel) sce.getServletContext().getAttribute("MsgChannel");
			Connection con = channel.getConnection();
			channel.close();
			con.close();
			
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			//TODO: handle error properly
		}
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	String DBHOST = "localhost";
    	int DBPORT = 27017;
    	String DB = "irep";
    	String DBUSER = "irepuser";
    	char[] DBPWD = new char[] {'0','0','0','0'};
    	String RABBITHOST = "localhost";
    	
    	try {
    		sce.getServletContext().setAttribute("JsonValidator", new JsonValidator());
    		
			ConnectionFactory rabbitFactory = new ConnectionFactory();
			rabbitFactory.setHost(RABBITHOST);
			Connection rabbitConnection = rabbitFactory.newConnection();
			Channel channel = rabbitConnection.createChannel();
			sce.getServletContext().setAttribute("MsgChannel", channel);
    		
    		MongoDB db = new MongoDB(DBHOST,DBPORT, DB, DBUSER, DBPWD);
    		sce.getServletContext().setAttribute("DBManager", db);
			sce.getServletContext().setAttribute("ExperimentManager", new ExperimentManager(db,new RunningExperiments(), channel));
			
    	} catch (IOException | ValidationException | ParseException | TimeoutException e) {
			e.printStackTrace();
			//TODO: handle error properly
		}
    }
	
}
