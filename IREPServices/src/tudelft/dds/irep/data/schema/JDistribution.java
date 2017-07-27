package tudelft.dds.irep.data.schema;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class JDistribution extends JCommon {
	
	@JsonIgnore
	public static final String schemaPath = "/schemas/experiment_schema.json";

	@JsonIgnore
	public static final String rootElement = "distItem";
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@JsonIgnore	
	public String getRootElement() {
		return rootElement;
	}

	private int segments;
	private String treatment;
	private String action = Action.ADD.toString();

	public int getSegments() {
		return segments;
	}
	public void setSegments(int segments) {
		this.segments = segments;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String variant) {
		this.treatment = variant;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	@JsonIgnore
	public Action getActionEnum() { //at the moment of development, mongo driver does not support enum types
		return Action.valueOf(getAction());
	}

	@JsonIgnore
	public Map<String, Object> getDocmap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("segments", getSegments());
		map.put("treatment", getTreatment());
		map.put("action", getAction());
		return map;
	}
	
}
