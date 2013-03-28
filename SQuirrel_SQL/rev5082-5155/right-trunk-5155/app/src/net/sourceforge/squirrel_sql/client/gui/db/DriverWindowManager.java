package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;

public class DriverWindowManager
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(WindowManager.class);

	
	private final IApplication _app;

	
	private final DriverWindowFactory _driverWinFactory;

	
	public DriverWindowManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
		_driverWinFactory = new DriverWindowFactory(_app);
	}

	
	public void showModifyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

      _driverWinFactory.getModifySheet(driver).moveToFront();
	}

	
	public void showNewDriverInternalFrame()
	{
      _driverWinFactory.getCreateSheet().moveToFront();
	}

	
	public void showCopyDriverInternalFrame(final ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

      _driverWinFactory.showCopySheet(driver).moveToFront();
   }

	public void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					GUIUtils.moveToFront(fr);
				}
			});
		}
		else
		{
			s_log.debug("JInternalFrame == null");
		}
	}
}
