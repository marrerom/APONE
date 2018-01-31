package tudelft.dds.irep.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.JSONArray;
import org.json.JSONObject;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.base.Preconditions;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.Security;


@Path("/test")
public class ClientTest {
		public static Map<String, String> experiments = new HashMap<String, String>(); //username - idrun
		public static Map<String, String> idmapname = new HashMap<String, String>(); //user id - user idname
		final static String localhost = "http://localhost:8080"; 
		final static String jerseyServices = "service";
		
		@Context ServletContext context;
		
		
		@Path("/setuser/{userid}/{username}")
		@Produces(MediaType.TEXT_PLAIN)
		@GET 
		public Response setUser(@PathParam("userid") String userid, @PathParam("username") String username, @Context HttpServletRequest request) throws IOException, ParseException {
			if (request.getLocalAddr().equals(request.getRemoteAddr())) { //TODO: check if valid
				ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
				JUser user = Security.setAuthenticatedUser(request, em, userid, username);
				return Response.ok(user.getIdname()).build();
			}
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		@Path("/setmasteruser")
		@GET 
		public Response setUser(@Context HttpServletRequest request) throws IOException, ParseException {
			if (request.getLocalAddr().equals(request.getRemoteAddr())) { //TODO: check if valid
				ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
				JUser master = em.getUserByIdname("socialdatadelft", Security.getMasterUser());
				Security.setAuthenticatedUser(request, em, master.getIdTwitter(), master.getIdname());
				return Response.ok().build();
			}
			return Response.status(Status.UNAUTHORIZED).build();
		}
		

		@GET
		public Response test(@Context HttpServletRequest request) throws ClientProtocolException, IOException {
			Response response;
			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			final Integer NUSERS = 120;
			final Integer NEXPS = 10;
			try {
				for (int i=1;i<=NUSERS;i++) {
					System.out.println("Creating experiment user "+i);
					createExperiment(Integer.toString(i));
				}
				for (int nexp=1;nexp<=NEXPS;nexp++) {
					for (int i=1;i<=NUSERS;i++) {
						System.out.println("Assigning experiment of user "+i);
						assignExperiment(Integer.toString(i));
						//Thread.sleep(3000);
					}
				
				}
				
				long startTime = System.currentTimeMillis();
				int iteration = 1;
				boolean stop = true;
				do {
					System.out.println("Iteration "+iteration);
					for (int i=1;i<=NUSERS;i++) {
						System.out.println("Testing events experiment user "+i);
						stop = stop & testEvents(Integer.toString(i));
					}
					//Thread.sleep(10000);
 				iteration++;
				} while (!stop && (System.currentTimeMillis() - startTime)< (1000 * 60 * 10));
				Preconditions.checkArgument(stop, "Error: stop condition failed, check monitoring results");

				
				response = Response.ok().build();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			} finally {
				for (int i=1;i<=NUSERS;i++) {
					System.out.println("Clearing user "+i);
					clear(Integer.toString(i));
				}
			}

			return response; 			
		}
		
		public void createExperiment(String userid) throws ClientProtocolException, IOException {
			String username = "user"+userid;
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			HttpResponse resUser = setUser( httpContext, userid, username);
			Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200 || resUser.getStatusLine().getStatusCode() == 204, "Error: create user call");
			String idname = EntityUtils.toString(resUser.getEntity());
			idmapname.put(userid, idname);
			username = idname;
			
			HttpResponse resCreate = createExperiment(httpContext, userid, username);
			Preconditions.checkArgument(resCreate.getStatusLine().getStatusCode()==200 || resCreate.getStatusLine().getStatusCode()==204,"Error: create experiment call");
			String expid = EntityUtils.toString(resCreate.getEntity());

			HttpResponse resCheck = checkExperiment(httpContext, userid, username, expid);
			Preconditions.checkArgument(resCheck.getStatusLine().getStatusCode() == 200 || resCheck.getStatusLine().getStatusCode() == 204, "Error: search experiment call");
			String resultSearch = EntityUtils.toString(resCheck.getEntity()); 
			JSONArray results = new JSONArray(resultSearch);
			Preconditions.checkArgument(results.length() == 1, "Error: search experiment result");
			
			JSONObject rsearch = new JSONObject(results.get(0).toString());
			String idrun = rsearch.get("idrun").toString();
			experiments.put(username,idrun);
			
			HttpResponse resStart = start(httpContext, idrun);
			Preconditions.checkArgument(resStart.getStatusLine().getStatusCode() == 200 || resStart.getStatusLine().getStatusCode() == 204, "Error: start experiment result");
		}
		
		public void assignExperiment(String userid) throws ClientProtocolException, IOException {
			String username = idmapname.get(userid);
			//Security.setAuthenticatedUser(request, em, userid, username);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

			HttpResponse resUser = setUser( httpContext, userid, username);
			Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200 || resUser.getStatusLine().getStatusCode() == 204, "Error: create user call");

			Boolean valid = false;
			while (!valid) {
				try {
					HttpResponse resAssign = assign(httpContext);
					if (resAssign.getStatusLine().getStatusCode() != 200 && resAssign.getStatusLine().getStatusCode() != 204)
						valid = false;
						
					Preconditions.checkArgument(resAssign.getStatusLine().getStatusCode() == 200 || resAssign.getStatusLine().getStatusCode() == 204, "Error: assign experiment call");
					String redirectionURI = EntityUtils.toString(resAssign.getEntity());
					for (String idrun: experiments.values()) {
						if (redirectionURI.contains(idrun)) {
							valid = true;
							HttpResponse resSetrun = setIdrun(httpContext, idrun);
							Preconditions.checkArgument(resSetrun.getStatusLine().getStatusCode() == 200 || resSetrun.getStatusLine().getStatusCode() == 204, "Error: set idrun call");

							HttpResponse resRedirect = redirect(httpContext,redirectionURI);
							//Preconditions.checkArgument(resRedirect.getStatusLine().getStatusCode() == 200 || resRedirect.getStatusLine().getStatusCode() == 204, "Error: redirect to experiment call");
							if (resRedirect.getStatusLine().getStatusCode() != 200 && resRedirect.getStatusLine().getStatusCode() != 204)
								valid = false;
							break;
						}
					}
					
				} catch (BadRequestException e) {
					System.out.println("No more experiments to assign to "+userid);
				} catch (Exception e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		
		public boolean testEvents(String userid) throws ClientProtocolException, IOException {
			String username = idmapname.get(userid);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			cookieStore.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("username", username));
			HttpResponse resUser = setUser( httpContext, userid, username);
			Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200 || resUser.getStatusLine().getStatusCode() == 204, "Error: create user call");

			Boolean stop = true;
			stop = stop & event ("exposure", httpContext, userid, username);
			stop = stop & event ("test", httpContext, userid, username);
			stop = stop & event ("completed", httpContext, userid, username);
			return stop;
		}
		
		public void clear(String userid) throws ClientProtocolException, IOException {
			String username = idmapname.get(userid);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			cookieStore.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("username", username));
			HttpResponse resUser = setMasterUser( httpContext);
			Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200 || resUser.getStatusLine().getStatusCode() == 204, "Error: create master user call");
			HttpResponse resDeleteUser = deleteUser( httpContext, username);
			Preconditions.checkArgument(resDeleteUser.getStatusLine().getStatusCode() == 200 || resDeleteUser.getStatusLine().getStatusCode() == 204, "Error: delete user call");
		}
		
		public boolean event(String ename, HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpResponse resCheckEvents = checkEvents(ename, httpContext, userid, username);
			Preconditions.checkArgument(resCheckEvents.getStatusLine().getStatusCode() == 200 || resCheckEvents.getStatusLine().getStatusCode() == 204, "Error: check "+ ename +" events call");
			String expevents = EntityUtils.toString(resCheckEvents.getEntity());
			JSONArray arrayevents = new JSONArray(expevents);
			Long differentExposures = arrayevents.toList().stream().map(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.get("idunit");}).distinct().count();
			Preconditions.checkArgument(arrayevents.length()==differentExposures, "Error: monitor "+ ename +": "+arrayevents.length()+" events, but only "+differentExposures +" events with different idunit");

			JSONArray eventids = new JSONArray(arrayevents.toList().stream().map(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.get("_id");}).toArray());
			HttpResponse resDownloadJSON = getFile(httpContext, eventids, "JSON");
			Preconditions.checkArgument(resDownloadJSON.getStatusLine().getStatusCode() == 200 || resDownloadJSON.getStatusLine().getStatusCode() == 204, "Error: check "+ ename +" events getJSON call");

			HttpResponse resDownloadCSV = getFile(httpContext, eventids, "CSV");
			Preconditions.checkArgument(resDownloadCSV.getStatusLine().getStatusCode() == 200 || resDownloadCSV.getStatusLine().getStatusCode() == 204, "Error: check "+ ename +" events getCSV call");
			

			
			HttpResponse resMonitor = monitor(ename, httpContext, userid, username);
			Preconditions.checkArgument(resMonitor.getStatusLine().getStatusCode() == 200 || resMonitor.getStatusLine().getStatusCode() == 204 || resMonitor.getStatusLine().getStatusCode() == 400, "Error: monitor "+ ename);
			if (resMonitor.getStatusLine().getStatusCode() == 400) {
				System.out.println("***Monitoring experiment of user "+username+" failed because its is already off");
			} else {
				String exposures = EntityUtils.toString(resMonitor.getEntity()); 
				JSONObject results = new JSONObject(exposures);
				JSONArray treatments = results.getJSONArray("treatments");
				Integer differentExposuresMonitor = treatments.toList().stream().mapToInt(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.getInt("value");}).sum();
			
			
				//Preconditions.checkArgument(differentExposures == (long)differentExposuresMonitor, "Error: exposures "+arrayevents.length()+" events, monitoring "+differentExposuresMonitor);
				System.out.println(username + " " +ename+" "+arrayevents.length()+" "+differentExposuresMonitor);
			
			
				if (ename.equals("exposures") || ename.equals("completed")) {
					if ((long)differentExposuresMonitor != differentExposures) {
						System.out.println("***"+username + " " +ename+" "+arrayevents.length()+" "+differentExposuresMonitor);
						return false;
					}
				}
			}
			return true;
			
		}
		
		public HttpResponse deleteUser(HttpContext httpContext, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(localhost+context.getContextPath()+"/"+jerseyServices+"/user/delete/"+username);
			HttpResponse res = httpClient.execute(httpPut, httpContext);
			return res;
		}
		
		public HttpResponse getFile(HttpContext httpContext, JSONArray arrayevents, String format) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/get"+format);
			StringEntity entity = new StringEntity(arrayevents.toString());
		    httpPost.setEntity(entity);
		    httpPost.setHeader("Accept", "application/octet-stream");
		    httpPost.setHeader("Content-type", "application/json");
		    HttpResponse res = httpClient.execute(httpPost, httpContext);
			return res;		//return experiment id
		}
		
		public HttpResponse redirect(HttpContext httpContext, String uri) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}
		
		public HttpResponse start(HttpContext httpContext, String idrun) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/start");
			httpPut.setEntity(new StringEntity(idrun));
			HttpResponse res = httpClient.execute(httpPut, httpContext);
			return res;
		}
		
		public HttpResponse assign(HttpContext httpContext) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/user/assignexp");
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}
		
		public HttpResponse setUser(HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/test/setuser/"+userid+"/"+username);
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}
		
		public HttpResponse setMasterUser(HttpContext httpContext) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/test/setmasteruser");
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}

		
		public HttpResponse setIdrun(HttpContext httpContext, String idrun) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/test/setidrun/"+idrun);
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}
		
		public HttpResponse createExperiment(HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/new/experiment");
			JSONObject experiment = getExperiment(userid, username);
			StringEntity entity = new StringEntity(experiment.toString());
		    httpPost.setEntity(entity);
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", "application/json");
		    HttpResponse res = httpClient.execute(httpPost, httpContext);
			
//			WebTarget base = client.target(localhost+context.getContextPath()+"/"+jerseyServices);
//			WebTarget target = base.path("/experiment/new/experiment");
//			Invocation.Builder builder = target.request();
//			JSONObject experiment = getExperiment(userid, username);
//			Response res = builder.post(Entity.entity(experiment.toString(), MediaType.APPLICATION_JSON));
			return res;		//return experiment id
		}
		
		public HttpResponse checkExperiment(HttpContext httpContext, String userid, String username, String expid) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/search");
			JSONObject experiment = getExperimentFilter(userid, username, expid);
			StringEntity entity = new StringEntity(experiment.toString());
		    httpPost.setEntity(entity);
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", "application/json");
		    HttpResponse res = httpClient.execute(httpPost, httpContext);
			
//			WebTarget base = client.target(localhost+context.getContextPath()+"/"+jerseyServices);
//			WebTarget target = base.path("/experiment/search");
//			Invocation.Builder builder = target.request();
//			JSONObject filter = getExperimentFilter(userid,username, expid);
//			Response res = builder.post(Entity.entity(filter.toString(), MediaType.APPLICATION_JSON));
			return res;		
		}
		
		public HttpResponse checkEvents(String ename, HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/search");
			JSONObject event = getEventFilter(ename, experiments.get(username));
			StringEntity entity = new StringEntity(event.toString());
		    httpPost.setEntity(entity);
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", "application/json");
		    HttpResponse res = httpClient.execute(httpPost, httpContext);
			
//			WebTarget base = client.target(localhost+context.getContextPath()+"/"+jerseyServices);
//			WebTarget target = base.path("/experiment/search");
//			Invocation.Builder builder = target.request();
//			JSONObject filter = getExperimentFilter(userid,username, expid);
//			Response res = builder.post(Entity.entity(filter.toString(), MediaType.APPLICATION_JSON));
			return res;		
		}
		
		public HttpResponse monitor(String ename, HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/monitor/subtreatments/"+ename+"/"+experiments.get(username));
			HttpResponse res = httpClient.execute(httpGet, httpContext);
			return res;
		}
		
		public JSONObject getEventFilter(String ename, String expid) {
			JSONObject filter = new JSONObject();
			filter.put("idconfig", expid);
			filter.put("ename", ename);
			return filter;
		}

		public JSONObject getExperimentFilter(String userid, String username, String expid) {
			JSONObject filter = getExperiment(userid, username);
			filter.put("_id", expid);
			JSONArray treatments = filter.getJSONArray("treatment");
			JSONArray newtreatments = new JSONArray();
			for (Object t: treatments) {
				JSONObject treat = (JSONObject)t;
				String url = treat.get("url").toString();
				String newurl = Pattern.quote(url);
				treat.put("url", newurl);
				newtreatments.put(treat);
			}
			filter.put("treatment", newtreatments);
			return filter;
		} 
		
		public JSONObject getExperiment(String userid, String username) {
			final Integer MINDATETOEND = 10;
			JSONObject experiment = new JSONObject();
			experiment.put("name", "experiment"+userid);
			experiment.put("experimenter", username);
			experiment.put("description", "Description experiment "+userid);
			JSONArray treatments = new JSONArray();
			JSONArray configurations = new JSONArray();
			JSONObject control = new JSONObject();
			control.put("name", "control");
			control.put("description", "Control description");
			control.put("control", true);
			treatments.put(control);
			JSONObject treatment = new JSONObject();
			treatment.put("name", "treatment");
			treatment.put("description", "Treatment description");
			treatment.put("control", false);
			treatments.put(treatment);
			JSONObject config = new JSONObject();
			config.put("name", "configuser"+userid);
			config.put("experimenter", username);
			configurations.put(config);
			JSONArray distribution = new JSONArray();
			config.put("distribution", distribution);
			
			//exp simple or advanced
			Integer mod = Integer.parseInt(userid) % 2;
			if (mod == 0) {
				control.put("url", "http://localhost:8080/IREPlatform/service/test/client?rankingAlg=default&linkColor=blue");
				treatment.put("url", "http://localhost:8080/IREPlatform/service/test/client?rankingAlg=default&linkColor=green");
			} else {
				control.put("url", "http://localhost:8080/IREPlatform/service/test/client");
				control.put("definition", "rankingAlg=\"default\";linkColor=\"blue\";");
				treatment.put("url", "http://localhost:8080/IREPlatform/service/test/client");
				treatment.put("definition", "rankingAlg=\"default\";linkColor=\"green\";");
			}
			
			//exp with nothing, date to end or completed units
			mod = Integer.parseInt(userid) % 3;
			if (mod == 1) {
				Calendar date = Calendar.getInstance();
				long t= date.getTimeInMillis();
				Date delay=new Date(t + (1 * 60000 * MINDATETOEND ));
				
				config.put("date_to_end",tudelft.dds.irep.utils.Utils.getTimestamp(delay));
			} else if (mod == 2) {
				config.put("max_exposures", 10);
			}
			
			//distributions
			JSONObject distcontrol = new JSONObject();
			distcontrol.put("treatment", "control");
			distribution.put(distcontrol);
			JSONObject distreatment = new JSONObject();
			distreatment.put("treatment", "treatment");
			distribution.put(distreatment);
			
			mod = Integer.parseInt(userid) % 5;			
			if (mod == 0) {
				distcontrol.put("segments", 50);
				distreatment.put("segments", 50);
			} else if (mod == 1) {
				distcontrol.put("segments", 80);
				distreatment.put("segments", 20);				
			} else if (mod == 2) {
				distcontrol.put("segments", 20);
				distreatment.put("segments", 80);				
			}  else if (mod == 3) {
				distcontrol.put("segments", 90);
				distreatment.put("segments", 10);				
			}   else if (mod == 4) {
				distcontrol.put("segments", 10);
				distreatment.put("segments", 90);				
			}  
			experiment.put("treatment", treatments);
			experiment.put("config", configurations);
			return experiment;
		}
}
