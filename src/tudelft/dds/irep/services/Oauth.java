package tudelft.dds.irep.services;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.User;
import tudelft.dds.irep.utils.InternalServerException;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Path("/")
public class Oauth {
	final String TWITTER_APP_CLIENT_ID = "xhbmgMn0JetyO5VQ51KVQd0K1";
	final String TWITTER_APP_CLIENT_SECRET = "hdvvFb0f0WeCa0BEXZySKbDKxRDerIFqmlRjvmlrHtx0lubqSg";

	@Path("/authenticate")
	@GET
	public Response doGet(@Context HttpServletRequest request) {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(TWITTER_APP_CLIENT_ID, TWITTER_APP_CLIENT_SECRET);
		request.getSession().setAttribute("twitter", twitter);
		try {
			StringBuffer callbackURL = request.getRequestURL();
			int index = callbackURL.lastIndexOf("/");
			callbackURL.replace(index, callbackURL.length(), "").append("/callback");

			RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
			// RequestToken requestToken = twitter.getOAuthRequestToken();
			request.getSession().setAttribute("requestToken", requestToken);

			URI uri = new URI(requestToken.getAuthenticationURL());
			Response response = Response.seeOther(uri).build();
			return response;
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}

	}

	@Path("/callback")
	@GET
	public Response callback(@Context HttpServletRequest request) {
		Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
		RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
		String verifier = request.getParameter("oauth_verifier");
		try {
			AccessToken tok = twitter.getOAuthAccessToken(requestToken, verifier);
			request.getSession().removeAttribute("requestToken");
			request.getSession().removeAttribute("twitter");

			//TODO: check if valid user in db and rol
			Security.setAuthenticatedUser(request, tok.getScreenName());			
			URI uri = new URI("http://localhost:8080/IREPlatform"); // Redirect to IREPGUI
			Response response = Response.seeOther(uri).build();
			return response;
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}
	
	@Path("/authenticatedUser")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAuthenticatedUser(@Context HttpServletRequest request) {
		User user = Security.getAuthenticatedUser(request);
		return user.getName();
	}

}
