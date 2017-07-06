package tudelft.dds.irep.test;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.glassdoor.planout4j.config.ValidationException;

@Provider
public class ParsingException implements ExceptionMapper<ValidationException>{
	
	   public Response toResponse(ValidationException e) {
	      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid YAML").build();
	   }
}



