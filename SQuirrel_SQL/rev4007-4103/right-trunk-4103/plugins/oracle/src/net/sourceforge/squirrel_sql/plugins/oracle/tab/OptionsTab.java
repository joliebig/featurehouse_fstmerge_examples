package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class OptionsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OptionsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.options");
		
		String HINT = s_stringMgr.getString("oracle.displayOptions");
	}

	
	private static String SQL =
		"select parameter, value from sys.v_$option";

	public OptionsTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		return getSession().getSQLConnection().prepareStatement(SQL);
	}
}
