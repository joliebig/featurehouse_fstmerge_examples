package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class GlobalPreferencesAction extends SquirrelAction
{
	public GlobalPreferencesAction(IApplication app)
	{
		super(app);
	}

	public void actionPerformed(ActionEvent evt)
	{
		new GlobalPreferencesCommand(getApplication()).execute();
	}
}
