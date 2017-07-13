package tudelft.dds.irep.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;

import tudelft.dds.irep.data.Database;

@Path("/definition")
public class Definition {
	
	@Context ServletContext context;
	
	
	@Path("/upload")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String uploadExpDefinition(@FormParam("expname") String expname, @FormParam("experimenter") String experimenter, @FormParam("description") String description, @FormParam("yaml") String yaml){
		String nsName = String.valueOf(yaml.hashCode());
		try {
			//TODO: validate definition ONLY, no needed generation of namespace
			NamespaceConfig nsConf = new YAMLConfigParser().parseAndValidate(new StringReader(yaml), nsName);
			Database db = (Database) context.getAttribute("DBManager");
			return db.AddExperiment(expname, experimenter, description, yaml);

		} catch (ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
		
	}
	

}
