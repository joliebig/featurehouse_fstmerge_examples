package net.sourceforge.squirrel_sql.plugins.postgres.tab;

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
	
	private static String SQL =
        "select view_definition " +
        "from information_schema.views " +
        "where table_schema = ? " +
        "and table_name = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

	public ViewSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+SQL);
        }
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
