package tudelft.dds.irep.data.schema;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JTreatment extends JCommon {
	
	@JsonIgnore
	public static final String schemaPath = "/schemas/experiment_schema.json";
	
	
	@JsonIgnore
	public static final String rootElement = "treatItem";
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@JsonIgnore	
	public String getRootElement() {
		return rootElement;
	}

	private String name;
	private String definition; //planout language
	private String description;
	private boolean control = false;
	

	public boolean isControl() {
		return control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String description) {
		this.definition = description;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
