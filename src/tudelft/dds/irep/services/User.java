package tudelft.dds.irep.services;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.data.schema.UserRol;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Path("/user")
public class User {
	final String TWITTER_APP_CLIENT_ID = "mHPy2OtLS3YECBjAhkvY63ph4";
	final String TWITTER_APP_CLIENT_SECRET = "GYFePsw6YEcM8IXyMz5iZ7iUj3byV9yvhBdhPSVus9PkNBwFuU";
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	@Context ServletContext context;

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
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Security.setAuthenticatedUser(request, em, ((Long)tok.getUserId()).toString());			
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
		JUser user = Security.getAuthenticatedUser(request);
		return user.getName();
	}
	
	@Path("/monitoring")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response monitoringUsers(@Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			UserRol restrictedTo = null; //assignment of experiments to participate restricted to this rol. Null means no restriction
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<JUser> users = em.getUsers(new JUser(), authuser);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			for (JUser user: users) {
				ObjectNode node = mapper.createObjectNode();
				node.put("_id", user.get_id());
				node.put("idname", user.getIdname());
				node.put("rol", user.getRol());
				node.put("nparticipated", user.getParticipatedexps().length);
				node.put("ncreated", getOwnExperiments(em,user).size());
				node.put("nleft", getLeft(em, getCandidateExperiments(em,user), user, restrictedTo ).size());
				arrayNode.add(node);
			}
		return Response.ok(mapper.writeValueAsString(arrayNode), MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	
	
	
	@Path("/assign")
	@GET
	public String assign(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			UserRol restrictedTo = null; //assignment of experiments to participate restricted to this rol. Null means no restriction
			List<Pair<String, Integer>> exp_participants = new ArrayList<Pair<String, Integer>>();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<RunningExpInfo> leftlist = getLeft(em, getCandidateExperiments(em, authuser), authuser, restrictedTo);
			if (leftlist.isEmpty())
				throw new BadRequestException("No more running experiments left to participate");
			for (RunningExpInfo left: leftlist) {
				Integer participants = left.getMonConsumer().getTotalCount();
				Pair<String,Integer> newpair = Pair.of(left.getIdconfig(), participants);
				exp_participants.add(newpair);
			}
			exp_participants.sort((a,b)->a.getRight().compareTo(b.getRight()));
			String idexp = exp_participants.get(0).getLeft();
			Set<String> list = new HashSet<String>();
			list.addAll(Arrays.asList(authuser.getParticipatedexps()));
			list.add(idexp);
			String[] array = list.toArray(new String[list.size()]);
			em.updateUserParticipation(authuser,array, Security.getMasterUser());
			Security.setAuthenticatedUser(request, em, authuser.getIdTwitter());
			URI redirection = new URI(uriInfo.getBaseUri()+"experiment/redirect/"+idexp+"/"+authuser.getIdTwitter());
			return redirection.toString();
		} catch (JsonProcessingException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException | URISyntaxException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	
	private Collection<JConfiguration> getOwnExperiments(ExperimentManager em, JUser user) throws JsonParseException, JsonMappingException, IOException, ParseException{
		Collection<JConfiguration> result = new HashSet<JConfiguration>();
		JExperiment filterExp = new JExperiment();
		filterExp.setExperimenter(user.getIdname());
		Collection<JExperiment> exps = em.getExperiments(filterExp, Security.getMasterUser());
		for (JExperiment exp:exps) {
			for (JConfiguration conf: exp.getConfig())
				result.add(conf);
		}
		return result;
	}
	
	
	//get running experiments not created by the user
	private Collection<RunningExpInfo> getCandidateExperiments(ExperimentManager em, JUser user) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Collection<RunningExpInfo> result = new HashSet<RunningExpInfo>();
		Collection<RunningExpInfo> running = em.getRunningExp(Security.getMasterUser());
		for (RunningExpInfo exp:running) {
			if (!exp.getExperimenter().equals(user.getIdname())) {
				result.add(exp);
			}
		}
		return result;
	}
	
	private Collection<RunningExpInfo> getLeft(ExperimentManager em, Collection<RunningExpInfo> candidates, JUser user, UserRol restrictToRol) throws IOException, ParseException{
		Collection<RunningExpInfo> result = new HashSet<RunningExpInfo>();
		List<String> list = Arrays.asList(user.getParticipatedexps());
		
		for (RunningExpInfo exp: candidates) {
			if (!list.contains(exp.getIdconfig())) {
				if (restrictToRol == null)
					result.add(exp);
				else {
					JUser expuser = em.getUserByIdname(exp.getExperimenter(), Security.getMasterUser());
					if (expuser.getRolEnum() == restrictToRol)
						result.add(exp);
				}
			}
		}
		return result;
	}
	

}
