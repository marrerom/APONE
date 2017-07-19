package tudelft.dds.irep.data.schema;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class JCommon {
	
	@JsonIgnore
	public abstract String getSchemaPath();
	
	@JsonIgnore
	public abstract String getRootElement();
	
	@JsonIgnore
	public abstract Map<String, Object> getDocmap();
}
