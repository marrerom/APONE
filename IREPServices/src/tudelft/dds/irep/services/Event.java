package tudelft.dds.irep.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.utils.ExperimentManager;
import tudelft.dds.irep.utils.JsonValidator;

@Path("/event")
public class Event {
	
	@Context ServletContext context;
	
	@Path("/register")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String register(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") String timestamp, 
			@FormDataParam("unitid") String unitid, @FormDataParam("binary") String binary, 
			@FormDataParam("ename") String ename, @FormDataParam("evalue") InputStream evalue) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			
			String valuestr;
			if (Boolean.valueOf(binary)) {
				byte[] valuebin = ByteStreams.toByteArray(evalue);
				valuestr = java.util.Base64.getEncoder().encodeToString(valuebin);
			} else {
				valuestr = CharStreams.toString(new InputStreamReader(evalue));
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode json = mapper.createObjectNode();
			
			//if the value is null, json schema does not recognize it as such
			if (idconfig != null)
				json.put("idconfig", idconfig);
			if (timestamp != null)
				json.put("timestamp", timestamp);
			if (unitid != null)
				json.put("unitid", unitid);
			if (binary != null)
				json.put("binary", Boolean.valueOf(binary));
			if (ename != null)
				json.put("ename", ename);
			if (valuestr != null)
				json.put("evalue", valuestr);
			JsonNode jnode = mapper.readTree(json.toString());
			JEvent event = mapper.convertValue(jnode, JEvent.class);
			ProcessingReport pr = jval.validate(event,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			return em.saveEvent(event, idconfig);
		} catch (IOException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}
	
	
	@Path("/get/{idevent}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String register(@PathParam("idevent") String idevent) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JEvent jevent = em.getEvent(idevent.toString());
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jevent.getDocmap());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}

//	try {
//		
//		String evalue = jevent.getEvalue();
//	} catch (IOException | ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	

	
	
	@Path("/timestampFormat")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTimestampFormat() {
		return JEvent.timestampFormat;
	}


}
