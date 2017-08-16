package tudelft.dds.irep.client;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.databind.ObjectMapper;

import tudelft.dds.irep.data.schema.JEvent;



//import com.google.common.net.MediaType;


public class ClientTest {
	
		final static String context = "http://localhost:8080/IREPServices/"; 
		final static String jerseyServices = "service";
		
		
		public static Response testYAMLForm(Client client) throws IOException{
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/definition/upload");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String nsName = "demo_namespace";
			String path = Paths.get("resources", nsName + ".yaml").toFile().toString();
			String yaml = Utils.readStrFile(path);
			Form form = new Form();
			form.param("yaml", yaml);
			Response res = builder.post(Entity.form(form));
			System.out.println(res);
			return res;
		}
		
		
		public static Response testDefinitionUpdateMultivariate(Client client) throws IOException{
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/definition/upload");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String nsName = "demo_namespace";
			String path = Paths.get("resources", nsName + ".yaml").toFile().toString();
			String yaml = Utils.readStrFile(path);
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			
			mmap.add("expname", "test experiment");
			mmap.add("experimenter", "mmarrero");
			mmap.add("description", "experiment description");
			mmap.add("yaml", yaml);
			
			Response res = builder.post(Entity.form(mmap));
			System.out.println(res);
			return res;
		}
		
		public static Response testExperimentUpload(Client client) throws IOException {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/experiment/upload");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String nsName = "demo_namespace";
			String experimenter = "mmarrero";
			String description = "desc of demo_namespace";
			String unit = "user_guid";

			String t_name1 = "exp_default";
			String t_definition1 = "page_size = 50;full_details = false;";
			String t_description1 = "treatment desc 1";
			
			//add: ,\"control\":true
			
			String treatment1 =	"{\"name\":\""+t_name1+"\",\"definition\":\""+t_definition1+"\","
					+ "\"description\":\""+t_description1+"\",\"control\":true}"; //check true value
			
			String t_name2 = "exp_page_size";
			String t_definition2 = "page_size = uniformChoice(choices = [15, 20, 30], unit = user_guid);";
			String t_description2 = "treatment desc 2";
			
			String treatment2 =	"{\"name\":\""+t_name2+"\",\"definition\":\""+t_definition2+"\","
					+ "\"description\":\""+t_description2+"\"}";		

			String t_name3 = "exp_everything";
			String t_definition3 = " full_details = bernoulliTrial(p=0.3, unit=user_guid);" + 
					"      if (full_details) {" + 
					"        page_size = weightedChoice(choices = [10, 15, 20], weights = [0.25, 0.5, 0.25], unit = user_guid);" + 
					"      } else {" + 
					"        page_size = weightedChoice(choices = [20, 25, 30], weights = [0.25, 0.5, 0.25], unit = user_guid);" + 
					"      }";
			String t_description3 = "treatment desc 3";
			
			String treatment3 =	"{\"name\":\""+t_name3+"\",\"definition\":\""+t_definition3+"\","
					+ "\"description\":\""+t_description3+"\"}";		

			
			
			String experiment = "{\"name\":\""+nsName+"\",\"experimenter\":\""+experimenter+"\","
					+ "\"description\":\""+description+"\",\"unit\":\""+unit+"\","
							+ "\"treatment\":["+treatment1+","+treatment2+","+treatment3+"]}";
			
			
			Response res = builder.post(Entity.entity(experiment, MediaType.APPLICATION_JSON));
			System.out.println(res);
			return res;

		}
		
		public static Response testExperimentStart(Client client, String idexp) throws IOException {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/experiment/start");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			//String idexp = "596e37362ada0137fbae2994";
			String name = "config_demo1";
			String experimenter = "mmarrero";
			String description = "conf of demo_namespace";
			String controller_code = "desc of code used";
			String run = "ON";
			

			String segments1 = "50";
			String treatment1 = "exp_page_size";
			String action1 = "ADD";
			
			String dist1 = "{\"segments\":\""+segments1+"\",\"treatment\":\""+treatment1+"\","
					+ "\"action\":\""+action1+"\"}";
			
			
			String segments2 = "50";
			String treatment2 = "exp_everything";
			String action2 = "ADD";
			
			String dist2 = "{\"segments\":\""+segments2+"\",\"treatment\":\""+treatment2+"\","
					+ "\"action\":\""+action2+"\"}";
			
			
			String configuration = "{\"name\":\""+name+"\",\"experimenter\":\""+experimenter+"\","
					+ "\"description\":\""+description+"\",\"controller_code\":\""+controller_code+"\","
							+ "\"run\":\""+run+"\",\"test\":false,\"distribution\":["+dist1+","+dist2+"]}";
			
			FormDataMultiPart mmap = new FormDataMultiPart();
			FormDataBodyPart part1 = new FormDataBodyPart("idexp", idexp);
			FormDataBodyPart part2 = new FormDataBodyPart("configuration", configuration, MediaType.APPLICATION_JSON_TYPE);
			mmap.bodyPart(part1);
			mmap.bodyPart(part2);
			Response res = builder.post(Entity.entity(mmap,mmap.getMediaType()));
			System.out.println(res);
			return res;

		}
		

		public static Response testGet(Client client) {
		   WebTarget base = client.target(context+jerseyServices);
		   WebTarget target =base.path("/get");
		   Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		   Response res = builder.get(); //also builder.get(String.class) to return only the string
		   System.out.println(res);
		   return res;
	   }
		
		
		public static Response testExperimentGetParams(Client client, String idconfig, String idunit){
			WebTarget base = client.target(context+jerseyServices);
			Date timestamp = new Date();
			WebTarget target =base.path("/event/timestampFormat");
			Invocation.Builder builder = target.request();
			String format = builder.get(String.class);
			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
			String timestampFormatted = datetarget.format(timestamp);
			
			
			target =base.path("/experiment/getParams");
			builder = target.request(MediaType.APPLICATION_JSON);
			FormDataMultiPart mmap = new FormDataMultiPart();
			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconfig);
			FormDataBodyPart part2 = new FormDataBodyPart("idunit", idunit);
			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted);
			mmap.bodyPart(part1);
			mmap.bodyPart(part2);
			mmap.bodyPart(part3);
			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
			System.out.println(res);
			return res;
		}
		
		public static Response testExperimentStop(Client client, String idconfig){
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/experiment/stop");
			Invocation.Builder builder = target.request();
			Response res = builder.put(Entity.text(idconfig));
			System.out.println(res);
			return res;
		}
	   
		
		public static Response testExpStartFromConfig(Client client, String idconfig){
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/experiment/start");
			Invocation.Builder builder = target.request();
			Response res = builder.put(Entity.text(idconfig));
			System.out.println(res);
			return res;
		}
		
		public static Response testRegisterEvent(Client client, String idconf, String unitid, String ename, String evalue) {
			WebTarget base = client.target(context+jerseyServices);
			
			Date timestamp = new Date();
			Boolean binary = false;
			
			WebTarget target =base.path("/event/timestampFormat");
			Invocation.Builder builder = target.request();
			String format = builder.get(String.class);
			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
			String timestampFormatted = datetarget.format(timestamp);

			
			FormDataMultiPart mmap = new FormDataMultiPart();
			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconf);
			FormDataBodyPart part2 = new FormDataBodyPart("unitid", unitid);
			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted.toString());
			FormDataBodyPart part4 = new FormDataBodyPart("binary", binary.toString());
			FormDataBodyPart part5 = new FormDataBodyPart("ename", ename);
			FormDataBodyPart part6 = new FormDataBodyPart("evalue", evalue);
			
			mmap.bodyPart(part1);
			mmap.bodyPart(part2);
			mmap.bodyPart(part3);
			mmap.bodyPart(part4);
			mmap.bodyPart(part5);
			mmap.bodyPart(part6);
			
			target =base.path("/event/register");
			builder = target.request();
			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
			System.out.println(res);
			return res;
		}
		
	
		public static Response testRegisterEvent(Client client, String idconf, String unitid, String ename, byte[] evalue) {
			WebTarget base = client.target(context+jerseyServices);
			
			Date timestamp = new Date();
			Boolean binary = true;
			
			WebTarget target =base.path("/event/timestampFormat");
			Invocation.Builder builder = target.request();
			String format = builder.get(String.class);
			DateFormat datetarget = new SimpleDateFormat(format); //Standard format recognized by Jackson
			String timestampFormatted = datetarget.format(timestamp);

			
			FormDataMultiPart mmap = new FormDataMultiPart();
			FormDataBodyPart part1 = new FormDataBodyPart("idconfig", idconf);
			FormDataBodyPart part2 = new FormDataBodyPart("unitid", unitid);
			FormDataBodyPart part3 = new FormDataBodyPart("timestamp", timestampFormatted.toString());
			FormDataBodyPart part4 = new FormDataBodyPart("binary", binary.toString());
			FormDataBodyPart part5 = new FormDataBodyPart("ename", ename);
			FormDataBodyPart part6 = new FormDataBodyPart("evalue", evalue, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			
			mmap.bodyPart(part1);
			mmap.bodyPart(part2);
			mmap.bodyPart(part3);
			mmap.bodyPart(part4);
			mmap.bodyPart(part5);
			mmap.bodyPart(part6);
			
			target =base.path("/event/register");
			builder = target.request();
			Response res = builder.post(Entity.entity(mmap, mmap.getMediaType()));
			System.out.println(res);
			return res;
		}
		
		
		public static Response testGetEvent(Client client, String idevent) {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/event/get" +"/"+idevent);
			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
			Response res = builder.get();
			System.out.println(res);
			return res;
		}
		
		public static Response testMonitor(Client client, String idconfig) {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/event/monitor" +"/"+idconfig);
			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
			Response res = builder.get();
			System.out.println(res);
			return res;
		}
		
		public static Response testSearch(Client client) {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/experiment/search");
			Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
			Response res = builder.get();
			System.out.println(res);
			return res;

		}

		
		
		
	   public static void main(String[] args) throws Exception{
		   Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
		    
		   try{
			   
			   Response res = testSearch(client);
			   String results = res.readEntity(String.class);
			   
//		   		Response res1 =  testExperimentUpload(client);
//		   		String idexp1 = res1.readEntity(String.class);
//		   
//		   		Response res2 = testExperimentStart(client, idexp1);
//		   		String idconf1 = res2.readEntity(String.class);
//
//				   for (int i=0;i<2;i++){
//					   String unitid = String.valueOf(i);
//					   Response res3 = testExperimentGetParams(client, idconf1, unitid);
//					   String params = res3.readEntity(String.class);
//					   System.out.println("Exp "+ idexp1 + " Run " +idconf1 +" Unit "+unitid +" Params "+params);
//				   }
//		   		
//		   		
//				   
//				Response res4 = testExperimentStop(client, idconf1);
//				
////				   for (int i=0;i<2;i++){
////					   String unitid = String.valueOf(i);
////					   Response res5 = testExperimentGetParams(client, idconf1, unitid);
////					   String params = res5.readEntity(String.class);
////					   System.out.println("Exp "+ idexp1 + " Run " +idconf1 +" Unit "+unitid +" Params "+params);
////				   }
//
//
//				Response res6 = testExpStartFromConfig(client,idconf1);
//		   
////			   String idconf = "5979e0152ada012b399e52b8";
////			   String idexp ="";
//			   for (int i=0;i<2;i++){
//				   String unitid = String.valueOf(i);
//				   Response res7 = testExperimentGetParams(client, idconf1, unitid);
//				   String params = res7.readEntity(String.class);
//				   System.out.println("Exp "+ idexp1 + " Run " +idconf1 +" Unit "+unitid +" Params "+params);
//			   }
//			   
//			   //string event
//	//		   Response res8 = testRegisterEvent(client, idconf, "0", "testparam", "test param value");
//	//		   String idevent = res8.readEntity(String.class);
//
//			   //binary event
//			   byte[] evalue = Utils.readSmallBinaryFile("/home/mmarrero/Downloads/example1.png");
//			   Response res9 = testRegisterEvent(client, idconf1, "0", "testparam", evalue);
//			   res9.readEntity(String.class);
//			   
////			   Response res10 = testGetEvent(client, "");
////				ObjectMapper mapper = new ObjectMapper();
////				JEvent jevent =  mapper.readValue(new StringReader(res10.readEntity(String.class)),JEvent.class);
////				if (jevent.isBinary())
////					Utils.writeSmallBinaryFile(java.util.Base64.getDecoder().decode(jevent.getEvalue()), "test.png");
////
////			   
//
//			   
//				Response res11 = testMonitor(client, idconf1);
//				String expcount = res11.readEntity(String.class);
//
			   
			   client.close();
		   } catch (Exception e){
			   client.close();
			   System.out.println(e.getCause().getMessage());
			   throw e;
		   }
		   
	   }
	
}
