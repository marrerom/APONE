package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JExperiment extends JCommon {
	
	class ObjectIdSerializer extends JsonSerializer<Object> {
	    @Override
	    public void serialize(Object value, JsonGenerator jsonGen,SerializerProvider provider) throws IOException {
	        jsonGen.writeString(value.toString());
	    }
	}
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/experiment_schema.json";
	
	@JsonIgnore
	public static final String rootElement = null;
	
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
	private String unit;
	private JTreatment[] treatment = {};
	
	private JConfiguration[] config = {};

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public JTreatment[] getTreatment() {
		return treatment;
	}
	public void setTreatment(JTreatment[] treatment) {
		this.treatment = treatment;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
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
	public JConfiguration[] getConfig() {
		return config;
	}
	public void setConfig(JConfiguration[] config) {
		this.config = config;
	}
	
	
	@JsonIgnore
	public Map<String, Object> getDocmap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getName());
		map.put("_id", get_id());
		map.put("description", getDescription());
		map.put("experimenter", getExperimenter());
		map.put("unit", getUnit());
		
		List<Map<String,Object>> treatlist = new ArrayList<>();
		for (JTreatment t: getTreatment()) 
			treatlist.add(t.getDocmap());
		map.put("treatment", treatlist);
		
		List<Map<String,Object>> configlist = new ArrayList<>();
		for (JConfiguration c: getConfig()) 
			configlist.add(c.getDocmap());
		map.put("config", configlist);

		
		return map;
	}
	
}
