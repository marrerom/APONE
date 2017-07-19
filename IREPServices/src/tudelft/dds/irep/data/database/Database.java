package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.Status;

public interface Database {
	
	public String addExperiment(JExperiment experiment );
	
	public String addExpConfig(String idexp,  JConfiguration conf);
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException;
	
	public Date addExpConfigDateStart(JConfiguration conf);
	
	public Date addExpConfigDateEnd(JConfiguration conf);
	
	public void setExpConfigRunStatus(JConfiguration conf, Status st);
	
	public void close();

}
