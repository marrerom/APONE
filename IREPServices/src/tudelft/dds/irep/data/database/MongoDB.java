package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.Status;

import static com.mongodb.client.model.Filters.*;
import org.bson.types.ObjectId;
import org.bson.types.Binary;


/*
 * Mongo types are not directly supported in Jackson (eg. _id is ObjectId). Apparently those types are kept when we use Document.toJson
 * TODO: extend serializer in Jackson
 */
class MongoToJackson extends Document {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MongoToJackson(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		super(mongodoc);
		convertId(this, jacksonclass);
		convertDate(this, jacksonclass);
		convertBinary(this, jacksonclass);
	}
	
	private void convertBinary(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		if (jacksonclass == JEvent.class) {
			binaryToStr(this);
		}
	}
	
	private void binaryToStr(Document mongodoc) {
		boolean binary = mongodoc.getBoolean("binary", false);
		if (binary) {
			Binary bin = (Binary) mongodoc.get("evalue");
			byte[] valuebin = bin.getData();
			String valuestr = java.util.Base64.getEncoder().encodeToString(valuebin);
			mongodoc.put("evalue", valuestr);
		}
	}
	
	private void convertDate(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		if (jacksonclass == JConfiguration.class) {
			mongodoc.put("date_started", dateToStandardFormat((ArrayList<Date>) mongodoc.get("date_started")));
			mongodoc.put("date_ended", dateToStandardFormat((ArrayList<Date>) mongodoc.get("date_ended")));
		} else if (jacksonclass == JExperiment.class) {
			for (Object item : ((ArrayList)this.get("config"))) {
				((Document)item).put("date_started", dateToStandardFormat((ArrayList<Date>)((Document)item).get("date_started")));
				((Document)item).put("date_ended", dateToStandardFormat((ArrayList<Date>)((Document)item).get("date_ended")));
			}
		} else if (jacksonclass == JEvent.class) {
			mongodoc.put("timestamp", dateToStandardFormat(mongodoc.getDate("timestamp")));
		}
	}
	
	private ArrayList<String> dateToStandardFormat(ArrayList<Date> array) throws ParseException  {
		ArrayList<String> newarray = new ArrayList<String>();
		for (Date item : array) {
			newarray.add(dateToStandardFormat(item));
		}
		return newarray;
	}
	
	private String dateToStandardFormat(Date item) throws ParseException  {
		if (item != null) {
			DateFormat input = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //Format in mongo
			DateFormat target = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); //Standard format recognized by Jackson
			Date date = input.parse(item.toString());
			String res = target.format(date);
			return res;
		}
		return null;
	}
	

	
	private void convertId(Document mongodoc, Class<? extends JCommon> jacksonclass) {
		if (jacksonclass == JConfiguration.class || jacksonclass == JEvent.class) {
			idToStr(this);
		} else if (jacksonclass == JExperiment.class) {
			idToStr(this);
			for (Object item : ((ArrayList)this.get("config"))) {
				idToStr((Document) item);
			}
		}
	}
	
	private void idToStr(Document mongodoc) {
		ObjectId value = (ObjectId) mongodoc.get("_id");
		if (value != null) {
			mongodoc.put("_id", value.toString());
		}
	}
}


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
	
	
	//TODO: should the db throw an exception when getting or adding data if the experiment/configuration does not exist?
	private Document checkExistExperiment(String idexp) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		try {
			return coll.find(eq("_id", new ObjectId(idexp))).first();
		} catch (NullPointerException e) {
			throw new NullPointerException("Experiment "+idexp+" does not exist");
		}
	}

	private Document checkExistConfiguration(String idconf) {
		
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		try {
			Document doc = coll.find(eq("config._id", new ObjectId(idconf)))
					.projection(com.mongodb.client.model.Projections.elemMatch("config", eq("_id", new ObjectId(idconf))))
					.first();
			return ((ArrayList<Document>)doc.get("config")).get(0);
		} catch (NullPointerException e) {
			throw new NullPointerException("Experiment Configuration "+idconf+" does not exist");
		}
	}
	
	private Document checkExistEvent(String idevent) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EVENT_COL);
		try {
			return coll.find(eq("_id", new ObjectId(idevent))).first();
		} catch (NullPointerException e) {
			throw new NullPointerException("Event "+idevent+" does not exist");
		}
	}

	
	public String addExperiment(JExperiment experiment) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		Map<String, Object> docmap = experiment.getDocmap();
		ObjectId idexp = new ObjectId();
		docmap.put("_id", idexp);			
		Document doc = new Document(docmap);
		coll.insertOne(doc);
		return idexp.toString();
		
	}
	
	public String addExpConfig(String idexp, JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		checkExistExperiment(idexp);
		Map<String, Object> docmap = conf.getDocmap();
		ObjectId idrun = new ObjectId();
		docmap.put("_id", idrun);
		Document doc = new Document(docmap);
		coll.updateOne(eq("_id", new ObjectId(idexp)), Updates.addToSet("config", doc));
		return idrun.toString();
		 
	}
	
	public Date addExpConfigDateStart(JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		checkExistConfiguration(conf.get_id());
		Date now = new Date();
		coll.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.push("config.$.date_started", now));
		return now;
	}

	public Date addExpConfigDateEnd(JConfiguration conf) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		checkExistConfiguration(conf.get_id());
		Date now = new Date();
		coll.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.push("config.$.date_ended", now));
		return now;
	}
	
	public String addEvent(JEvent event) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EVENT_COL);
		Map<String,Object> docmap = event.getDocmap();
		if (event.isBinary()) {
			byte[] binvalue = java.util.Base64.getDecoder().decode(event.getEvalue());
			docmap.put("evalue", binvalue); //CHECK
		}
		ObjectId idevent = new ObjectId();
		docmap.put("_id", idevent);
		Document doc = new Document(docmap);
		coll.insertOne(doc);
		return idevent.toString();
	}
	
	public void setExpConfigRunStatus(JConfiguration conf, Status status) {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		checkExistConfiguration(conf.get_id());
		coll.updateOne(eq("config._id", new ObjectId(conf.get_id())), Updates.set("config.$.run", status.toString()));
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
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		checkExistConfiguration(idconf);
		Document doc = coll.find(eq("config._id", new ObjectId(idconf))).first();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc, JExperiment.class).toJson()),JExperiment.class);
	}
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistConfiguration(idconf);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new MongoToJackson(doc, JConfiguration.class).toJson()),JConfiguration.class);
	}
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status) throws JsonParseException, JsonMappingException, IOException, ParseException {
		MongoDatabase database = mongo.getDatabase(DB);
		MongoCollection<Document> coll = database.getCollection(EXP_COL);
		List<Bson> conditions = new ArrayList<Bson>();
		for (Status st:status) {
			conditions.add(eq("config.run", st.toString()));
		}
		
		List<Bson> condConfig = new ArrayList<Bson>();
		for (Status st:status) {
			condConfig.add(eq("run", st.toString()));
		}

		FindIterable<Document> docs = coll.find(or(conditions)).projection(com.mongodb.client.model.Projections.elemMatch("config", or(condConfig)));
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
	
	
	
}
