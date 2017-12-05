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

import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.UserRol;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.services.Experiment;

public class Security {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	
	public static boolean isAuthorized(JUser authuser, String username) {
		if (authuser.getRolEnum() == UserRol.ADMIN)
			return true;
		if (authuser.getName().equals(username))
			return true;
		return false;
	}
	
	public static void checkAuthorized(JUser authuser, String username) {
		if (!isAuthorized(authuser, username)) {
			String msg = "User not authorized";
			NotAuthorizedException e = new NotAuthorizedException(msg);
			log.log(Level.WARNING, msg, e);
			throw e;
		}
	}
	
	
	public static JUser getAuthenticatedUser(HttpServletRequest request)  {
		if (request.getSession().getAttribute("authuser") == null)
			throw new AuthenticationException();
		return (JUser) request.getSession().getAttribute("authuser");
	}
	
	public static void setAuthenticatedUser(HttpServletRequest request, ExperimentManager em, String idTwitter) throws IOException, ParseException {
//		JUser me = new JUser("socialdatadelft", UserRol.ADMIN);
//		me.setIdTwitter(idTwitter.toString());
//		me.setName(me.getIdname());
//		em.createUser(me, getMasterUser());
		JUser authuser = em.getUserByIdtwitter(idTwitter, getMasterUser());
		request.getSession().setAttribute("authuser", authuser);
	}
	
	public static JUser getMasterUser() {
		return new JUser("app-master", UserRol.ADMIN);
	}
	
	public static JUser getClientUser() {
		return new JUser("app-client", UserRol.ADMIN);
	}

}
