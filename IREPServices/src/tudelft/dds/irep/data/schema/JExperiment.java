package tudelft.dds.irep.data.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JExperiment extends JCommon {
	
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
	private String control_treatment;
	private JTreatment[] treatment;
	
	private JConfiguration[] config;

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getControl_treatment() {
		return control_treatment;
	}
	public void setControl_treatment(String control_treatment) {
		this.control_treatment = control_treatment;
	}
	public JTreatment[] getTreatment() {
		return treatment;
	}
	public void setTreatment(JTreatment[] treatment) {
		this.treatment = treatment;
	}
	public String get_Id() {
		return _id;
	}
	public void set_Id(String idexp) {
		this._id = idexp;
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
	public JConfiguration[] getRun() {
		return config;
	}
	public void setRun(JConfiguration[] run) {
		this.config = run;
	}
	
	@JsonIgnore
	public Map<String, Object> getDocmap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getName());
		map.put("_id", get_Id());
		map.put("description", getDescription());
		map.put("experimenter", getExperimenter());
		map.put("unit", getUnit());
		map.put("control_treatment", getControl_treatment());
		
		List<Map<String,Object>> treatlist = new ArrayList<>();
		for (JTreatment t: getTreatment()) 
			treatlist.add(t.getDocmap());
		map.put("distr", treatlist);
		
		return map;
	}
	
}
