package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class ToolsPopupAction extends SquirrelAction
											implements ISQLPanelAction
{
	private ISQLPanelAPI _panel;

	
	public ToolsPopupAction(IApplication app)
	{
		super(app);
	}

	public void setSQLPanel(ISQLPanelAPI panel)
	{
		_panel = panel;
      setEnabled(null != panel);


	}

	
	public void actionPerformed(ActionEvent evt)
	{
      if(null == _panel)
      {
         return;
      }

      _panel.showToolsPopup();
	}
}
