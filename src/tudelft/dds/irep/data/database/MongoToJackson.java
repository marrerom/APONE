package tudelft.dds.irep.data.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import tudelft.dds.irep.data.schema.EventType;
import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.utils.Utils;


/*
 * Mongo types are not directly supported in Jackson (eg. _id is ObjectId). Apparently those types are kept when we use Document.toJson
 * 
 */
//TODO: make specific codecs with Mongo java driver Codec interface
public class MongoToJackson extends Conversor {

	/*Binary*/
	
	@Override
	protected Map<String,Object> eType(Map<String,Object> mongodoc) {
		String ename = (String) mongodoc.get("ename");
		String etype = (String) mongodoc.get("etype");
		EventType etypeEnum = EventType.valueOf(etype);
		if (etypeEnum == EventType.BINARY) {
			Binary bin = (Binary) mongodoc.get("evalue");
			byte[] valuebin = bin.getData();
			String valuestr = Utils.encodeBinary(valuebin);
			mongodoc.put("evalue", valuestr);
		} else if (etypeEnum == EventType.JSON){
				Document value = (Document) mongodoc.get("evalue");
				mongodoc.put("evalue", value.toJson());
		}
		return mongodoc;
	}
	
	/*Date*/
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String,Object> convertDate(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		if (jacksonclass == JConfiguration.class) {
			mongodoc.put("date_started", dateToStandardFormat((ArrayList<Date>) mongodoc.get("date_started")));
			mongodoc.put("date_ended", dateToStandardFormat((ArrayList<Date>) mongodoc.get("date_ended")));
			mongodoc.put("date_to_end", dateToStandardFormat((Date) mongodoc.get("date_to_end")));
		} else if (jacksonclass == JExperiment.class) {
			for (Map<String,Object> item : ((ArrayList<Map<String,Object>>)mongodoc.get("config"))) {
				item.put("date_started", dateToStandardFormat((ArrayList<Date>)item.get("date_started")));
				item.put("date_ended", dateToStandardFormat((ArrayList<Date>)item.get("date_ended")));
				item.put("date_to_end", dateToStandardFormat((Date) item.get("date_to_end")));
			}
		} else if (jacksonclass == JEvent.class) {
			mongodoc.put("timestamp", dateToStandardFormat((Date)mongodoc.get("timestamp")));
		}
		return mongodoc;
	}

	
	protected ArrayList<String> dateToStandardFormat(ArrayList<Date> array) throws ParseException {
		ArrayList<String> newarray = new ArrayList<String>();
		for (Date item : array) {
			newarray.add(dateToStandardFormat(item));
		}
		return newarray;
	}
	
	protected String dateToStandardFormat(Date item) throws ParseException  {
		if (item != null) {
			//return Utils.getTimestamp(item);
			DateFormat input = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //Format in mongo
			DateFormat target = new SimpleDateFormat(JsonDateSerializer.timestampFormat); //Standard format recognized by Jackson
			Date date = input.parse(item.toString());
			String res = target.format(date);
			return res;
		}
		return null;
	}
	
	
	/*Id*/
	@SuppressWarnings("unchecked")
	protected Map<String,Object> convertId(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) {
	if (jacksonclass == JConfiguration.class || jacksonclass == JEvent.class || jacksonclass == JUser.class) {
		id(mongodoc);
	} else if (jacksonclass == JExperiment.class) {
		id(mongodoc);
		for(Map<String,Object> item : ((ArrayList<Map<String,Object>>)mongodoc.get("config"))) {
			id(item);
		}
	}
	return mongodoc;
}

	

	@Override
	protected void id(Map<String,Object> mongodoc) {
		ObjectId value = (ObjectId) mongodoc.get("_id");
		if (value != null) {
			mongodoc.put("_id", value.toString());
		}
	}
}


