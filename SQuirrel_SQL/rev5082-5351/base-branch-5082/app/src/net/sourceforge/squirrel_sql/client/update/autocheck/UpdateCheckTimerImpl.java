
package net.sourceforge.squirrel_sql.client.update.autocheck;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.update.UpdateCheckFrequency;
import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactory;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactoryImpl;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.client.update.async.ReleaseFileUpdateCheckTask;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateCheckTimerImpl implements UpdateCheckTimer
{

	private UpdateCheckRunnable runnable = new UpdateCheckRunnable();

	private UpdateControllerFactory updateControllerFactory = new UpdateControllerFactoryImpl();

	private UpdateController updateController = null;

	private IApplication _app = null;

	private IUpdateSettings _updateSettings = null;

	
	private static final ILogger s_log = LoggerController.createLogger(UpdateCheckTimerImpl.class);
	
	private UpdateUtil _util = new UpdateUtilImpl();
	
	public UpdateCheckTimerImpl(IApplication app)
	{
		this._app = app;
		_updateSettings = _app.getSquirrelPreferences().getUpdateSettings();
		
	}

	public void start()
	{
		if (!_updateSettings.isEnableAutomaticUpdates()) {
			return;
		}
		updateController =
			updateControllerFactory.createUpdateController(_app, new ArtifactDownloaderFactoryImpl(), _util);
		Thread t = new Thread(runnable);
		t.setName("Update Check Timer Thread");
		t.start();
	}

	public void stop()
	{
		if (!_updateSettings.isEnableAutomaticUpdates()) {
			return;
		}		
		runnable.stop();
	}

	
	private class UpdateCheckRunnable implements Runnable
	{

		private boolean stopped = false;

		private boolean firstCheck = true;

		
		public void run()
		{
			
			
			Utilities.sleep(120 * 1000L);

			while (!stopped)
			{
				if (firstCheck)
				{
					firstCheck = false;
					if (isUpdateCheckFrequencyAtStartup() && !isUpToDate())
					{
						logDebug("run: update check configured for startup and software is not up-to-date");
						updateController.promptUserToDownloadAvailableUpdates();
						
						
						
						return;
					}
				}
				else
				{
					logDebug("run: not the first check; sleeping for an hour.");
					sleepForAnHour();
				}

				if (!isUpdateCheckFrequencyAtStartup() && updateController.isTimeToCheckForUpdates())
				{
					logDebug("run: not configured to check at startup and it's now time to check again.");
					if (!isUpToDate())
					{
						logDebug("run: software is not up-to-date, so prompting user to download updates.");
						updateController.promptUserToDownloadAvailableUpdates();
					}
				}
			}

		}
		
		private void logDebug(String msg) {
			if (s_log.isDebugEnabled()) {
				s_log.debug(msg);
			}
		}

		private boolean isUpToDate()
		{
			boolean result = true;
			try
			{
				logDebug("isUpToDate: checking to see if software is up-to-date; currentTimeMillis = "+
					System.currentTimeMillis());
				
				ReleaseFileUpdateCheckTask task = 
					new ReleaseFileUpdateCheckTask(null, _updateSettings, _util, _app);
				
				
				task.run();
				result = task.isUpToDate();
			}
			catch (Exception e)
			{
				s_log.error("isUpToDate: Unable to determine up-to-date status: "+e.getMessage(), e);
			}
			return result;
		}

		private boolean isUpdateCheckFrequencyAtStartup()
		{
			String freqStr = _updateSettings.getUpdateCheckFrequency();
			UpdateCheckFrequency updateCheckFrequency = UpdateCheckFrequency.getEnumForString(freqStr);

			return updateCheckFrequency == UpdateCheckFrequency.STARTUP;
		}

		public void stop()
		{
			stopped = true;
		}
		
		private void sleepForAnHour()
		{
			Utilities.sleep(1000 * 60 * 60);
		}
		
	}

}
