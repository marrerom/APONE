package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
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
	MongoCollection<Document> experiments;
	MongoCollection<Document> events;
	
	public MongoDB(String host, int port, String db, String user, char[] pwd){
		MongoCredential credential = MongoCredential.createCredential(user, db, pwd);
		mongo = new MongoClient(new ServerAddress( host , port ), Arrays.asList(credential));
		MongoDatabase database = mongo.getDatabase(DB);
		experiments = database.getCollection(EXP_COL);
		events = database.getCollection(EVENT_COL);
	}
	
	public void close(){
		mongo.close();
	}
	
	
	//TODO: should the db throw an exception when getting or adding data if the experiment/configuration does not exist?
	private Document checkExistExperiment(String idexp) {
		try {
			return experiments.find(eq("_id", new ObjectId(idexp))).first();
		} catch (NullPointerException e) {
			throw new NullPointerException("Experiment "+idexp+" does not exist");
		}
	}

	private Document checkExistConfiguration(String idconf) {
		try {
			Document doc = experiments.find(eq("config._id", new ObjectId(idconf)))
					.projection(com.mongodb.client.model.Projections.elemMatch("config", eq("_id", new ObjectId(idconf))))
					.first();
			return ((ArrayList<Document>)doc.get("config")).get(0);
		} catch (NullPointerException e) {
			throw new NullPointerException("Experiment Configuration "+idconf+" does not exist");
		}
	}
	
	private Document checkExistEvent(String idevent) {
		try {
			return events.find(eq("_id", new ObjectId(idevent))).first();
		} catch (NullPointerException e) {
			throw new NullPointerException("Event "+idevent+" does not exist");
		}
	}

	
	public String addExperiment(JExperiment experiment) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(experiment, Map.class);
		ObjectId idexp = new ObjectId();
		docmap.put("_id", idexp);			
		Document doc = new Document(docmap);
		experiments.insertOne(doc);
		return idexp.toString();
		
	}
	
	public String addExpConfig(String idexp, JConfiguration conf) {
		checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(conf, Map.class);
		ObjectId idrun = new ObjectId();
		docmap.put("_id", idrun);
		Document doc = new Document(docmap);
		experiments.updateOne(eq("_id", new ObjectId(idexp)), Updates.addToSet("config", doc));
		return idrun.toString();
		 
	}
	
	public Date addExpConfigDateStart(JConfiguration conf) {
		checkExistConfiguration(conf.get_id());
		Date now = new Date();
		experiments.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.push("config.$.date_started", now));
		return now;
	}

	public Date addExpConfigDateEnd(JConfiguration conf) {
		checkExistConfiguration(conf.get_id());
		Date now = new Date();
		experiments.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.push("config.$.date_ended", now));
		return now;
	}
	
	public String addEvent(JEvent event) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(event, Map.class);

		if (event.isBinary()) {
			byte[] binvalue = java.util.Base64.getDecoder().decode(event.getEvalue());
			docmap.put("evalue", binvalue);
		}
		ObjectId idevent = new ObjectId();
		docmap.put("_id", idevent);
		Document doc = new Document(docmap);
		events.insertOne(doc);
		return idevent.toString();
	}
	
	public void setExpConfigRunStatus(JConfiguration conf, Status status) {
		checkExistConfiguration(conf.get_id());
		experiments.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.set("config.$.run", status.toString()));
	}
		
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc,JExperiment.class).toJson()),JExperiment.class);
	}
	
	public JEvent getEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistEvent(idevent);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc,JEvent.class).toJson()),JEvent.class);
	}
	

	public JExperiment getExpFromConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		checkExistConfiguration(idconf);
		Document doc = experiments.find(eq("config._id", new ObjectId(idconf))).first();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc, JExperiment.class).toJson()),JExperiment.class);
	}
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistConfiguration(idconf);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc, JConfiguration.class).toJson()),JConfiguration.class);
	}
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<Bson> conditions = new ArrayList<Bson>();
		for (Status st:status) {
			conditions.add(eq("config.run", st.toString()));
		}
		
		List<Bson> condConfig = new ArrayList<Bson>();
		for (Status st:status) {
			condConfig.add(eq("run", st.toString()));
		}

		FindIterable<Document> docs = experiments.find(or(conditions)).projection(com.mongodb.client.model.Projections.elemMatch("config", or(condConfig)));
		List<JConfiguration> result = new ArrayList<JConfiguration>();
		ObjectMapper mapper = new ObjectMapper();
		for (Document d:docs) {
			ArrayList<Document> configArray = (ArrayList<Document>) d.get("config");
			for (Document config: configArray) {
				result.add(mapper.readValue(new StringReader(new MongoToJackson(config, JConfiguration.class).toJson()),JConfiguration.class));
			}
		}
		return result;
	}
	
	public List<JTreatment> getTreatments(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<JTreatment> result = new ArrayList<JTreatment>();
		Document doc = checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Document> treatArray = (ArrayList<Document>) doc.get("treatment");
		for (Document tdoc: treatArray) {
			result.add(mapper.readValue(new StringReader(new MongoToJackson(tdoc, JTreatment.class).toJson()),JTreatment.class));
		}
		return result;
	}
	
	public List<JEvent> getEvents(String idconfig, String ename) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JEvent> result = new ArrayList<JEvent>();
		FindIterable<Document> docs = events.find(and(eq("idconfig", idconfig), eq("ename",ename)));
		ObjectMapper mapper = new ObjectMapper();
		for (Document doc: docs) {
			result.add(mapper.readValue(new StringReader(new MongoToJackson(doc, JEvent.class).toJson()),JEvent.class));
		}
		return result;
	}
	
	
	
}
