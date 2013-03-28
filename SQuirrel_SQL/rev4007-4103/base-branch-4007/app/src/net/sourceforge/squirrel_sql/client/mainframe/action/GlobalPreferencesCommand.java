package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;

public class GlobalPreferencesCommand implements ICommand
{
	
	private IApplication _app;

	
	public GlobalPreferencesCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	
	public void execute()
	{
		GlobalPreferencesSheet.showSheet(_app, null);
	}
}