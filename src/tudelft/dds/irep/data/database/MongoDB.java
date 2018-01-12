package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotAuthorizedException;

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
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.data.schema.UserRol;
import tudelft.dds.irep.services.Experiment;
import tudelft.dds.irep.utils.AuthenticationException;
import tudelft.dds.irep.utils.BadRequestException;
import tudelft.dds.irep.utils.InternalServerException;
import tudelft.dds.irep.utils.Security;
import tudelft.dds.irep.utils.Utils;

import static com.mongodb.client.model.Filters.*;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Projections.*;


public class MongoDB implements Database {
	
	static protected final Logger log = Logger.getLogger(Experiment.class.getName());
	
	//public final String DB = "test"; //irep in production, test for testing purposes
	public final String EXP_COL = "experiment";
	public final String EVENT_COL = "event";
	public final String USER_COL = "user";

	private MongoClient mongo;
	MongoCollection<Document> experiments;
	MongoCollection<Document> events;
	MongoCollection<Document> users;
	
	public MongoDB(String host, int port, String db, String user, char[] pwd){
		MongoCredential credential = MongoCredential.createCredential(user, db, pwd);
		mongo = new MongoClient(new ServerAddress( host , port ), Arrays.asList(credential));
		MongoDatabase database = mongo.getDatabase(db);
		experiments = database.getCollection(EXP_COL);
		events = database.getCollection(EVENT_COL);
		users = database.getCollection(USER_COL);
	}
	
	public void close(){
		mongo.close();
	}
	
	private Document checkExistExperiment(String idexp, JUser authuser) {
		try {
			return experiments.find(and(getExperimenterFilter(authuser),eq("_id", new ObjectId(idexp)))).first();
		} catch (NullPointerException e) {
			String msg = "Experiment "+idexp+" does not exist or you don't have authorization to access it";
			log.log(Level.WARNING, msg, e);
			throw new BadRequestException(msg);
		}
	}

	private Document checkExistConfiguration(String idconf, JUser authuser) {
		try {
			Document doc = experiments.find(and(getExperimenterFilter(authuser),eq("config._id", new ObjectId(idconf))))
					.projection(elemMatch("config"))
					.first();
			return ((ArrayList<Document>)doc.get("config")).get(0);
		} catch (NullPointerException e) {
			String msg = "Experiment configuration "+idconf+" does not exist or you don't have authorization to access it";
			log.log(Level.WARNING, msg, e);
			throw new BadRequestException(msg);
		}
	}
	
	private Document checkExistEvent(String idevent, JUser authuser) {
		try {
			return events.find(and(getExperimenterFilter(authuser),eq("_id", new ObjectId(idevent)))).first();
		} catch (NullPointerException e) {
			String msg = "Event "+idevent+" does not exist or you don't have authorization to access it";
			log.log(Level.WARNING, msg, e);
			throw new BadRequestException(msg);
		}
	}
	
	public Document checkExistUserByIdtwitter(String idTwitter, JUser authuser) {
		try{
			return users.find(and(getUserFilter(authuser), eq("idTwitter", idTwitter))).first();
		} catch (NullPointerException e) {
			String msg = "User "+idTwitter+" does not exist or you don't have authorization to access it";
			log.log(Level.WARNING, msg, e);
			throw new BadRequestException(msg);
		}
	}
	
	public Document checkExistUserByIdname(String idname, JUser authuser) {
		try{
			return users.find(and(getUserFilter(authuser), eq("idname", idname))).first();
		} catch (NullPointerException e) {
			String msg = "User "+idname+" does not exist or you don't have authorization to access it";
			log.log(Level.WARNING, msg, e);
			throw new BadRequestException(msg);
		}
	}
	
	
	public String addExperiment(JExperiment experiment, JUser authuser) throws ParseException, JsonProcessingException, IOException {
		Security.checkAuthorized(authuser, experiment.getExperimenter());
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
	
	private String setIdname(String originalidname, String idname, JUser authuser, int count) throws IOException, ParseException {
		JUser filter = new JUser();
		filter.setIdname(idname);
		if (!this.getUsers(filter, authuser).isEmpty()) {
			return setIdname(originalidname, originalidname+"-"+count, authuser, ++count);
		}
		return idname;
		
	}
	
	public JUser addUser(String idTwitter, String screenName, JUser authuser) throws ParseException, IOException {
		if (idTwitter == null)
			throw new BadRequestException("addUser: IdTwitter can not be null");
		
		Security.checkAuthorized(authuser,Security.getMasterUser().getIdname());
		JUser filter = new JUser();
		filter.setIdTwitter(idTwitter);
		List<JUser> sameidtwitter = this.getUsers(filter, authuser);
		if (!sameidtwitter.isEmpty()) { //already exists
			return sameidtwitter.get(0);
			//throw new BadRequestException("User with idTwitter "+newuser.getIdTwitter()+" already exists in the database");
		}
		JUser newuser = new JUser();
		newuser.setIdTwitter(idTwitter);
		newuser.setName(screenName);
		newuser.setIdname(setIdname(screenName, screenName, authuser, 1));
		newuser.setRol(UserRol.USER.toString());
		ObjectId iduser = new ObjectId();
		newuser.set_id(iduser.toString());
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap = mapper.convertValue(newuser, Map.class);
		Document doc = new Document(new JacksonToMongo().convert(docmap, JUser.class));
		users.insertOne(doc);
		return newuser;
	}
	
//	public void updateUser(JUser toupdate, JUser authuser) throws ParseException, IOException {
//		Security.checkAuthorized(authuser, toupdate.getIdname());
//		Document doc = this.checkExistUserByIdtwitter(toupdate.getIdTwitter(), authuser);
//		ObjectId id = (ObjectId) doc.get("_id");
//		if (!id.toString().equals(toupdate.get_id())){
//			InternalServerException e = new InternalServerException("Error updating user: idTwitter is not unique in users db any more?");
//			log.log(Level.SEVERE, e.getMessage(), e);
//		}
//		if (!doc.getString("idname").equals(toupdate.getIdname())){
//			BadRequestException e = new BadRequestException("Error updating user: it is not possible to modify idTwitter or idName");
//			log.log(Level.WARNING, e.getMessage(), e);
//		}
//				
//		ObjectMapper mapper = new ObjectMapper();
//		Map<String, Object> docmap = mapper.convertValue(toupdate, Map.class);
//		Document toupdatedoc = new Document(new JacksonToMongo().convert(docmap, JUser.class));
//		users.insertOne(toupdatedoc, InsertOneOptions);
//	}
	
	public void updateUserParticipation(JUser user, String[] newlist, JUser authuser) throws IOException {
		Security.checkAuthorized(authuser, user.getIdname());
		Document doc = this.checkExistUserByIdtwitter(user.getIdTwitter(), authuser);
		ObjectMapper mapper = new ObjectMapper();

		Bson filter = new Document("_id", new ObjectId(user.get_id()));
		
		Bson newValue = new Document("participatedexps", Arrays.asList(newlist));
		Bson updateOperationDocument = new Document("$set", newValue);
		users.updateOne(filter, updateOperationDocument);
	}
	
	
	public String addExpConfig(String idexp, JConfiguration conf, JUser authuser) throws ParseException, JsonProcessingException, IOException {
		checkExistExperiment(idexp, authuser);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(conf, Map.class);
		ObjectId idrun = new ObjectId();
		docmap.put("_id", idrun.toString());
		Document doc = new Document(new JacksonToMongo().convert(docmap, JConfiguration.class));
		experiments.updateOne(eq("_id", new ObjectId(idexp)), Updates.addToSet("config", doc));
		return idrun.toString();
	}
	
	public void addExpConfigDateStart(String idconf, Date timestamp, JUser authuser) {
		checkExistConfiguration(idconf, authuser);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.push("config.$.date_started", timestamp));
	}

	public void addExpConfigDateEnd(String idconf, Date timestamp, JUser authuser) {
		checkExistConfiguration(idconf, authuser);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.push("config.$.date_ended", timestamp));
	}
	
	public String addEvent(JEvent event, JUser authuser) throws ParseException, JsonProcessingException, IOException {
		Security.checkAuthorized(authuser, event.getExperimenter());
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
	
	public void setExpConfigRunStatus(String idconf, Status status, JUser authuser) {
		checkExistConfiguration(idconf, authuser);
		experiments.updateOne(eq("config._id", new ObjectId(idconf)), Updates.set("config.$.run", status.toString()));
	}
		
	
	public JExperiment getExperiment(String idexp, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistExperiment(idexp, authuser);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JExperiment.class)).toJson()),JExperiment.class);
	}
	
	public JUser getUserByIdtwitter(String idTwitter, JUser authuser) throws IOException, ParseException {
		Document doc = checkExistUserByIdtwitter(idTwitter, authuser);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JUser.class)).toJson()),JUser.class);

	}
	
	public JUser getUserByIdname(String idName, JUser authuser) throws IOException, ParseException {
		Document doc = checkExistUserByIdname(idName, authuser);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JUser.class)).toJson()),JUser.class);

	}
	
	
	public JEvent getEvent(String idevent, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistEvent(idevent, authuser);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc,JEvent.class)).toJson()),JEvent.class);
	}

	public JExperiment getExpFromConfiguration(String idconf, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		checkExistConfiguration(idconf, authuser);
		//Document doc = experiments.find(eq("config._id", new ObjectId(idconf))).projection(fields(elemMatch("config"),include("name", "experimenter", "description", "unit", "treatment"))).first();
		Document doc = experiments.find(eq("config._id", new ObjectId(idconf))).first();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JExperiment.class)).toJson()),JExperiment.class);
	}
	
	private FindIterable<Document> getFilteredEvents(JEvent filter, JUser authuser) throws ParseException, JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(filter, Map.class);
		docmap = new JacksonToMongo().convert(docmap, JEvent.class);

		List<Bson> conditions = new ArrayList<Bson>();
		if (docmap.get("_id") != null) conditions.add(eq("_id", docmap.get("_id")));	
		if (docmap.get("ename") != null) conditions.add(eq("ename", docmap.get("ename")));	
		if (docmap.get("experimenter") != null) conditions.add(eq("experimenter", docmap.get("experimenter")));	
		if (docmap.get("idunit") != null) conditions.add(eq("idunit", docmap.get("idunit")));
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
				Map<String, Object> evaluemap =  mapper.convertValue(docmap.get("evalue"), Map.class);
				for (String key:evaluemap.keySet()) {
					conditions.add(eq("evalue."+key, evaluemap.get(key)));
				}
				//JsonNode node = mapper.valueToTree(docmap.get("evalue")); //TODO: create a docmap and take class of value to convert to with mapper
//				for (Iterator<String> iterator = node.fieldNames(); iterator.hasNext();) {
//					String field = iterator.next();
//					conditions.add(eq("evalue."+field, node.get(field)));
//				}
			}
		}
		
		conditions.add(getExperimenterFilter(authuser));
		FindIterable<Document> results = events.find(and(conditions));
		return results;

	}
	
	private FindIterable<Document> getFilteredUsers(JUser filter, JUser authuser) throws ParseException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(filter, Map.class);
		docmap = new JacksonToMongo().convert(docmap, JUser.class);
		List<Bson> conditions = new ArrayList<Bson>();
		if (docmap.get("_id") != null) conditions.add(eq("_id", docmap.get("_id")));	
		if (docmap.get("idTwitter") != null) conditions.add(eq("idTwitter", docmap.get("idTwitter")));	
		if (docmap.get("idname") != null) conditions.add(eq("idname", docmap.get("idname")));	
		if (docmap.get("rol") != null) conditions.add(eq("rol", docmap.get("rol")));	
//		ArrayList<String> participated = ((ArrayList<String>)docmap.get("participatedexps"));
//		if (!participated.isEmpty()) conditions.add(in("participatedexps", participated));	
		
		conditions.add(getUserFilter(authuser));
		FindIterable<Document> results = users.find(and(conditions));
		return results;
	}
	

	private FindIterable<Document> getFilteredExperiments(JExperiment filter, JUser authuser) throws ParseException, JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> docmap =  mapper.convertValue(filter, Map.class);
		docmap = new JacksonToMongo().convert(docmap, JExperiment.class);
		List<Bson> conditions = new ArrayList<Bson>();
		if (docmap.get("_id") != null) conditions.add(eq("_id", docmap.get("_id")));	
		if (docmap.get("name") != null) conditions.add(eq("name", docmap.get("name")));	
		if (docmap.get("experimenter") != null) conditions.add(eq("experimenter", docmap.get("experimenter").toString()));	
		if (docmap.get("unit") != null) conditions.add(eq("unit", docmap.get("unit")));
		if (docmap.get("description") != null) conditions.add(regex("description", docmap.get("description").toString()));	
		
		List<Bson> treatmentscond = new ArrayList<Bson>();
		for (Map<String,Object> treatitem : ((ArrayList<Map<String,Object>>)docmap.get("treatment"))) {
			List<Bson> treatcond = new ArrayList<Bson>();
			if (treatitem.get("name") != null) treatcond.add(eq("treatment.name", treatitem.get("name")));
			if (treatitem.get("description") != null) treatcond.add(regex("treatment.description", treatitem.get("description").toString()));
			if (treatitem.get("definition") != null) treatcond.add(regex("treatment.definition", treatitem.get("definition").toString()));
//			if (treatitem.get("url") != null) treatcond.add(regex("treatment.url", treatitem.get("url").toString()));
			if (treatitem.get("control") != null) treatcond.add(eq("treatment.control", treatitem.get("control")));
			treatmentscond.add(and(treatcond));
		}
		if (!treatmentscond.isEmpty())
			conditions.add(or(treatmentscond));
		

		boolean configsearch = false;
		List<Bson> configurationcond = new ArrayList<Bson>();
		for (Map<String,Object> configitem : ((ArrayList<Map<String,Object>>)docmap.get("config"))) {
			List<Bson> configcond = new ArrayList<Bson>();
			if (configitem.get("_id") != null) {configsearch=true; configcond.add(eq("config._id", configitem.get("_id")));}
			if (configitem.get("name") != null) {configsearch=true;configcond.add(eq("config.name", configitem.get("name")));}
			if (configitem.get("description") != null) {configsearch=true;configcond.add(regex("config.description", configitem.get("description").toString()));}
			if (configitem.get("experimenter") != null) {configsearch=true;configcond.add(eq("config.experimenter", configitem.get("experimenter")));}
			if (configitem.get("controller_code") != null) {configsearch=true;configcond.add(regex("config.controller_code", configitem.get("controller_code").toString()));}
			if (configitem.get("run") != null) {configsearch=true;configcond.add(eq("config.run", configitem.get("run")));}
			if (configitem.get("max_exposures") != null) {configsearch=true;configcond.add(eq("config.max_exposures", configitem.get("max_exposures")));}
			for (Date date: ((ArrayList<Date>)configitem.get("date_started"))) {
				configsearch=true;
				BasicDBObject criteria = new BasicDBObject();
				BasicDBObject elemMatch = new BasicDBObject();
				BasicDBObject valueMatch = new BasicDBObject();
				valueMatch.append("$gte", date);
				valueMatch.append("$lte", Utils.addDay(date));
				elemMatch.append("$elemMatch", valueMatch);
				criteria.append("config.date_started", elemMatch);
				configcond.add(criteria);
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
				configcond.add(criteria);
				//conditions.add(in("config.date_ended", and(gte("config.date_ended", date ),lte("config.date_ended", Utils.addDay(date)))));
				}
			
			if (configitem.get("date_to_end") != null) {
				configsearch=true; 
				Date date = (Date) configitem.get("date_to_end");
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				 
				// Set time fields to zero
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				 
				// Put it back in the Date object
				date = cal.getTime();
				
				
				conditions.add(gte("config.date_to_end", date ));
				conditions.add(lte("config.date_to_end", Utils.addDay(date) ));
			}
			configurationcond.add(and(configcond));
		}
		if (!configurationcond.isEmpty())
			conditions.add(or(configurationcond));

		conditions.add(getExperimenterFilter(authuser));
		FindIterable<Document> results = experiments.find(and(conditions));
		if (configsearch)
			results = results.projection(fields(elemMatch("config"),include("name", "experimenter", "description", "unit", "treatment")));
		return results;
	}
	
	public List<JExperiment> getExperiments(JExperiment filter, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JExperiment> result = new ArrayList<JExperiment>();
		FindIterable<Document> exps = getFilteredExperiments(filter, authuser);
		ObjectMapper mapper = new ObjectMapper();		
		for (Document exp: exps) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(exp, JExperiment.class)).toJson()),JExperiment.class));
		}
		return result;
	}
	
	public List<JEvent> getEvents(JEvent filter, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JEvent> result = new ArrayList<JEvent>();
		FindIterable<Document> exps = getFilteredEvents(filter, authuser);
		ObjectMapper mapper = new ObjectMapper();		
		for (Document exp: exps) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(exp, JEvent.class)).toJson()),JEvent.class));
		}
		return result;
	}
	
	public List<JUser> getUsers(JUser filter, JUser authuser) throws IOException, ParseException {
		List<JUser> result = new ArrayList<JUser>();
		FindIterable<Document> users = getFilteredUsers(filter, authuser);
		ObjectMapper mapper = new ObjectMapper();		
		for (Document user: users) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(user, JUser.class)).toJson()),JUser.class));
		}
		return result;
	}
	
	public JConfiguration getConfiguration(String idconf, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Document doc = checkExistConfiguration(idconf, authuser);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JConfiguration.class)).toJson()),JConfiguration.class);
	}
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<Bson> conditions = new ArrayList<Bson>();
		for (Status st:status) {
			conditions.add(and(getExperimenterFilter(authuser),eq("config.run", st.toString())));
		}
		
		//TODO: check! condConfig is not used
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
	
	public List<JTreatment> getTreatments(String idexp, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<JTreatment> result = new ArrayList<JTreatment>();
		Document doc = checkExistExperiment(idexp, authuser);
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Document> treatArray = (ArrayList<Document>) doc.get("treatment");
		for (Document tdoc: treatArray) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(tdoc, JTreatment.class)).toJson()),JTreatment.class));
		}
		return result;
	}
	
	public List<JEvent> getEvents(String idconfig, String ename, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException{
		List<JEvent> result = new ArrayList<JEvent>();
		FindIterable<Document> docs = events.find(and(getExperimenterFilter(authuser),eq("idconfig", idconfig), eq("ename",ename)));
		ObjectMapper mapper = new ObjectMapper();
		for (Document doc: docs) {
			result.add(mapper.readValue(new StringReader(new Document(new MongoToJackson().convert(doc, JEvent.class)).toJson()),JEvent.class));
		}
		return result;
	}
	
	public void deleteConfig(String idconf, JUser authuser) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JExperiment exp= getExpFromConfiguration(idconf, authuser);
		if (exp.getConfig().length <= 1) {
			experiments.deleteOne(and(getExperimenterFilter(authuser),eq("_id", new ObjectId(exp.get_id()))));	
		} else {
			BasicDBObject update = new BasicDBObject("config", new BasicDBObject("_id", new ObjectId(idconf)));
			experiments.updateOne(and(getExperimenterFilter(authuser),eq("config._id", new ObjectId(idconf))), new BasicDBObject("$pull", update));
		}
	}
	
	//only admins
	public void deleteUser(String idname, JUser authuser) {
		if (!authuser.isAdmin()) 
			throw new AuthenticationException();
		Document user = checkExistUserByIdname(idname, authuser);
		users.deleteOne(eq("_id", (ObjectId) user.get("_id")));
	}
	
	public void deleteEvent(String idevent, JUser authuser) {
		events.deleteMany(and(getExperimenterFilter(authuser),eq("_id", new ObjectId(idevent))));
	}
	
	
	private Bson getExperimenterFilter(JUser authuser) {
		if (authuser.isAdmin())
			return regex("experimenter", ".*");
		return eq("experimenter", authuser.getIdname());
	}
	
	private Bson getUserFilter(JUser authuser) {
		if (authuser.isAdmin())
			return regex("idname", ".*");
		return eq("idname", authuser.getIdname());
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
