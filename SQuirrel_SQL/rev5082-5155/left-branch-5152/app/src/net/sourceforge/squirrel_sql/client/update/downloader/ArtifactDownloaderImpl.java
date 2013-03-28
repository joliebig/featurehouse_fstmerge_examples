
package net.sourceforge.squirrel_sql.client.update.downloader;

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
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ArtifactDownloaderImpl implements Runnable, ArtifactDownloader
{
	
	private final static ILogger s_log = LoggerController.createLogger(ArtifactDownloaderImpl.class);

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

	private IProxySettings _proxySettings = null;

	public ArtifactDownloaderImpl(List<ArtifactStatus> artifactStatus)
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
		long totalBytesDownloaded = 0;
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

				if (_util.isPresentInDownloadsDirectory(status))
				{
					if (s_log.isInfoEnabled())
					{
						s_log.info("run: Skipping download of file (" + fileToGet + ") which is already present "
							+ "in the downloads directory.");
					}
					sendDownloadFileCompleted(status.getName());
					continue;
				}

				boolean result = true;
				if (_isRemoteUpdateSite)
				{
					int count = 0;
					boolean success = false;
					while (count++ <= 3 && !success)
					{
						success = attemptFileDownload(fileToGet, destDir, status);
						if (!success)
						{
							long sleepTime = (count + 1) * 3000;
							Utilities.sleep(sleepTime);
						}
					}
					if (!success)
					{
						sendDownloadFailed();
						return;
					}
				}
				else
				{
					fileToGet =
						_pathUtils.buildPath(false, this._fileSystemUpdatePath, status.getType(), status.getName());
					result = _util.downloadLocalUpdateFile(fileToGet, destDir);
				}
				if (result == false)
				{
					sendDownloadFailed();
					return;
				}
				else
				{
					sendDownloadFileCompleted(status.getName());
					totalBytesDownloaded += status.getSize();
				}
			}
		}
		catch (FileNotFoundException e)
		{
			s_log.error("run: Unexpected exception: " + e.getMessage(), e);
			sendDownloadFailed();
			return;
		}
		catch (IOException e)
		{
			s_log.error("run: Unexpected exception: " + e.getMessage(), e);
			sendDownloadFailed();
			return;
		}
		if (s_log.isInfoEnabled())
		{
			s_log.info("run: Downloaded " + totalBytesDownloaded + " bytes total for all update files.");
		}
		sendDownloadComplete();
	}

	private boolean attemptFileDownload(String fileToGet, String destDir, ArtifactStatus status)
	{
		boolean success = true;

		try
		{
			_util.downloadHttpUpdateFile(_host, _port, fileToGet, destDir, status.getSize(),
				status.getChecksum(), _proxySettings);
		}
		catch (Exception e)
		{
			s_log.error("run: encountered exception while attempting to download file (" + fileToGet + "): "
				+ e.getMessage(), e);
			success = false;
		}
		return success;
	}

	private String getArtifactDownloadDestDir(ArtifactStatus status)
	{

		FileWrapper destDir = _util.getCoreDownloadsDir();
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

	
	public void setProxySettings(IProxySettings settings)
	{
		_proxySettings = settings;
	}

}