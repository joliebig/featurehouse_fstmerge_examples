package net.sourceforge.squirrel_sql.plugins.derby.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ViewSourceTab extends BaseSourceTab
{
	
	private static String SQL =
        "select v.VIEWDEFINITION " +
        "from sys.SYSVIEWS v, sys.SYSTABLES t, sys.SYSSCHEMAS s " +
        "where v.TABLEID = t.TABLEID " +
        "and s.SCHEMAID = t.SCHEMAID " +
        "and t.TABLENAME = ? " +
        "and s.SCHEMANAME = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

	public ViewSourceTab(String hint)
	{
		super(hint);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running View Source SQL: "+SQL);
            s_log.debug("View Name="+doi.getSimpleName());
            s_log.debug("Schema Name="+doi.getSchemaName());
        }        
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName()); 
        pstmt.setString(2, doi.getSchemaName()); 
		return pstmt;
	}
}
