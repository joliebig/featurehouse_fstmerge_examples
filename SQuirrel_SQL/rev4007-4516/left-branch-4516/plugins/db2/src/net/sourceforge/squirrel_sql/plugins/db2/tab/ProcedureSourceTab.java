package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ProcedureSourceTab extends FormattedSourceTab
{
    
    private static interface i18n {
        StringManager s_stringMgr =
            StringManagerFactory.getStringManager(ProcedureSourceTab.class);   

        
        
        String C_LANGUAGE_PROC_MSG = 
            s_stringMgr.getString("ProcedureSourceTab.cLanguageProcMsg"); 
    }        
    
	
	private static String SQL =
        "select " +
        "    case " +
        "        when language = 'C' then '" +i18n.C_LANGUAGE_PROC_MSG+"' " +
        "        else text " +
        "    end as text " +
        "from SYSCAT.PROCEDURES " +
        "where PROCSCHEMA = ? " +
        "and PROCNAME = ? ";
    
	
	private static String OS_400_SQL = 
	    "select routine_definition from qsys2.sysroutines " +
	    "where routine_schema = ? " +
	    "and routine_name = ? ";
		
	
	private final static ILogger s_log =
		LoggerController.createLogger(ProcedureSourceTab.class);

    
    private boolean isOS400 = false;    
	
    
	public ProcedureSourceTab(String hint, boolean isOS400, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(false);
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
            s_log.debug("Running SQL for procedure source: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("procedure name="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
