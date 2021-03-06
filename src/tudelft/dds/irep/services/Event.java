package tudelft.dds.irep.services;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NameBinding;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import com.google.common.io.ByteStreams;

import tudelft.dds.irep.data.schema.EventAggregation;
import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JEventCSV;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.Utils;



@Path("/event")
@MultipartConfig
public class Event extends ResourceConfig {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	@Context ServletContext context;

	//compressing everything: it doesn't work
//	public Event() {
//
//        packages("tudelft.dds.irep.services");
//		//register(EntityFilteringFeature.class);
//		EncodingFilter.enableFor(this, GZipEncoder.class);		
//
//	}
	
	
	//compressin only specific endopints: it does't work
//	@NameBinding
//	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
//	public @interface Compress {}
//	
//	@Provider
//	@Compress
//	public class GZIPWriterInterceptor implements WriterInterceptor {
//
//	    @Override
//	    public void aroundWriteTo(WriterInterceptorContext context)
//	                    throws IOException, WebApplicationException {
//
//	    	MultivaluedMap<String,Object> headers = context.getHeaders();
//	    	headers.add("Content-Encoding", "gzip");
//
//	        final OutputStream outputStream = context.getOutputStream();
//	        context.setOutputStream(new GZIPOutputStream(outputStream));
//	        context.proceed();
//	    }
//	}

	
//	@Path("/registerTest")
//	@POST
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response registerMultipartTest(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") String timestamp,   @FormDataParam("evalue") InputStream evalue, @Context HttpServletRequest request) {
//		//public Response registerMultipartTest(@FormDataParam("idconfig") String idconfig) {
//		return registerMultipart(null,null,null,null, null, null, null, null, null);
//	}
	
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
			
			if (idunit == null)
				idunit = Utils.getRequestIdentifier(idconfig,request);
			
			ObjectMapper mapper = new ObjectMapper();
			
			//JParamValues params = mapper.convertValue(jnode, JParamValues.class);
			JExperiment jexp = em.getExperimentFromConf(idconfig,authuser);
			String unitExp = jexp.getUnit();
			String treatment = em.getTreatment(unitExp, idconfig, idunit, authuser); //could be obtained from nsconfig if the experiment is running
			JTreatment jtreat = em.getTreatment(jexp,treatment);
			
			JParamValues params;
			if (paramvalues == null) {
				params =  mapper.convertValue(em.getParams(jexp.getUnit(), idconfig, idunit, new HashMap<String,Object>(), authuser), JParamValues.class);
			} else {
				JsonNode jnode = mapper.readTree(paramvalues);
				params = mapper.convertValue(jnode, JParamValues.class);
			}
			
			
			byte[] valuebin = ByteStreams.toByteArray(evalue);
			InputStream evalueEncoded = new ByteArrayInputStream(Utils.encodeBinary(valuebin).getBytes());

			JEvent event = em.createEvent(idconfig, idunit, ename, EventType.valueOf(etype), evalueEncoded, timestamp,treatment, params, useragent, jexp.getExperimenter());
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event, authuser);
			ResponseBuilder response = Response.ok()
					.cookie(new NewCookie(idconfig, idunit, "/", "", "", (int)30 * 24 * 60 * 60, false))
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers","origin, content-type, accept")
					.header("Access-Control-Allow-Credentials", "true")
		    		.header("Access-Control-Allow-Methods","GET, POST, OPTIONS");

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
		log.log(Level.INFO, "To register: "+inputJson);
   	
    	ObjectMapper mapper = new ObjectMapper();
		
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
			JsonNode idunitnode = inputNode.get("idunit");
			String idunit;
			if (idunitnode == null) {
				idunit = Utils.getRequestIdentifier(idconfig,request);
			} else {
				idunit = inputNode.get("idunit").asText();
			}
			String etype = inputNode.get("etype").asText();
			String ename = inputNode.get("ename").asText();
			
			String evalue;
			if (etype.equals(EventType.BINARY.toString()))
				evalue = inputNode.get("evalue").asText(); //no quotes
			else {
				JsonNode evalueNode = inputNode.get("evalue");
				evalue = evalueNode.toString();
				
				if (etype.equals(EventType.JSON.toString())) {
					JsonNode evalueTest = mapper.readTree(evalue);
					Map<String,Object> map = mapper.convertValue(evalueTest, Map.class);
				}
			}

			String timestamp;
			if (inputNode.get("timestamp") != null)
				timestamp = inputNode.get("timestamp").asText();
			else 
				timestamp = Utils.getTimestamp(new Date());
			log.log(Level.INFO, "Register: validated content");
			JExperiment jexp = em.getExperimentFromConf(idconfig, authuser);
			log.log(Level.INFO, "Register: get experiment id");
			String unitExp = jexp.getUnit();
			
			String treatment = em.getTreatment(unitExp, idconfig, idunit, authuser); //could be obtained from nsconfig if the experiment is running
			JTreatment jtreat = em.getTreatment(jexp,treatment);
			
			log.log(Level.INFO, "Register: get treatment");
			
			JParamValues params;//	@NameBinding
//			@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
//			public @interface Compress {}
		//	
//			@Provider
//			@Compress
//			public class GZIPWriterInterceptor implements WriterInterceptor {
		//
//			    @Override
//			    public void aroundWriteTo(WriterInterceptorContext context)
//			                    throws IOException, WebApplicationException {
		//
//			    	MultivaluedMap<String,Object> headers = context.getHeaders();
//			    	headers.add("Content-Encoding", "gzip");
		//
//			        final OutputStream outputStream = context.getOutputStream();
//			        context.setOutputStream(new GZIPOutputStream(outputStream));
//			        context.proceed();
//			    }
//			}
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
			
			log.log(Level.INFO, "Register: event validated");
			
			em.registerEvent(idconfig, event, authuser);
			
			log.log(Level.INFO, "Register: registered");
			ResponseBuilder response = Response.ok()
					.cookie(new NewCookie(idconfig, idunit, "/", "", "", (int)30 * 24 * 60 * 60, false))
					.header("Access-Control-Allow-Origin", "*")
		    		.header("Access-Control-Allow-Headers","origin, content-type, accept")
		    		.header("Access-Control-Allow-Methods","GET, POST, OPTIONS")
		    		.header("Access-Control-Allow-Credentials", "true");
		    
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
	
	@Path("/enames/{idrun}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getEnames(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			RunningExpInfo exp = em.getRunningExp(idrun, authuser);
	
			Set<String> eventNames = exp.getMonConsumer().getEventNamesProcessed();
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(eventNames);
		} catch (BadRequestException | JsonProcessingException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());		
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	@Path("/aggregationTypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAggregationTypes() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Set<String> result = new HashSet<String>();
			for (EventAggregation e:EventAggregation.values()) {
				result.add(e.name());
			}
			return mapper.writeValueAsString(result);
		} catch (BadRequestException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		}  
	}
	
	@Path("/aggregate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String aggregate(String input, @Context HttpServletRequest request) {
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode inputNode = mapper.readTree(input);
			String idrun = inputNode.get("idconfig").asText();
			String ename = inputNode.get("ename").asText();
			boolean completed = inputNode.get("completed").asBoolean();
			
			EventAggregation aggregation = EventAggregation.valueOf(inputNode.get("aggregation").asText());
			JEvent filter = new JEvent();
			filter.setEname(ename);
			filter.setIdconfig(idrun);
			ArrayNode events = searchEvents(filter, authuser);
			RunningExpInfo exp = em.getRunningExp(idrun, authuser);
			
			Map<String, List<Double>> unitvalue = new HashMap<String,List<Double>>();
			Map<String, List<String>> unitspertreat;
			if (completed) {
				unitspertreat = exp.getMonConsumer().getEventMonitor(JEvent.COMPLETED_ENAME).getExperimentalUnits();
			} else {
				unitspertreat = exp.getMonConsumer().getEventMonitor(JEvent.EXPOSURE_ENAME).getExperimentalUnits();
			}
			for (String treat: exp.getConf().getActiveExperimentNames()) {
				if (!unitspertreat.containsKey(treat)) {
					unitspertreat.put(treat, new ArrayList<String>());
				}
			}
			
			events.forEach(p -> {
				String id = p.get("idunit").asText();
				Double value;
				switch(aggregation) {
					case COUNT: value = 1.0; break;
					default: value = p.get("evalue").asDouble(); //if it can't be converted, result is 0.0 (no exception launched)
				}
				if (!unitvalue.containsKey(id)) {
					unitvalue.put(id, new ArrayList<Double>());
				}
				unitvalue.get(id).add(value);
				});
			
			ArrayNode arrayNode = mapper.createArrayNode();
			for (String treat: unitspertreat.keySet()) {
				ObjectNode node = mapper.createObjectNode();
				node.put("name", treat);
				ArrayNode values = mapper.createArrayNode();
				
				for (String id: unitspertreat.get(treat).stream().distinct().collect(Collectors.toSet())) {
					if (unitvalue.containsKey(id)) {
						 DoubleSummaryStatistics stats = unitvalue.get(id).stream().collect(DoubleSummaryStatistics::new,
	                             DoubleSummaryStatistics::accept,
	                             DoubleSummaryStatistics::combine);
						double value;
						switch (aggregation) {
							case MAX: value = stats.getMax(); break;
							case MIN: value = stats.getMin(); break;
							case AVG: value = stats.getAverage(); break;
							default: value = stats.getCount();
						}
						values.add(value);
					} else if (aggregation == EventAggregation.COUNT){ //eg. if the user hasn't clicked, and the operation is count, the result should be 0
						values.add(0.0);
					}
				}
				node.set("values", values);
				arrayNode.add(node);
			}
			return mapper.writeValueAsString(arrayNode);
			
		} catch (BadRequestException | ParseException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
		
	}
	
	@Path("/search")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String search(String filter, @Context HttpServletRequest request) {
		//final Integer SNIPPET = 100;
		try {
			JUser authuser = Security.getAuthenticatedUser(request);
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(filter);
			
			JEvent eventfilter = mapper.convertValue(jnode, JEvent.class);
			ProcessingReport pr = jval.validate(eventfilter,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			

			return mapper.writeValueAsString(searchEvents(eventfilter, authuser));
			
		} catch (BadRequestException | ProcessingException | IllegalArgumentException e) {
			log.log(Level.INFO, e.getMessage(), e);
			throw new BadRequestException(e.getMessage());
		} catch (AuthenticationException e) {
			throw new AuthenticationException();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new InternalServerException(e.getMessage());
		} 
		
	}
	
	private ArrayNode searchEvents(JEvent eventfilter, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
		List<JEvent> events = em.getEvents(eventfilter,authuser);
		ObjectMapper mapper = new ObjectMapper();
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
		return arrayNode;
	}

	
	@POST
	@Path("/getCSV")
	//@Compress
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
	//@Compress
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
					//.encoding("gzip")
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
