
package net.sourceforge.squirrel_sql.client.update;

import static java.lang.System.currentTimeMillis;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.RELEASE_XML_FILENAME;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesActionListener;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.preferences.UpdatePreferencesPanel;
import net.sourceforge.squirrel_sql.client.update.async.ReleaseFileUpdateCheckTask;
import net.sourceforge.squirrel_sql.client.update.async.UpdateCheckRunnableCallback;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloader;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusEvent;
import net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadStatusListener;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.CheckUpdateListener;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateManagerDialog;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateSummaryDialog;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateControllerImpl implements UpdateController, CheckUpdateListener
{

	
	private static final ILogger s_log = LoggerController.createLogger(UpdateControllerImpl.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UpdateControllerImpl.class);

	
	private IApplication _app = null;

	
	private UpdateUtil _util = null;

	
	private ChannelXmlBean _currentChannelBean = null;

	
	private ChannelXmlBean _installedChannelBean = null;

	
	private static GlobalPrefsListener listener = null;

	
	private ArtifactDownloader _downloader = null;

	private ArtifactDownloaderFactory _downloaderFactory = null;

	static interface i18n
	{

		
		String DOWNLOADING_UPDATES_MSG = s_stringMgr.getString("UpdateControllerImpl.downloadingUpdatesMsg");

		
		String EXCEPTION_MSG = s_stringMgr.getString("UpdateControllerImpl.exceptionMsg");

		
		String UPDATE_CHECK_FAILED_TITLE = s_stringMgr.getString("UpdateControllerImpl.updateCheckFailedTitle");

		
		String SOFTWARE_VERSION_CURRENT_MSG =
			s_stringMgr.getString("UpdateControllerImpl.softwareVersionCurrentMsg");

		
		String UPDATE_CHECK_TITLE = s_stringMgr.getString("UpdateControllerImpl.updateCheckTitle");

		
		String CHANGES_RECORDED_TITLE =
			s_stringMgr.getString("UpdateControllerImpl.changesRecordedTitle");

		
		
		String CHANGES_RECORDED_MSG =
			s_stringMgr.getString("UpdateControllerImpl.changesRecordedMsg");
		
		
		String UPDATE_DOWNLOAD_FAILED_TITLE =
			s_stringMgr.getString("UpdateControllerImpl.updateDownloadFailed");

		
		String UPDATE_DOWNLOAD_FAILED_MSG =
			s_stringMgr.getString("UpdateControllerImpl.updateDownloadFailedMsg");

		
		
		String RELEASE_FILE_DOWNLOAD_FAILED_MSG =
			s_stringMgr.getString("UpdateControllerImpl.releaseFileDownloadFailedMsg");

		
		
		String PROMPT_TO_DOWNLOAD_AVAILABLE_UPDATES_MSG =
			s_stringMgr.getString("UpdateControllerImpl.promptToDownloadAvailableUpdatesMsg");

		
		String PROMPT_TO_DOWNLOAD_AVAILABLE_UPDATES_TITLE =
			s_stringMgr.getString("UpdateControllerImpl.promptToDownloadAvailableUpdatesTitle");

	}

	
	public UpdateControllerImpl(IApplication app)
	{
		_app = app;
		if (listener == null)
		{
			listener = new GlobalPrefsListener();
			GlobalPreferencesSheet.addGlobalPreferencesActionListener(listener);
		}
	}

	
	public void setArtifactDownloaderFactory(ArtifactDownloaderFactory factory)
	{
		this._downloaderFactory = factory;
	}

	
	public void setUpdateUtil(UpdateUtil util)
	{
		this._util = util;
		_util.setPluginManager(_app.getPluginManager());
	}

	
	public void showUpdateDialog()
	{
		JFrame parent = _app.getMainFrame();
		IUpdateSettings settings = getUpdateSettings();
		boolean isRemoteUpdateSite = settings.isRemoteUpdateSite();
		UpdateManagerDialog dialog = new UpdateManagerDialog(parent, isRemoteUpdateSite);
		if (isRemoteUpdateSite)
		{
			dialog.setUpdateServerName(settings.getUpdateServer());
			dialog.setUpdateServerPort(settings.getUpdateServerPort());
			dialog.setUpdateServerPath(settings.getUpdateServerPath());
			dialog.setUpdateServerChannel(settings.getUpdateServerChannel());
		}
		else
		{
			dialog.setLocalUpdatePath(settings.getFileSystemUpdatePath());
		}
		dialog.addCheckUpdateListener(this);
		dialog.setVisible(true);
	}

	
	public boolean isUpToDate() throws Exception
	{

		IUpdateSettings settings = getUpdateSettings();

		
		String releaseFilename = _util.getLocalReleaseFile().getAbsolutePath();

		
		_installedChannelBean = _util.getLocalReleaseInfo(releaseFilename);

		
		String channelName = getDesiredChannel(settings);

		StringBuilder releasePath = new StringBuilder("/");
		releasePath.append(getUpdateServerPath());
		releasePath.append("/");
		releasePath.append(channelName);
		releasePath.append("/");

		
		
		if (settings.isRemoteUpdateSite())
		{

			_currentChannelBean =
				_util.downloadCurrentRelease(getUpdateServerName(), getUpdateServerPortAsInt(),
					releasePath.toString(), RELEASE_XML_FILENAME, _app.getSquirrelPreferences().getProxySettings());
		}
		else
		{
			_currentChannelBean = _util.loadUpdateFromFileSystem(settings.getFileSystemUpdatePath());
		}

		settings.setLastUpdateCheckTimeMillis("" + currentTimeMillis());
		saveUpdateSettings(settings);

		
		
		return _currentChannelBean.equals(_installedChannelBean);
	}

	
	private String getDesiredChannel(final IUpdateSettings settings)
	{
		String desiredChannel = settings.getUpdateServerChannel().toLowerCase();
		String currentChannelName = _installedChannelBean.getName();

		if (!currentChannelName.equals(desiredChannel))
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("getDesiredChannel: User is switching distribution channel from "
					+ "installed channel (" + currentChannelName + ") to new channel (" + desiredChannel + ")");
			}
		}
		return desiredChannel;
	}

	
	public Set<String> getInstalledPlugins()
	{
		Set<String> result = new HashSet<String>();
		IPluginManager pmgr = _app.getPluginManager();
		PluginInfo[] infos = pmgr.getPluginInformation();
		for (PluginInfo info : infos)
		{
			result.add(info.getInternalName());
		}
		return result;
	}

	
	public void pullDownUpdateFiles(List<ArtifactStatus> artifactStatusList, DownloadStatusListener listener)
	{

		List<ArtifactStatus> newartifactsList = new ArrayList<ArtifactStatus>();

		for (ArtifactStatus status : artifactStatusList)
		{
			if (status.getArtifactAction() == ArtifactAction.INSTALL)
			{
				newartifactsList.add(status);
			}
		}

		if (newartifactsList.size() > 0) {		
			_downloader = _downloaderFactory.create(newartifactsList);
			_downloader.setUtil(_util);
			_downloader.setProxySettings(_app.getSquirrelPreferences().getProxySettings());
			_downloader.setIsRemoteUpdateSite(isRemoteUpdateSite());
			_downloader.setHost(getUpdateServerName());
			_downloader.setPort(Integer.parseInt(getUpdateServerPort()));
			_downloader.setPath(getUpdateServerPath());
			_downloader.setFileSystemUpdatePath(getUpdateSettings().getFileSystemUpdatePath());
			_downloader.addDownloadStatusListener(listener);
			_downloader.setChannelName(getUpdateServerChannel().toLowerCase());
			_downloader.start();
		} else {
			showMessage(i18n.CHANGES_RECORDED_TITLE, i18n.CHANGES_RECORDED_MSG);
		}
	}

	
	public String getUpdateServerChannel()
	{
		return getUpdateSettings().getUpdateServerChannel();
	}

	
	public String getUpdateServerName()
	{
		return getUpdateSettings().getUpdateServer();
	}

	
	public boolean isRemoteUpdateSite()
	{
		return getUpdateSettings().isRemoteUpdateSite();
	}

	
	public String getUpdateServerPath()
	{
		return getUpdateSettings().getUpdateServerPath();
	}

	
	public String getUpdateServerPort()
	{
		return getUpdateSettings().getUpdateServerPort();
	}

	
	public int getUpdateServerPortAsInt()
	{
		return Integer.parseInt(getUpdateServerPort());
	}

	
	public boolean showConfirmMessage(String title, String msg)
	{
		int result =
			JOptionPane.showConfirmDialog(_app.getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return (result == JOptionPane.YES_OPTION);
	}

	
	public void showMessage(String title, String msg)
	{
		JOptionPane.showMessageDialog(_app.getMainFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE);

	}

	
	public void showErrorMessage(String title, String msg, Exception e)
	{
		s_log.error(msg, e);
		JOptionPane.showMessageDialog(_app.getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
	}

	
	public void showErrorMessage(String title, String msg)
	{
		showErrorMessage(title, msg, null);
	}

	
	public boolean isTimeToCheckForUpdates()
	{
		IUpdateSettings settings = getUpdateSettings();

		if (!settings.isEnableAutomaticUpdates()) { return false; }

		long lastCheckTime = Long.parseLong(settings.getLastUpdateCheckTimeMillis());
		long delta = currentTimeMillis() - lastCheckTime;

		UpdateCheckFrequency updateCheckFrequency = _util.getUpdateCheckFrequency(settings);

		return updateCheckFrequency.isTimeForUpdateCheck(delta);
	}

	
	public void promptUserToDownloadAvailableUpdates()
	{
		boolean userSaidYes =
			showConfirmMessage(i18n.PROMPT_TO_DOWNLOAD_AVAILABLE_UPDATES_TITLE,
				i18n.PROMPT_TO_DOWNLOAD_AVAILABLE_UPDATES_MSG);
		if (userSaidYes)
		{
			showUpdateDialog();
		}
		else
		{
			s_log.info("promptUserToDownloadAvailableUpdates: user decided not to download updates at "
				+ "this time (currentTimeMillis=" + System.currentTimeMillis() + ")");
		}
	}

	
	public void checkUpToDate()
	{
		UpdateCheckRunnableCallback callback = new UpdateCheckRunnableCallback()
		{

			public void updateCheckComplete(final boolean isUpdateToDate,
				final ChannelXmlBean installedChannelXmlBean, final ChannelXmlBean currentChannelXmlBean)
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						if (isUpdateToDate)
						{
							showMessage(i18n.UPDATE_CHECK_TITLE, i18n.SOFTWARE_VERSION_CURRENT_MSG);
						}
						
						_currentChannelBean = currentChannelXmlBean;
						_installedChannelBean = installedChannelXmlBean;
						List<ArtifactStatus> artifactStatusItems = _util.getArtifactStatus(_currentChannelBean);
						String installedVersion = _installedChannelBean.getCurrentRelease().getVersion();
						String currentVersion = _currentChannelBean.getCurrentRelease().getVersion();

						
						showUpdateSummaryDialog(artifactStatusItems, installedVersion, currentVersion);
					}

				});
			}

			public void updateCheckFailed(final Exception e)
			{
				if (e == null || e instanceof FileNotFoundException)
				{
					showErrorMessage(i18n.UPDATE_CHECK_FAILED_TITLE, i18n.RELEASE_FILE_DOWNLOAD_FAILED_MSG);
				}
				else
				{
					showErrorMessage(i18n.UPDATE_CHECK_FAILED_TITLE, i18n.EXCEPTION_MSG + e.getClass().getName()
						+ ":" + e.getMessage(), e);
				}
			}


			
		};

		ReleaseFileUpdateCheckTask runnable =
			new ReleaseFileUpdateCheckTask(callback, getUpdateSettings(), _util, _app);
		runnable.start();

	}

	private void showUpdateSummaryDialog(final List<ArtifactStatus> artifactStatusItems,
		final String installedVersion, final String currentVersion)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{

			public void run()
			{
				UpdateSummaryDialog dialog =
					new UpdateSummaryDialog(_app.getMainFrame(), artifactStatusItems, UpdateControllerImpl.this);
				dialog.setInstalledVersion(installedVersion);
				dialog.setAvailableVersion(currentVersion);
				GUIUtils.centerWithinParent(_app.getMainFrame());
				dialog.setVisible(true);
			}

		});
	}

	
	public void applyChanges(List<ArtifactStatus> artifactStatusList, boolean releaseVersionWillChange)
	{
		try
		{
			
			_util.saveChangeList(artifactStatusList);

			
			
			pullDownUpdateFiles(artifactStatusList, new DownloadStatusEventHandler());

			
			
			
			
			
			
			if (!releaseVersionWillChange)
			{
				_util.copyDir(_util.getSquirrelLibraryDir(), _util.getCoreDownloadsDir());
				_util.copyFile(_util.getInstalledSquirrelMainJarLocation(), _util.getCoreDownloadsDir());
			}
		}
		catch (Exception e)
		{
			showErrorMessage(i18n.UPDATE_CHECK_FAILED_TITLE, i18n.EXCEPTION_MSG + e.getClass().getName() + ":"
				+ e.getMessage(), e);
		}

	}

	
	public void showPreferences()
	{
		
		listener.setWaitingForOk(true);

		
		GlobalPreferencesSheet.showSheet(_app, UpdatePreferencesPanel.class);

	}

	

	
	private IUpdateSettings getUpdateSettings()
	{
		return _app.getSquirrelPreferences().getUpdateSettings();
	}

	
	private void saveUpdateSettings(final IUpdateSettings settings)
	{
		_app.getSquirrelPreferences().setUpdateSettings(settings);
	}

	private class GlobalPrefsListener implements GlobalPreferencesActionListener
	{

		private boolean waitingForOk = false;

		public void onDisplayGlobalPreferences()
		{
		}

		public void onPerformClose()
		{
			showDialog();
		}

		public void onPerformOk()
		{
			showDialog();
		}

		
		private void showDialog()
		{
			
			if (waitingForOk)
			{
				waitingForOk = false;
				showUpdateDialog();
			}
		}

		
		public void setWaitingForOk(boolean waitingForOk)
		{
			this.waitingForOk = waitingForOk;
		}
	}

	
	private class DownloadStatusEventHandler implements DownloadStatusListener
	{

		ProgressMonitor progressMonitor = null;

		int currentFile = 0;

		int totalFiles = 0;

		
		public void handleDownloadStatusEvent(DownloadStatusEvent evt)
		{

			if (progressMonitor != null && progressMonitor.isCanceled())
			{
				_downloader.stopDownload();
				return;
			}

			if (evt.getType() == DownloadEventType.DOWNLOAD_STARTED)
			{
				totalFiles = evt.getFileCountTotal();
				handleDownloadStarted();
			}
			if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_STARTED)
			{
				setNote("File: " + evt.getFilename());
			}

			if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_COMPLETED)
			{
				setProgress(++currentFile);
			}

			if (evt.getType() == DownloadEventType.DOWNLOAD_STOPPED)
			{
				setProgress(totalFiles);
			}

			
			
			if (evt.getType() == DownloadEventType.DOWNLOAD_COMPLETED)
			{
				showMessage(i18n.CHANGES_RECORDED_TITLE, i18n.CHANGES_RECORDED_MSG);
				setProgress(totalFiles);
			}
			if (evt.getType() == DownloadEventType.DOWNLOAD_FAILED)
			{
				showErrorMessage(i18n.UPDATE_DOWNLOAD_FAILED_TITLE, i18n.UPDATE_DOWNLOAD_FAILED_MSG);
				setProgress(totalFiles);
			}
		}

		private void setProgress(final int value)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					progressMonitor.setProgress(value);
				}
			});
		}

		private void setNote(final String note)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					progressMonitor.setNote(note);
				}
			});
		}

		private void handleDownloadStarted()
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					final MainFrame frame = UpdateControllerImpl.this._app.getMainFrame();
					progressMonitor =
						new ProgressMonitor(frame, i18n.DOWNLOADING_UPDATES_MSG, i18n.DOWNLOADING_UPDATES_MSG, 0,
							totalFiles);
					setProgress(0);
				}
			});
		}

	}
}
