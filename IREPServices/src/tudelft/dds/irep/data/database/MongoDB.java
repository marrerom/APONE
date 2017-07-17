package tudelft.dds.irep.data.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import tudelft.dds.irep.data.schema.Experiment;
import tudelft.dds.irep.data.schema.Treatment;

import static com.mongodb.client.model.Filters.*;
import org.bson.types.ObjectId;


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
	
	public String AddExperiment(Experiment experiment) {
	
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		
		Map<String, Object> docmap = new HashMap<String,Object>();
		docmap.put("name", experiment.getName());
		docmap.put("experimenter", experiment.getExperimenter());
		docmap.put("description", experiment.getDescription());
		docmap.put("unit", experiment.getUnit());
		docmap.put("control_treatment", experiment.getControl_treatment());
		
		docmap.put("configuration", new ArrayList<>());
		
		List<Map<String,Object>> treatmentlist = new ArrayList<>();
		
		for (Treatment t: experiment.getTreatment()) {
			Map<String,Object> treatmentdoc = new HashMap<String, Object>();
			treatmentdoc.put("name", t.getName());
			treatmentdoc.put("description", t.getDescription());
			treatmentdoc.put("definition", t.getDefinition());
			treatmentlist.add(treatmentdoc);
		}
		docmap.put("treatment", treatmentlist);
				
		Document doc = new Document(docmap);
		coll.insertOne(doc);
		return doc.getObjectId("_id").toString();
	}
	
	public String GetYAML(String idexp) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		return coll.find(eq("_id",new ObjectId(idexp))).first().getString("yaml");
	}
	
	
	public void test(){
//		MongoDatabase database = mongoClient.getDatabase("irep");
//		MongoCollection<Document> coll = database.getCollection("experiment");

//		mongo.
//		MongoDatabase db = mongo.getDatabase("test");
//		MongoCollection<> table = db.getCollection("user");
	}


}
