package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class CreateAliasCommand implements ICommand
{
	
	private final IApplication _app;

	
	public CreateAliasCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
	}

	
	public void execute()
	{
		_app.getWindowManager().showNewAliasInternalFrame();
	}
}
