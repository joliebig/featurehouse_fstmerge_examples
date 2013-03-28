package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ModifyAliasCommand implements ICommand
{
	
	private final IApplication _app;

	
	private final ISQLAlias _sqlAlias;

	
	public ModifyAliasCommand(IApplication app, ISQLAlias sqlAlias)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == nu;;");
		}
		_app = app;
		_sqlAlias = sqlAlias;
	}

	
	public void execute()
	{
		_app.getWindowManager().showModifyAliasInternalFrame(_sqlAlias);
	}
}
