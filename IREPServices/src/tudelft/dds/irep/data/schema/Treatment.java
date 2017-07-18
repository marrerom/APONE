package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Treatment extends Common {
	
	@JsonIgnore
	public static final String schemaPath = "/schemas/treatment_schema.json";
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}

	private String name;
	private String definition; //planout language
	private String description;
	
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
