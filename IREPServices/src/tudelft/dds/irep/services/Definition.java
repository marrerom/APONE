package tudelft.dds.irep.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.utils.JsonValidator;

@Path("/definition")
public class Definition {
	
	@Context ServletContext context;
	
	
	@Path("/upload")
	@POST
	@Consumes("application/json")
	@Produces("text/plain")
	public String uploadExpDefinition(InputStream experiment){
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(experiment);
			JExperiment exp = mapper.convertValue(jnode, JExperiment.class);
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ProcessingReport pr = jval.validate(exp,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			JTreatment[] treatments = exp.getTreatment();
			for (JTreatment t:treatments) {
				String dsl = t.getDefinition();
				PlanoutDSLCompiler.dsl_to_json(dsl); //just to check if the dsl are valids 
			}
			Database db = (Database) context.getAttribute("DBManager");
			return db.addExperiment(exp);
		} catch (IOException | IllegalArgumentException | ProcessingException | ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
		
	}
	

}
