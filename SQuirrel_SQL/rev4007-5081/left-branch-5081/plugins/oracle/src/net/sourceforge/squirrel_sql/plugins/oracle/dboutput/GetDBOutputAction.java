package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

public class GetDBOutputAction extends SquirrelAction
{
        private DBOutputPanel _panel;

	public GetDBOutputAction(IApplication app, Resources resources, DBOutputPanel panel)
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
                        _panel.populateDBOutput();
                }
                finally
                {
                        cursorChg.restore();
                }
	}
}
