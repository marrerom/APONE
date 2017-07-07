package tudelft.dds.irep.client;

import java.io.IOException;
import java.net.URLEncoder;
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
		
		
		public static Response testYAMLForm(Client client) throws IOException{
			WebTarget base = client.target(context);
			WebTarget target =base.path("/repository/upload");
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
		
		
		public static Response testRepositoryUpdateMultivariate(Client client) throws IOException{
			WebTarget base = client.target(context);
			WebTarget target =base.path("/repository/upload");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			String nsName = "demo_namespace";
			String path = Paths.get("resources", nsName + ".yaml").toFile().toString();
			String yaml = Utils.readStrFile(path);
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			//String enc = URLEncoder.encode(yaml, "UTF-8");
			mmap.add("yaml", yaml);
			Response res = builder.post(Entity.form(mmap));
			System.out.println(res);
			return res;
		}

		public static Response testGet(Client client) {
		   WebTarget base = client.target(context);
		   WebTarget target =base.path("/get");
		   Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		   Response res = builder.get(); //also builder.get(String.class) to return only the string
		   System.out.println(res);
		   return res;
	   }
		
		public static Response testConfigurationStart(Client client, String idexp){
			WebTarget base = client.target(context);
			WebTarget target =base.path("/configuration/start");
			Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
			MultivaluedMap<String, String> mmap = new MultivaluedHashMap<String, String>();
			mmap.add("idexp", idexp);
			Response res = builder.post(Entity.form(mmap));
			System.out.println(res);
			return res;
		}
		
		public static Response testRunGetParams(Client client, String idrun, String idunit){
			WebTarget base = client.target(context);
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
		   //testGet(client);
		   Response res1 =  testRepositoryUpdateMultivariate(client);
		   String idexp = res1.readEntity(String.class);
		   
		   Response res2 = testConfigurationStart(client, idexp);
		   String idrun = res2.readEntity(String.class);
		   
		   for (int i=0;i<5;i++){
			   String unitid = String.valueOf(i);
			   Response res3 = testRunGetParams(client, idrun, unitid);
			   String params = res3.readEntity(String.class);
			   System.out.println("Exp "+ idexp + " Run " +idrun +" Unit "+unitid +" Params "+params);
		   }
		   
		   client.close();
		   } catch (Exception e){
			   client.close();
			   throw e;
		   }
		   
	   }
	
}
