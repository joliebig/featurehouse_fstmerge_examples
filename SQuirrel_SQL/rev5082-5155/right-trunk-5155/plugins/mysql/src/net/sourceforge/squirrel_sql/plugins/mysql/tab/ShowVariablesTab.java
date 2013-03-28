package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ShowVariablesTab extends BaseSQLTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ShowVariablesTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("mysql.variables");
		
		String HINT = s_stringMgr.getString("mysql.shoeVariables");
	}

	public ShowVariablesTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected String getSQL()
	{
		return "show variables";
	}
}
