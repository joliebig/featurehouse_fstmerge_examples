package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

class ExplainSelectTableCommand extends AbstractMultipleSQLCommand
{
	
	public ExplainSelectTableCommand(ISession session, MysqlPlugin plugin)
	{
		super(session, plugin);
	}

	
	protected String getMySQLCommand(IDatabaseObjectInfo dbObj)
	{
		return "explain select * from " + dbObj.getQualifiedName();
	}
}
