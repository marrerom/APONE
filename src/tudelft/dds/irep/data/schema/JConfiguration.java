package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class JConfiguration extends JCommon {
	
//	class ObjectIdSerializer extends JsonSerializer<Object> {
//	    @Override
//	    public void serialize(Object value, JsonGenerator jsonGen,SerializerProvider provider) throws IOException {
//	        jsonGen.writeString(value.toString());
//	    }
//	}
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/experiment_schema.json";
	
	@JsonIgnore
	private static final String rootElement = "configItem";
	

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
	private String controller_code;
	private Date[] date_started = {};
	private Date[] date_ended = {};
	private String run;
	private Date date_to_end;
	private Integer max_exposures;
	private JDistribution[] distribution = {};
	private Boolean test;
	
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getDate_to_end() {
		return date_to_end;
	}
	@JsonDeserialize(using=JsonDateDeserializer.class)
	public void setDate_to_end(Date date_to_end) {
		this.date_to_end = date_to_end;
	}
	public Integer getMax_exposures() {
		return max_exposures;
	}
	public void setMax_exposures(Integer max_exposures) {
		this.max_exposures = max_exposures;
	}
	public JDistribution[] getDistribution() {
		return distribution;
	}
	public void setDistribution(JDistribution[] distr) {
		this.distribution = distr;
	}
	public Boolean getTest() {
		return test;
	}
	public void setTest(Boolean test) {
		this.test = test;
	}

	public String get_id() {
		return _id;
	}
	public void set_id(String idrun) {
		this._id = idrun;
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
	public String getController_code() {
		return controller_code;
	}
	public void setController_code(String controller_code) {
		this.controller_code = controller_code;
	}
	@JsonSerialize(using=JsonArrayDateSerializer.class)
	public Date[] getDate_started() {
		return date_started;
	}
	@JsonDeserialize(using=JsonArrayDateDeserializer.class)
	public void setDate_started(Date[] date_started) {
		this.date_started = date_started;
	}
	@JsonSerialize(using=JsonArrayDateSerializer.class)
	public Date[] getDate_ended() {
		return date_ended;
	}
	@JsonDeserialize(using=JsonArrayDateDeserializer.class)
	public void setDate_ended(Date[] date_ended) {
		this.date_ended = date_ended;
	}
	
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	} 
	
	@JsonIgnore
	public Status getRunEnum() { //at the moment of development, mongo driver does not support enum types
		return Status.valueOf(getRun());
	}
	

}
