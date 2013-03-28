
package net.sourceforge.squirrel_sql.client.preferences;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.Version;


public class UpdateSettings implements Cloneable, IUpdateSettings, Serializable
{
	private static final long serialVersionUID = -1305655179503568153L;

	
	private String updateServer = "www.squirrel-sql.net";

	
	private String updateServerPort = "80";

	
	private String updateServerPath = "updates";

	
	private String updateServerChannel = "stable";

	
	private boolean enableAutomaticUpdates = true;

	
	private String updateCheckFrequency = "WEEKLY";

	
	private String lastUpdateCheckTimeMillis = "0";
	
	
	private boolean isRemoteUpdateSite = true;
	
	private String fileSystemUpdatePath = "";
	
	public UpdateSettings() {
		if (Version.isSnapshotVersion()) {
			updateServerChannel = "snapshot";
		}
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	
	 public UpdateSettings(IUpdateSettings updateSettings) 
	 {
	     this.updateServer = updateSettings.getUpdateServer();
	     this.updateServerPort = updateSettings.getUpdateServerPort();
	     this.updateServerPath = updateSettings.getUpdateServerPath();
	     this.updateServerChannel = updateSettings.getUpdateServerChannel();
	     this.enableAutomaticUpdates = updateSettings.isEnableAutomaticUpdates();
	     this.updateCheckFrequency = updateSettings.getUpdateCheckFrequency();
	     this.lastUpdateCheckTimeMillis = updateSettings.getLastUpdateCheckTimeMillis();
	     this.isRemoteUpdateSite = updateSettings.isRemoteUpdateSite();
	     this.fileSystemUpdatePath = updateSettings.getFileSystemUpdatePath();
	 }

   
   public String getUpdateServer() {
      return updateServer;
   }

   
   public void setUpdateServer(String updateServer) {
      this.updateServer = updateServer;
   }

   
   public String getUpdateServerPort() {
      return updateServerPort;
   }

   
   public void setUpdateServerPort(String updateServerPort) {
      this.updateServerPort = updateServerPort;
   }

   
   public String getUpdateServerPath() {
      return updateServerPath;
   }

   
   public void setUpdateServerPath(String updateServerPath) {
      this.updateServerPath = updateServerPath;
   }

   
   public String getUpdateServerChannel() {
      return updateServerChannel;
   }

   
   public void setUpdateServerChannel(String updateServerChannel) {
      this.updateServerChannel = updateServerChannel;
   }

   
   public boolean isEnableAutomaticUpdates() {
      return enableAutomaticUpdates;
   }

   
   public void setEnableAutomaticUpdates(boolean enableAutomaticUpdates) {
      this.enableAutomaticUpdates = enableAutomaticUpdates;
   }

   
   public String getUpdateCheckFrequency() {
      return updateCheckFrequency;
   }
   
   
   public void setUpdateCheckFrequency(String updateCheckFrequency) {
      this.updateCheckFrequency = updateCheckFrequency;
   }

   
   public String getLastUpdateCheckTimeMillis() {
      return lastUpdateCheckTimeMillis;
   }

   
   public void setLastUpdateCheckTimeMillis(String lastUpdateCheckTimeMillis) {
      this.lastUpdateCheckTimeMillis = lastUpdateCheckTimeMillis;
   }

   
   public boolean isRemoteUpdateSite() {
      return this.isRemoteUpdateSite;
   }

   
   public void setRemoteUpdateSite(boolean isRemoteUpdateSite) {
      this.isRemoteUpdateSite = isRemoteUpdateSite;
   }

   
   public String getFileSystemUpdatePath() {
      return fileSystemUpdatePath;
   }

   
   public void setFileSystemUpdatePath(String fileSystemUpdatePath) {
      this.fileSystemUpdatePath = fileSystemUpdatePath;
   }
   
   
}