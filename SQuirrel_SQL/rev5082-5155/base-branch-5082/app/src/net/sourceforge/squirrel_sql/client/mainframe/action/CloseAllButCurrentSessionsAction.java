package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;


public class CloseAllButCurrentSessionsAction extends SquirrelAction
{
	
	public CloseAllButCurrentSessionsAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
		cursorChg.show();
		try
		{
			new CloseAllButCurrentSessionsCommand(app).execute();
		}
		finally
		{
			cursorChg.restore();
		}
	}
}