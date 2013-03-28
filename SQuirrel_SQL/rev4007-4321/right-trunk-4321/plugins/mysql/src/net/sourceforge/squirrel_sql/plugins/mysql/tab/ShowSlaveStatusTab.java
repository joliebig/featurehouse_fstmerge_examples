package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowSlaveStatusTab extends BaseSQLTab
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowSlaveStatusTab.class);

	public ShowSlaveStatusTab()
	{
		super(s_stringMgr.getString("ShowSlaveStatusTab.title"),
				s_stringMgr.getString("ShowSlaveStatusTab.hint"));
	}

	protected String getSQL()
	{
		return "show slave status";
	}
}
