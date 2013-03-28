package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ShowLogsTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowLogsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.logs");
		
		String HINT = s_stringMgr.getString("mysql.showLogs");
	}

	public ShowLogsTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		return "show logs";
	}
}
