package tudelft.dds.irep.test;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

import com.google.common.base.Preconditions;

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
		    Preconditions.checkArgument(resExposure.getStatusLine().getStatusCode()==200,"Error: register exposure call");
		    
		    //send test event
		    JSONObject evalue = new JSONObject();
		    evalue.put("property1", "value1");
		    evalue.put("property2", true);
		    evalue.put("property3", 1);
		    JSONArray evalueArray = new JSONArray();
		    evalueArray.put("Item1");
		    evalue.put("property4", evalueArray);
		    JSONObject testEvent = getEvent(idrun, idunit, "JSON", "test", evalue.toString(), params);
		    HttpResponse resTestEvent = registerEvent(testEvent);
		    Preconditions.checkArgument(resTestEvent.getStatusLine().getStatusCode()==200,"Error: register test event call");
		    
		    //check complete
		    HttpResponse resCheck = checkCompleted(idrun, idunit);
		    Preconditions.checkArgument(resCheck.getStatusLine().getStatusCode() == 200, "Error: check completed call");
		    String iscompleted = EntityUtils.toString(resCheck.getEntity()); 
		    Preconditions.checkArgument(iscompleted.equals("false"), "Error: experiment already completed");
		    
		    //send complete
		    JSONObject completed = getEvent(idrun, idunit, "STRING", "completed", "", params );
		    HttpResponse resCompleted = registerEvent(completed);
		    Preconditions.checkArgument(resCompleted.getStatusLine().getStatusCode()==200,"Error: register completed call");
		    
		    return Response.ok().build();
	    
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	    
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
		HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/user/checkCompletedExp/"+idrun+"/"+idunit);
	    HttpResponse res = httpClient.execute(httpGet);
	    return res;
	}

}
