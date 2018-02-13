package tudelft.dds.irep.services;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JEventCSV;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.Utils;

@Path("/event")
public class Event {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	@Context ServletContext context;
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/register")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response registerMultipart(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") String timestamp, 
			@FormDataParam("idunit") String idunit, @FormDataParam("etype") String etype, 
			@FormDataParam("ename") String ename, @FormDataParam("evalue") InputStream evalue, 
		    @FormDataParam("paramvalues") String paramvalues, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			
			if (timestamp == null)
				timestamp = Utils.getTimestamp(new Date());
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(paramvalues);
			//JParamValues params = mapper.convertValue(jnode, JParamValues.class);
			JExperiment jexp = em.getExperimentFromConf(idconfig,authuser);
			String unitExp = jexp.getUnit();
			String treatment = em.getTreatment(unitExp, idconfig, idunit, authuser); //could be obtained from nsconfig if the experiment is running
			JTreatment jtreat = em.getTreatment(jexp,treatment);
			
			JParamValues params;
			if (paramvalues == null || paramvalues.isEmpty()) {
				params =  mapper.convertValue(em.getParams(jexp.getUnit(), idconfig, idunit, new HashMap<String,Object>(), authuser), JParamValues.class);
				
			} else {
				params = mapper.convertValue(paramvalues, JParamValues.class);
			}
			
			JEvent event = em.createEvent(idconfig, idunit, ename, EventType.valueOf(etype), evalue, timestamp,treatment, params, useragent, jexp.getExperimenter());
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event, authuser);
			ResponseBuilder response = Response.ok();
			response.header("Access-Control-Allow-Origin", "*");
		    response.header("Access-Control-Allow-Headers","origin, content-type, accept");
		    //response.header("Access-Control-Allow-Credentials", "true");
		    response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

//			if (jtreat.getUrl() != null) {
//				//response.header("Access-Control-Allow-Origin", jtreat.getUrl());
//			    //response.allow("OPTIONS");
//			}
		    return response.build();
		} catch (BadRequestException | ParseException | ProcessingException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
	}
	

	@Path("/register")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response registerTxt(String inputJson, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		return registerJson(inputJson, useragent, request);
	}

	
	
	
	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerJson(String inputJson, @HeaderParam("user-agent") String useragent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getClientUser();
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode inputNode = mapper.readTree(inputJson);
			String idconfig = inputNode.get("idconfig").asText();
			String idunit = inputNode.get("idunit").asText();
			
			String etype = inputNode.get("etype").asText();
			String ename = inputNode.get("ename").asText();
			JsonNode evalueNode = inputNode.get("evalue");
			String evalue = evalueNode.toString();
			
			String timestamp;
			if (inputNode.get("timestamp") != null)
				timestamp = inputNode.get("timestamp").asText();
			else 
				timestamp = Utils.getTimestamp(new Date());
			
			JExperiment jexp = em.getExperimentFromConf(idconfig, authuser);
			String unitExp = jexp.getUnit();
			String treatment = em.getTreatment(unitExp, idconfig, idunit, authuser); //could be obtained from nsconfig if the experiment is running
			JTreatment jtreat = em.getTreatment(jexp,treatment);
			
			JParamValues params;
			JsonNode paramnode = inputNode.get("paramvalues");
			if (paramnode == null) {
				params =  mapper.convertValue(em.getParams(jexp.getUnit(), idconfig, idunit, new HashMap<String,Object>(), authuser), JParamValues.class);
				
			} else {
				params = mapper.convertValue(paramnode, JParamValues.class);
			}

			
			InputStream stream = new ByteArrayInputStream(evalue.getBytes(StandardCharsets.UTF_8.name()));
			JEvent event = em.createEvent(idconfig, idunit, ename, EventType.valueOf(etype), stream, timestamp,treatment, params, useragent, jexp.getExperimenter());
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event, authuser);
			ResponseBuilder response = Response.ok();
			response.header("Access-Control-Allow-Origin", "*");
		    response.header("Access-Control-Allow-Headers","origin, content-type, accept");
		    //response.header("Access-Control-Allow-Credentials", "true");
		    response.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

//			if (jtreat.getUrl() != null) {
//				//response.header("Access-Control-Allow-Origin", jtreat.getUrl());
//			    //response.allow("OPTIONS");
//			}
			return response.build();
		} catch (BadRequestException | ParseException | ProcessingException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		}
	}

	
	@Path("/get/{idevent}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idevent") String idevent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JEvent jevent = em.getEvent(idevent, authuser);
			ObjectMapper mapper = new ObjectMapper();
			String eventstr = mapper.writeValueAsString(jevent); 
			return eventstr;
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


	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void delete(String idevent, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.deleteEvent(idevent,authuser);
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
	
	
		
	@Path("/timestampFormat")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTimestampFormat() {
		return JsonDateSerializer.timestampFormat;
	}
	


	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String search(String filter, @Context HttpServletRequest request) {
		//final Integer SNIPPET = 100;
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(filter);
			JEvent eventfilter = mapper.convertValue(jnode, JEvent.class);
			ProcessingReport pr = jval.validate(eventfilter,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			List<JEvent> events = em.getEvents(eventfilter,authuser);
			ArrayNode arrayNode = mapper.createArrayNode();
			for (JEvent ev: events) {
				ObjectNode node = mapper.createObjectNode();
				node.put("_id", ev.get_id());
				node.put("ename", ev.getEname());
			    node.put("idunit", ev.getIdunit());
			    node.put("timestamp", Utils.getTimestamp(ev.getTimestamp()));
			    node.put("etype", ev.getEtype());
			    node.put("evalue", ev.getEvalue());
			    node.put("experimenter", ev.getExperimenter());
			    try {
			    	JExperiment exp = em.getExperimentFromConf(ev.getIdconfig(), authuser);
			    	node.put("experiment", exp.getName());
			    } catch (NullPointerException e) {
			    	node.put("experiment", "[deleted]");
			    }
		        arrayNode.add(node);
			}
			return mapper.writeValueAsString(arrayNode);
			
		} catch (BadRequestException | ParseException | ProcessingException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
		
	}

	
	@POST
	@Path("/getCSV")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getCSV(String idevents, @Context HttpServletRequest request) {
		try { 
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(idevents);
			List<JEvent> events = new ArrayList<JEvent>(); 
			for (JsonNode item : jnode) {
				String id = item.asText();
				events.add(em.getEvent(id,authuser));
			}
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException {
					CsvMapper csvmapper = new CsvMapper();
					CsvSchema schema = csvmapper.schemaFor(JEventCSV.class);
					schema = schema.withColumnSeparator('\t');
					ObjectWriter myObjectWriter = csvmapper.writer(schema);
	
					Writer writer = new BufferedWriter(new OutputStreamWriter(out));
					SequenceWriter sw = myObjectWriter.writeValues(writer);
					for (JEvent event : events) {
						JEventCSV ecsv = new JEventCSV(event);
						//myObjectWriter.write.writeValue(writer, ecsv);
						sw.write(ecsv);
					}
					writer.flush();
					sw.close();
					
				}
			};
	
			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=\"" + "events.csv" + "\"") // optional
					.build();
		
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

	
	@POST
	@Path("/getJSON")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getJSON(String idevents, @Context HttpServletRequest request) throws JsonProcessingException, IOException, ParseException {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(idevents);
			List<JEvent> events = new ArrayList<JEvent>(); 
			for (JsonNode item : jnode) {
				String id = item.asText();
				events.add(em.getEvent(id,authuser));
			}
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException {
					Writer writer = new BufferedWriter(new OutputStreamWriter(out));
					ObjectMapper mapper = new ObjectMapper();
					for (JEvent event : events) {
						if (event.getETypeEnum() == EventType.JSON) {
							try {
								JsonNode eventNode = mapper.convertValue(event, JsonNode.class);
								JsonNode evalueNode = mapper.readTree(event.getEvalue());
								((ObjectNode)eventNode).replace("evalue", evalueNode);
								writer.write(mapper.writeValueAsString(eventNode));
							} catch (IOException e) {
								writer.write(mapper.writeValueAsString(event));
							}
						} else {
							writer.write(mapper.writeValueAsString(event));
						}
					}
					writer.flush();
					writer.close();
					
				}
			};
	
			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition", "attachment; filename=\"" + "events.json" + "\"") // optional
					.build();
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

}
