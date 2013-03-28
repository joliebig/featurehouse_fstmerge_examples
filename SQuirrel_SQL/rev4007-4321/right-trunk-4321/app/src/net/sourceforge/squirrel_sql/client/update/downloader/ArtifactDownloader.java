
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.util.PathUtilsImpl;


public class ArtifactDownloader implements Runnable
{

	private List<ArtifactStatus> _artifactStatus = null;

	private volatile boolean _stopped = false;

	private boolean _isRemoteUpdateSite = true;

	private String _host = null;

	private String _path = null;

	private String _fileSystemUpdatePath = null;

	private List<DownloadStatusListener> listeners = new ArrayList<DownloadStatusListener>();

	Thread downloadThread = null;

	String _updatesDir = null;

	private int _port = 80;

	
	private String _channelName;
	
	private UpdateUtil _util = null;
	
	
	private PathUtils _pathUtils = new PathUtilsImpl();

	public ArtifactDownloader(List<ArtifactStatus> artifactStatus)
	{
		_artifactStatus = artifactStatus;
		downloadThread = new Thread(this, "ArtifactDownloadThread");
	}

	public void start()
	{
		downloadThread.start();
	}	
	
	
	public void run()
	{
		sendDownloadStarted(_artifactStatus.size());

		try
		{
			for (ArtifactStatus status : _artifactStatus)
			{
				if (_stopped)
				{
					sendDownloadStopped();
					return;
				}
				else
				{
					sendDownloadFileStarted(status.getName());
				}
				String fileToGet =
					_pathUtils.buildPath(true, _path, _channelName, status.getType(), status.getName());
				String destDir = getArtifactDownloadDestDir(status);

				if (fileWasDownloadedPreviously(status)) {
					continue;
				}
				
				boolean result = true;
				if (_isRemoteUpdateSite)
				{
					try
					{
						_util.downloadHttpFile(_host, _port, fileToGet, destDir);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						sendDownloadFailed();
						return;
					}
				}
				else
				{
					fileToGet = _pathUtils.buildPath(false, this._fileSystemUpdatePath,fileToGet);
					result = _util.downloadLocalFile(fileToGet, destDir);
				}
				if (result == false)
				{
					sendDownloadFailed();
					return;
				}
				else
				{
					sendDownloadFileCompleted(status.getName());
				}
			}
		}
		catch (FileNotFoundException e)
		{
			
			e.printStackTrace();
			sendDownloadFailed();
			return;
		}
		catch (IOException e)
		{
			
			e.printStackTrace();
			sendDownloadFailed();
			return;
		}
		sendDownloadComplete();
	}

	private boolean fileWasDownloadedPreviously(ArtifactStatus status)
	{
		boolean result = false;
		
		
		
		return result;
	}

	private String getArtifactDownloadDestDir(ArtifactStatus status) {

		File destDir = _util.getCoreDownloadsDir();		
		if (UpdateUtil.PLUGIN_ARTIFACT_ID.equals(status.getType()))
		{
			destDir = _util.getPluginDownloadsDir();
		}
		if (UpdateUtil.TRANSLATION_ARTIFACT_ID.equals(status.getType()))
		{
			destDir = _util.getI18nDownloadsDir();
		}
		return destDir.getAbsolutePath();
	}
	
	
	public void stopDownload()
	{
		_stopped = true;
	}

	
	public List<ArtifactStatus> getArtifactStatus()
	{
		return _artifactStatus;
	}

	
	public void setArtifactStatus(List<ArtifactStatus> status)
	{
		_artifactStatus = status;
	}

	
	public boolean isRemoteUpdateSite()
	{
		return _isRemoteUpdateSite;
	}

	
	public void setIsRemoteUpdateSite(boolean remoteUpdateSite)
	{
		_isRemoteUpdateSite = remoteUpdateSite;
	}

	
	public String getHost()
	{
		return _host;
	}

	
	public void setHost(String host)
	{
		this._host = host;
	}

	
	public String getPath()
	{
		return _path;
	}

	
	String downloadHttpFile(String host, int port, String path, String fileToGet, String destDir)
		throws Exception
	{
		return _util.downloadHttpFile(host, port, fileToGet, destDir);
	}

	public void setPath(String path)
	{
		this._path = path;
	}

	
	public UpdateUtil getUtil()
	{
		return _util;
	}

	
	public void addDownloadStatusListener(DownloadStatusListener listener)
	{
		listeners.add(listener);
	}

	
	public void removeDownloadListener(DownloadStatusListener listener)
	{
		listeners.remove(listener);
	}

	private void sendEvent(DownloadStatusEvent evt)
	{
		for (DownloadStatusListener listener : listeners)
		{
			listener.handleDownloadStatusEvent(evt);
		}
	}

	private void sendDownloadStarted(int totalFileCount)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STARTED);
		evt.setFileCountTotal(totalFileCount);
		sendEvent(evt);
	}

	private void sendDownloadStopped()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STOPPED);
		sendEvent(evt);
	}

	private void sendDownloadComplete()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		sendEvent(evt);
	}

	private void sendDownloadFailed()
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FAILED);
		sendEvent(evt);
	}

	private void sendDownloadFileStarted(String filename)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_STARTED);
		evt.setFilename(filename);
		sendEvent(evt);
	}

	private void sendDownloadFileCompleted(String filename)
	{
		DownloadStatusEvent evt = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_COMPLETED);
		evt.setFilename(filename);
		sendEvent(evt);
	}

	
	public String getFileSystemUpdatePath()
	{
		return _fileSystemUpdatePath;
	}

	
	public void setFileSystemUpdatePath(String systemUpdatePath)
	{
		_fileSystemUpdatePath = systemUpdatePath;
	}

	public void setPort(int updateServerPort)
	{
		_port = updateServerPort;
	}

	public void setChannelName(String name)
	{
		_channelName = name;
	}

	
	public void setUtil(UpdateUtil util)
	{
		this._util = util;
	}
	
}