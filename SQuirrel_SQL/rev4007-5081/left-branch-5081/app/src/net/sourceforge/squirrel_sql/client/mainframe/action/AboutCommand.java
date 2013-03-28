package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.AboutBoxDialog;

public class AboutCommand implements ICommand
{
	
	private IApplication _app;

	
	public AboutCommand(IApplication app)
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
		AboutBoxDialog.showAboutBox(_app);
	}
}
