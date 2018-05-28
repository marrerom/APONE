package tudelft.dds.irep.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerException extends WebApplicationException {

	 public InternalServerException (String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			.header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Headers","origin, content-type, accept")
			.header("Access-Control-Allow-Methods","GET, POST, OPTIONS")
            .entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
