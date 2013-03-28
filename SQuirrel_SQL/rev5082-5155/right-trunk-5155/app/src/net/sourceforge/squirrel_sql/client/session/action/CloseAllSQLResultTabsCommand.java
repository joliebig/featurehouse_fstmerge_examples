package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

public class CloseAllSQLResultTabsCommand implements ICommand
{
	private final ISQLPanelAPI _api;

	
	public CloseAllSQLResultTabsCommand(ISQLPanelAPI api)
	{
		super();
		if (api == null)
		{
			throw new IllegalArgumentException("ISQLPanelAPI == null");
		}

		_api = api;
	}

	public void execute()
	{
		_api.closeAllSQLResultTabs();
	}
}
