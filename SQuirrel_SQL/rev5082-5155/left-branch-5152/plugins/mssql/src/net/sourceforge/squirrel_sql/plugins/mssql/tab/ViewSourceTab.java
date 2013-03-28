package net.sourceforge.squirrel_sql.plugins.mssql.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ViewSourceTab extends BaseSourceTab
{

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ViewSourceTab.class);
    
    
	private interface i18n
	{
        
		String HINT = s_stringMgr.getString("ViewSourceTab.display");
	}
	
	
	private static final String BIGVIEW_SQL = 
		"SELECT text  FROM sysobjects o , syscomments c " +
		"where  o.name = ? " +
		"and o.id = c.id " +
		"order by c.colid ";		
		
		
    
	public ViewSourceTab()
	{
		super(i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(BIGVIEW_SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
