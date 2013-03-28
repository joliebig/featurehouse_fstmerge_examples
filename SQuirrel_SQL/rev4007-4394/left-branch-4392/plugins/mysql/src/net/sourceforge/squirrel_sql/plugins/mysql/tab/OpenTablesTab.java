package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;




public class OpenTablesTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OpenTablesTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.openTables");
		
		String HINT = s_stringMgr.getString("mysql.displayOpenTables");
	}

	



	public OpenTablesTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		final String catalog = getDatabaseObjectInfo().getSimpleName();
		return "show open tables from " + catalog;
	}
}
