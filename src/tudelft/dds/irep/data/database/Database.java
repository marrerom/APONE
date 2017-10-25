package tudelft.dds.irep.data.database;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JEvent;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;

public interface Database {
	
	public JExperiment getExperiment(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public List<JExperiment> getExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException;

	public List<JTreatment> getTreatments(String idexp) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JExperiment getExpFromConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JConfiguration getConfiguration(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	//List<JExperiment> searchExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JEvent getEvent(String idevent) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public List<JEvent> getEvents(JEvent filter) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public String addExperiment(JExperiment experiment ) throws ParseException, JsonProcessingException, IOException;
	
	public String addExpConfig(String idexp,  JConfiguration conf) throws ParseException, JsonProcessingException, IOException;
	
	public void addExpConfigDateStart(String idconf, Date timestamp);
	
	public void addExpConfigDateEnd(String idconf, Date timestamp);
	
	public String addEvent(JEvent event) throws ParseException, JsonProcessingException, IOException;
	
	public void setExpConfigRunStatus(String idconf, Status status);
	
	public void deleteConfig(String idconf) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public void deleteEvent(String idevent);
	
	public void close();

}