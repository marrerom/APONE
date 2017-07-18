package tudelft.dds.irep.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.Experiment;
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
			Experiment exp = mapper.convertValue(jnode, Experiment.class);
			
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ProcessingReport pr = jval.validate(exp,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
		    //Experiment exp = mapper.readValue(experiment,Experiment.class);
		    //Definition yaml = exp..getDefinition();
		    //String nsName = String.valueOf(yaml.hashCode());    
		    //System.out.println(json);
			//TODO: validate definition ONLY, no needed generation of namespace
			//NamespaceConfig nsConf = new YAMLConfigParser().parseAndValidate(new StringReader(yaml), nsName);
			Database db = (Database) context.getAttribute("DBManager");
			return db.AddExperiment(exp);
			

		} catch (IOException | ProcessingException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
		
	}
	

}
