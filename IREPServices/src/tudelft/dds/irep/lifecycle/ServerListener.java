package tudelft.dds.irep.lifecycle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import tudelft.dds.irep.data.Database;

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
         Database db = (Database) sce.getServletContext().getAttribute("DBManager");
         db.close();
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
    	final String HOST = "localhost";
    	final int PORT = 27017;
    	final String DB = "irep";
    	
    	sce.getServletContext().setAttribute("DBHost", HOST);
    	sce.getServletContext().setAttribute("DBPort", PORT);
    	sce.getServletContext().setAttribute("DBName", DB);
    	sce.getServletContext().setAttribute("DBManager", new Database(HOST,PORT));
    }
	
}
