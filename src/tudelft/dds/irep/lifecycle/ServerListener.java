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


import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.database.MongoDB;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExperiments;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.JsonValidator;

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
			channel.close();
			con.close();
			
		} catch (IOException | TimeoutException e) {
			//TODO: LOG
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
//    	String DBHOST = "localhost";
//    	int DBPORT = 27017;
//    	String DB = "irep";
//    	String DBUSER = "irepuser";
//    	char[] DBPWD = new char[] {'0','0','0','0'};
//    	String RABBITHOST = "localhost";
    	
    	try {
    		sce.getServletContext().setAttribute("JsonValidator", new JsonValidator());
    		Properties prop = readProperties(sce.getServletContext());
    		
			ConnectionFactory rabbitFactory = new ConnectionFactory();
			rabbitFactory.setHost(prop.getProperty("RABBITHOST"));
			Connection rabbitConnection = rabbitFactory.newConnection();
			Channel channel = rabbitConnection.createChannel();
			sce.getServletContext().setAttribute("MsgChannel", channel);
    		
    		MongoDB db = new MongoDB(prop.getProperty("DBHOST"),Integer.parseInt(prop.getProperty("DBPORT")), prop.getProperty("DB"), prop.getProperty("DBUSER"), prop.getProperty("DBPWD").toCharArray());
    		sce.getServletContext().setAttribute("DBManager", db);
			sce.getServletContext().setAttribute("ExperimentManager", new ExperimentManager(db,new RunningExperiments(), channel));
			
    	} catch (IOException | ValidationException | ParseException | TimeoutException e) {
    		log.log(Level.SEVERE, e.getMessage(), e);
    		throw new RuntimeException(e);
		}
    }
	
}
