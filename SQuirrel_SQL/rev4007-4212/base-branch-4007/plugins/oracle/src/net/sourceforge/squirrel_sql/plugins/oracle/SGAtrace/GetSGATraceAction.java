package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

public class GetSGATraceAction extends SquirrelAction
{
        private SGATracePanel _panel;

	public GetSGATraceAction(IApplication app, Resources resources, SGATracePanel panel)
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
                        _panel.populateSGATrace();
                }
                finally
                {
                        cursorChg.restore();
                }
	}
}
