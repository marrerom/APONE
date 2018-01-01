package tudelft.dds.irep.test;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

@Path("/test/client")
public class Client extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Context ServletContext context;
	
	final static String localhost = "http://localhost:8080"; 
	final static String jerseyServices = "service";
	
	@GET 
	public Response client(@Context UriInfo uriInfo) {
		try {
		
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		    String rankingAlg = queryParams.getFirst("rankingAlg");
		    String linkColor = queryParams.getFirst("linkColor");
		    String idunit = queryParams.getFirst("_idunit");
		    
		    JSONObject params = new JSONObject();
		    params.put("rankingAlg", rankingAlg);
		    params.put("linkColor", linkColor);
		    
		    //send event expose
		    JSONObject exposure = getEvent(idunit, "STRING", "exposure", "", params.toString() );
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
		    JSONObject testEvent = getEvent(idunit, "JSON", "test", evalue.toString(), params.toString());
		    HttpResponse resTestEvent = registerEvent(testEvent);
		    Preconditions.checkArgument(resTestEvent.getStatusLine().getStatusCode()==200,"Error: register test event call");
		    
		    //check complete
		    HttpResponse resCheck = checkCompleted(idunit);
		    Preconditions.checkArgument(resCheck.getStatusLine().getStatusCode() == 200, "Error: check completed call");
		    String iscompleted = EntityUtils.toString(resCheck.getEntity()); 
		    Preconditions.checkArgument(iscompleted.equals("true"), "Error: experiment already completed");
		    
		    //send complete
		    JSONObject completed = getEvent(idunit, "STRING", "completed", "", params.toString() );
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
	
	public JSONObject getEvent(String idunit, String etype, String ename, String evalue, String paramvalues ) {
		JSONObject event = new JSONObject();
		event.put("idunit",idunit);
		event.put("idconfig", ClientTest.experiments.get(idunit));
		event.put("etype",etype);
		event.put("ename", ename);
		event.put("evalue", evalue);
		event.put("paramvalues", paramvalues);
		return event;
	}
	
	public HttpResponse checkCompleted(String idunit) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/user/checkCompletedExp/"+ClientTest.experiments.get(idunit)+"/"+idunit);
	    HttpResponse res = httpClient.execute(httpGet);
	    return res;
	}

}
