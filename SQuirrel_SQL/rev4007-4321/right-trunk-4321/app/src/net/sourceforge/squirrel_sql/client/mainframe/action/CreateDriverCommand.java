package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;


public class CreateDriverCommand implements ICommand
{
	
	private final IApplication _app;

	
	public CreateDriverCommand(IApplication app)
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
		_app.getWindowManager().showNewDriverInternalFrame();
	}
}
