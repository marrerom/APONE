package tudelft.dds.irep.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters.*;


public class MongoDB implements Database {
	
	public final String DB = "test"; //irep in production
	public final String EXP_COL = "experiment";
	public final String EVENT_COL = "event";

	private MongoClient mongo;
	
	public MongoDB(String host, int port, String db, String user, char[] pwd){
		MongoCredential credential = MongoCredential.createCredential(user, db, pwd);
		mongo = new MongoClient(new ServerAddress( host , port ), Arrays.asList(credential));
	}
	
	public void close(){
		mongo.close();
	}
	
	public String AddExperiment(String expname, String experimenter, String description, String yaml) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		
		Map<String, Object> docmap = new HashMap<String,Object>();
		docmap.put("name", expname);
		docmap.put("experimenter", experimenter);
		docmap.put("description", description);
		docmap.put("yaml", yaml);

		Document doc = new Document(docmap);
		coll.insertOne(doc);
		return doc.getObjectId("_id").toString();
	}
	
	public String GetYAML(String idexp) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		
		coll.find(eq("_id",idexp));
		
	}
	
	
	public void test(){
//		MongoDatabase database = mongoClient.getDatabase("irep");
//		MongoCollection<Document> coll = database.getCollection("experiment");

//		mongo.
//		MongoDatabase db = mongo.getDatabase("test");
//		MongoCollection<> table = db.getCollection("user");
	}


}
