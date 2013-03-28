package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

class AnalyzeTableCommand extends AbstractTableListCommand
{
	
	public AnalyzeTableCommand(ISession session, MysqlPlugin plugin)
	{
		super(session, plugin);
	}

	
	protected String getMySQLCommand()
	{
		return "analyze table";
	}
}
