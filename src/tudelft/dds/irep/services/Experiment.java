package tudelft.dds.irep.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.JsonValidator;

@Path("/experiment")
public class Experiment {
	
	@Context ServletContext context;

	@Path("/new/experiment")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String uploadExperiment(String experiment) {
		
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(experiment);
			JExperiment exp = mapper.convertValue(jnode, JExperiment.class);
			ProcessingReport pr = jval.validate(exp,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());

			JTreatment[] treatments = exp.getTreatment();
			for (JTreatment t:treatments) {  //just to check if the dsl are valids
				em.treatment_to_json(t);
			}
			
			JConfiguration[] config = exp.getConfig();
			for (JConfiguration c:config) {
				c.setRun("OFF");
			}
			
			return em.addExperiment(exp);
		} catch (IOException | IllegalArgumentException | ProcessingException | ValidationException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	@Path("/new/configuration")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String uploadConfiguration(String inputJson) {
		
		try {
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
			return em.addConfig(idexp, conf);
		} catch (IOException | IllegalArgumentException | ProcessingException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
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
	public Response start(String idconf) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JConfiguration conf = em.getConfiguration(idconf);
			JExperiment exp = em.getExperimentFromConf(idconf);
			boolean started = em.start(exp,conf);
			if (!started)
				return Response.status(Response.Status.NOT_ACCEPTABLE).build();
			return Response.ok().build();
		} catch (IOException | ParseException | ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}

	
	@Path("/stop")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void stop(String idconfig) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.stop(idconfig);
		} catch (IOException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void delete(String idconfig) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.stop(idconfig);
			em.deleteConfig(idconfig);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	@Path("/get/{idrun}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idrun") String idrun) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JExperiment jexp = em.getExperimentFromConf(idrun);
			ObjectMapper mapper = new ObjectMapper();
			String expstr = mapper.writeValueAsString(jexp); 
			return expstr;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/getParams")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String getParamsMultipart(@FormDataParam("idconfig") String idconfig, @FormDataParam("idunit") String idunit, 
			@FormDataParam("timestamp") String timestamp, @FormDataParam("overrides") String overrides) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
	
			ObjectMapper mapper = new ObjectMapper();
			JExperiment exp = em.getExperimentFromConf(idconfig);
			String unitExp = exp.getUnit();
			JsonNode jnode = mapper.readTree(overrides);
			Map<String,?> overridesMap = mapper.convertValue(jnode, Map.class);
			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit, overridesMap);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			
			String paramsstr = mapper.writeValueAsString(params);
			
			//TODO: is it possible to run asynchronously the following?
			String treatment = em.getTreatment(unitExp, idconfig, idunit);
//			JExposureBody expbody = em.createExposureBody(treatment, params);
//			ProcessingReport pr = jval.validate(expbody,mapper.readTree(mapper.writeValueAsString(expbody)), context);
//			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
//			JEvent event = em.createExposureEvent(idconfig, idunit, timestamp, expbody);
			InputStream is = new ByteArrayInputStream("".getBytes());
			JEvent event = em.createEvent(idconfig, idunit, JEvent.EXPOSURE_ENAME, false, is, timestamp, treatment, jparams);
			ProcessingReport pr = jval.validate(event,mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event);
			em.monitorEvent(event);
			
			return paramsstr;
		} catch (IOException | ParseException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/getParams")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getParamsJson(String inputJson) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
	
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode inputNode = mapper.readTree(inputJson);
			String idconfig = inputNode.get("idconfig").asText();
			String idunit = inputNode.get("idunit").asText();
			String timestamp = inputNode.get("timestamp").asText();
			
			JExperiment exp = em.getExperimentFromConf(idconfig);
			String unitExp = exp.getUnit();
			
			Map<String,?> overridesMap = mapper.convertValue(inputNode.get("overrides"), Map.class);
			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit, overridesMap);
			
			JParamValues jparams = mapper.convertValue(params, JParamValues.class);
			
			String paramsstr = mapper.writeValueAsString(params);
			
			//TODO: is it possible to run asynchronously the following?
			String treatment = em.getTreatment(unitExp, idconfig, idunit);
//			JExposureBody expbody = em.createExposureBody(treatment, params);
//			ProcessingReport pr = jval.validate(expbody,mapper.readTree(mapper.writeValueAsString(expbody)), context);
//			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
//			JEvent event = em.createExposureEvent(idconfig, idunit, timestamp, expbody);
			InputStream is = new ByteArrayInputStream("".getBytes());
			JEvent event = em.createEvent(idconfig, idunit, JEvent.EXPOSURE_ENAME, false, is, timestamp, treatment, jparams);
			ProcessingReport pr = jval.validate(event,mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event);
			em.monitorEvent(event);
			
			return paramsstr;
		} catch (IOException | ParseException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
	//public String search(@QueryParam("idexp") String idexp, @QueryParam("name") String name, 
//	@QueryParam("experimenter") String experimenter, @QueryParam("description") String desc, 
//	@QueryParam("tname") String tname,@QueryParam("tdef") String tdef, @QueryParam("cname") String cname,
//	@QueryParam("controler") String controller,@QueryParam("dstarted") String dstarted, 
//	@QueryParam("dended") String dended, @QueryParam("dtoend") String dtoend, @QueryParam("maxexp") String maxexp) {

	
	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String search(String filter) {
	
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(filter);
			JExperiment expfilter = mapper.convertValue(jnode, JExperiment.class);
			ProcessingReport pr = jval.validate(expfilter,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			List<JExperiment> experiments = em.getExperiments(expfilter);
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
			return mapper.writeValueAsString(arrayNode);
			
		} catch (IOException | ParseException | ProcessingException e) {
			System.out.println(filter);
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
		
	}
	
	@Path("/monitor/treatments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorTreatments() {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			Collection<RunningExpInfo> running = em.getRunningExp();
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			for (RunningExpInfo exp: running) {
				ObjectNode node = mapper.createObjectNode();
				node.put("idrun", exp.getIdconfig());
				node.put("laststarted", exp.getLastStarted().getTime());
		        ArrayNode treatments = mapper.createArrayNode();
		        for (String treatname: exp.getMonConsumer().getExposurecount().keySet()){
		        	ObjectNode treatment = mapper.createObjectNode();
		        	treatment.put("name", treatname);
		        	treatment.put("value", exp.getMonConsumer().getExposurecount().get(treatname));
		        	treatments.add(treatment);
		        }
		        node.set("treatments", treatments);
	        		
		        arrayNode.add(node);
			}
			return mapper.writeValueAsString(arrayNode);
		} catch (BadRequestException | JsonProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}

	@Path("/monitor/subtreatments/{idrun}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String monitorSubtreatments(@PathParam("idrun") String idrun) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			RunningExpInfo exp = em.getRunningExp(idrun);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode node = mapper.createObjectNode();
			node.put("idrun", exp.getIdconfig());
			node.put("laststarted", exp.getLastStarted().getTime());
	        ArrayNode treatments = mapper.createArrayNode();
	        for (String treatname: exp.getMonConsumer().getSubtreatmentcount().keySet()){
	        	ObjectNode treatment = mapper.createObjectNode();
	        	treatment.put("name", treatname);
	        	treatment.put("value", exp.getMonConsumer().getExposurecount().get(treatname));
	        	ArrayNode subtreatments = mapper.createArrayNode();
	        	for (Map.Entry<Map<String,?>, Integer> entry:exp.getMonConsumer().getSubtreatmentcount().get(treatname).entrySet()) {
	        		ObjectNode subtreatment = mapper.createObjectNode();
	        		subtreatment.put("params", mapper.writeValueAsString(entry.getKey()));
	        		subtreatment.put("value", entry.getValue());
	        		subtreatments.add(subtreatment);
	        	}
	        	treatment.set("subtreatments", subtreatments);
	        	treatments.add(treatment);
	        }
	        
	        node.set("treatments", treatments);
			return mapper.writeValueAsString(node);
		} catch (BadRequestException | JsonProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	

}
