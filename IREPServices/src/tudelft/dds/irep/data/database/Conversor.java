package tudelft.dds.irep.data.database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;

public abstract class Conversor {
	
	public Map<String,Object> convert(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		mongodoc = convertId(mongodoc, jacksonclass);
		mongodoc = convertDate(mongodoc, jacksonclass);
		mongodoc = convertBinary(mongodoc, jacksonclass);
		return mongodoc;
	}
	

	/*Binary*/
	protected Map<String,Object> convertBinary(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) {
		if (jacksonclass == JEvent.class) {
			mongodoc = binary(mongodoc);
		}
		return mongodoc;
	}
	
	abstract protected Map<String,Object> binary(Map<String,Object> mongodoc);
	
	
	
	/*Date*/
	abstract protected Map<String,Object> convertDate(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException;
	
	/*Id*/
	abstract protected Map<String,Object> convertId(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass);

	
	abstract protected void id(Map<String,Object> mongodoc);
}
