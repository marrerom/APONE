package tudelft.dds.irep.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/yamlrepository")
public class YAMLRepository {

	@Path("/definition/upload")
	@POST
	@Consumes("application/json")
	@Produces("text/plain")
	public String uploadExpDefinition(@QueryParam("yamlFile") String yamlFile){
		//TODO: insert in db
		String id="1";
		return id;
	}
	
	@Path("/distribution/upload")
	@POST
	@Consumes("application/json")
	@Produces("text/plain")
	public String uploadExpDefinition(@QueryParam("idDef") String idDef, @QueryParam("yamlFile") String yamlFile){
		//TODO: insert in db
		String id="1";
		return id;
	}
	
	
	
	
}
