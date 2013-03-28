package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class TableStatusTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TableStatusTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.tabStatus");
		
		String HINT = s_stringMgr.getString("mysql.hintTabStatus");
	}

	public TableStatusTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		final String db = getDatabaseObjectInfo().getQualifiedName();
		return "show table status from " + db;
	}
}
