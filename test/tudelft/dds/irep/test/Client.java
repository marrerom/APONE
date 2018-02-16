package tudelft.dds.irep.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import com.google.common.base.Preconditions;

import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.Utils;

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
	public Response client(@Context HttpServletRequest request) throws UnsupportedEncodingException {
	
		Map<String, String> queryparams = 	tudelft.dds.irep.utils.Utils.decodeQueryParams(request.getQueryString());
	    String idunit = queryparams.get("_idunit");
	    String idrun= (String) request.getSession().getAttribute("idrun"); 
	    JSONObject params = new JSONObject(queryparams);
	  
	    
	    Random rand = new Random();
	    //send tests (click and search) event
	    JSONObject evalue = new JSONObject();
	    evalue.put("property1", "value1");
	    evalue.put("property2", true);
	    evalue.put("property3", 1);
	    JSONArray evalueArray = new JSONArray();
	    evalueArray.put("Item1");
	    evalue.put("property4", evalueArray);
	    
	    Boolean withparams = rand.nextBoolean();
	    
		try {

			//get params
			HttpResponse resGetParams = getParams(idrun, idunit);
			String returnedParams = tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: getparams")), resGetParams, "Client: getparams");
			
			//if planout script, overwrite param
			boolean overwrite = false;
			JSONObject overrides = new JSONObject();
			if (resGetParams.getStatusLine().getStatusCode() == 200) {
				JSONObject jreturnedParams = new JSONObject(returnedParams);
				JSONObject rparams = jreturnedParams.getJSONObject("params");
				if (rparams.length() > 0) {
					for (String key:rparams.keySet()) {
						if (key.equals("linkColor")) {
							overrides.put(key, "orange");
							overwrite = true;
						}
					}
				}
			}
			
			//get params with overwriting if planout script exists
			JSONObject inputGetParams = new JSONObject();
			inputGetParams.put("idconfig", idrun);
			inputGetParams.put("idunit", idunit);
			inputGetParams.put("overrides", overrides);
			HttpResponse resGetParamsJSON = getParamsJSON(inputGetParams);
			String returnedParamsJSON = tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: getparams overwriting")), resGetParamsJSON, "Client: getparams overwriting");
			if (overwrite) {
				JSONObject jsonparams = new JSONObject(returnedParamsJSON);
				Assert.assertTrue("Client: overwrited param received", jsonparams.getJSONObject("params").getString("linkColor").equals("orange"));
			}
			
			
		    //send event expose
		    JSONObject exposure = getEvent(idrun, idunit, "STRING", "exposure", "", params, withparams);
		    //JSONObject exposure = getEvent(idrun, idunit, "JSON", "exposure", evalue.toString(), params ); JUST TO TEST. WORKS FINE
		    HttpResponse resExposure = registerEvent(exposure);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register exposure")), resExposure, "Client: register exposure");
			Integer exposures = RunTest.idrun2exp.get(idrun);
			if (exposures == null) {
				exposures = 0;
			}
			RunTest.idrun2exp.put(idrun, ++exposures);
			
			
		    int iter = rand.nextInt(11);
		    do {
			    JSONObject clickEvent = getEvent(idrun, idunit, "JSON", "click", evalue.toString(), params, withparams);
			    HttpResponse resClickEvent = registerEvent(clickEvent);
				tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register click")), resClickEvent, "Client: register click");
		    } while (--iter > 0);
			
		    iter = rand.nextInt(4);
		    do {
			    JSONObject searchEvent = getEvent(idrun, idunit, "JSON", "search", evalue.toString(), params, withparams);
			    HttpResponse resSearchEvent = registerEventText(searchEvent);
				tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register search")), resSearchEvent, "Client: register search");
		    } while (--iter > 0);
		    
		    String content = "mycontents";
		    String encodedvalue = Utils.encodeBinary(content.getBytes());
		    
		    JSONObject bt1Event = getEvent(idrun, idunit, "BINARY", "binarytest1", encodedvalue, params, withparams);
		    HttpResponse resbt1Event = registerEvent(bt1Event);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register binarytest1")), resbt1Event, "Client: register binarytest1");

			
		    JSONObject bt2Event = getEvent(idrun, idunit, "BINARY", "binarytest2", content, params, withparams);
		    HttpResponse resbt2Event = registerEventMultipart(bt2Event);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register binarytest2")), resbt2Event, "Client: register binarytest2");

		    
		    //check complete
	    	HttpResponse resCheck = checkCompleted(idrun, idunit);
	    	String iscompleted = 	tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resCheck, "Client: check completed");
	    	Assert.assertTrue("Client Error: experiment already completed", iscompleted.equals("false"));
		    
		    
		    //send complete
	    	boolean sendcompleted = rand.nextBoolean();
	    	if (sendcompleted) {
	    		JSONObject completed = getEvent(idrun, idunit, "STRING", "completed", "", params, withparams);
	    		HttpResponse resCompleted = registerEvent(completed);
	    		tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Client: register completed")), resCompleted, "Client: register completed");
	    	}
		    return Response.ok().build();
	    
		} catch (BadRequestException e) {
			RunTest.finishedExperiment(idrun);
			return Response.ok().build();
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
	
	public HttpResponse registerEventText(JSONObject event) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/register");
		StringEntity entity = new StringEntity(event.toString());
	    httpPost.setEntity(entity);
	    httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type",MediaType.TEXT_PLAIN);
	    HttpResponse res = httpClient.execute(httpPost);
	    return res;
	}
	
	public HttpResponse registerEventMultipart(JSONObject event) throws Exception  {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/register");
		
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("idconfig", event.getString("idconfig"));
		builder.addTextBody("idunit", event.getString("idunit"));
		builder.addTextBody("ename", event.getString("ename"));
		builder.addTextBody("etype", event.getString("etype"));
		if (!event.isNull("paramvalues"))
			builder.addTextBody("paramvalues", event.get("paramvalues").toString() );
		builder.addBinaryBody("evalue", event.get("evalue").toString().getBytes());

		HttpEntity entity = builder.build();
		
	    httpPost.setEntity(entity);
	    //httpPost.setHeader("Content-type", MediaType.MULTIPART_FORM_DATA); If we set the type, it does not work O_o
	    HttpResponse res = httpClient.execute(httpPost);
	    return res;
	}
	
	public JSONObject getEvent(String idrun, String idunit, String etype, String ename, String evalue, JSONObject paramvalues, Boolean withparams ) {
		JSONObject event = new JSONObject();
		event.put("idunit",idunit);
		event.put("idconfig", idrun);
		event.put("etype",etype);
		event.put("ename", ename);
		if (etype.equals("JSON"))
			event.put("evalue", new JSONObject(evalue));
		else 
			event.put("evalue", evalue);
		if (withparams)
			event.put("paramvalues", paramvalues);
		return event;
	}
	
	public HttpResponse checkCompleted(String idrun, String idunit) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/user/checkcompleted/"+idrun+"/"+idunit);
	    HttpResponse res = httpClient.execute(httpGet);
	    return res;
	}
	
	public HttpResponse getParams(String idrun, String idunit) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/getparams/"+idrun+"/"+idunit);
	    HttpResponse res = httpClient.execute(httpGet);
	    return res;
	}
	
	public HttpResponse getParamsJSON(JSONObject input) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/getparams");
		
		StringEntity entity = new StringEntity(input.toString());
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type",MediaType.TEXT_PLAIN);

	    HttpResponse res = httpClient.execute(httpPost);
	    return res;
	}

}
