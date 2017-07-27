package tudelft.dds.irep.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

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

public class Event {
	
	@Context ServletContext context;
	
	@Path("/register")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void stop(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") Date timestamp, 
			@FormDataParam("unitid") String unitid, @FormDataParam("binary") boolean binary, 
			@FormDataParam("ename") String ename, @FormDataParam("evalue") InputStream evalue) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			
			String valuestr;
			if (binary) {
				byte[] valuebin = ByteStreams.toByteArray(evalue);
				valuestr = java.util.Base64.getEncoder().encodeToString(valuebin);
			} else {
				valuestr = CharStreams.toString(new InputStreamReader(evalue));
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode json = mapper.createObjectNode();
			json.put("idconfig", idconfig);
			json.put("timestamp", timestamp.toString()); //CHECK
			json.put("unitid", unitid);
			json.put("binary", binary);
			json.put("ename", ename);
			json.put("evalue", valuestr);
			JsonNode jnode = mapper.readTree(json.asText());
			JEvent event = mapper.convertValue(jnode, JEvent.class);
			ProcessingReport pr = jval.validate(event,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			em.saveEvent(event, idconfig);
		} catch (IOException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}

}
