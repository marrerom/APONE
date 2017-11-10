package tudelft.dds.irep.data.schema;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class JEventCSV extends JCommon implements Serializable {
	@JsonIgnore
	public static final String EXPOSURE_ENAME = "exposure";
	
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/event_schema.json";
	
	@JsonIgnore
	private static final String rootElement = null;
	
	protected String _id;
	protected String idconfig;
	protected Date timestamp;
	protected String idunit;
	protected String treatment;
	protected String ename;
	protected String evalue; //in case the value is binary, it is saved in base64 (using Java.Encode64)
	protected String etype;
	protected String paramvalues;
	protected String useragent;
	
	@JsonIgnore
	public JEventCSV(JEvent event) {
		this._id = event._id;
		this.etype =event.etype;
		this.ename = event.ename;
		this.evalue = event.evalue;
		this.idconfig = event.idconfig;
		this.treatment = event.treatment;
		this.timestamp = event.timestamp;
		this.idunit = event.idunit;
		this.paramvalues = event.paramvalues.any().toString(); //TODO:check!
		this.useragent = event.useragent;
	}
	
	@JsonIgnore	
	public String getSchemaPath() {
		return schemaPath;
	}
	
	@JsonIgnore	
	public String getRootElement() {
		return rootElement;
	}
	
	
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

	public String getParamvalues() {
		return paramvalues;
	}

	public void setParamvalues(String paramvalues) {
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


	
	@JsonIgnore
	public EventType getETypeEnum() { //at the moment of development, mongo driver does not support enum types
		return EventType.valueOf(getEtype());
	}
	
}
