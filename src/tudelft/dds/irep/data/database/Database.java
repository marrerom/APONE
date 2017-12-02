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
import tudelft.dds.irep.utils.User;

public interface Database {
	
	public JExperiment getExperiment(String idexp, User experimenter) throws IOException, ParseException;
	
	public List<JExperiment> getExperiments(JExperiment filter, User experimenter) throws IOException, ParseException;

	public List<JTreatment> getTreatments(String idexp, User experimenter) throws IOException, ParseException;
	
	public JExperiment getExpFromConfiguration(String idconf, User experimenter) throws IOException, ParseException;
	
	public JConfiguration getConfiguration(String idconf, User experimenter) throws IOException, ParseException;
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status, User experimenter) throws IOException, ParseException;
	
	//List<JExperiment> searchExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JEvent getEvent(String idevent, User experimenter) throws IOException, ParseException;
	
	public List<JEvent> getEvents(JEvent filter, User experimenter) throws IOException, ParseException;
	
	public String addExperiment(JExperiment experiment, User experimenter) throws IOException, ParseException;
	
	public String addExpConfig(String idexp,  JConfiguration conf, User experimenter) throws IOException, ParseException;
	
	public void addExpConfigDateStart(String idconf, Date timestamp, User experimenter);
	
	public void addExpConfigDateEnd(String idconf, Date timestamp, User experimenter);
	
	public String addEvent(JEvent event, User experimenter) throws IOException, ParseException;
	
	public void setExpConfigRunStatus(String idconf, Status status, User experimenter);
	
	public void deleteConfig(String idconf, User experimenter) throws IOException, ParseException;
	
	public void deleteEvent(String idevent, User experimenter);
	
	public void close();

}
