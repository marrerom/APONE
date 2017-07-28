package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.Status;

public interface Database {
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JExperiment getExpFromConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JEvent getEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public String addExperiment(JExperiment experiment );
	
	public String addExpConfig(String idexp,  JConfiguration conf);
	
	public Date addExpConfigDateStart(JConfiguration conf);
	
	public Date addExpConfigDateEnd(JConfiguration conf);
	
	public String addEvent(JEvent event);
	
	public void setExpConfigRunStatus(JConfiguration conf, Status status);
	
	public void close();

}
