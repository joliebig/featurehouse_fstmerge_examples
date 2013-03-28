package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

class CheckTableCommand extends AbstractTableListCommand
{
	
	private int _checkType;

	
	public CheckTableCommand(ISession session, MysqlPlugin plugin, int checkType)
	{
		super(session, plugin);
		_checkType = checkType;
	}

	
	protected String getMySQLCommand()
	{
		return "check table";
	}

	
	protected String checkSQL(String sql)
	{
		final StringBuffer buf = new StringBuffer(sql);
		buf.append(' ')
			.append(getResolvedCheckType());
		return buf.toString();
	}

	private String getResolvedCheckType()
	{
		switch (_checkType)
		{
			case CheckTableAction.ICheckTypes.QUICK:
				return "Quick";
			case CheckTableAction.ICheckTypes.FAST:
				return "Fast";
			case CheckTableAction.ICheckTypes.MEDIUM:
				return "Medium";
			case CheckTableAction.ICheckTypes.EXTENDED:
				return "Extended";
			case CheckTableAction.ICheckTypes.CHANGED:
				return "Changed";
			default:
				throw new IllegalStateException("Invalid check type of " + _checkType);
		}
	}
}
