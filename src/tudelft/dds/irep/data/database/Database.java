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
import tudelft.dds.irep.data.schema.JUser;
import tudelft.dds.irep.data.schema.Status;

public interface Database {
	
	public JExperiment getExperiment(String idexp, JUser authuser) throws IOException, ParseException;
	
	public List<JExperiment> getExperiments(JExperiment filter, JUser authuser) throws IOException, ParseException;

	public List<JTreatment> getTreatments(String idexp, JUser authuser) throws IOException, ParseException;
	
	public JExperiment getExpFromConfiguration(String idconf, JUser authuser) throws IOException, ParseException;
	
	public JConfiguration getConfiguration(String idconf, JUser authuser) throws IOException, ParseException;
	
	public List<JConfiguration> getConfigurations(Iterable<Status> status, JUser authuser) throws IOException, ParseException;
	
	public JUser getUserByIdtwitter(String idTwitter, JUser authuser) throws  IOException, ParseException;
	
	public JUser getUserByIdname(String idname, JUser authuser) throws  IOException, ParseException;
	
	public List<JUser> getUsers(JUser filter, JUser authuser) throws IOException, ParseException;
	
	//List<JExperiment> searchExperiments(JExperiment filter) throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	public JEvent getEvent(String idevent, JUser authuser) throws IOException, ParseException;
	
	public List<JEvent> getEvents(JEvent filter, JUser authuser) throws IOException, ParseException;
	
	public String addExperiment(JExperiment experiment, JUser authuser) throws IOException, ParseException;
	
	public String addExpConfig(String idexp,  JConfiguration conf, JUser authuser) throws IOException, ParseException;
	
	public void addExpConfigDateStart(String idconf, Date timestamp, JUser authuser);
	
	public void addExpConfigDateEnd(String idconf, Date timestamp, JUser authuser);
	
	public String addEvent(JEvent event, JUser authuser) throws IOException, ParseException;
	
	public JUser addUser(String idTwitter, String screenName, JUser authuser) throws ParseException, IOException;
	
	public void updateUserParticipation(JUser user, String[] newlist, JUser authuser) throws IOException;
	
	public void setExpConfigRunStatus(String idconf, Status status, JUser authuser);
	
	public void deleteConfig(String idconf, JUser authuser) throws IOException, ParseException;
	
	public void deleteEvent(String idevent, JUser authuser);
	
	public void close();

}
