package tudelft.dds.irep.services;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.experiment.JsonValidator;

@Path("/event")
public class Event {
	
	@Context ServletContext context;
	
	@Path("/register")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void register(@FormDataParam("idconfig") String idconfig, @FormDataParam("timestamp") String timestamp, 
			@FormDataParam("unitid") String unitid, @FormDataParam("binary") String binary, 
			@FormDataParam("ename") String ename, @FormDataParam("evalue") InputStream evalue) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ObjectMapper mapper = new ObjectMapper();
			Date timestampDate = (new SimpleDateFormat(JsonDateSerializer.timestampFormat)).parse(timestamp);
			JEvent event = em.createEvent(idconfig, unitid, ename, Boolean.valueOf(binary), evalue, timestampDate);
			ProcessingReport pr = jval.validate(event, mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(event);
		} catch (IOException | ProcessingException | ParseException e) {
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
			return mapper.writeValueAsString(jevent);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}

	 
		
	@Path("/timestampFormat")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTimestampFormat() {
		return JsonDateSerializer.timestampFormat;
	}

	
	


}
