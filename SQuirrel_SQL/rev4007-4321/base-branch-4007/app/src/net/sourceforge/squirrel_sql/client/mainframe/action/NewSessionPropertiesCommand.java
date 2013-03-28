package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.NewSessionPropertiesSheet;

public class NewSessionPropertiesCommand
{
	
	private IApplication _app;

	
	public NewSessionPropertiesCommand(IApplication app)
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
		NewSessionPropertiesSheet.showSheet(_app);
	}
}
