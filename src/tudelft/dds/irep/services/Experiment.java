package tudelft.dds.irep.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
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
import com.fasterxml.uuid.Generators;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.User;
import tudelft.dds.irep.utils.Utils;

@Path("/experiment")
public class Experiment {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	@Context ServletContext context;

	@Path("/new/experiment")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String uploadExperiment(String experiment, @Context HttpServletRequest request) {
		
		try {
			User authuser = Security.getAuthenticatedUser(request);
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
			User authuser = Security.getAuthenticatedUser(request);
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
			User authuser = Security.getAuthenticatedUser(request);
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
			User authuser = Security.getAuthenticatedUser(request);
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
			User authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.stop(idconfig, authuser);
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
	
	@Path("/get/{idrun}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			User authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
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
	
//	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
//	@Path("/getParams")
//	@POST
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getParamsMultipart(@FormDataParam("idconfig") String idconfig, @FormDataParam("idunit") String idunit, 
//			 @FormDataParam("overrides") String overrides) {
//		try {
//			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
//			ObjectMapper mapper = new ObjectMapper();
//			JExperiment exp = em.getExperimentFromConf(idconfig);
//			String unitExp = exp.getUnit();
//			JsonNode jnode = mapper.readTree(overrides);
//			Map<String,?> overridesMap = mapper.convertValue(jnode, Map.class);
//			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit, overridesMap);
//			
//			//JParamValues jparams = mapper.convertValue(params, JParamValues.class);
//			
//			//TODO: is it possible to run asynchronously the following?
////			String treatment = em.getTreatment(unitExp, idconfig, idunit);
////			InputStream is = new ByteArrayInputStream("".getBytes());
////			JEvent event = em.createEvent(idconfig, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams);
////			ProcessingReport pr = jval.validate(event,mapper.readTree(mapper.writeValueAsString(event)), context);
////			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
////			em.registerEvent(idconfig, event);
////			em.monitorEvent(event);
//			
//			return mapper.writeValueAsString(params);
//		} catch (IOException | ParseException e) {
//			e.printStackTrace();
//			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
//		}
//	}
//	
//	
//	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
//	@Path("/getParams")
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getParamsJson(String inputJson) {
//		try {
//			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
//			ObjectMapper mapper = new ObjectMapper();
//			
//			JsonNode inputNode = mapper.readTree(inputJson);
//			String idconfig = inputNode.get("idconfig").asText();
//			String idunit = inputNode.get("idunit").asText();
//			
//			JExperiment exp = em.getExperimentFromConf(idconfig);
//			String unitExp = exp.getUnit();
//			
//			Map<String,?> overridesMap = mapper.convertValue(inputNode.get("overrides"), Map.class);
//			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit, overridesMap);
//			
//			//TODO: is it possible to run asynchronously the following?
////			String treatment = em.getTreatment(unitExp, idconfig, idunit);
////			InputStream is = new ByteArrayInputStream("".getBytes());
////			JEvent event = em.createEvent(idconfig, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams);
////			ProcessingReport pr = jval.validate(event,mapper.readTree(mapper.writeValueAsString(event)), context);
////			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
//			//em.registerEvent(idconfig, event);
//			//em.monitorEvent(event);
//			
//			return mapper.writeValueAsString(params);
//		} catch (IOException | ParseException  e) {
//			e.printStackTrace();
//			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
//		}
//	}
//	
	//public String search(@QueryParam("idexp") String idexp, @QueryParam("name") String name, 
//	@QueryParam("experimenter") String experimenter, @QueryParam("description") String desc, 
//	@QueryParam("tname") String tname,@QueryParam("tdef") String tdef, @QueryParam("cname") String cname,
//	@QueryParam("controler") String controller,@QueryParam("dstarted") String dstarted, 
//	@QueryParam("dended") String dended, @QueryParam("dtoend") String dtoend, @QueryParam("maxexp") String maxexp) {

	
	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(String filter, @Context HttpServletRequest request) {
	
		try {
			User authuser = Security.getAuthenticatedUser(request);
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
			        node.put("experimenter", exp.getExperimenter());
			        node.put("description", exp.getDescription());
			        node.put("run", conf.getRun());
			        node.put("cname", conf.getName());
			        arrayNode.add(node);
				}
			}
			return Response.ok(mapper.writeValueAsString(arrayNode), MediaType.TEXT_PLAIN).build();
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
	
	@Path("/monitor/treatments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorTreatments(@Context HttpServletRequest request) {
		try {
			User authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<RunningExpInfo> running = em.getRunningExp(authuser);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			for (RunningExpInfo exp: running) {
				ObjectNode node = mapper.createObjectNode();
				node.put("idrun", exp.getIdconfig());
				node.put("laststarted", exp.getLastStarted().getTime());
		        ArrayNode treatments = mapper.createArrayNode();
		        for (String treatname: exp.getMonConsumer().getTreatmentCount().keySet()){
		        	ObjectNode treatment = mapper.createObjectNode();
		        	treatment.put("name", treatname);
		        	treatment.put("value", exp.getMonConsumer().getTreatmentCount().get(treatname).size());
		        	treatments.add(treatment);
		        }
		        node.set("treatments", treatments);
	        		
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

	@Path("/monitor/subtreatments/{idrun}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorSubtreatments(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			User authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			RunningExpInfo exp = em.getRunningExp(idrun, authuser);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.createObjectNode();
			node.put("idrun", exp.getIdconfig());
			node.put("laststarted", exp.getLastStarted().getTime());
	        ArrayNode treatments = mapper.createArrayNode();
	        for (String treatname: exp.getMonConsumer().getSubtreatmentCount().keySet()){
	        	ObjectNode treatment = mapper.createObjectNode();
	        	treatment.put("name", treatname);
	        	treatment.put("value", exp.getMonConsumer().getTreatmentCount().get(treatname).size());
	        	ArrayNode subtreatments = mapper.createArrayNode();
	        	for (Map.Entry<Map<String,?>, Set<String>> entry:exp.getMonConsumer().getSubtreatmentCount().get(treatname).entrySet()) {
	        		ObjectNode subtreatment = mapper.createObjectNode();
	        		subtreatment.put("params", mapper.writeValueAsString(entry.getKey()));
	        		subtreatment.put("value", entry.getValue().size());
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
		try {
			User authuser = Security.getClientUser();
			authuser.setAsAdmin();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			
			String idunit = Utils.getRequestIdentifier(idrun,request);
			
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);

			JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
			em.registerEvent(idrun, event, authuser);
			em.monitorEvent(event);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			String target = jtreat.getUrl();
			if (target != null) {
				URI uri = new URI(Utils.getVariantURL(target,params, idunit, jtreat.getName()));
					
				NewCookie newcookieTarget = new NewCookie(idrun,idunit,uri.getPath().toString(),uri.getHost(),"",Integer.MAX_VALUE,false); //Only valid if same domain
				//NewCookie newcookieOrigin1 = new NewCookie("user2",idunit,uriOrigin.getBaseUri().toString(), uriOrigin.getBaseUri().toString(),"",Integer.MAX_VALUE,false);
				NewCookie newcookieOrigin = new NewCookie(idrun,idunit); 
						
				Response response = Response.seeOther(uri).cookie(newcookieOrigin, newcookieTarget).build(); //302, temporaryRedirect(uri) for 301
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
	public Response exposureRedirectUnit(@PathParam("idrun") String idrun, @PathParam("idunit") String idunit, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			User authuser = Security.getClientUser();
			authuser.setAsAdmin();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, authuser);
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);

			JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
			em.registerEvent(idrun, event, authuser);
			em.monitorEvent(event);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			String target = jtreat.getUrl();
			if (target != null) {
				URI uri = new URI(Utils.getVariantURL(target,params, idunit, jtreat.getName()));
				Response response = Response.seeOther(uri).cookie(new NewCookie("user",idunit)).build();
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParamsUnit(@PathParam("idrun") String idrun, @PathParam("idunit") String idunit, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		String result= "{}";
		try {
			User authuser = Security.getClientUser();
			authuser.setAsAdmin();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, null);
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			jparams.set("_idunit", idunit);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			jparams.set("_variant", jtreat.getName());
			URI origin=null;
			if (jtreat.getUrl() != null) {
				origin = new URI(jtreat.getUrl());
				jparams.set("_url", jtreat.getUrl());
			}
			result = mapper.writeValueAsString(jparams);
			ResponseBuilder response = Response.ok(result,MediaType.APPLICATION_JSON);
			response.header("Access-Control-Allow-Origin", "*");
	    	response.header("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
	    	response.header("Access-Control-Allow-Credentials", "true");
	    	response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");
//			if (origin != null) {
//				//response.header("Access-Control-Allow-Origin", "https?://"+origin.getHost());
//		    	//response.allow("OPTIONS");
//			}
			if (response.build().getStatus() < 400) {
				JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
				em.registerEvent(idrun, event, authuser);
				em.monitorEvent(event);
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
	
//reads cookies so it need to be called with-credits from the browser in case of cross-domain calls
	@Path("/getparams/{idrun}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParams(@PathParam("idrun") String idrun, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		String result= "{}";
		try {
			User authuser = Security.getClientUser();
			authuser.setAsAdmin();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun, null);
			
			String idunit = Utils.getRequestIdentifier(idrun,request);

			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());
			ObjectMapper mapper = new ObjectMapper();
			InputStream is = new ByteArrayInputStream("".getBytes());
			Map<String, ?> params = em.getParams(jexp.getUnit(), idrun, idunit, new HashMap<String,Object>(), authuser);
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			jparams.set("_idunit", idunit);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			jparams.set("_variant", jtreat.getName());
			URI origin=null;
			if (jtreat.getUrl() != null) {
				origin = new URI(jtreat.getUrl());
				jparams.set("_url", jtreat.getUrl());
			}
			result = mapper.writeValueAsString(jparams);
			ResponseBuilder response = Response.ok(result,MediaType.APPLICATION_JSON);

			NewCookie newcookie = new NewCookie(idrun,idunit); 
			response.cookie(newcookie);
			response.header("Access-Control-Allow-Origin", "*");
	    	response.header("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
	    	response.header("Access-Control-Allow-Credentials", "true");
	    	response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

//			if (origin != null) {
//				//response.header("Access-Control-Allow-Origin", "https?://"+origin.getHost());
//		    	//response.allow("OPTIONS");
//			}
			if (response.build().getStatus() < 400) {
				JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
				em.registerEvent(idrun, event, authuser);
				em.monitorEvent(event);
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
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/getparams")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exposureGetParamsJson(String inputJson, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			User authuser = Security.getClientUser();
			authuser.setAsAdmin();
			String result= "{}";
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode inputNode = mapper.readTree(inputJson);
			String idrun = inputNode.get("idconfig").asText();
			JExperiment jexp = em.getExperimentFromConf(idrun, null);
			String unitExp = jexp.getUnit();
			String idunit = null;
			if (inputNode.get("idunit") != null)
				idunit = inputNode.get("idunit").asText();
			
			if (idunit == null)
				idunit = Utils.getRequestIdentifier(idrun,request);
			
			Map<String,?> overridesMap = mapper.convertValue(inputNode.get("overrides"), Map.class);
			Map<String, ?> params = em.getParams(unitExp, idrun, idunit, overridesMap, authuser);
			
			String treatment = em.getTreatment(jexp.getUnit(), idrun, idunit, authuser);
			String timestamp = Utils.getTimestamp(new Date());

			InputStream is = new ByteArrayInputStream("".getBytes());
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			jparams.set("_idunit", idunit);
			JTreatment jtreat = em.getTreatment(jexp, treatment);
			jparams.set("_variant", jtreat.getName());
			URI origin=null;
			if (jtreat.getUrl() != null) {
				origin = new URI(jtreat.getUrl());
				jparams.set("_url", jtreat.getUrl());
			}
			result = mapper.writeValueAsString(jparams);
			ResponseBuilder response = Response.ok(result,MediaType.APPLICATION_JSON);
			

			NewCookie newcookie = new NewCookie(idrun,idunit); 
			response.cookie(newcookie);
			response.header("Access-Control-Allow-Origin", "*");
	    	response.header("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
	    	response.header("Access-Control-Allow-Credentials", "true");
	    	response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

//			if (origin != null) {
//				//response.header("Access-Control-Allow-Origin", "https?://"+origin.getHost());
//		    	//response.allow("OPTIONS");
//			}
			if (response.build().getStatus() < 400) {
				JEvent event = em.createEvent(idrun, idunit, JEvent.EXPOSURE_ENAME, EventType.STRING, is, timestamp, treatment, jparams, useragent, jexp.getExperimenter());
				em.registerEvent(idrun, event, authuser);
				em.monitorEvent(event);
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
