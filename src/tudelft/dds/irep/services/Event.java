package tudelft.dds.irep.services;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
import com.google.common.base.Supplier;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JEventCSV;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.RunningExpInfo;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.Utils;

@Path("/event")
public class Event {
	
	@Context ServletContext context;
	
	//TODO: make it more efficient asking first if the experiment is running, and only in that case obtain the treatment from nsConfig in memory
	@Path("/register")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void registerMultipart(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") String timestamp, 
			@FormDataParam("idunit") String idunit, @FormDataParam("binary") String binary, 
			@FormDataParam("ename") String ename, @FormDataParam("evalue") InputStream evalue, 
		    @FormDataParam("paramvalues") String paramvalues) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(paramvalues);
			JParamValues params = mapper.convertValue(jnode, JParamValues.class);
			String unitExp = em.getExperimentFromConf(idconfig).getUnit();
			String treatment = em.getTreatment(unitExp, idconfig, idunit); //could be obtained from nsconfig if the experiment is running
			JEvent event = em.createEvent(idconfig, idunit, ename, Boolean.valueOf(binary), evalue, timestamp,treatment, params);
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event);
		} catch (IOException | ProcessingException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	

	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerJson(String inputJson) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode inputNode = mapper.readTree(inputJson);
			String idconfig = inputNode.get("idconfig").asText();
			String idunit = inputNode.get("idunit").asText();
			String timestamp = inputNode.get("timestamp").asText();
			String binary = inputNode.get("binary").asText();
			String ename = inputNode.get("ename").asText();
			String evalue = inputNode.get("evalue").asText();
			
			JParamValues params = mapper.convertValue(inputNode.get("paramvalues"), JParamValues.class);
			String unitExp = em.getExperimentFromConf(idconfig).getUnit();
			String treatment = em.getTreatment(unitExp, idconfig, idunit); //could be obtained from nsconfig if the experiment is running
			InputStream stream = new ByteArrayInputStream(evalue.getBytes(StandardCharsets.UTF_8.name()));
			JEvent event = em.createEvent(idconfig, idunit, ename, Boolean.valueOf(binary), stream, timestamp,treatment, params);
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(idconfig, event);
		} catch (IOException | ProcessingException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}

	
	@Path("/get/{idevent}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idevent") String idevent) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JEvent jevent = em.getEvent(idevent);
			ObjectMapper mapper = new ObjectMapper();
			String eventstr = mapper.writeValueAsString(jevent); 
			return eventstr;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
	}
	
//	@Path("/monitor/{idconf}")
//	@GET
//	@Consumes(MediaType.TEXT_PLAIN)
//	@Produces(MediaType.APPLICATION_JSON)
//	public String monitor(@PathParam("idconf") String idconf) {
//		try {
//			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
//			Map<String, Integer> expcount = em.getExposures(idconf);
//			//return Response.ok(expcount,MediaType.APPLICATION_JSON).build();
//			ObjectMapper mapper = new ObjectMapper();
//			String expcountstr = mapper.writeValueAsString(expcount); 
//			return expcountstr;
//		} catch (BadRequestException | JsonProcessingException e) {
//			e.printStackTrace();
//			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
//		}
//	}
	


	@Path("/delete")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void delete(String idevent) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			em.deleteEvent(idevent);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
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
	public String search(String filter) {
	
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(filter);
			JEvent eventfilter = mapper.convertValue(jnode, JEvent.class);
			ProcessingReport pr = jval.validate(eventfilter,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			List<JEvent> events = em.getEvents(eventfilter);
			ArrayNode arrayNode = mapper.createArrayNode();
			for (JEvent ev: events) {
				ObjectNode node = mapper.createObjectNode();
				node.put("_id", ev.get_id());
				node.put("ename", ev.getEname());
			    node.put("unitid", ev.getUnitid());
			    node.put("timestamp", Utils.getTimestamp(ev.getTimestamp()));
			    node.put("binary", ev.isBinary());
			    if (!ev.isBinary()) {
			    	node.put("evalue", ev.getEvalue());
			    }
		        arrayNode.add(node);
			}
			return mapper.writeValueAsString(arrayNode);
			
		} catch (IOException | ParseException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e.getCause().getMessage());
		}
		
	}

	
	@POST
	@Path("/getCSV")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getCSV(String idevents) throws JsonProcessingException, IOException, ParseException {
		ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jnode = mapper.readTree(idevents);
		List<JEvent> events = new ArrayList<JEvent>(); 
		for (JsonNode item : jnode) {
			String id = item.asText();
			events.add(em.getEvent(id));
		}
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
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
	}

	


}
