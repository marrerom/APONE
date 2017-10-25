package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Updates;

import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JParamValues;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.utils.Utils;

import static com.mongodb.client.model.Filters.*;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Projections.*;


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
					.projection(elemMatch("config"))
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

	
	public String addExperiment(JExperiment experiment) throws ParseException, JsonProcessingException, IOException {
		for (JConfiguration conf:experiment.getConfig()) {
			if (conf.get_id() == null || conf.get_id()=="") {
				ObjectId idconf = new ObjectId();
				conf.set_id(idconf.toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(experiment, Map.class);
		ObjectId idexp = new ObjectId();
		docmap.put("_id", idexp.toString());
		Document doc = new Document(new JacksonToMongo().convert(docmap, JExperiment.class));
		experiments.insertOne(doc);
		return idexp.toString();
	}
	
	public String addExpConfig(String idexp, JConfiguration conf) throws ParseException, JsonProcessingException, IOException {
		checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(conf, Map.class);
		ObjectId idrun = new ObjectId();
		docmap.put("_id", idrun.toString());
		
		Document doc = new Document(new JacksonToMongo().convert(docmap, JConfiguration.class));
		experiments.updateOne(eq("_id", new ObjectId(idexp)), Updates.addToSet("config", doc));
		return idrun.toString();
		 
	}
	
	public void addExpConfigDateStart(String idconf, Date timestamp) {
		checkExistConfiguration(idconf);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.push("config.$.date_started", timestamp));
	}

	public void addExpConfigDateEnd(String idconf, Date timestamp) {
		checkExistConfiguration(idconf);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.push("config.$.date_ended", timestamp));
	}
	
	public String addEvent(JEvent event) throws ParseException, JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(event, Map.class);

		ObjectId idevent = new ObjectId();
		docmap.put("_id", idevent.toString());
		
//		String timestamp = (String) docmap.get("timestamp");
//		docmap.put("timestamp", Utils.getDate(timestamp));
		
		Document doc = new Document(new JacksonToMongo().convert(docmap, JEvent.class));
		events.insertOne(doc);
		return idevent.toString();
	}
	
	public void setExpConfigRunStatus(String idconf, Status status) {
		checkExistConfiguration(idconf);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.set("config.$.run", status.toString()));
	}
		
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JExperiment.class)).toJson()),JExperiment.class);
	}
	
	public JEvent getEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistEvent(idevent);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JEvent.class)).toJson()),JEvent.class);
	}

	public JExperiment getExpFromConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		checkExistConfiguration(idconf);
		//Document doc = experiments.find(eq("config._id", new ObjectId(idconf))).projection(fields(elemMatch("config"),include("name", "experimenter", "description", "unit", "treatment"))).first();
		Document doc = experiments.find(eq("config._id", new ObjectId(idconf))).first();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JExperiment.class)).toJson()),JExperiment.class);
	}
	
	private FindIterable<Document> getFilteredEvents(JEvent filter) throws ParseException, JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(filter, Map.class);
		docmap = new JacksonToMongo().convert(docmap, JEvent.class);

		List<Bson> conditions = new ArrayList<Bson>();
		if (docmap.get("_id") != null) conditions.add(eq("_id", docmap.get("_id")));	
		if (docmap.get("ename") != null) conditions.add(eq("ename", docmap.get("ename")));	
			
		if (docmap.get("unitid") != null) conditions.add(eq("unitid", docmap.get("unitid")));
		if (docmap.get("idconfig") != null) conditions.add(eq("idconfig", docmap.get("idconfig")));
		if (docmap.get("treatment") != null) conditions.add(eq("treatment", docmap.get("treatment")));
		if (docmap.get("useragent") != null) conditions.add(regex("useragent", docmap.get("useragent").toString()));
		
		JParamValues jparams = filter.getParamvalues();
		if (jparams != null) {
			Map<String,?> params = jparams.any();
			for (String key: params.keySet()) {
				if (params.get(key).getClass() == String.class && StringUtils.isNumeric((String)params.get(key))) { //TODO: coming from the interface, we don't know the type
					String value = (String) params.get(key);
					conditions.add(or(eq("paramvalues."+key, params.get(key)), eq("paramvalues."+key, Integer.parseInt(value))));
				} else {
					conditions.add(eq("paramvalues."+key, params.get(key)));
				}
			}
		}
		
		if (docmap.get("timestamp") != null) {
			Date date = (Date) docmap.get("timestamp");
			conditions.add(gte("timestamp", date ));
			conditions.add(lte("timestamp", Utils.addDay(date) ));
		}

		String etype = null;
		if (docmap.get("etype") != null) {
			etype  = (String) docmap.get("etype");
			conditions.add(eq("etype", etype));
		}
		
		if (docmap.get("evalue") != null) { 
			if (etype == null || EventType.valueOf(etype) == EventType.STRING) { 
				conditions.add(regex("evalue", docmap.get("evalue").toString()));
			} else if (etype != null && EventType.valueOf(etype) == EventType.JSON) {
				JsonNode node = mapper.valueToTree(docmap.get("evalue"));
				for (Iterator<String> iterator = node.fieldNames(); iterator.hasNext();) {
					String field = iterator.next();
					conditions.add(eq("evalue."+field, node.get(field).asText()));
				}
			}
		}
		
		FindIterable<Document> results;
		if (conditions.isEmpty())
			results = events.find();
		else
			results = events.find(and(conditions));
		return results;

	}
	

	private FindIterable<Document> getFilteredExperiments(JExperiment filter) throws ParseException, JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(filter, Map.class);
		docmap = new JacksonToMongo().convert(docmap, JExperiment.class);
		List<Bson> conditions = new ArrayList<Bson>();
		if (docmap.get("_id") != null) conditions.add(eq("_id", docmap.get("_id")));	
		if (docmap.get("name") != null) conditions.add(eq("name", docmap.get("name")));	
		if (docmap.get("experimenter") != null) conditions.add(eq("experimenter", docmap.get("experimenter").toString()));	
		if (docmap.get("unit") != null) conditions.add(eq("unit", docmap.get("unit")));
		if (docmap.get("description") != null) conditions.add(regex("description", docmap.get("description").toString()));	
		
		for (Map<String,Object> treatitem : ((ArrayList<Map<String,Object>>)docmap.get("treatment"))) {
			if (treatitem.get("name") != null) conditions.add(eq("treatment.name", treatitem.get("name")));
			if (treatitem.get("description") != null) conditions.add(regex("treatment.description", treatitem.get("description").toString()));
			if (treatitem.get("definition") != null) conditions.add(regex("treatment.definition", treatitem.get("definition").toString()));
			if (treatitem.get("url") != null) conditions.add(regex("treatment.url", treatitem.get("url").toString()));
			if (treatitem.get("control") != null) conditions.add(eq("treatment.control", treatitem.get("control")));
		}

		boolean configsearch = false;
		for (Map<String,Object> configitem : ((ArrayList<Map<String,Object>>)docmap.get("config"))) {
			if (configitem.get("_id") != null) {configsearch=true; conditions.add(eq("config._id", configitem.get("_id")));}
			if (configitem.get("name") != null) {configsearch=true;conditions.add(eq("config.name", configitem.get("name")));}
			if (configitem.get("description") != null) {configsearch=true;conditions.add(regex("config.description", configitem.get("description").toString()));}
			if (configitem.get("experimenter") != null) {configsearch=true;conditions.add(eq("config.experimenter", configitem.get("experimenter")));}
			if (configitem.get("controller_code") != null) {configsearch=true;conditions.add(regex("config.controller_code", configitem.get("controller_code").toString()));}
			if (configitem.get("run") != null) {configsearch=true;conditions.add(eq("config.run", configitem.get("run")));}
			if (configitem.get("max_exposures") != null) {configsearch=true;conditions.add(eq("config.max_exposures", configitem.get("max_exposures")));}
			for (Date date: ((ArrayList<Date>)configitem.get("date_started"))) {
				configsearch=true;
				BasicDBObject criteria = new BasicDBObject();
				BasicDBObject elemMatch = new BasicDBObject();
				BasicDBObject valueMatch = new BasicDBObject();
				valueMatch.append("$gte", date);
				valueMatch.append("$lte", Utils.addDay(date));
				elemMatch.append("$elemMatch", valueMatch);
				criteria.append("config.date_started", elemMatch);
				conditions.add(criteria);
				//conditions.add(com.mongodb.client.model.Filters.elemMatch("config.date_started", gte("date", date )));
				}
			for (Date date: ((ArrayList<Date>)configitem.get("date_ended"))) {
				configsearch=true;
				BasicDBObject criteria = new BasicDBObject();
				BasicDBObject elemMatch = new BasicDBObject();
				BasicDBObject valueMatch = new BasicDBObject();
				valueMatch.append("$gte", date);
				valueMatch.append("$lte", Utils.addDay(date));
				elemMatch.append("$elemMatch", valueMatch);
				criteria.append("config.date_ended", elemMatch);
				conditions.add(criteria);
				//conditions.add(in("config.date_ended", and(gte("config.date_ended", date ),lte("config.date_ended", Utils.addDay(date)))));
				}
			
			if (configitem.get("date_to_end") != null) {
				configsearch=true; 
				Date date = (Date) configitem.get("date_to_end");
				conditions.add(gte("config.date_to_end", date ));
				conditions.add(lte("config.date_to_end", Utils.addDay(date) ));
			}
		}

		
		FindIterable<Document> results;
		if (conditions.isEmpty())
			results = experiments.find();
		else
			results = experiments.find(and(conditions));
		if (configsearch)
			results = results.projection(fields(elemMatch("config"),include("name", "experimenter", "description", "unit", "treatment")));
		return results;
	}
	
	public List<JExperiment> getExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JExperiment> result = new ArrayList<JExperiment>();
		FindIterable<Document> exps = getFilteredExperiments(filter);
		ObjectMapper mapper = new ObjectMapper();		
		for (Document exp: exps) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(exp, JExperiment.class)).toJson()),JExperiment.class));
		}
		return result;
	}
	
	public List<JEvent> getEvents(JEvent filter) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JEvent> result = new ArrayList<JEvent>();
		FindIterable<Document> exps = getFilteredEvents(filter);
		ObjectMapper mapper = new ObjectMapper();		
		for (Document exp: exps) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(exp, JEvent.class)).toJson()),JEvent.class));
		}
		return result;
	}
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistConfiguration(idconf);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JConfiguration.class)).toJson()),JConfiguration.class);
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

		//WRONG! elemMatch returns just the first element
		//FindIterable<Document> docs = experiments.find(or(conditions)).projection(com.mongodb.client.model.Projections.elemMatch("config", or(condConfig)));
		
		
		Bson unwind = Aggregates.unwind("$config");
	    List<Bson> list = new ArrayList<Bson>();
	    list.add(Aggregates.match(or(conditions)));
	    list.add(unwind);
	    list.add(Aggregates.match(or(conditions)));
        List<Document> docs = experiments.aggregate(list).into(
                new ArrayList<Document>());
	    
	    
		List<JConfiguration> result = new ArrayList<JConfiguration>();
		ObjectMapper mapper = new ObjectMapper();
		for (Document d:docs) {
			Document config = (Document) d.get("config");
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(config, JConfiguration.class)).toJson()),JConfiguration.class));
		}
		return result;
	}
	
	public List<JTreatment> getTreatments(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<JTreatment> result = new ArrayList<JTreatment>();
		Document doc = checkExistExperiment(idexp);
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Document> treatArray = (ArrayList<Document>) doc.get("treatment");
		for (Document tdoc: treatArray) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(tdoc, JTreatment.class)).toJson()),JTreatment.class));
		}
		return result;
	}
	
	public List<JEvent> getEvents(String idconfig, String ename) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JEvent> result = new ArrayList<JEvent>();
		FindIterable<Document> docs = events.find(and(eq("idconfig", idconfig), eq("ename",ename)));
		ObjectMapper mapper = new ObjectMapper();
		for (Document doc: docs) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JEvent.class)).toJson()),JEvent.class));
		}
		return result;
	}
	
	public void deleteConfig(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JExperiment exp= getExpFromConfiguration(idconf);
		if (exp.getConfig().length <= 1) {
			experiments.deleteOne(eq("_id", new ObjectId(exp.get_id())));	
		} else {
			BasicDBObject update = new BasicDBObject("config", new BasicDBObject("_id", new ObjectId(idconf)));
			experiments.updateOne(eq("config._id", new ObjectId(idconf)), new BasicDBObject("$pull", update));
		}
	}
	
	public void deleteEvent(String idevent) {
		events.deleteMany(eq("_id", new ObjectId(idevent)));
	}
	
//	public List<JExperiment> getExperiments() throws JsonParseException, JsonMappingException, IOException, ParseException{
//		List<JExperiment> result = new ArrayList<JExperiment>();
//		FindIterable<Document> docs = experiments.find();
//		ObjectMapper mapper = new ObjectMapper();
//		for (Document doc: docs) {
//			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JExperiment.class)).toJson()),JExperiment.class));
//		}
//		return result;
//	}
	
	
}
