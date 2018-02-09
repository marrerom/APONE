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
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

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
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
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
			Security.setAuthenticatedUser(request, em, ((Long)tok.getUserId()).toString(), tok.getScreenName());			
			//URI uri = new URI("http://ireplatform.ewi.tudelft.nl:8080/IREPlatform");
			
			URI uri = new URI(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getServletContext().getContextPath());
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
				node.put("nparticipated", getCompletedExperiments(em, user).size());
				node.put("ncreated", getOwnExperiments(em,user).size());
				node.put("nleft", getCandidateExperiments(getCompletedExperiments(em, user), em,user,restrictedTo).size());
				arrayNode.add(node);
			}
		return Response.ok(mapper.writeValueAsString(arrayNode), MediaType.APPLICATION_JSON).build();
		} catch (BadRequestException | JsonProcessingException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
	}
	
	
	
	
	@Path("/assignexp")
	@GET
	public String assign(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			UserRol restrictedTo = null; //assignment of experiments to participate restricted to this rol. Null means no restriction
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<RunningExpInfo> leftlist = getCandidateExperiments(getCompletedExperiments(em, authuser), em, authuser, restrictedTo);
			if (leftlist.isEmpty())
				throw new BadRequestException("No more running experiments left to participate");
			//Select that one with less exposures -tricky: 
			//what if the client does not send the event exposure properly? we would be stuck on it
//			List<Pair<String, Integer>> exp_participants = new ArrayList<Pair<String, Integer>>();
//			for (RunningExpInfo left: leftlist) {
//				Integer participants = left.getMonConsumer().getExposureEvents().getTotalCount();
//				Pair<String,Integer> newpair = Pair.of(left.getIdconfig(), participants);
//				exp_participants.add(newpair);
//			}
//			Random rand = new Random();
//			exp_participants.sort((a,b)-> a.getRight().compareTo(b.getRight()));
//			Integer minexposures = exp_participants.get(0).getRight();
//			Integer lastindex = 0;
//			for (Pair<String, Integer> p: exp_participants) {
//				if (p.getRight() == minexposures)
//					lastindex = exp_participants.indexOf(p);
//			}
//			String idexp = exp_participants.get(rand.nextInt(lastindex+1)).getLeft();
			
			//Select experiment randomly
			Random rand = new Random();
			String idexp = new ArrayList<RunningExpInfo>(leftlist).get(rand.nextInt(leftlist.size())).getIdconfig();
			
			URI redirection = new URI(uriInfo.getBaseUri()+"experiment/redirect/"+idexp+"/"+authuser.getName());
			String redirectionStr = redirection.toString(); 
			return redirectionStr;
		} catch (BadRequestException | JsonProcessingException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
	}
	
	@Path("/checkcompleted/{idrun}/{idunit}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response userCompletedExp(@PathParam("idrun") String idrun, @PathParam("idunit") String idunit, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JEvent filter = new JEvent();
			filter.setEname(JEvent.COMPLETED_ENAME);
			filter.setIdunit(idunit);
			filter.setIdconfig(idrun);
			Boolean result = false;
			if (!em.getEvents(filter, authuser).isEmpty())
				result = true;
			ResponseBuilder response = Response.ok(result.toString(),MediaType.TEXT_PLAIN);
			response.header("Access-Control-Allow-Origin", "*");
		    response.header("Access-Control-Allow-Headers","origin, content-type, accept");
		    response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");
			return response.build();
		} catch (BadRequestException | JsonProcessingException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
	}
	
//	@Path("/setCompletedExp/{idrun}/{idunit}")
//	@PUT
//	@Produces(MediaType.APPLICATION_JSON)
//	public void setCompletedExp() {
//		Set<String> list = new HashSet<String>();
//		list.addAll(Arrays.asList(authuser.getParticipatedexps()));
//		list.add(idexp);
//		String[] array = list.toArray(new String[list.size()]);
//		em.updateUserParticipation(authuser,array, Security.getMasterUser());
//Security.setAuthenticatedUser(request, em, authuser.getIdTwitter());
//	}
	
	
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
	
	private Collection<String> getCompletedExperiments(ExperimentManager em, JUser user) throws JsonParseException, JsonMappingException, IOException, ParseException{
		JUser authuser = Security.getMasterUser();
		JEvent filterEvent = new JEvent();
		filterEvent.setEname(JEvent.COMPLETED_ENAME);
		filterEvent.setIdunit(user.getIdname());
		Set<String> completedExps = em.getEvents(filterEvent,authuser).stream().map(p->p.getIdconfig()).collect(Collectors.toSet());
		return completedExps;
	}
	
	
	//get running experiments not created by the user, on and not done previously by the user (event ename=COMPLETED_ENAME)
	private Collection<RunningExpInfo> getCandidateExperiments(Collection<String> completedExperiments, ExperimentManager em, JUser user, UserRol restrictToRol) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Collection<RunningExpInfo> result = new HashSet<RunningExpInfo>();
		JUser authuser = Security.getMasterUser();
		Set<String> running = em.getRunningExp(authuser).stream().map(p->p.getIdconfig()).collect(Collectors.toSet());

		JUser filterUser = new JUser();
		if (restrictToRol != null)
			filterUser.setRol(restrictToRol.toString());;
		Set<String> validusers = em.getUsers(filterUser, authuser).stream().map(p->p.getIdname()).collect(Collectors.toSet());;
		running.removeAll(completedExperiments);
		validusers.remove(user.getIdname());
		
		for (String idconf:running) {
			try {
				RunningExpInfo rei = em.getRunningExp(idconf, authuser);
				if (validusers.contains(rei.getExperimenter()) && containURL(em, rei.getIdconfig())) {
					result.add(rei);
				}
			} catch(BadRequestException e) {
				System.out.println("Experiment "+idconf +" not valid anymore");
			}
		}
		return result;
	}
	
	private boolean containURL(ExperimentManager em, String idconfig) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JUser authuser = Security.getMasterUser();
		JExperiment exp = em.getExperimentFromConf(idconfig, authuser);
		for (JTreatment treatment:exp.getTreatment()) {
			if (treatment.getUrl().isEmpty())
				return false;
		}
		return true;
	}
	
	@Path("/delete/{idname}")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void delete(@PathParam("idname") String idname, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			if (!authuser.isAdmin())
				throw new AuthenticationException();
			
			JExperiment filter = new JExperiment();
			filter.setExperimenter(idname);
			for (JExperiment exp: em.getExperiments(filter, authuser))
				for (JConfiguration conf: exp.getConfig()) {
					em.stop(conf.get_id(), authuser);
					em.deleteConfig(conf.get_id(),authuser);
					em.deleteEvents(conf.get_id(), authuser); //always remove the events of an experiment that is going to be deleted
				}
			em.deleteUser(idname, authuser);
		} catch (BadRequestException | ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		}
	}
	
//	private Collection<RunningExpInfo> getLeft(ExperimentManager em, Collection<RunningExpInfo> candidates, JUser user, UserRol restrictToRol) throws IOException, ParseException{
//		Collection<RunningExpInfo> result = new HashSet<RunningExpInfo>();
//		List<String> list = Arrays.asList(user.getParticipatedexps());
//		
//		for (RunningExpInfo exp: candidates) {
//			if (!list.contains(exp.getIdconfig())) {
//				if (restrictToRol == null)
//					result.add(exp);
//				else {
//					JUser expuser = em.getUserByIdname(exp.getExperimenter(), Security.getMasterUser());
//					if (expuser.getRolEnum() == restrictToRol)
//						result.add(exp);
//				}
//			}
//		}
//		return result;
//	}
	

}
