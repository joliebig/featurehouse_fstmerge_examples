package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;




public class ProcessesTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProcessesTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.processes");
		
		String HINT = s_stringMgr.getString("mysql.displayProcesses");
	}

	



	public ProcessesTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		return "show full processlist";
	}
}
