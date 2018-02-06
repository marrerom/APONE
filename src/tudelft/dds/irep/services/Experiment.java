package tudelft.dds.irep.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.validator.routines.UrlValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.Utils;

@Path("/experiment")
public class Experiment {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	@Context ServletContext context;
	
	@Path("/demos")
	@GET
	public Response createDemos(@Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			JUser anonymous = Security.getAnonymousUser();
			Security.checkAuthorized(authuser, anonymous.getIdname(), Security.Useraction.WRITE); //if requested by admin, or by anonymous user itself (anonymous as idname)
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Security.setAuthenticatedUser(request, em, anonymous.getIdTwitter(), anonymous.getIdname());
			String color = CharStreams.toString(new InputStreamReader(context.getResourceAsStream("/WEB-INF/demoColor.json")));
		    uploadExperiment(color, request);
		    String ranking = CharStreams.toString(new InputStreamReader(context.getResourceAsStream("/WEB-INF/demoRanking.json")));
		    uploadExperiment(ranking, request);
		    String factorial = CharStreams.toString(new InputStreamReader(context.getResourceAsStream("/WEB-INF/demoFactorial.json")));
		    uploadExperiment(factorial, request);
		    Security.setAuthenticatedUser(request, em, authuser.getIdTwitter(), authuser.getIdname());
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
		return Response.ok("Demos created!", MediaType.TEXT_PLAIN).build();
	}

	@Path("/new/experiment")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String uploadExperiment(String experiment, @Context HttpServletRequest request) {
		
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(experiment);
			JExperiment exp = mapper.convertValue(jnode, JExperiment.class);
			
			ProcessingReport pr = jval.validate(exp,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			if (exp.getUnit() == null || exp.getUnit().isEmpty())
				exp.setUnit("defaultunit");
			
			if (request.getUserPrincipal() != null)
				exp.setExperimenter(request.getUserPrincipal().getName());

			UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
			JTreatment[] treatments = exp.getTreatment();
			for (JTreatment t:treatments) {  
				if (t.getDefinition() == null || t.getDefinition().isEmpty())
					t.setDefinition(" "); //this is compiled into a valid empty planout json, otherwise I'd have to ask every time I launch the experiment or get parameters from it
				else
					em.treatment_to_json(t); //just to check if the dsl are valids
				if (t.getUrl() != null && !t.getUrl().isEmpty())
					Preconditions.checkArgument(urlValidator.isValid(t.getUrl()), "Invalid URL");
			}
			
			JConfiguration[] config = exp.getConfig();
			for (JConfiguration c:config) {
				c.setRun("OFF");
			}
			
			return em.addExperiment(exp, authuser);
		} catch (JsonProcessingException | ProcessingException | ValidationException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			//throw new javax.ws.rs.WebApplicationException(e.getMessage(),e.getCause(), Status.BAD_REQUEST.getStatusCode());
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	@Path("/new/configuration")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String uploadConfiguration(String inputJson, @Context HttpServletRequest request) {
		
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode inputNode = mapper.readTree(inputJson);
			String idexp = inputNode.get("idexp").asText();
			JsonNode jnode = inputNode.get("configuration");
			JConfiguration conf = mapper.convertValue(jnode, JConfiguration.class);
			ProcessingReport pr = jval.validate(conf,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			conf.setRun("OFF");
			return em.addConfig(idexp, conf, authuser);
		} catch (IllegalArgumentException | ProcessingException | ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
//	@Path("/start")
//	@POST
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.TEXT_PLAIN)
//	public String startExperiment(@FormDataParam("idexp") String idexp, @FormDataParam("configuration") InputStream configuration){
//		try {
//			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
//			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
//			
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode jnode = mapper.readTree(configuration);
//			JConfiguration conf = mapper.convertValue(jnode, JConfiguration.class);
//			ProcessingReport pr = jval.validate(conf,jnode, context);
//			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
//			
//			JExperiment exp = em.getExperiment(idexp);
//			String idrun = em.addConfig(idexp,conf);
//			conf.set_id(idrun);
//			em.start(exp,conf);
//			return idrun;
//		} catch (IOException | ProcessingException | ParseException | ValidationException e) {
//			e.printStackTrace();
//			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage()));
//		}
//	}
	

	
	@Path("/start")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response start(String idconf, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JConfiguration conf = em.getConfiguration(idconf, authuser);
			JExperiment exp = em.getExperimentFromConf(idconf, authuser);
			boolean started = em.start(exp,conf, authuser);
			if (!started)
				return Response.status(Response.Status.NOT_ACCEPTABLE).build();
			return Response.ok().build();
		} catch (JsonProcessingException | ValidationException | ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}

	
	@Path("/stop")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void stop(String idconfig, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.stop(idconfig, authuser);
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void delete(String idconfig, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.stop(idconfig, authuser);
			em.deleteEvents(idconfig, authuser); //always remove the events of an experiment that is going to be deleted
			em.deleteConfig(idconfig, authuser);
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	@Path("/deleteEvents")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void deleteEvents(String idconfig, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.deleteEvents(idconfig, authuser);
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}	
	
	@Path("/get/{idrun}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			JConfiguration jconf = null;
			for (JConfiguration conf: jexp.getConfig()) {
				if (conf.get_id().equals(idrun)) {
					jconf = conf;
					break;
				}
			}
			JConfiguration[] newconf = {jconf}; 
			jexp.setConfig(newconf);
			ObjectMapper mapper = new ObjectMapper();
			String expstr = mapper.writeValueAsString(jexp); 
			return expstr;
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	

	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(String filter, @Context HttpServletRequest request) {
	
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(filter);
			JExperiment expfilter = mapper.convertValue(jnode, JExperiment.class);
			ProcessingReport pr = jval.validate(expfilter,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			List<JExperiment> experiments = em.getExperiments(expfilter,authuser);
			ArrayNode arrayNode = mapper.createArrayNode();
			for (JExperiment exp: experiments) {
				for (JConfiguration conf:exp.getConfig()) {
					ObjectNode node = mapper.createObjectNode();
					node.put("idrun", conf.get_id());
			        node.put("name", exp.getName());
			        node.put("experimenter", conf.getExperimenter());
			        node.put("description", exp.getDescription());
			        node.put("run", conf.getRun());
			        node.put("cname", conf.getName());
			        arrayNode.add(node);
				}
			}
			return Response.ok(mapper.writeValueAsString(arrayNode), MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException | ParseException | ProcessingException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	//TODO: change to accept any type of event
	@Path("/monitor/treatments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorTreatmentsComplete(@Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<RunningExpInfo> running = em.getRunningExp(authuser);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			for (RunningExpInfo exp: running) {
				ObjectNode node = mapper.createObjectNode();
				node.put("idrun", exp.getIdconfig());
				node.put("laststarted", exp.getLastStarted().getTime());
		        ArrayNode exposures = mapper.createArrayNode();
		        for (String treatname: exp.getConf().getActiveExperimentNames()){
		        	ObjectNode treatment = mapper.createObjectNode();
		        	treatment.put("name", treatname);
		        	treatment.put("value", exp.getMonConsumer().getEventMonitor(JEvent.EXPOSURE_ENAME).getTreatmentCount(treatname, true));
		        	exposures.add(treatment);
		        }
		        node.set("exposures", exposures);
		        ArrayNode completed = mapper.createArrayNode();
		        for (String treatname: exp.getConf().getActiveExperimentNames()){
		        	ObjectNode treatment = mapper.createObjectNode();
		        	treatment.put("name", treatname);
		        	treatment.put("value", exp.getMonConsumer().getEventMonitor(JEvent.COMPLETED_ENAME).getTreatmentCount(treatname, true));
		        	completed.add(treatment);
		        }
		        node.set("completed", completed);
		        arrayNode.add(node);
			}
			return mapper.writeValueAsString(arrayNode);
		} catch (JsonProcessingException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());		
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	
	//TODO: the treatments are taken from RunningExp.Conf, but, what happens with the subtreatments? if there are no events, we will have no subtreatments displayed 
	@Path("/monitor/subtreatments/{idrun}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorAllSubtreatments(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			RunningExpInfo exp = em.getRunningExp(idrun, authuser);
	
			Set<String> eventNames = exp.getMonConsumer().getEventNamesProcessed();
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode node = mapper.createArrayNode();
			
			for (String ename:eventNames) {
				String edata = monitorSubtreatments(ename, idrun, request);
				JsonNode edataNode = mapper.readTree(edata);
				node.add(edataNode);
			}
			return mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		}

	}

	@Path("/monitor/subtreatments/{idrun}/{ename}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorSubtreatments(@PathParam("ename") String ename, @PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			RunningExpInfo exp = em.getRunningExp(idrun, authuser);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.createObjectNode();
			node.put("idrun", exp.getIdconfig());
			node.put("laststarted", exp.getLastStarted().getTime());
			node.put("ename", ename);
	        ArrayNode treatments = mapper.createArrayNode();
	        
	        Boolean differentusers = false; //TODO: if the count is with distinct() or not, should be dependent on the specific event
	        if (ename.equals(JEvent.EXPOSURE_ENAME.toString()) || ename.equals(JEvent.COMPLETED_ENAME.toString()))
	        	differentusers = true;
	        
	        for (String treatname: exp.getConf().getActiveExperimentNames()){
	        	ObjectNode treatment = mapper.createObjectNode();
	        	treatment.put("name", treatname);
	        	treatment.put("value", exp.getMonConsumer().getEventMonitor(ename).getTreatmentCount(treatname, differentusers));
	        	ArrayNode subtreatments = mapper.createArrayNode();
	        	for (Map<String, ?> entry:exp.getMonConsumer().getEventMonitor(ename).getSubtreatments(treatname)) {
	        		ObjectNode subtreatment = mapper.createObjectNode();
	        		subtreatment.put("params", mapper.writeValueAsString(entry));
	        		subtreatment.put("value", exp.getMonConsumer().getEventMonitor(ename).getSubTreatmentCount(treatname, entry, differentusers));
	        		subtreatments.add(subtreatment);
	        	}
	        	treatment.set("subtreatments", subtreatments);
	        	treatments.add(treatment);
	        }
	        
	        node.set("treatments", treatments);
			return mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	//Generate idunit by using UUID version 1 with the library JUG V.3.1.3 (https://github.com/cowtowncoder/java-uuid-generator)
	@Path("/redirect/{idrun}")
	@GET
	public Response exposureRedirect(@PathParam("idrun") String idrun,@HeaderParam("user-agent") String useragent, @Context UriInfo uriOrigin, @Context HttpServletRequest request) {
		String idunit = null;
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			
//			if (request.getCookies()!=null) {
//				for (Cookie cookie: request.getCookies()) {
//					if (cookie.getName().equals(idrun))
//						idunit = cookie.getName();
//				}
//			}
			if (idunit == null)
				idunit = Utils.getRequestIdentifier(idrun,request);
			
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);

			//JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
			//em.registerEvent(idrun, event, authuser);
			
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			String target = jtreat.getUrl();
			if (target != null) {
				URI uri = new URI(Utils.getVariantURL(target,params, idunit, jtreat.getName()));
				Response response = Response.seeOther(uri).cookie(Utils.getCookie(uri, idrun, idunit)).build(); //302, temporaryRedirect(uri) for 301
				return response;
			}
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (URISyntaxException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}

	
	
	@Path("/redirect/{idrun}/{idunit}")
	@GET
	public Response 
RedirectUnit(@PathParam("idrun") String idrun, @PathParam("idunit") String idunit, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);

			//JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
			//em.registerEvent(idrun, event, authuser);
			
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			String target = jtreat.getUrl();
			if (target != null) {
				URI uri = new URI(Utils.getVariantURL(target,params, idunit, jtreat.getName()));
				Response response = Response.seeOther(uri).build();
				return response;
			}
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (URISyntaxException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

	    
		return Response.status(Status.BAD_REQUEST).build();
	}
//	
//	
//	@Path("/redirect")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response exposureRedirect(String inputJson) {
//		try {
//			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode inputNode;
//			inputNode = mapper.readTree(inputJson);
//			String idconfig = inputNode.get("idconfig").asText();
//			String idunit = inputNode.get("idunit").asText(); //TODO: if idunit is null,assign
//			String timestamp = Utils.getTimestamp(new Date());
//			JExperiment exp = em.getExperimentFromConf(idconfig);
//			String unitExp = exp.getUnit();
//			String treatment = em.getTreatment(unitExp, idconfig, idunit);
//			Map<String,?> overridesMap = mapper.convertValue(inputNode.get("overrides"), Map.class);
//			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit, overridesMap);
//			
//			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
//			InputStream is = new ByteArrayInputStream("".getBytes());
//			JEvent event = em.createEvent(idconfig, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams);
//			em.registerEvent(idconfig, event);
//			em.monitorEvent(event);
//			
//			for (JTreatment treat: exp.getTreatment()) {
//				if (treat.getName().equals(treatment)) {
//					String target = treat.getUrl();
//					if (target != null) {
//						URI uri = new URI(target+"?user="+idunit);
//						return Response.seeOther(uri).cookie(new NewCookie("user",idunit)).build();
//					}
//				}
//			}
//		} catch (IOException | URISyntaxException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return Response.accepted().build();
//	}
	
	@Path("/getparams/{idrun}/{idunit}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParamsUnit(@PathParam("idrun") String idrun, @PathParam("idunit") String idunit, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		String result= "{}";
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			ObjectNode node = mapper.createObjectNode();
			
			node.putPOJO("params", jparams);
			node.put("_idunit", idunit);
			
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			node.put("_variant", jtreat.getName());
			
			URI origin=null;
			if (jtreat.getUrl() != null) {
				origin = new URI(jtreat.getUrl());
				node.put("_url", jtreat.getUrl());
//				response.header("Access-Control-Allow-Origin", origin.getScheme()+"://"+origin.getHost()+":"+origin.getPort());
			}
			result = mapper.writeValueAsString(node);
			ResponseBuilder response = Response.ok(result,MediaType.APPLICATION_JSON);
			response.header("Access-Control-Allow-Origin", "*");
		    response.header("Access-Control-Allow-Headers","origin, content-type, accept");
		    response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");
//			if (response.build().getStatus() < 400) {
//				JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
//				em.registerEvent(idrun, event, authuser);
//			}
			return response.build();
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (URISyntaxException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
		
	}
	
	@Path("/getparams/{idrun}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParams(@PathParam("idrun") String idrun, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		String idunit = Utils.getRequestIdentifier(idrun,request);
		return exposureGetParamsUnit(idrun, idunit, useragent, request);
	}
	
	@Path("/getparams")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParamsJsonTxt(String inputJson, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		return exposureGetParamsJson(inputJson, useragent, request);
	}
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/getparams")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParamsJson(String inputJson, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getClientUser();
			String result= "{}";
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode inputNode = mapper.readTree(inputJson);
			String idrun = inputNode.get("idconfig").asText();
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			String unitExp = jexp.getUnit();
			String idunit = null;
			if (request.getCookies()!=null) {
				for (Cookie cookie: request.getCookies()) {
					if (cookie.getName().equals(idrun))
						idunit = cookie.getName();
				} 
			}
			if (idunit == null)
				idunit = Utils.getRequestIdentifier(idrun,request);
			
			Map<String,?> overridesMap = mapper.convertValue(inputNode.get("overrides"), Map.class);
			Map<String, ?> params = em.getParams(unitExp, idrun, idunit, overridesMap, authuser);
			
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());

			InputStream is = new ByteArrayInputStream("".getBytes());
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			ObjectNode node = mapper.createObjectNode();
			
			node.putPOJO("params", jparams);
			node.put("_idunit", idunit);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			node.put("_variant", jtreat.getName());
			ResponseBuilder response = Response.ok(result,MediaType.APPLICATION_JSON);
			URI origin=null;
			if (jtreat.getUrl() != null) {
				origin = new URI(jtreat.getUrl());
				node.put("_url", jtreat.getUrl());
				response.header("Access-Control-Allow-Origin", jtreat.getUrl());
				response = response.cookie(Utils.getCookie(origin, idrun, idunit));
				}
			result = mapper.writeValueAsString(node);

	    	response.header("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
	    	response.header("Access-Control-Allow-Credentials", "true");
	    	response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

			if (response.build().getStatus() < 400) {
				JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
				em.registerEvent(idrun, event, authuser);
				//em.monitorEvent(event);
			}
			return response.build();
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (URISyntaxException | IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
			
	}
	

	


}
