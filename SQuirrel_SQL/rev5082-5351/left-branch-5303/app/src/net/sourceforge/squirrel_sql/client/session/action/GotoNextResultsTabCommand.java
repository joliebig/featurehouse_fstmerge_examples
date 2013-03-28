package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class GotoNextResultsTabCommand implements ICommand
{
	
	private final ISQLPanelAPI _panel;

	
	public GotoNextResultsTabCommand(ISQLPanelAPI panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("ISQLPanelAPI == null");
		}
		_panel = panel;
	}

	
	public void execute()
	{
		_panel.gotoNextResultsTab();
	}
}