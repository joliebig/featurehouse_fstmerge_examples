package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowColumnsTab extends BaseSQLTab
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowColumnsTab.class);

	public ShowColumnsTab()
	{
		super(s_stringMgr.getString("ShowColumnsTab.title"),
				s_stringMgr.getString("ShowColumnsTab.hint"));
	}

	protected String getSQL()
	{
		final String tbl = getDatabaseObjectInfo().getQualifiedName();
		return "show full columns from " + tbl;
	}
}
