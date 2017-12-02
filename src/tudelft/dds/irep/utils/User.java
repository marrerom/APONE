package tudelft.dds.irep.utils;

import tudelft.dds.irep.utils.Security.Rol;

public class User {
	

	private String name;
	
	private Rol rol;
	
	public User (String name, Rol rol) {
		this.name = name;
		this.rol = rol;
	}

	public String getName() {
		return name;
	}

	public Rol getRol() {
		return rol;
	}
	
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	
	public boolean isAdmin() {
		if (getRol() == Rol.ADMIN)
			return true;
		return false;
	}
	
	public void setAsAdmin() {
		setRol(Rol.ADMIN);
	}
}
