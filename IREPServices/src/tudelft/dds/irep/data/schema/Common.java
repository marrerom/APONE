package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Common {
	
	@JsonIgnore
	public abstract String getSchemaPath();
	
}
