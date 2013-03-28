package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class DisplayPluginSummaryAction extends SquirrelAction
{
	
	public DisplayPluginSummaryAction(IApplication app)
	{
		super(app);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		final IApplication app = getApplication();
		new DisplayPluginSummaryCommand(app, getParentFrame(evt)).execute();
	}
}
