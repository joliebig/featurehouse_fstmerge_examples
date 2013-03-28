package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class ShowLoadedDriversOnlyCommand implements ICommand
{
	
	private final IApplication _app;

	
	private Boolean _show;

	
	public ShowLoadedDriversOnlyCommand(IApplication app)
	{
		this(app, null);
	}

	
	public ShowLoadedDriversOnlyCommand(IApplication app, boolean show)
	{
		this(app, Boolean.valueOf(show));
	}

	
	private ShowLoadedDriversOnlyCommand(IApplication app, Boolean show)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		_show = show;
	}

	
	public void execute()
	{
		SquirrelPreferences prefs = _app.getSquirrelPreferences();
		if (_show == null)
		{
			prefs.setShowLoadedDriversOnly(!prefs.getShowLoadedDriversOnly());
		}
		else
		{
			prefs.setShowLoadedDriversOnly(_show.booleanValue());
		}
	}
}
