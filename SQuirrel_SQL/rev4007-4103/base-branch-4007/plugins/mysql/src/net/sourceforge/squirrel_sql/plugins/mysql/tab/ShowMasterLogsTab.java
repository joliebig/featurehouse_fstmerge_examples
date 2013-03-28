package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class ShowMasterLogsTab extends BaseSQLTab
{
	



	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowSlaveStatusTab.class);

	public ShowMasterLogsTab()
	{
		super(s_stringMgr.getString("ShowMasterLogsTab.title"),
		s_stringMgr.getString("ShowMasterLogsTab.hint"));
	}

	protected String getSQL()
	{
		return "show master logs";
	}
}
