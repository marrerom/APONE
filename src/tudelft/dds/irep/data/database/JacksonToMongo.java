package tudelft.dds.irep.data.database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


import org.bson.types.ObjectId;
import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.utils.Utils;

public class JacksonToMongo extends Conversor {

	/*Binary*/
	
	@Override
	protected Map<String,Object> binary(Map<String,Object> mongodoc) {
		Boolean binary = (Boolean) mongodoc.get("binary");
		if (binary!=null && binary && mongodoc.get("evalue")!=null) {
			String str = (String) mongodoc.get("evalue");
			byte[] bin = Utils.decodeBinary(str);
			mongodoc.put("evalue", bin);
		}
		return mongodoc;
	}
	
	
	/*Date*/

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String,Object> convertDate(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		if (jacksonclass == JConfiguration.class) {
			mongodoc.put("date_started", date((ArrayList<String>) mongodoc.get("date_started")));
			mongodoc.put("date_ended", date((ArrayList<String>) mongodoc.get("date_ended")));
			mongodoc.put("date_to_end", date((String) mongodoc.get("date_to_end")));
		} else if (jacksonclass == JExperiment.class) {
			for (Map<String,Object> item : ((ArrayList<Map<String,Object>>)mongodoc.get("config"))) {
				item.put("date_started", date((ArrayList<String>)item.get("date_started")));
				item.put("date_ended", date((ArrayList<String>)item.get("date_ended")));
				item.put("date_to_end", date((String)item.get("date_to_end")));
			}
		} else if (jacksonclass == JEvent.class) {
			mongodoc.put("timestamp", date((String) mongodoc.get("timestamp")));
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
	@SuppressWarnings("unchecked")
	protected Map<String,Object> convertId(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) {
		if (jacksonclass == JConfiguration.class || jacksonclass == JEvent.class) {
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
		if (mongodoc.get("_id") != null && mongodoc.get("_id").getClass() == String.class) {
			String value = (String) mongodoc.get("_id");
			mongodoc.put("_id", new ObjectId(value));
		}
	}
	
	

}
