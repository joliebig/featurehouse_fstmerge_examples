
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;


public class FirebirdManagerBackupAndRestorePreferenceBean 
implements IFirebirdManagerSessionPreferencesBean, Cloneable, Serializable {
	private static final long serialVersionUID = 5308526497399393529L;

	private String port = "";
	private String user = "";
	private String server = "";
	private String bckDatabaseFilename = "";
	private String bckBackupFilename = "";
	private boolean displayProcess = true;

	
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

	
	public String getBckDatabaseFilename() {
		return bckDatabaseFilename;
	}

	
	public void setBckDatabaseFilename(String bckDatabaseFilename) {
		this.bckDatabaseFilename = bckDatabaseFilename;
	}

	
	public String getBckBackupFilename() {
		return bckBackupFilename;
	}

	
	public void setBckBackupFilename(String bckBackupFilename) {
		this.bckBackupFilename = bckBackupFilename;
	}

	
	public boolean isDisplayProcess() {
		return displayProcess;
	}

	
	public void setDisplayProcess(boolean displayProcess) {
		this.displayProcess = displayProcess;
	}

	

	
	public FirebirdManagerBackupAndRestorePreferenceBean() {
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

