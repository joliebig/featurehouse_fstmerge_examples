package net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

public class GetInvalidObjectsAction extends SquirrelAction
{
        private InvalidObjectsPanel _panel;

	public GetInvalidObjectsAction(IApplication app, Resources resources, InvalidObjectsPanel panel)
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
                        _panel.repopulateInvalidObjects();
                }
                finally
                {
                        cursorChg.restore();
                }
	}
}
