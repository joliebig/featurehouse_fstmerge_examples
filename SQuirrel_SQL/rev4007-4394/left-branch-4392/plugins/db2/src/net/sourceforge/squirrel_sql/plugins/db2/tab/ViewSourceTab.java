package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;

public class ViewSourceTab extends FormattedSourceTab
{
	
	private static final String SQL =
        "SELECT TEXT " +
        "FROM SYSCAT.VIEWS " +
        "WHERE VIEWSCHEMA = ? " +
        "AND VIEWNAME = ? ";
	
	
	private static final String OS_400_SQL = 
	    "select view_definition " +
	    "from qsys2.sysviews " +
	    "where table_schema = ? " +
	    "and table_name = ? ";
	
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

    
    private boolean isOS400 = false;    
	
        
	public ViewSourceTab(String hint, String stmtSep, boolean isOS400)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null); 
        this.isOS400 = isOS400;
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		ISQLConnection conn = session.getSQLConnection();
        String sql = SQL;
        if (isOS400) {
            sql = OS_400_SQL;
        }		
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("view name="+doi.getSimpleName());            
        }
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
