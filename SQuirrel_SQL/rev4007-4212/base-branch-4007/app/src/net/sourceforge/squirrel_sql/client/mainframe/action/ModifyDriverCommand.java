package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ModifyDriverCommand implements ICommand
{
	
	private final IApplication _app;

	
	private ISQLDriver _driver;

	
	public ModifyDriverCommand(IApplication app, ISQLDriver driver)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (driver == null)
		{
			throw new IllegalArgumentException("ISQLDriver == null");
		}

		_app = app;
		_driver = driver;
	}

	
	public void execute()
	{
		_app.getWindowManager().showModifyDriverInternalFrame(_driver);
	}
}
