package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class IndexColumnInfoTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexColumnInfoTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.columns");
		
		String HINT = s_stringMgr.getString("oracle.displayColumns");
	}

	
	private static String SQL =
            "select table_name, column_name, column_length, decode(descend, \'Y\', \'DESC\', \'ASC\')"
            + " from sys.all_ind_columns where index_owner = ?"
            + " and index_name = ?"
            + " order by column_position";

	public IndexColumnInfoTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
