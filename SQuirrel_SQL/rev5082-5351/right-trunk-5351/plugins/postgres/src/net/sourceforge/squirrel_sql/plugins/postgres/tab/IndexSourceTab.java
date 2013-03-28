package net.sourceforge.squirrel_sql.plugins.postgres.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.ViewSourceTab;

public class IndexSourceTab extends FormattedSourceTab
{
	
	private static String SQL =
        "select indexdef " +
        "from pg_indexes " +
        "where schemaname = ? " +
        "and indexname = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

	public IndexSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setupFormatter(stmtSep, null);
        super.setCompressWhitespace(true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for index source tab: "+SQL);
            s_log.debug("schema name: "+doi.getSchemaName());
            s_log.debug("index name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
