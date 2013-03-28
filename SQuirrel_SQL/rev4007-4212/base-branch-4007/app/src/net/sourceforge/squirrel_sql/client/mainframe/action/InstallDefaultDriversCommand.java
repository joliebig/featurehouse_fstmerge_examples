package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class InstallDefaultDriversCommand implements ICommand
{
	
	private final IApplication _app;

	
	private final URL _url;

	
	public InstallDefaultDriversCommand(IApplication app, URL url)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}

		_app = app;
		_url = url;
	}

	
	public void execute() throws BaseException
	{
		try
		{
			final DataCache cache = _app.getDataCache();
			cache.loadDefaultDrivers(_url);
			new ShowLoadedDriversOnlyCommand(_app, false).execute();
		}
		catch (IOException ex)
		{
			throw new BaseException(ex);
		}
	}
}
