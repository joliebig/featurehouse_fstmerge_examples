package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ShowIndexesTab extends BaseSQLTab
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowIndexesTab.class);

	public ShowIndexesTab()
	{
		super(s_stringMgr.getString("ShowIndexesTab.title"),
				s_stringMgr.getString("ShowIndexesTab.hint"));
	}

	protected String getSQL()
	{
		final String tbl = getDatabaseObjectInfo().getQualifiedName();
		return "show indexes from " + tbl;
	}
}
