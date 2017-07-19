package tudelft.dds.irep.client;

import java.io.IOException;
import java.nio.file.Paths;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;



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
		
		public static Response testDefinitionUpdateJson(Client client) throws IOException {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/definition/upload");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String nsName = "demo_namespace";
			String experimenter = "mmarrero";
			String description = "desc of demo_namespace";
			String unit = "user_guid";
			String control_treatment = "exp_default";

			String t_name1 = "exp_default";
			String t_definition1 = "page_size = 50;full_details = false;";
			String t_description1 = "treatment desc 1";
			
			String treatment1 =	"{\"name\":\""+t_name1+"\",\"definition\":\""+t_definition1+"\","
					+ "\"description\":\""+t_description1+"\"}";
			
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
							+ "\"control_treatment\":\""+control_treatment+"\",\"treatment\":["+treatment1+","+treatment2+","+treatment3+"]}";
			
			
			Response res = builder.post(Entity.entity(experiment, MediaType.APPLICATION_JSON));
			System.out.println(res);
			return res;

		}
		
		public static Response testConfigurationStart(Client client) throws IOException {
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/configuration/start");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String idexp = "596e37362ada0137fbae2994";
			String name = "config_demo1";
			String experimenter = "mmarrero";
			String description = "conf of demo_namespace";
			String controller_code = "desc of code used";
			String status = "on";
			String test = "off";

			String segments1 = "50";
			String treatment1 = "exp_page_size";
			String action1 = "add";
			
			String dist1 = "{\"segments\":\""+segments1+"\",\"treatment\":\""+treatment1+"\","
					+ "\"action\":\""+action1+"\"}";
			
			
			String segments2 = "50";
			String treatment2 = "exp_everything";
			String action2 = "add";
			
			String dist2 = "{\"segments\":\""+segments2+"\",\"treatment\":\""+treatment2+"\","
					+ "\"action\":\""+action2+"\"}";
			
			
			String configuration = "{\"name\":\""+name+"\",\"experimenter\":\""+experimenter+"\","
					+ "\"description\":\""+description+"\",\"controller_code\":\""+controller_code+"\","
							+ "\"status\":\""+status+"\",\"\"test\":\""+test+"\",\"distribution\":["+dist1+","+dist2+"]}";
			
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			
			mmap.add("idexp", idexp);
			mmap.add("configuration", configuration);
			
			Response res = builder.post(Entity.form(mmap));
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
		
		public static Response testConfigurationStart(Client client, String idexp){
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/configuration/start");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			mmap.add("idexp", idexp);
			Response res = builder.post(Entity.form(mmap));
			System.out.println(res);
			return res;
		}
		
		public static Response testRunGetParams(Client client, String idrun, String idunit){
			WebTarget base = client.target(context+jerseyServices);
			WebTarget target =base.path("/run/getParams");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			mmap.add("idrun", idrun);
			mmap.add("idunit", idunit);
			Response res = builder.post(Entity.form(mmap));
			System.out.println(res);
			return res;
		}
	   
	   public static void main(String[] args) throws Exception{
		   Client client = ClientBuilder.newClient();
		   try{
		   Response res1 =  testDefinitionUpdateJson(client);
		   String idexp = res1.readEntity(String.class);
		   
//		   Response res2 = testConfigurationStart(client, idexp);
//		   String idrun = res2.readEntity(String.class);
//		   
//		   for (int i=0;i<5;i++){
//			   String unitid = String.valueOf(i);
//			   Response res3 = testRunGetParams(client, idrun, unitid);
//			   String params = res3.readEntity(String.class);
//			   System.out.println("Exp "+ idexp + " Run " +idrun +" Unit "+unitid +" Params "+params);
//		   }
		   
		   client.close();
		   } catch (Exception e){
			   client.close();
			   throw e;
		   }
		   
	   }
	
}
