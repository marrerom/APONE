package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JEventCSV;
import tudelft.dds.irep.data.schema.JExperiment;

public abstract class Conversor {
	
	public Map<String,Object> convert(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException, JsonProcessingException, IOException {
		mongodoc = convertId(mongodoc, jacksonclass);
		mongodoc = convertDate(mongodoc, jacksonclass);
		mongodoc = convertEtype(mongodoc, jacksonclass);
		return mongodoc;
	}
	

	/*Binary*/
	protected Map<String,Object> convertEtype(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws JsonProcessingException, IOException {
		if (jacksonclass == JEvent.class || jacksonclass == JEventCSV.class) {
			mongodoc = eType(mongodoc);
		}
		return mongodoc;
	}
	
	abstract protected Map<String,Object> eType(Map<String,Object> mongodoc)  throws JsonProcessingException, IOException;
	
	
	
	/*Date*/
	abstract protected Map<String,Object> convertDate(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException;
	
	/*Id*/
	abstract protected Map<String,Object> convertId(Map<String,Object> mongodoc, Class<? extends JCommon> jacksonclass);

	
	abstract protected void id(Map<String,Object> mongodoc);
}
