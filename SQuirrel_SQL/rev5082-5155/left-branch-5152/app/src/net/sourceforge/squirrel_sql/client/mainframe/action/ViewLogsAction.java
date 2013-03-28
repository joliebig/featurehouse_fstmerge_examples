package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class ViewLogsAction extends SquirrelAction
{
	
	public ViewLogsAction(IApplication app)
	{
		super(app);
		app.getResources().setupAction(this, true);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		new ViewLogsCommand(getApplication()).execute();
	}
}
