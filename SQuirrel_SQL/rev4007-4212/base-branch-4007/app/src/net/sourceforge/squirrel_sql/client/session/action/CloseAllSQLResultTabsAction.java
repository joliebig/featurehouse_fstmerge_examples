package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class CloseAllSQLResultTabsAction extends SquirrelAction
											implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	
	public CloseAllSQLResultTabsAction(IApplication app)
	{
		super(app);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
      setEnabled(null != _panel);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
      if(null == _panel)
      {
         return;
      }

		IApplication app = getApplication();
		CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
		cursorChg.show();
		try
		{
			new CloseAllSQLResultTabsCommand(_panel).execute();
		}
		finally
		{
			cursorChg.restore();
		}
	}
}
