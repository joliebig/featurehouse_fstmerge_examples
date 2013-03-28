
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;

public interface ArtifactDownloader
{

	void start();

	
	void stopDownload();

	
	List<ArtifactStatus> getArtifactStatus();

	
	void setArtifactStatus(List<ArtifactStatus> status);

	
	boolean isRemoteUpdateSite();

	
	void setIsRemoteUpdateSite(boolean remoteUpdateSite);

	
	String getHost();

	
	void setHost(String host);

	
	String getPath();

	void setPath(String path);

	
	UpdateUtil getUtil();

	
	void addDownloadStatusListener(DownloadStatusListener listener);

	
	void removeDownloadListener(DownloadStatusListener listener);

	
	String getFileSystemUpdatePath();

	
	void setFileSystemUpdatePath(String systemUpdatePath);

	void setPort(int updateServerPort);

	void setChannelName(String name);

	
	void setUtil(UpdateUtil util);

	public void setProxySettings(IProxySettings settings);

	
	public boolean isReleaseVersionWillChange();

	
	public void setReleaseVersionWillChange(boolean releaseVersionWillChange);

}