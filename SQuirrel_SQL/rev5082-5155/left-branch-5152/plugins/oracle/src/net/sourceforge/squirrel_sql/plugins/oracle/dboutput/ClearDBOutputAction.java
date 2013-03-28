package net.sourceforge.squirrel_sql.plugins.oracle.dboutput;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class ClearDBOutputAction extends SquirrelAction
{
        private DBOutputPanel _panel;

	public ClearDBOutputAction(IApplication app, Resources resources, DBOutputPanel panel)
	{
		super(app, resources);
                _panel = panel;
	}

	public void actionPerformed(ActionEvent evt)
	{
          _panel.clearOutput();
	}
}
