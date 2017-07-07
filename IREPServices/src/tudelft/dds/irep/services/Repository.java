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

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.yaml.snakeyaml.reader.StreamReader;

import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.collect.ImmutableMap;

@Path("/repository")
public class Repository {

	@Path("/upload")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String uploadExpDefinition(@FormParam("yaml") String yaml){
		String nsName = String.valueOf(yaml.hashCode());
		try {
			NamespaceConfig nsConf = new YAMLConfigParser().parseAndValidate(new StringReader(yaml), nsName);
			String idexp = String.valueOf(nsConf.hashCode());
			TemporalDDBB.idExpYaml.put(idexp, yaml);
			return String.valueOf(idexp);
			//TODO: validate definition ONLY
			//TODO: insert info in database.repository
			//TODO: return value is the identifier

		} catch (ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}
		
	}
	

}
