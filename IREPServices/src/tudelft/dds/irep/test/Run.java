package tudelft.dds.irep.test;

import javax.ws.rs.Path;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import javax.ws.rs.POST;
//import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;


@Path("/run")
public class Run {
	
		
	@POST
	public Response predefinedExperiment(){
		String output = "<p>Creating conf object</p>";
		
		String nsName = "demo_namespace";
		try {
			NamespaceConfig nsConf = new YAMLConfigParser().parseAndValidate(
			    		new FileReader(Paths.get("conf", nsName + ".yaml").toFile()), nsName);
			ConfigRepository.addConf(nsConf.hashCode(),nsConf);
			
		} catch (FileNotFoundException e) {
			throw new javax.ws.rs.NotFoundException();
		} catch (ValidationException e) {
			new ParsingException().toResponse(e);
		}
		
		return Response.status(200).entity(output).build();
	}

}
