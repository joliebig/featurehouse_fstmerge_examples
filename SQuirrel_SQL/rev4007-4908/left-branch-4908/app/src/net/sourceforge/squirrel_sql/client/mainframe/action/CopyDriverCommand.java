package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class CopyDriverCommand implements ICommand
{
	
	private final IApplication _app;

	
	private final ISQLDriver _sqlDriver;

	
	public CopyDriverCommand(IApplication app, ISQLDriver sqlDriver)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlDriver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_app = app;
		_sqlDriver = sqlDriver;
	}

	public void execute()
	{
		_app.getWindowManager().showCopyDriverInternalFrame(_sqlDriver);
	}
}
