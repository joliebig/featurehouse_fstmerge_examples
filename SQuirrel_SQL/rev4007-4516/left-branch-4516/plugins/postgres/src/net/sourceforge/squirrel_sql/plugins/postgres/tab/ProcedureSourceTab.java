package net.sourceforge.squirrel_sql.plugins.postgres.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ProcedureSourceTab extends FormattedSourceTab
{
	
	private static String SQL =
        "SELECT p.prosrc FROM pg_proc p, pg_namespace n " +
        "where p.pronamespace = n.oid " +
        "and n.nspname = ? " +
        "and p.proname = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ProcedureSourceTab.class);

	public ProcedureSourceTab(String hint)
	{
		super(hint);
		super.setCompressWhitespace(false);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for procedure source: "+SQL);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("procedure name="+doi.getSimpleName());
        }
        
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        
        
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
