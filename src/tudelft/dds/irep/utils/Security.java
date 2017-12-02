package tudelft.dds.irep.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import tudelft.dds.irep.services.Experiment;

public class Security {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	public enum Rol {
		USER,
		ADMIN;
	}
	
	public static boolean isAuthorized(User authuser, String username) {
		if (authuser.getRol() == Rol.ADMIN)
			return true;
		if (authuser.getName().equals(username))
			return true;
		return false;
	}
	
	public static void checkAuthorized(User authuser, String username) {
		if (!isAuthorized(authuser, username)) {
			String msg = "User not authorized";
			NotAuthorizedException e = new NotAuthorizedException(msg);
			log.log(Level.WARNING, msg, e);
			throw e;
		}
	}
	
	public static User getAuthenticatedUser(HttpServletRequest request)  {
		if (request.getSession().getAttribute("user") == null || request.getSession().getAttribute("rol") == null)
			throw new AuthenticationException();
		return new User(request.getSession().getAttribute("user").toString(), Rol.valueOf(request.getSession().getAttribute("rol").toString()));
	}
	
	public static void setAuthenticatedUser(HttpServletRequest request, String user) {
		request.getSession().setAttribute("user", user);
		request.getSession().setAttribute("rol", Security.Rol.ADMIN.toString()); //TODO: check in database, update there
	}
	
	public static User getMasterUser() {
		return new User("master", Rol.ADMIN);
	}
	
	public static User getClientUser() {
		return new User("client", Rol.ADMIN);
	}

}
