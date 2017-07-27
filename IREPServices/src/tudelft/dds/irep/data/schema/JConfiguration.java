package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class JConfiguration extends JCommon {
	
//	class ObjectIdSerializer extends JsonSerializer<Object> {
//	    @Override
//	    public void serialize(Object value, JsonGenerator jsonGen,SerializerProvider provider) throws IOException {
//	        jsonGen.writeString(value.toString());
//	    }
//	}
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/experiment_schema.json";
	
	@JsonIgnore
	private static final String rootElement = "configItem";
	

	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@JsonIgnore	
	public String getRootElement() {
		return rootElement;
	}

	
	private String _id;
	private String name;
	private String experimenter;
	private String description;
	private String controller_code;
	private Date[] date_started = {};
	private Date[] date_ended = {};
	private String run = Status.ON.toString(); //again, problems in jackson to serialize enum types
	private Date date_to_end;
	private int max_exposures;
	private JDistribution[] distribution = {};
	private boolean test;
	
	public Date getDate_to_end() {
		return date_to_end;
	}
	public void setDate_to_end(Date date_to_end) {
		this.date_to_end = date_to_end;
	}
	public int getMax_exposures() {
		return max_exposures;
	}
	public void setMax_exposures(int max_exposures) {
		this.max_exposures = max_exposures;
	}
	public JDistribution[] getDistribution() {
		return distribution;
	}
	public void setDistribution(JDistribution[] distr) {
		this.distribution = distr;
	}
	public boolean getTest() {
		return test;
	}
	public void setTest(boolean test) {
		this.test = test;
	}

	public String get_id() {
		return _id;
	}
	public void set_id(String idrun) {
		this._id = idrun;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExperimenter() {
		return experimenter;
	}
	public void setExperimenter(String experimenter) {
		this.experimenter = experimenter;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getController_code() {
		return controller_code;
	}
	public void setController_code(String controller_code) {
		this.controller_code = controller_code;
	}
	public Date[] getDate_started() {
		return date_started;
	}
	public void setDate_started(Date[] date_started) {
		this.date_started = date_started;
	}
	public Date[] getDate_ended() {
		return date_ended;
	}
	public void setDate_ended(Date[] date_ended) {
		this.date_ended = date_ended;
	}
	
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	} 
	
	@JsonIgnore
	public Status getRunEnum() { //at the moment of development, mongo driver does not support enum types
		return Status.valueOf(getRun());
	}
	
	@JsonIgnore
	public Map<String, Object> getDocmap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_id", get_id());
		map.put("name", getName());
		map.put("experimenter", getExperimenter());
		map.put("description", getDescription());
		map.put("controller_code", getController_code());
		map.put("run", getRun());
		map.put("date_to_end", getDate_to_end());
		map.put("max_exposures", getMax_exposures());
		map.put("test", getTest());

		List<Date> dsList = new ArrayList<>();
		for (Date ds:getDate_started())
			dsList.add(ds);
		map.put("date_started", dsList);
		
		List<Date> deList = new ArrayList<>();
		for (Date de:getDate_ended())
			deList.add(de);
		map.put("date_ended", deList);

		List<Map<String,Object>> distrlist = new ArrayList<>();
		for (JDistribution d: getDistribution()) 
			distrlist.add(d.getDocmap());
		map.put("distribution", distrlist);
		
		return map;
	}

	
}
