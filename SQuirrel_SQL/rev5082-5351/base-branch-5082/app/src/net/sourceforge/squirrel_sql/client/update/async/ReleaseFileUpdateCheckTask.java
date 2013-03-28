
package net.sourceforge.squirrel_sql.client.update.async;

import static java.lang.System.currentTimeMillis;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.RELEASE_XML_FILENAME;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ReleaseFileUpdateCheckTask implements Runnable
{

	
	private static final ILogger s_log = LoggerController.createLogger(ReleaseFileUpdateCheckTask.class);

	private UpdateCheckRunnableCallback _callback = null;

	private IUpdateSettings _settings = null;

	private UpdateUtil _util = null;

	private IApplication _app = null;

	private boolean isUpToDate = false;

	public ReleaseFileUpdateCheckTask(UpdateCheckRunnableCallback callback, IUpdateSettings settings,
		UpdateUtil util, IApplication app)
	{
		Utilities.checkNull("ReleaseFileUpdateCheckRunnable", "settings", settings, "util", util, "app", app);
		_callback = callback;
		_settings = settings;
		_util = util;
		_app = app;
	}

	public void start()
	{
		if (_app == null) { throw new IllegalStateException(
			"_app was null - cannot access the thread pool for asynchronous use"); }
		_app.getThreadPool().addTask(this);
	}

	
	public void run()
	{
		String releaseFilename = null;

		
		try
		{
			releaseFilename = _util.getLocalReleaseFile().getAbsolutePath();
		}
		catch (Exception e)
		{
			s_log.error("Unexpected exception while attempting to find local release file: "+e.getMessage(), e);
			if (_callback != null) {
				_callback.updateCheckFailed(e);
			}
			return;
		}

		
		ChannelXmlBean installedChannelBean = _util.getLocalReleaseInfo(releaseFilename);

		
		
		ChannelXmlBean currentChannelBean = getCurrentChannelXmlBean(installedChannelBean);

		
		_settings.setLastUpdateCheckTimeMillis("" + currentTimeMillis());
		_app.getSquirrelPreferences().setUpdateSettings(_settings);

		
		
		if (currentChannelBean == null)
		{
			s_log.warn("run: currentChannelBean was null - it is inconclusive whether or not the software "
				+ "is current : assuming that it is for now");
			if (_callback != null) {
				_callback.updateCheckFailed(null);
			}
		}
		else
		{
			isUpToDate = currentChannelBean.equals(installedChannelBean);
			if (_callback != null)
			{
				_callback.updateCheckComplete(isUpToDate, installedChannelBean, currentChannelBean);
			}
		}
	}

	
	private ChannelXmlBean getCurrentChannelXmlBean(ChannelXmlBean installedChannelBean)
	{
		ChannelXmlBean currentChannelBean = null;
		if (_settings.isRemoteUpdateSite())
		{
			
			String channelName = getDesiredChannel(_settings, installedChannelBean);

			try
			{
				StringBuilder releasePath = new StringBuilder("/");
				releasePath.append(_settings.getUpdateServerPath());
				releasePath.append("/");
				releasePath.append(channelName);
				releasePath.append("/");

				currentChannelBean =
					_util.downloadCurrentRelease(_settings.getUpdateServer(),
						Integer.parseInt(_settings.getUpdateServerPort()), releasePath.toString(),
						RELEASE_XML_FILENAME, _app.getSquirrelPreferences().getProxySettings());
			}
			catch (Exception e)
			{
				s_log.error("Unexpected exception: " + e.getMessage(), e);
			}
		}
		else
		{
			currentChannelBean = _util.loadUpdateFromFileSystem(_settings.getFileSystemUpdatePath());
		}
		return currentChannelBean;
	}

	
	private String getDesiredChannel(final IUpdateSettings settings, final ChannelXmlBean _installedChannelBean)
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

	
	public boolean isUpToDate()
	{
		return isUpToDate;
	}

}
