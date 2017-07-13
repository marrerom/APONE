package tudelft.dds.irep.lifecycle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import tudelft.dds.irep.data.Database;
import tudelft.dds.irep.data.MongoDB;
import tudelft.dds.irep.services.RunningExperiments;

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
    	
    	sce.getServletContext().setAttribute("DBManager", new MongoDB(HOST,PORT, DB, USER, PWD));
    	sce.getServletContext().setAttribute("RunningExperiments", new RunningExperiments());
    }
	
}
