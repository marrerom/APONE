package tudelft.dds.irep.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
		public static Map<String, String> experiments = new HashMap<String, String>();
		final static String localhost = "http://localhost:8080"; 
		final static String jerseyServices = "service";
		
		@Context ServletContext context;
		
		@Path("/setuser/{userid}/{username}")
		@GET 
		public Response setUser(@PathParam("userid") String userid, @PathParam("username") String username, @Context HttpServletRequest request) throws IOException, ParseException {
			if (request.getLocalAddr().equals(request.getRemoteAddr())) { //TODO: check if valid
				ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
				Security.setAuthenticatedUser(request, em, userid, username);
				return Response.ok().build();
			}
			return Response.status(Status.UNAUTHORIZED).build();
		}

		@GET
		public Response test(@Context HttpServletRequest request) {

			ExperimentManager em = (ExperimentManager)context.getAttribute("ExperimentManager");
			try {
				for (int i=1;i<=2;i++) {
					String userid = Integer.toString(i);
					String username = "user"+userid;
					//Security.setAuthenticatedUser(request, em, userid, username);
					HttpClient httpClient = new DefaultHttpClient();
					CookieStore cookieStore = new BasicCookieStore();
					HttpContext httpContext = new BasicHttpContext();
					httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
					HttpResponse resUser = setUser( httpContext, userid, username);
					Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200, "Error: create user call");
					
					HttpResponse resCreate = createExperiment(httpContext, userid, username);
					Preconditions.checkArgument(resCreate.getStatusLine().getStatusCode()==200,"Error: create experiment call");
					String expid = EntityUtils.toString(resCreate.getEntity());

					HttpResponse resCheck = checkExperiment(httpContext, userid, username, expid);
					Preconditions.checkArgument(resCheck.getStatusLine().getStatusCode() == 200, "Error: search experiment call");
					String resultSearch = EntityUtils.toString(resCheck.getEntity()); 
					JSONArray results = new JSONArray(resultSearch);
					Preconditions.checkArgument(results.length() == 1, "Error: search experiment result");
					
					JSONObject rsearch = new JSONObject(results.get(0).toString());
					String idrun = rsearch.get("idrun").toString();
					experiments.put(userid,idrun);
					
					//TODO: start experiment
				}
				
				for (int i=1;i<=2;i++) {
					String userid = Integer.toString(i);
					String username = "user"+userid;
					//Security.setAuthenticatedUser(request, em, userid, username);
					HttpClient httpClient = new DefaultHttpClient();
					CookieStore cookieStore = new BasicCookieStore();
					HttpContext httpContext = new BasicHttpContext();
					httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

					HttpResponse resUser = setUser( httpContext, userid, username);
					Preconditions.checkArgument(resUser.getStatusLine().getStatusCode() == 200, "Error: create user call");

					Boolean valid = false;
					while (!valid) {
						try {
							HttpResponse resAssign = assign(httpContext);
							Preconditions.checkArgument(resAssign.getStatusLine().getStatusCode() == 200, "Error: assign experiment call");
							String redirectionURI = EntityUtils.toString(resAssign.getEntity());
							for (String idrun: experiments.values()) {
								if (redirectionURI.contains(idrun)) {
									valid = true;
									HttpResponse resRedirect = redirect(httpContext,redirectionURI);
									break;
								}
							}
							
						} catch (BadRequestException e) {
							System.out.println("No more experiments to assign to "+userid);
						}
					}

				}
			   
			   //check events
				
			   //setuser(admin)
			   //check monitoring
			   
			   //remove events, experiment, user

			return Response.ok().build();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		public HttpResponse redirect(HttpContext httpContext, String uri) throws ClientProtocolException, IOException {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse res = httpClient.execute(httpGet, httpContext);
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
//			WebTarget base = client.target(localhost+context.getContextPath()+"/"+jerseyServices);
//			WebTarget target = base.path("/test/setuser/"+userid+"/"+username);
//			
//			Invocation.Builder builder = target.request(); //TODO: different requests each time
//			
//			builder.cookie("authuser", "test");
//			
//			Response res = builder.get();
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
				Date onemin=new Date(t + (1 * 60000));
				
				config.put("date_to_end",tudelft.dds.irep.utils.Utils.getTimestamp(onemin));
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
		
		
		
//		public static Response testYAMLForm(Client client) throws IOException{
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/definition/upload");
//			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
//			String nsName = "demo_namespace";
//			String path = Paths.get("resources", nsName + ".yaml").toFile().toString();
//			String yaml = Utils.readStrFile(path);
//			Form form = new Form();
//			form.param("yaml", yaml);
//			Response res = builder.post(Entity.form(form));
//			System.out.println(res);
//			return res;
//		}
		
		
//		public static Response testDefinitionUpdateMultivariate(Client client) throws IOException{
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/definition/upload");
//			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
//			String nsName = "demo_namespace";
//			String path = Paths.get("resources", nsName + ".yaml").toFile().toString();
//			String yaml = Utils.readStrFile(path);
//			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
//			
//			mmap.add("expname", "test experiment");
//			mmap.add("experimenter", "mmarrero");
//			mmap.add("description", "experiment description");
//			mmap.add("yaml", yaml);
//			
//			Response res = builder.post(Entity.form(mmap));
//			System.out.println(res);
//			return res;
//		}
		
//		public static Response testExperimentUpload(Client client) throws IOException {
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/experiment/upload");
//			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
//			String nsName = "demo_namespace";
//			String experimenter = "mmarrero";
//			String description = "desc of demo_namespace";
//			String unit = "user_guid";
//
//			String t_name1 = "exp_default";
//			String t_definition1 = "page_size = 50;full_details = false;";
//			String t_description1 = "treatment desc 1";
//			
//			//add: ,\"control\":true
//			
//			String treatment1 =	"{\"name\":\""+t_name1+"\",\"definition\":\""+t_definition1+"\","
//					+ "\"description\":\""+t_description1+"\",\"control\":true}"; //check true value
//			
//			String t_name2 = "exp_page_size";
//			String t_definition2 = "page_size = uniformChoice(choices = [15, 20, 30], unit = user_guid);";
//			String t_description2 = "treatment desc 2";
//			
//			String treatment2 =	"{\"name\":\""+t_name2+"\",\"definition\":\""+t_definition2+"\","
//					+ "\"description\":\""+t_description2+"\"}";		
//
//			String t_name3 = "exp_everything";
//			String t_definition3 = " full_details = bernoulliTrial(p=0.3, unit=user_guid);" + 
//					"      if (full_details) {" + 
//					"        page_size = weightedChoice(choices = [10, 15, 20], weights = [0.25, 0.5, 0.25], unit = user_guid);" + 
//					"      } else {" + 
//					"        page_size = weightedChoice(choices = [20, 25, 30], weights = [0.25, 0.5, 0.25], unit = user_guid);" + 
//					"      }";
//			String t_description3 = "treatment desc 3";
//			
//			String treatment3 =	"{\"name\":\""+t_name3+"\",\"definition\":\""+t_definition3+"\","
//					+ "\"description\":\""+t_description3+"\"}";		
//
//			
//			
//			String experiment = "{\"name\":\""+nsName+"\",\"experimenter\":\""+experimenter+"\","
//					+ "\"description\":\""+description+"\",\"unit\":\""+unit+"\","
//							+ "\"treatment\":["+treatment1+","+treatment2+","+treatment3+"]}";
//			
//			
//			Response res = builder.post(Entity.entity(experiment, MediaType.APPLICATION_JSON));
//			System.out.println(res);
//			return res;
//
//		}
//		
//		public static Response testExperimentStart(Client client, String idexp) throws IOException {
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/experiment/start");
//			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
//			//String idexp = "596e37362ada0137fbae2994";
//			String name = "config_demo1";
//			String experimenter = "mmarrero";
//			String description = "conf of demo_namespace";
//			String controller_code = "desc of code used";
//			String run = "ON";
//			
//
//			String segments1 = "50";
//			String treatment1 = "exp_page_size";
//			String action1 = "ADD";
//			
//			String dist1 = "{\"segments\":\""+segments1+"\",\"treatment\":\""+treatment1+"\","
//					+ "\"action\":\""+action1+"\"}";
//			
//			
//			String segments2 = "50";
//			String treatment2 = "exp_everything";
//			String action2 = "ADD";
//			
//			String dist2 = "{\"segments\":\""+segments2+"\",\"treatment\":\""+treatment2+"\","
//					+ "\"action\":\""+action2+"\"}";
//			
//			
//			String configuration = "{\"name\":\""+name+"\",\"experimenter\":\""+experimenter+"\","
//					+ "\"description\":\""+description+"\",\"controller_code\":\""+controller_code+"\","
//							+ "\"run\":\""+run+"\",\"test\":false,\"distribution\":["+dist1+","+dist2+"]}";
//			
//			FormDataMultiPart mmap = new FormDataMultiPart();
//			FormDataBodyPart part1 = new FormDataBodyPart("idexp", idexp);
//			FormDataBodyPart part2 = new FormDataBodyPart("configuration", configuration, MediaType.APPLICATION_JSON_TYPE);
//			mmap.bodyPart(part1);
//			mmap.bodyPart(part2);
//			Response res = builder.post(Entity.entity(mmap,mmap.getMediaType()));
//			System.out.println(res);
//			return res;
//
//		}
//		
//
//		public static Response testGet(Client client) {
//		   WebTarget base = client.target(context+jerseyServices);
//		   WebTarget target =base.path("/get");
//		   Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
//		   Response res = builder.get(); //also builder.get(String.class) to return only the string
//		   System.out.println(res);
//		   return res;
//	   }
//		
//		
//		public static Response testExperimentGetParams(Client client, String idconfig, String idunit, String overrides){
//			WebTarget base = client.target(context+jerseyServices);
//			Date timestamp = new Date();
//			WebTarget target =base.path("/event/timestampFormat");
//			Invocation.Builder builder = target.request();
//			String format = builder.get(String.class);
//			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
//			String timestampFormatted = datetarget.format(timestamp);
//			
//			
//			target =base.path("/experiment/getParams");
//			builder = target.request(MediaType.APPLICATION_JSON);
//			FormDataMultiPart mmap = new FormDataMultiPart();
//			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconfig);
//			FormDataBodyPart part2 = new FormDataBodyPart("idunit", idunit);
//			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted);
//			FormDataBodyPart part4 = new FormDataBodyPart("overrides", overrides);
//			mmap.bodyPart(part1);
//			mmap.bodyPart(part2);
//			mmap.bodyPart(part3);
//			mmap.bodyPart(part4);
//			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
//			System.out.println(res);
//			return res;
//		}
//		
//		public static Response testExperimentStop(Client client, String idconfig){
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/experiment/stop");
//			Invocation.Builder builder = target.request();
//			Response res = builder.put(Entity.text(idconfig));
//			System.out.println(res);
//			return res;
//		}
//	   
//		
//		public static Response testExpStartFromConfig(Client client, String idconfig){
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/experiment/start");
//			Invocation.Builder builder = target.request();
//			Response res = builder.put(Entity.text(idconfig));
//			System.out.println(res);
//			return res;
//		}
//		
//		public static Response testRegisterEvent(Client client, String idconf, String unitid, String etype, String ename, String evalue, String paramvalues) {
//			WebTarget base = client.target(context+jerseyServices);
//			
//			Date timestamp = new Date();
//			WebTarget target =base.path("/event/timestampFormat");
//			Invocation.Builder builder = target.request();
//			String format = builder.get(String.class);
//			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
//			String timestampFormatted = datetarget.format(timestamp);
//
//			
//			FormDataMultiPart mmap = new FormDataMultiPart();
//			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconf);
//			FormDataBodyPart part2 = new FormDataBodyPart("idunit", unitid);
//			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted.toString());
//			FormDataBodyPart part4 = new FormDataBodyPart("etype", etype);
//			FormDataBodyPart part5 = new FormDataBodyPart("ename", ename);
//			FormDataBodyPart part6 = new FormDataBodyPart("evalue", evalue);
//			FormDataBodyPart part7 = new FormDataBodyPart("paramvalues", paramvalues);
//			
//			mmap.bodyPart(part1);
//			mmap.bodyPart(part2);
//			mmap.bodyPart(part3);
//			mmap.bodyPart(part4);
//			mmap.bodyPart(part5);
//			mmap.bodyPart(part6);
//			mmap.bodyPart(part7);
//			
//			target =base.path("/event/register");
//			builder = target.request();
//			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
//			System.out.println(res);
//			return res;
//		}
//		
//	
//		public static Response testRegisterEvent(Client client, String idconf, String unitid, String ename, byte[] evalue) {
//			WebTarget base = client.target(context+jerseyServices);
//			
//			Date timestamp = new Date();
//			String etype = "STRING";
//			
//			WebTarget target =base.path("/event/timestampFormat");
//			Invocation.Builder builder = target.request();
//			String format = builder.get(String.class);
//			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
//			String timestampFormatted = datetarget.format(timestamp);
//
//			
//			FormDataMultiPart mmap = new FormDataMultiPart();
//			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconf);
//			FormDataBodyPart part2 = new FormDataBodyPart("unitid", unitid);
//			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted.toString());
//			FormDataBodyPart part4 = new FormDataBodyPart("etype", etype);
//			FormDataBodyPart part5 = new FormDataBodyPart("ename", ename);
//			FormDataBodyPart part6 = new FormDataBodyPart("evalue", evalue, MediaType.APPLICATION_OCTET_STREAM_TYPE);
//			
//			mmap.bodyPart(part1);
//			mmap.bodyPart(part2);
//			mmap.bodyPart(part3);
//			mmap.bodyPart(part4);
//			mmap.bodyPart(part5);
//			mmap.bodyPart(part6);
//			
//			target =base.path("/event/register");
//			builder = target.request();
//			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
//			System.out.println(res);
//			return res;
//		}
//		
//		
//		public static Response testGetEvent(Client client, String idevent) {
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/event/get" +"/"+idevent);
//			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
//			Response res = builder.get();
//			System.out.println(res);
//			return res;
//		}
//		
//		public static Response testMonitor(Client client, String idconfig) {
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/event/monitor" +"/"+idconfig);
//			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
//			Response res = builder.get();
//			System.out.println(res);
//			return res;
//		}
//		
//		public static Response testSearch(Client client) {
//			WebTarget base = client.target(context+jerseyServices);
//			WebTarget target =base.path("/experiment/search");
//			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
//			Response res = builder.get();
//			System.out.println(res);
//			return res;
//
//		}
//
//		
//		
//		
////	   public static void main(String[] args) throws Exception{
////		   Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
////		   setUser(client, "1", "user1");
////		   
//		   
//		   
////		   Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
////		    
////		   try{
////			   String idnodef ="59e4bf132ada012336b6f884";
////			   String idranking = "59ca668f2ada012eec5088ee";
////			   
//////			   Response resEvent1 = testGetEvent(client, "59db93b62ada013d4b2a4eba"); //json
//////			   String ev1 = resEvent1.readEntity(String.class);
//////			   Response resEvent2 = testGetEvent(client, "59db93b52ada013d4b2a4eb8"); //string
//////			   String ev2 = resEvent2.readEntity(String.class);
//////			   Response resEvent3 = testGetEvent(client, "59db93ad2ada013d4b2a4ea4"); //exposure
//////			   String ev3 = resEvent3.readEntity(String.class);
////			   
////				   for (Integer i=0;i<10;i++){
////					   
////					   Map<String, Object> overrides = new HashMap<String,Object>();
////					   //overrides.put("a", 11);
////					   String unitid = String.valueOf(i);
////					   Response res3 = testExperimentGetParams(client, idnodef, unitid, JSONObject.toJSONString(overrides));
////					   String params = res3.readEntity(String.class);
////					   System.out.println("Unit "+unitid +" Params "+params);
////					   
////					   testRegisterEvent(client, idnodef, i.toString(),"JSON", "rankingtestparam_json", "{\"test\":0, \"param\":1, \"value\":\"dos\"}",params);
////					   testRegisterEvent(client, idnodef, i.toString(),"STRING", "rankingtestparam_string", "test param",params);
////				   }
//////		   		
//////		   		
//////				   
//////				Response res4 = testExperimentStop(client, idconf1);
//////				
////////				   for (int i=0;i<2;i++){
////////					   String unitid = String.valueOf(i);
////////					   Response res5 = testExperimentGetParams(client, idconf1, unitid);
////////					   String params = res5.readEntity(String.class);
////////					   System.out.println("Exp "+ idexp1 + " Run " +idconf1 +" Unit "+unitid +" Params "+params);
////////				   }
//////
//////
//////				Response res6 = testExpStartFromConfig(client,idconf1);
//////		   
////////			   String idconf = "5979e0152ada012b399e52b8";
////////			   String idexp ="";
//////			   for (int i=0;i<2;i++){
//////				   String unitid = String.valueOf(i);
//////				   Response res7 = testExperimentGetParams(client, idconf1, unitid);
//////				   String params = res7.readEntity(String.class);
//////				   System.out.println("Exp "+ idexp1 + " Run " +idconf1 +" Unit "+unitid +" Params "+params);
//////			   }
//////			   
//////			   String idconf = "599be9062ada0113360132a4";
//////			   //string event
//////			   Response res8 = testRegisterEvent(client, idconf, "0", "testparam", "test param value");
//////			   String idevent = res8.readEntity(String.class);
//////
//////			   //binary event
//////			   byte[] evalue = Utils.readSmallBinaryFile("/home/mmarrero/Downloads/example1.png");
//////			   Response res9 = testRegisterEvent(client, idconf, "0", "testparam", evalue);
//////			   res9.readEntity(String.class);
//////			   
////////			   Response res10 = testGetEvent(client, idevent);
////////				ObjectMapper mapper = new ObjectMapper();
////////				JEvent jevent =  mapper.readValue(new StringReader(res10.readEntity(String.class)),JEvent.class);
////////				if (jevent.isBinary())
////////					Utils.writeSmallBinaryFile(java.util.Base64.getDecoder().decode(jevent.getEvalue()), "test.png");
////////
////////			   
////////
////////			   
////////				Response res11 = testMonitor(client, idconf1);
////////				String expcount = res11.readEntity(String.class);
////////
////			   
////			   client.close();
////		   } catch (Exception e){
////			   client.close();
////			   System.out.println(e.getCause().getMessage());
////			   throw e;
////		   }
////		   
////	   }
//	
}
