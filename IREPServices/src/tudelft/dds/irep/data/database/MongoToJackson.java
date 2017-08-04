package tudelft.dds.irep.data.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JsonDateSerializer;
import tudelft.dds.irep.utils.Utils;


/*
 * Mongo types are not directly supported in Jackson (eg. _id is ObjectId). Apparently those types are kept when we use Document.toJson
 * 
 */
//TODO: make specific codecs with Mongo java driver Codec interface
public class MongoToJackson extends Document {
	
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
			//return Utils.getTimestamp(item);
			DateFormat input = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //Format in mongo
			DateFormat target = new SimpleDateFormat(JsonDateSerializer.timestampFormat); //Standard format recognized by Jackson
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


