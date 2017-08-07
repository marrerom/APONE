package tudelft.dds.irep.data.database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import tudelft.dds.irep.data.schema.JCommon;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;

public abstract class Conversor {
	
	public Document convert(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException {
		mongodoc = convertId(mongodoc, jacksonclass);
		mongodoc = convertDate(mongodoc, jacksonclass);
		mongodoc = convertBinary(mongodoc, jacksonclass);
		return mongodoc;
	}
	

	/*Binary*/
	protected Document convertBinary(Document mongodoc, Class<? extends JCommon> jacksonclass) {
		if (jacksonclass == JEvent.class) {
			mongodoc = binary(mongodoc);
		}
		return mongodoc;
	}
	
	abstract protected Document binary(Document mongodoc);
	
	
	
	/*Date*/
	abstract protected Document convertDate(Document mongodoc, Class<? extends JCommon> jacksonclass) throws ParseException;
	
	/*Id*/
	protected Document convertId(Document mongodoc, Class<? extends JCommon> jacksonclass) {
		if (jacksonclass == JConfiguration.class || jacksonclass == JEvent.class) {
			id(mongodoc);
		} else if (jacksonclass == JExperiment.class) {
			id(mongodoc);
			for (Object item : ((ArrayList)mongodoc.get("config"))) {
				id((Document) item);
			}
		}
		return mongodoc;
	}
	
	abstract protected void id(Document mongodoc);
}
