package net.sourceforge.squirrel_sql.client.gui.db;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

class DriverWindowFactory implements AliasInternalFrame.IMaintenanceType
{
	
	private static ILogger s_log =
		LoggerController.createLogger(DriverWindowFactory.class);

	
	private IApplication _app;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DriverWindowFactory.class);
    
	
	private final Map<IIdentifier, DriverInternalFrame> _modifySheets = 
        new HashMap<IIdentifier, DriverInternalFrame>();

	
	public DriverWindowFactory(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
	}

	
	public synchronized DriverInternalFrame getModifySheet(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		DriverInternalFrame sheet = get(driver);
		if (sheet == null)
		{
			sheet = new DriverInternalFrame(_app, driver, MODIFY);
			_modifySheets.put(driver.getIdentifier(), sheet);
			_app.getMainFrame().addWidget(sheet);

			sheet.addWidgetListener(new WidgetAdapter()
			{
            @Override
            public void widgetClosed(WidgetEvent evt)
            {
               synchronized (DriverWindowFactory.this)
               {
                  DriverInternalFrame frame = (DriverInternalFrame) evt.getWidget();
                  _modifySheets.remove(frame.getSQLDriver().getIdentifier());
               }
            }

			});
			DialogWidget.centerWithinDesktop(sheet);
		}

		return sheet;
	}

	
	public DriverInternalFrame getCreateSheet()
	{
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLDriver driver = cache.createDriver(factory.createIdentifier());
		final DriverInternalFrame sheet = new DriverInternalFrame(_app, driver, NEW);
		_app.getMainFrame().addWidget(sheet);
		DialogWidget.centerWithinDesktop(sheet);
		return sheet;
	}

	
	public DriverInternalFrame showCopySheet(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		final DataCache cache = _app.getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		ISQLDriver newDriver = cache.createDriver(factory.createIdentifier());
		try
		{
			newDriver.assignFrom(driver);
		}
		catch (ValidationException ex)
		{
            
			s_log.error(s_stringMgr.getString("DriverWindowFactory.error.copyingdriver"), ex);
		}
		final DriverInternalFrame sheet =
			new DriverInternalFrame(_app, newDriver, COPY);
		_app.getMainFrame().addWidget(sheet);
		DialogWidget.centerWithinDesktop(sheet);

		return sheet;
	}

	private DriverInternalFrame get(ISQLDriver driver)
	{
		return _modifySheets.get(driver.getIdentifier());
	}
}

