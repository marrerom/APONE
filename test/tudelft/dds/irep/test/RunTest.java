package tudelft.dds.irep.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.experiment.ExperimentManager;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.Utils;



@Path("/test")
public class RunTest {
	
		final Integer MINDATETOEND = 2; //experiments with userid % 3 = 1, will be running for those minutes
	
		public static Map<String, String> user2idrun = new HashMap<String, String>(); //username - idrun
		public static Map<String, String> idrun2user = new HashMap<String, String>(); //idrun - username
		public static Map<String, Long> user2endtime = new HashMap<String, Long>(); //username - endtime
		public static Map<String, Integer> user2maxexp = new HashMap<String, Integer>(); //username - maxexposures
		public static Map<String, String> idname2name = new HashMap<String, String>(); //username - user idname
		public static Map<String, Integer> idrun2exp = new HashMap<String, Integer>(); //idrun - exposures
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
		@Path("/{timebeforetest}")
		public Response test(@PathParam("timebeforetest") String timebeforetest, @Context HttpServletRequest request) throws ClientProtocolException, IOException {
			Response response;
			final Integer NUSERS = 120;
			final Integer NEXPS = 10;
			Integer sleep;
			try {
				sleep = Integer.parseInt(timebeforetest);
			} catch (Exception e) {
				sleep = 60000;
			}
			try {
				for (int i=1;i<=NUSERS;i++) {
					System.out.println("Creating experiment user "+i);
					createExperiment(Integer.toString(i));
				}
				for (int nexp=1;nexp<=NEXPS;nexp++) {
					for (int i=1;i<=NUSERS;i++) {
						System.out.println("Assigning experiment of user "+i);
						assignExperiment(Integer.toString(i));
					}
				}
				Thread.sleep(sleep); //to prevent events not registered by rabbit during the following check of monitoring events
				for (int i=1;i<=NUSERS;i++) {
						System.out.println("Testing events experiment user "+i);
						testEvents(Integer.toString(i));
					}
				response = Response.ok("Test completed", MediaType.TEXT_PLAIN).build();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				throw new InternalServerException(e.getMessage());
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
			String idname = tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resUser, "Experiment set-up Error: create user");
			idname2name.put(userid, idname);
			username = idname;
			
			HttpResponse resCreate = createExperiment(httpContext, userid, username);
			String expid = tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resCreate, "Experiment set-up Error: create experiment");

			HttpResponse resCheck = checkExperiment(httpContext, userid, username, expid);
			String resultSearch = tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resCheck, "Experiment set-up Error: search");
			JSONArray results = new JSONArray(resultSearch);
			Assert.assertTrue("Experiment set-up Error: search experiment", results.length() == 1);
			
			JSONObject rsearch = new JSONObject(results.get(0).toString());
			String idrun = rsearch.get("idrun").toString();
			user2idrun.put(username,idrun);
			idrun2user.put(idrun,username);	
			HttpResponse resStart = start(httpContext, idrun);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resStart, "Experiment set-up Error: start");
		}
		
		public void assignExperiment(String userid) throws ClientProtocolException, IOException {
			String username = idname2name.get(userid);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

			HttpResponse resUser = setUser( httpContext, userid, username);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resUser, "Assignment: set user");
			try {
				Boolean assigned = false;
				do {
					HttpResponse resAssign = assign(httpContext);
					String redirectionURI = tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Assignment: assign")), resAssign, "Assignment: assign");
					for (String idrun: user2idrun.values()) {
						if (redirectionURI.contains(idrun)) {
							HttpResponse resSetrun = setIdrun(httpContext, idrun);
							tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resSetrun, "Assignment: set idrun");
							try {
								HttpResponse resRedirect = redirect(httpContext,redirectionURI);
								tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Assignment: redirect")), resRedirect, "Assignment: redirect");
								assigned = true;
								break;
							} catch (BadRequestException e) {
								String expuser = idrun2user.get(idrun);
								finishedExperiment(idrun);
							}
						}
					} 
				} while (!assigned);
			} catch (BadRequestException e) {
				System.out.println("No more experiments to assign to "+userid);
			}
		}
		
		public void testEvents(String userid) throws ClientProtocolException, IOException {
			String username = idname2name.get(userid);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			cookieStore.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("username", username));
			HttpResponse resUser = setUser( httpContext, userid, username);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resUser, "Events: set user");
			event ("exposure", httpContext, userid, username);
			event ("search", httpContext, userid, username);
			event ("click", httpContext, userid, username);
			event ("binarytest1", httpContext, userid, username);
			event ("binarytest2", httpContext, userid, username);
			event ("completed", httpContext, userid, username);
		}
		
		public void clear(String userid) throws ClientProtocolException, IOException {
			String username = idname2name.get(userid);
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			cookieStore.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("username", username));
			HttpResponse resUser = setMasterUser( httpContext);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resUser, "Clear: set user");
			HttpResponse resDeleteUser = deleteUser( httpContext, username);
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resDeleteUser, "Clear: delete user");
		}
		
		public void event(String ename, HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			
			HttpResponse resCheckEvents = checkEvents(ename, httpContext, username);
			String expevents = tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resCheckEvents, "Events: check");
			
			JSONArray arrayevents = new JSONArray(expevents);
			Long distinct = arrayevents.toList().stream().map(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.get("idunit");}).distinct().count();
			
			if (arrayevents.length() > 0 && (ename.equals("binarytest1") || ename.equals("binarytest2"))){
				JSONObject event = (JSONObject) arrayevents.get(0);
				String encodedValue = event.get("evalue").toString();
				String original = new String(Utils.decodeBinary(encodedValue), StandardCharsets.UTF_8);
				Assert.assertTrue("Event "+ename+" : Contents", original.equals("mycontents"));
			}
			
			JSONArray eventids = new JSONArray(arrayevents.toList().stream().map(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.get("_id");}).toArray());
			HttpResponse resDownloadJSON = getFile(httpContext, eventids, "JSON");
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resDownloadJSON, "Events: download JSON");

			HttpResponse resDownloadCSV = getFile(httpContext, eventids, "CSV");
			tudelft.dds.irep.utils.Utils.checkWebResponse(Collections.emptySet(), resDownloadCSV, "Events: download CSV");
						
			try {
				HttpResponse resMonitor = monitor(ename, httpContext, userid, username);
				String exposures = tudelft.dds.irep.utils.Utils.checkWebResponse(Arrays.asList(new BadRequestException("Events: monitor")), resMonitor, "Events: monitor");
				JSONObject results = new JSONObject(exposures);
				JSONArray treatments = results.getJSONArray("treatments");
				Integer differentExposuresMonitor = treatments.toList().stream().mapToInt(p->{JSONObject pjson = new JSONObject((HashMap<String, Object>)p);return pjson.getInt("value");}).sum();
				if (ename.equals(JEvent.EXPOSURE_ENAME.toString()) || ename.equals(JEvent.COMPLETED_ENAME.toString())) {
					Assert.assertTrue("Events "+ename+": number of monitored events does not match with number of DISTINCT actual events", distinct == (long)differentExposuresMonitor);
				} else {
					if (arrayevents.length() != differentExposuresMonitor)
						System.out.println(arrayevents.length() +" "+ differentExposuresMonitor);
					Assert.assertTrue("Events "+ename+": number of monitored events does not match with the actual events", arrayevents.length() == differentExposuresMonitor);
				}
			} catch (BadRequestException e) {
				finishedExperiment(user2idrun.get(username));
			}
			
		}
		
		public static void finishedExperiment(String idrun) {
			String username = idrun2user.get(idrun);
			Long endtime = user2endtime.get(username);
			if (endtime != null) {
				Assert.assertNotNull(endtime);
				Calendar now = Calendar.getInstance();
				Assert.assertTrue("Finished experiment: test deadline", now.getTimeInMillis() >= endtime);
			} else {
				Integer maxexp = user2maxexp.get(username);
				Assert.assertTrue("Finisthed experiment: test max exposures", idrun2exp.get(idrun) >= maxexp);
			}
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
			return res;		
		}
		
		public HttpResponse checkEvents(String ename, HttpContext httpContext, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(localhost+context.getContextPath()+"/"+jerseyServices+"/event/search");
			JSONObject event = getEventFilter(ename, user2idrun.get(username));
			StringEntity entity = new StringEntity(event.toString());
		    httpPost.setEntity(entity);
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", "application/json");
		    HttpResponse res = httpClient.execute(httpPost, httpContext);
			return res;		
		}
		
		public HttpResponse monitor(String ename, HttpContext httpContext, String userid, String username) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(localhost+context.getContextPath()+"/"+jerseyServices+"/experiment/monitor/subtreatments/"+user2idrun.get(username)+"/"+ename);
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
				control.put("url", "http://localhost:8080/APONE/service/test/client?rankingAlg=default&linkColor=blue");
				treatment.put("url", "http://localhost:8080/APONE/service/test/client?rankingAlg=default&linkColor=green");
			} else {
				control.put("url", "http://localhost:8080/APONE/service/test/client");
				control.put("definition", "rankingAlg=\"default\";linkColor=\"blue\";");
				treatment.put("url", "http://localhost:8080/APONE/service/test/client");
				treatment.put("definition", "rankingAlg=\"default\";linkColor=\"green\";");
			}
			
			//exp with nothing, date to end or completed units
			mod = Integer.parseInt(userid) % 3;
			if (mod == 1) {
				Calendar date = Calendar.getInstance();
				long t= date.getTimeInMillis();
				Date delay=new Date(t + (1 * 60000 * MINDATETOEND ));
				
				config.put("date_to_end",tudelft.dds.irep.utils.Utils.getTimestamp(delay));
				Calendar now = Calendar.getInstance();
				now.add(Calendar.MINUTE, MINDATETOEND);
				user2endtime.put(username, now.getTimeInMillis());
			} else if (mod == 2) {
				config.put("max_exposures", 10);
				user2maxexp.put(username, 10);
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
