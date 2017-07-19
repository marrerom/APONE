package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;

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
	
	public String addExperiment(JExperiment experiment) {
	
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		
		Map<String, Object> docmap = experiment.getDocmap();
//		docmap.put("name", experiment.getName());
//		docmap.put("experimenter", experiment.getExperimenter());
//		docmap.put("description", experiment.getDescription());
//		docmap.put("unit", experiment.getUnit());
//		docmap.put("control_treatment", experiment.getControl_treatment());
//		
//		docmap.put("configuration", new ArrayList<>());
//		
//		List<Map<String,Object>> treatmentlist = new ArrayList<>();
//		
//		for (JTreatment t: experiment.getTreatment()) {
//			Map<String,Object> treatmentdoc = new HashMap<String, Object>();
//			treatmentdoc.put("name", t.getName());
//			treatmentdoc.put("description", t.getDescription());
//			treatmentdoc.put("definition", t.getDefinition());
//			treatmentlist.add(treatmentdoc);
//		}
//		docmap.put("treatment", treatmentlist);
		ObjectId idexp = new ObjectId();
		docmap.put("_id", idexp);			
		Document doc = new Document(docmap);
		coll.insertOne(doc);
		return idexp.toString();
	}
	
	public String addExpConfig(String idexp, JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		Map<String, Object> docmap = conf.getDocmap();
		ObjectId idrun = new ObjectId();
		docmap.put("_id", idrun);
		Document doc = new Document(docmap);
		coll.updateOne(eq("_id", new ObjectId(idexp)), Updates.addToSet("config", doc));
		return idrun.toString();
	}
	
	public Date addExpConfigDateStart(JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		Date now = new Date();
		coll.updateOne(eq("configuration._id", new ObjectId(conf.get_Id())), Updates.push("date_started", now));
		return now;
	}

	public Date addExpConfigDateEnd(JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		Date now = new Date();
		coll.updateOne(eq("configuration._id", new ObjectId(conf.get_Id())), Updates.push("date_ended", now));
		return now;
	}
	
	public void setExpConfigRunStatus(JConfiguration conf, Status st) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		coll.updateOne(eq("configuration._id", new ObjectId(conf.get_Id())), Updates.set("run", st));
	}
		
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection("experiment");
		Document doc = coll.find(eq("idexp", new ObjectId(idexp))).first();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(doc.toJson()),JExperiment.class);
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
