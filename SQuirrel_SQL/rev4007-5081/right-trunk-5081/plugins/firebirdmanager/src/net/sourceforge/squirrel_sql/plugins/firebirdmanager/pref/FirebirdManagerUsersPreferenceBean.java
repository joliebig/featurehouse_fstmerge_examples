
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;


public class FirebirdManagerUsersPreferenceBean  
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = 486709537344788163L;
	private String server = "";
	private String port = "";
	private String user = "";
	
	
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

	
	public FirebirdManagerUsersPreferenceBean() {
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
