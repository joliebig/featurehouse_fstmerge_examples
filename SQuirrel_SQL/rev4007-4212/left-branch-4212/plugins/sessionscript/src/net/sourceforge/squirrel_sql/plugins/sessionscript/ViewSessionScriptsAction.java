package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class ViewSessionScriptsAction extends SquirrelAction
{
	private SessionScriptPlugin _plugin;

	
	public ViewSessionScriptsAction(IApplication app, Resources rsrc,
										SessionScriptPlugin plugin)
	{
		super(app, rsrc);
		if (plugin == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}

		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		ViewSessionScriptsCommand cmd = new ViewSessionScriptsCommand(
							getApplication(), getParentFrame(evt), _plugin);
		cmd.execute();
	}
}


