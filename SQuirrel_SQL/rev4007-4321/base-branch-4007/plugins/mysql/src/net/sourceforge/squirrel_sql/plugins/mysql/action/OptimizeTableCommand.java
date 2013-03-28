package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

class OptimizeTableCommand extends AbstractTableListCommand
{
	
	public OptimizeTableCommand(ISession session, MysqlPlugin plugin)
	{
		super(session, plugin);
	}

	
	protected String getMySQLCommand()
	{
		return "optimize table";
	}
}
