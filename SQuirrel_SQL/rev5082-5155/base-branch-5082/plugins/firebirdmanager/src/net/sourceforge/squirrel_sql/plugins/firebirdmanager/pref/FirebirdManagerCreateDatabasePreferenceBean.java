
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;


public class FirebirdManagerCreateDatabasePreferenceBean  
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = -2249980211724848230L;
	
	private String port = "";
	private String user = "";
	private String server = "";
	private String databaseFolder = "";
	private int sqlDialect;
	
	
	public String getServer() {
		return server;
	}

	
	public void setServer(String server) {
		this.server = server;
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

	
	public String getDatabaseFolder() {
		return databaseFolder;
	}

	
	public void setDatabaseFolder(String databaseFolder) {
		this.databaseFolder = databaseFolder;
	}

	
	public int getSqlDialect() {
		return sqlDialect;
	}

	
	public void setSqlDialect(int sqlDialect) {
		this.sqlDialect = sqlDialect;
	}


	
	public FirebirdManagerCreateDatabasePreferenceBean() {
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
