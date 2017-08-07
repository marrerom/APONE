package tudelft.dds.irep.data.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.utils.Utils;

public class JacksonToMongo extends Conversor {

	/*Binary*/
	
	@Override
	protected Document binary(Document mongodoc) {
		boolean binary = mongodoc.getBoolean("binary", false);
		if (binary) {
			String str = (String) mongodoc.get("evalue");
			byte[] bin = Utils.decodeBinary(str);
			mongodoc.put("evalue", bin);
		}
		return mongodoc;
	}
	
	
	/*Date*/

	@Override
	protected Document convertDate(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		if (jacksonclass == JConfiguration.class) {
			mongodoc.put("date_started", date((ArrayList<String>) mongodoc.get("date_started")));
			mongodoc.put("date_ended", date((ArrayList<String>) mongodoc.get("date_ended")));
		} else if (jacksonclass == JExperiment.class) {
			for (Object item : ((ArrayList)mongodoc.get("config"))) {
				((Document)item).put("date_started", date((ArrayList<String>)((Document)item).get("date_started")));
				((Document)item).put("date_ended", date((ArrayList<String>)((Document)item).get("date_ended")));
			}
		} else if (jacksonclass == JEvent.class) {
			mongodoc.put("timestamp", date(mongodoc.getString("timestamp")));
		}
		return mongodoc;
	}

	
	protected ArrayList<Date> date(ArrayList<String> array) throws ParseException {
		ArrayList<Date> newarray = new ArrayList<Date>();
		for (String item : array) {
			newarray.add(date(item));
		}
		return newarray;
	}
	
	protected Date date(String item) throws ParseException  {
		if (item != null && item.getClass() == String.class) {
			return Utils.getDate(item);
		}
		return null;
	}
	

	/*Id*/
	
	@Override
	protected void id(Document mongodoc) {
		if (mongodoc.get("_id") != null && mongodoc.get("_id").getClass() == String.class) {
			String value = (String) mongodoc.get("_id");
			mongodoc.put("_id", new ObjectId(value));
		}
	}
	
	

}
