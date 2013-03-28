
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;


public class FirebirdManagerPreferenceBean 
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = -2450004977019415947L;
	
	private String databaseFolder = "";
	private String port = "3050";
	private String user = "SYSDBA";
	private String server = "localhost";
	private String propertiesFolder = "";

	
	public String getServer() {
		return server;
	}

	
	public void setServer(String server) {
		this.server = server;
	}

	
	public String getDatabaseFolder() {
		return databaseFolder;
	}

	
	public void setDatabaseFolder(String databaseFolder) {
		this.databaseFolder = databaseFolder;
	}

	
	public String getPropertiesFolder() {
		return propertiesFolder;
	}

	
	public void setPropertiesFolder(String propertiesFolder) {
		this.propertiesFolder = propertiesFolder;
	}

	
	public String getPort() {
		return port;
	}

	
	public void setPort(String port) {
		this.port = port;
	}

	
	public String getUser() {
		return user;
	}

	
	public void setUser(String user) {
		this.user = user;
	}

	
	
	public FirebirdManagerPreferenceBean() {
		super();
	}

	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage());
		}
	}
}

