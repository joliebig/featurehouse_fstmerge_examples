
package net.sourceforge.squirrel_sql.client.preferences;

public interface IUpdateSettings
{

	
	public abstract String getUpdateServer();

	
	public abstract void setUpdateServer(String updateServer);

	
	public abstract String getUpdateServerPort();

	
	public abstract void setUpdateServerPort(String updateServerPort);

	
	public abstract String getUpdateServerPath();

	
	public abstract void setUpdateServerPath(String updateServerPath);

	
	public abstract String getUpdateServerChannel();

	
	public abstract void setUpdateServerChannel(String updateServerChannel);

	
	public abstract boolean isEnableAutomaticUpdates();

	
	public abstract void setEnableAutomaticUpdates(boolean enableAutomaticUpdates);

	
	public abstract String getUpdateCheckFrequency();

	
	public abstract void setUpdateCheckFrequency(String updateCheckFrequency);

	
	public abstract String getLastUpdateCheckTimeMillis();

	
	public abstract void setLastUpdateCheckTimeMillis(String lastUpdateCheckTimeMillis);

	
	public abstract boolean isRemoteUpdateSite();

	
	public abstract void setRemoteUpdateSite(boolean isRemoteUpdateSite);

	
	public abstract String getFileSystemUpdatePath();

	
	public abstract void setFileSystemUpdatePath(String fileSystemUpdatePath);

}