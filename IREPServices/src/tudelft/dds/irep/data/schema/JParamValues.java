package tudelft.dds.irep.data.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JParamValues extends JCommon implements Serializable {
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/event_schema.json";
	
	@JsonIgnore
	public static final String rootElement = "paramvalues";
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@Override
	public String getRootElement() {
		return rootElement;
	}
	 
	
	private Map<String, Object> other = new HashMap<String, Object>();

	  @JsonAnyGetter
	  public Map<String, ?> any() {
	   return other;
	  }

	 @JsonAnySetter
	  public void set(String name, Object value) {
	   other.put(name, value);
	  }


}
