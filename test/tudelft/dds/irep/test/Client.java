package tudelft.dds.irep.test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import com.google.common.base.Preconditions;

import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.Security;

@Path("/test")
public class Client extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Context ServletContext context;
	
	final static String localhost = "http://localhost:8080"; 
	final static String jerseyServices = "service";
	
	@Path("/setidrun/{idrun}")
	@GET 
	public Response setIdrun(@PathParam("idrun") String idrun, @Context HttpServletRequest request) {
		if (request.getLocalAddr().equals(request.getRemoteAddr())) { //TODO: check if valid
			request.getSession().setAttribute("idrun", idrun);
			return Response.ok().build();
		}
		return Response.status(Status.UNAUTHORIZED).build();
	}

	@Path("/client")
	@GET 
	public Response client(@Context HttpServletRequest request) {
		try {
			Map<String, String> queryparams = Utils.decodeQueryParams(request.getQueryString());
		    String idunit = queryparams.get("_idunit");
		    String idrun= (String) request.getSession().getAttribute("idrun"); 
		    JSONObject params = new JSONObject(queryparams);
		    
		    //send event expose
		    JSONObject exposure = getEvent(idrun, idunit, "STRING", "exposure", "", params );
		    HttpResponse resExposure = registerEvent(exposure);
		    Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register exposure")), resExposure, "Client: register exposure");
		    
		    Random rand = new Random();
		    //send tests (click and search) event
		    JSONObject evalue = new JSONObject();
		    evalue.put("property1", "value1");
		    evalue.put("property2", true);
		    evalue.put("property3", 1);
		    JSONArray evalueArray = new JSONArray();
		    evalueArray.put("Item1");
		    evalue.put("property4", evalueArray);

		    int iter = rand.nextInt(11);
		    do {
			    JSONObject clickEvent = getEvent(idrun, idunit, "JSON", "click", evalue.toString(), params);
			    HttpResponse resClickEvent = registerEvent(clickEvent);
			    Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register click")), resClickEvent, "Client: register click");
		    } while (--iter > 0);
			
		    iter = rand.nextInt(4);
		    do {
			    JSONObject searchEvent = getEvent(idrun, idunit, "JSON", "search", evalue.toString(), params);
			    HttpResponse resSearchEvent = registerEvent(searchEvent);
			    Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register search")), resSearchEvent, "Client: register search");
		    } while (--iter > 0);
		    
		    //check complete
	    	HttpResponse resCheck = checkCompleted(idrun, idunit);
	    	String iscompleted = Utils.checkWebResponse(Collections.emptySet(), resCheck, "Client: check completed");
	    	Assert.assertTrue("Client Error: experiment already completed", iscompleted.equals("false"));
		    
		    
		    //send complete
	    	boolean sendcompleted = rand.nextBoolean();
	    	if (sendcompleted) {
	    		JSONObject completed = getEvent(idrun, idunit, "STRING", "completed", "", params );
	    		HttpResponse resCompleted = registerEvent(completed);
	    		Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register completed")), resCompleted, "Client: register completed");
	    	}
		    return Response.ok().build();
	    
		} catch (BadRequestException e) {
			System.out.println(e.getMessage());
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
	}
	
	public HttpResponse registerEvent(JSONObject event) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/register");
		StringEntity entity = new StringEntity(event.toString());
	    httpPost.setEntity(entity);
	    httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type", "application/json");
	    HttpResponse res = httpClient.execute(httpPost);
	    return res;
	}
	
	public JSONObject getEvent(String idrun, String idunit, String etype, String ename, String evalue, JSONObject paramvalues ) {
		JSONObject event = new JSONObject();
		event.put("idunit",idunit);
		event.put("idconfig", idrun);
		event.put("etype",etype);
		event.put("ename", ename);
		if (etype.equals("JSON"))
			event.put("evalue", new JSONObject(evalue));
		else 
			event.put("evalue", evalue);
		event.put("paramvalues", paramvalues);
		return event;
	}
	
	public HttpResponse checkCompleted(String idrun, String idunit) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/user/checkcompleted/"+idrun+"/"+idunit);
	    HttpResponse res = httpClient.execute(httpGet);
	    return res;
	}

}
