package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



@JsonInclude(Include.NON_NULL)
public class JEvent extends JCommon implements Serializable {
	@JsonIgnore
	public static final String EXPOSURE_ENAME = "exposure";
	public static final String COMPLETED_ENAME = "completed";
	
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/event_schema.json";
	
	@JsonIgnore
	private static final String rootElement = null;
	
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@JsonIgnore	
	public String getRootElement() {
		return rootElement;
	}
	
	protected String _id;
	protected String idconfig;
	protected Date timestamp;
	protected String idunit;
	protected String treatment;
	protected JParamValues paramvalues;
	protected String ename;
	protected String evalue; //in case the value is binary, it is saved in base64 (using Java.Encode64)
	protected String etype; //one of the values of EventType
	protected String useragent;
	protected String experimenter;
	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getIdconfig() {
		return idconfig;
	}

	public void setIdconfig(String idconfig) {
		this.idconfig = idconfig;
	}

	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getTimestamp() {
		return timestamp;
	}
	@JsonDeserialize(using=JsonDateDeserializer.class)
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getIdunit() {
		return idunit;
	}

	public void setIdunit(String unitid) {
		this.idunit = unitid;
	}
	
	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public JParamValues getParamvalues() {
		return paramvalues;
	}

	public void setParamvalues(JParamValues paramvalues) {
		this.paramvalues = paramvalues;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getEvalue() {
		return evalue;
	}

	public void setEvalue(String evalue) {
		this.evalue = evalue;
	}

	public String getEtype() {
		return etype;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}
	
	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public String getExperimenter() {
		return experimenter;
	}

	public void setExperimenter(String experimenter) {
		this.experimenter = experimenter;
	}

	
	@JsonIgnore
	public EventType getETypeEnum() { //at the moment of development, mongo driver does not support enum types
		return EventType.valueOf(getEtype());
	}


}
