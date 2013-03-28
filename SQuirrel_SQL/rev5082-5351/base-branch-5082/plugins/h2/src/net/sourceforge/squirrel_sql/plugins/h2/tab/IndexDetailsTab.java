package net.sourceforge.squirrel_sql.plugins.h2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class IndexDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");
		
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	
	private static final String SQL =
        "SELECT TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME, " +
        "NON_UNIQUE,ORDINAL_POSITION,COLUMN_NAME, " +
        "CARDINALITY,PRIMARY_KEY,INDEX_TYPE_NAME, " +
        "IS_GENERATED,INDEX_TYPE,ASC_OR_DESC,PAGES, " +
        "FILTER_CONDITION,REMARKS " +
        "FROM INFORMATION_SCHEMA.INDEXES " +
        "WHERE TABLE_SCHEMA = ? " +
        "AND INDEX_NAME = ? ";
        
	public IndexDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
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
