package tudelft.dds.irep.data.schema;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JEvent extends JCommon {
	
	@JsonIgnore
	public static final String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
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
	
	private String _id;
	private String idconfig;
	private Date timestamp;
	private String unitid;
	private String ename;
	private String evalue; //in case the value is binary, it is saved in base64 (using Java.Encode64)
	private boolean binary = false; //if event value (evalue) is binary or not

	
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getUnitid() {
		return unitid;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
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

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}
	
	@Override
	public Map<String, Object> getDocmap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("idconfig", getIdconfig());
		map.put("timestamp", getTimestamp());
		map.put("unitid", getUnitid());
		map.put("ename", getEname());
		map.put("evalue", getEvalue());
		map.put("binary", isBinary());
		return map;
	}

}
