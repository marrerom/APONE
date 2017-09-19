package tudelft.dds.irep.data.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JParamValues extends JCommon implements Serializable {

	
	private Map<String, Object> map = new HashMap<String, Object>();
	
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
	
	
	@JsonAnyGetter
	public Map<String, ?> any() {
		return map;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		map.put(name, value);
	}

}
