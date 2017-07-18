package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Distribution extends Common {
	
	@JsonIgnore
	public static final String schemaPath = "/schemas/distribution_schema.json";
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}

	private int segments;
	private String treatment;
	private Action action;

	public int getSegments() {
		return segments;
	}
	public void setSegments(int segments) {
		this.segments = segments;
	}
	public String getVariant() {
		return treatment;
	}
	public void setVariant(String variant) {
		this.treatment = variant;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
}
