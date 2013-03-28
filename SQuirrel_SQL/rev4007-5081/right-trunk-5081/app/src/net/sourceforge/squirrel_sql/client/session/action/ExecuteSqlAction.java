package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class ExecuteSqlAction extends SquirrelAction implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	public ExecuteSqlAction(IApplication app)
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
		if (_panel != null)
		{
			CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
			cursorChg.show();
			try
			{
				_panel.executeCurrentSQL();
			}
			finally
			{
				cursorChg.restore();
			}
		}
	}
}
