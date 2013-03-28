package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;




public class DatabaseStatusTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DatabaseStatusTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.status");
		
		String HINT = s_stringMgr.getString("mysql.displayStatus");
	}

	



	public DatabaseStatusTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		return "show status";
	}
}
