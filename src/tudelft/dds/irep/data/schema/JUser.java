package tudelft.dds.irep.data.schema;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JUser extends JCommon implements Serializable {
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private static final String schemaPath = "/schemas/user_schema.json";
	
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
	protected String idTwitter;
	protected String idname; //unique name assigned. The actual name in twitter of someone may change, but this id must stay the same
	protected String name; //screen name in twitter (it can be changed in twitter). Not used except to create the idname. After that, idname is the one used
	protected String rol;
	
	public JUser() {
		
	}

	public JUser(String idname, String idTwitter, UserRol rol) {
		this.idname = idname;
		this.rol = rol.toString();
		this.name = idname;
		this.idTwitter = idTwitter;
	}
	
	public JUser(String idname, UserRol rol) {
		this(idname, idname, rol);
	}
	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
	
	public String getIdTwitter() {
		return idTwitter;
	}

	public void setIdTwitter(String idTwitter) {
		this.idTwitter = idTwitter;
	}

	public String getIdname() {
		return idname;
	}

	public void setIdname(String idname) {
		this.idname = idname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	
	@JsonIgnore
	public boolean isAdmin() {
		if (getRolEnum() == UserRol.ADMIN)
			return true;
		return false;
	}
	
	@JsonIgnore
	public void setAsAdmin() {
		setRol(UserRol.ADMIN.toString());
	}
	

	@JsonIgnore
	public UserRol getRolEnum() { //at the moment of development, mongo driver does not support enum types
		return UserRol.valueOf(getRol());
	}
	


	
}
