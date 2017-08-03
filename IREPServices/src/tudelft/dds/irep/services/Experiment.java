package tudelft.dds.irep.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JExposureBody;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.JsonValidator;

@Path("/experiment")
public class Experiment {
	
	@Context ServletContext context;
	
	@Path("/upload")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String uploadExpDefinition(InputStream experiment){
		
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
			return em.addExperiment(exp);
		} catch (IOException | IllegalArgumentException | ProcessingException | ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}
	
	
	@Path("/start")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String startExperiment(@FormDataParam("idexp") String idexp, @FormDataParam("configuration") InputStream configuration){
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(configuration);
			JConfiguration conf = mapper.convertValue(jnode, JConfiguration.class);
			ProcessingReport pr = jval.validate(conf,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			
			JExperiment exp = em.getExperiment(idexp);
			String idrun = em.addConfig(idexp,conf);
			conf.set_id(idrun);
			em.start(exp,conf);
			return idrun;
		} catch (IOException | ProcessingException | ParseException | TimeoutException | ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}
	
	@Path("/start")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void start(String idconf) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JConfiguration conf = em.getConfiguration(idconf);
			JExperiment exp = em.getExperimentFromConf(idconf);
			em.start(exp,conf);
		} catch (IOException | ParseException | TimeoutException | ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}

	
	@Path("/stop")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void stop(String idconfig) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");

			JConfiguration conf = em.getConfiguration(idconfig);
			em.stop(conf);
		} catch (IOException | ParseException | TimeoutException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}
	
	
	@Path("/getParams")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String getParams(@FormDataParam("idconfig") String idconfig, @FormDataParam("idunit") String idunit, 
			@FormDataParam("timestamp") String timestamp) {
		try {
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
	
			ObjectMapper mapper = new ObjectMapper();
			JExperiment exp = em.getExperimentFromConf(idconfig);
			String unitExp = exp.getUnit();
			Map<String, ?> params = em.getParams(unitExp, idconfig, idunit);
			String paramsstr = mapper.writeValueAsString(params);
			
			//TODO: is it possible to run asynchronously the following?
			String treatment = em.getTreatment(unitExp, idconfig, idunit);
			JExposureBody expbody = em.createExposureBody(treatment, params);
			ProcessingReport pr = jval.validate(expbody,mapper.readTree(mapper.writeValueAsString(expbody)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			JEvent event = em.createExposureEvent(idconfig, idunit, timestamp, expbody);
			pr = jval.validate(event,mapper.readTree(mapper.writeValueAsString(event)), context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			em.registerEvent(event);
			em.monitorEvent(event);
			
			return paramsstr;
		} catch (IOException | ParseException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
	}
	
	
	

}
