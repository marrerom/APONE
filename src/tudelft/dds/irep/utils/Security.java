package tudelft.dds.irep.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonValue;

import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.UserRol;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.services.Experiment;

public class Security {
	
	public enum PredefinedUsers {
		MASTER,
		CLIENT,
		ANONYMOUS;
		
		public static boolean isPredefined(String idname) {
			for (PredefinedUsers predefined: PredefinedUsers.values()) {
				if (idname.equals(predefined.toString()))
						return true;
			}
			return false;
		}
	}
	
	public enum Useraction {
		READ,
		WRITE,
	}
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	
	public static boolean isAuthorized(JUser authuser, String username, Useraction action) {
		if (authuser.getRolEnum() == UserRol.ADMIN)
			return true;
		if (authuser.getName().equals(username))
			return true;
		if (username.equals(PredefinedUsers.ANONYMOUS.toString()) && action == Useraction.READ)
			return true;
		return false;
	}
	
	public static void checkAuthorized(JUser authuser, String username, Useraction action) {
		if (!isAuthorized(authuser, username, action)) {
			String msg = "User not authorized";
			NotAuthorizedException e = new NotAuthorizedException(msg);
			log.log(Level.WARNING, msg, e);
			throw e;
		}
	}
	
	public synchronized static JUser getAuthenticatedUser(HttpServletRequest request)  {
		if (request.getSession().getAttribute("authuser") == null)
			throw new AuthenticationException();
		return (JUser) request.getSession().getAttribute("authuser");
	}
	
	public synchronized static JUser setAuthenticatedUser(HttpServletRequest request, ExperimentManager em, String idTwitter, String screenName) throws IOException, ParseException {
		JUser user = em.createUser(idTwitter, screenName, getMasterUser());
		request.getSession().setAttribute("authuser", user);
		return user;
	}
	
	public static JUser getMasterUser() {
		return new JUser(PredefinedUsers.MASTER.toString(), UserRol.ADMIN);
	}
	
	public static JUser getClientUser() {
		return new JUser(PredefinedUsers.CLIENT.toString(), UserRol.ADMIN);
	}
	
	public static JUser getAnonymousUser() {
		return new JUser(PredefinedUsers.ANONYMOUS.toString(), UserRol.USER);
	}

}
