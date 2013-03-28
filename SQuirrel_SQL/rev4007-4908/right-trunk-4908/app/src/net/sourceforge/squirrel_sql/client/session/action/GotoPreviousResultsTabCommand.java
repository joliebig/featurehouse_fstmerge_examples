package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class GotoPreviousResultsTabCommand implements ICommand
{
	
	private final ISQLPanelAPI _panel;

	
	public GotoPreviousResultsTabCommand(ISQLPanelAPI panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_panel = panel;
	}

	
	public void execute()
	{
		_panel.gotoPreviousResultsTab();
	}
}