package tudelft.dds.irep.data.schema;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JExposureBody extends JCommon {

	@JsonIgnore
	public static final String EVENT_ENAME = "exposure";

	
	@JsonIgnore
	private static final String schemaPath = "/schemas/exposure_schema.json";
	
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

	private String treatment;
	private Map<String, ?> paramvalues;
	
	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public Map<String, ?> getParamvalues() {
		return paramvalues;
	}

	public void setParamvalues(Map<String, ?> paramvalues) {
		this.paramvalues = paramvalues;
	}


}
