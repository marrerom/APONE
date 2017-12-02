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
                 .entity("Unauthenticated user").type(MediaType.TEXT_PLAIN).build());
    }
	
	
	 
}
