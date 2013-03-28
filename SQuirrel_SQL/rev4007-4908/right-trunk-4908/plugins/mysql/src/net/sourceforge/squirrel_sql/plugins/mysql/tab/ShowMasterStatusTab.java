package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowMasterStatusTab extends BaseSQLTab
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowMasterStatusTab.class);

	public ShowMasterStatusTab()
	{
		super(s_stringMgr.getString("ShowMasterStatusTab.title"),
				s_stringMgr.getString("ShowMasterStatusTab.hint"));
	}

	protected String getSQL()
	{
		return "show master status";
	}
}
