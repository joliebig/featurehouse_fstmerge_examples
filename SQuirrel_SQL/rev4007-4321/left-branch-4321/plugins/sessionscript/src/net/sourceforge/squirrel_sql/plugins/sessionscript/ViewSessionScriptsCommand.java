package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ViewSessionScriptsCommand implements ICommand
{
	
	private final Frame _frame;

	
	private SessionScriptPlugin _plugin;

	
	private IApplication _app;

	
	public ViewSessionScriptsCommand(IApplication app, Frame frame,
										SessionScriptPlugin plugin)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null IPlugin passed");
		}

		_app = app;
		_frame = frame;
		_plugin = plugin;
	}

	
	public void execute()
	{
		ScriptsSheet.showSheet(_plugin, _app);
	}

}
