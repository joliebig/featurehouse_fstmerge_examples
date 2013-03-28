package net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

public class GetSessionInfoAction extends SquirrelAction
{
        private SessionInfoPanel _panel;

	public GetSessionInfoAction(IApplication app, Resources resources, SessionInfoPanel panel)
	{
		super(app, resources);
                _panel = panel;
	}

	public void actionPerformed(ActionEvent evt)
	{
                CursorChanger cursorChg = new CursorChanger(getApplication().getMainFrame());
                cursorChg.show();
                try
                {
                        _panel.populateSessionInfo();
                }
                finally
                {
                        cursorChg.restore();
                }
	}
}
