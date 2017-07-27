package tudelft.dds.irep.lifecycle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.BSON;
import org.bson.Transformer;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.glassdoor.planout4j.config.ValidationException;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.database.MongoDB;
import tudelft.dds.irep.utils.ExperimentManager;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.RunningExperiments;

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
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 

    	//TODO: check RunningExperiments, what should we do if there are running experiments?
    	
    	 Database db = (Database) sce.getServletContext().getAttribute("DBManager");
         db.close();
         
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	String HOST = "localhost";
    	int PORT = 27017;
    	String DB = "irep";
    	String USER = "irepuser";
    	char[] PWD = new char[] {'0','0','0','0'};
    	
    	try {
    		MongoDB db = new MongoDB(HOST,PORT, DB, USER, PWD);
    		RunningExperiments re = new RunningExperiments();
			sce.getServletContext().setAttribute("ExperimentManager", new ExperimentManager(db,re));
			sce.getServletContext().setAttribute("JsonValidator", new JsonValidator());
    	} catch (IOException | ValidationException | ParseException e) {
			e.printStackTrace();
			//TODO: manage this exception by showing an error message in the servlets requests
		}
    	
    	//TODO: check RunningExperiments, what should we do if there are running experiments?
    }
	
}
