package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;

public class TriggerSourceTab extends FormattedSourceTab
{
	
	private final static String SQL =
        "select TEXT from SYSCAT.TRIGGERS " +
        "where TABSCHEMA = ? " +
        "and TRIGNAME = ? ";
    
	
	private final static String OS2_400_SQL = 
	    "select action_statement " +
	    "from qsys2.systriggers " +
	    "where trigger_schema = ? " +
	    "and trigger_name = ? ";
	
	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerSourceTab.class);
	
	private boolean isOS2400 = false;

	public TriggerSourceTab(String hint, boolean isOS2400, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
        this.isOS2400 = isOS2400;
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		String sql = SQL;
		if (isOS2400) {
		    sql = OS2_400_SQL;
		}
		
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("trigname="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
