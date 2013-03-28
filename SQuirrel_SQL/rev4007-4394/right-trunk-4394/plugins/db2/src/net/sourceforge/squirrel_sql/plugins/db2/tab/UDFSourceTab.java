package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;

public class UDFSourceTab extends FormattedSourceTab
{
	
	private static String SQL =
	    "SELECT " +
	    "case " +
	    "    when body is null then 'No source available' " +
	    "    else body " +
	    "end " + 	    
	    "FROM SYSIBM.SYSFUNCTIONS " +
	    "WHERE schema = ? " +
	    "AND name = ? " +
	    "AND implementation is null ";
	
	
	private static final String OS_400_SQL = 
	    "select " +
	    "case " +
	    "    when body = 'SQL' and routine_definition is not null then routine_definition " +
	    "    when body = 'SQL' and routine_definition is null then 'no source available' " +
	    "    when body = 'EXTERNAL' and external_name is not null then external_name " +
	    "    when body = 'EXTERNAL' and external_name is null then 'system-generated function' " +
	    "end as definition " +
	    "from QSYS2.SYSFUNCS " +
	    "where routine_schema = ? " +
	    "and routine_name = ? ";	    
	
	
	private final static ILogger s_log =
		LoggerController.createLogger(UDFSourceTab.class);

    
    private boolean isOS400 = false;	
	
    
	public UDFSourceTab(String hint, String stmtSep, boolean isOS400)
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

        String sql = SQL;
        if (isOS400) {
            sql = OS_400_SQL;
        }		
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("udf name="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
