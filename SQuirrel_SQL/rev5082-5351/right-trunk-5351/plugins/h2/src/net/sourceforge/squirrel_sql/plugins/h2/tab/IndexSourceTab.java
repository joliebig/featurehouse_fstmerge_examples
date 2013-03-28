package net.sourceforge.squirrel_sql.plugins.h2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;

public class IndexSourceTab extends FormattedSourceTab
{
	
	private static String SQL =
        "select " +
        "'create '||index_type_name||' '||index_name||' ON '||table_name||'('||column_name||')' " +
        "from INFORMATION_SCHEMA.INDEXES " +
        "where table_schema = ? " +
        "and index_name = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(IndexSourceTab.class);

	public IndexSourceTab(String hint, String stmtSep)
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
