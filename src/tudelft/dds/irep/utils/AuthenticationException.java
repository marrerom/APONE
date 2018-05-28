package tudelft.dds.irep.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AuthenticationException extends WebApplicationException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationException ()  {
         super(Response.status(Response.Status.UNAUTHORIZED)
     			.header("Access-Control-Allow-Origin", "*")
    			.header("Access-Control-Allow-Headers","origin, content-type, accept")
    			.header("Access-Control-Allow-Methods","GET, POST, OPTIONS")
                .entity("Unauthenticated user").type(MediaType.TEXT_PLAIN).build());
    }
	
	
	 
}
