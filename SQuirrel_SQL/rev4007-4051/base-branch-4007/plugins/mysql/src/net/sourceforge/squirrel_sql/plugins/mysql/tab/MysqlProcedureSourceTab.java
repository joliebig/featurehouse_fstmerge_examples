package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MysqlProcedureSourceTab extends FormattedSourceTab {
	
	private static String SQL =
        "select routine_definition " +
        "from information_schema.ROUTINES " +
        "where ROUTINE_SCHEMA = ? " +
        "and ROUTINE_NAME = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(MysqlProcedureSourceTab.class);

	public MysqlProcedureSourceTab(String hint)
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
            s_log.debug("schema="+doi.getCatalogName());
            s_log.debug("procedure name="+doi.getSimpleName());
        }
        
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, doi.getCatalogName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
