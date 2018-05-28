package tudelft.dds.irep.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class BadRequestException extends WebApplicationException {

	 public BadRequestException (String message) {
		 super(Response.status(Response.Status.BAD_REQUEST)
				 .header("Access-Control-Allow-Origin", "*")
				 .header("Access-Control-Allow-Headers","origin, content-type, accept")
				 .header("Access-Control-Allow-Methods","GET, POST, OPTIONS")
				 .entity(message).type(MediaType.TEXT_PLAIN).build());
     }
}
