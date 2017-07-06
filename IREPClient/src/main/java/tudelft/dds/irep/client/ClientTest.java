package tudelft.dds.irep.client;

import java.io.IOException;
import java.nio.file.Paths;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

//import com.google.common.net.MediaType;


public class ClientTest {

	   static Client client = ClientBuilder.newClient();
	   	   
	   public static Response testYAMLRepository_upload_definition(String contextPath) throws IOException{
		   String nsName = "demo_namespace.yaml";
		   //String path = Paths.get("", nsName + ".yaml").toFile().toString();
		   String path = contextPath + "/"+ nsName;
		   String yaml = Utils.readStrFile(path);
		   
		   
		   Form form = new Form();
		   form.param("yamlFile", yaml);
	   
		   
		   Response res = client
				   .target("http://localhost:8080/planout4jTestJax/yamlrepository/definition/upload")
				   .request("text/plain")
				   .post(Entity.entity(form, "application/x-www-form-urlencoded"));
		   
		   return res;
	   }
	
}
