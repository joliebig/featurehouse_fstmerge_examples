package net.sourceforge.squirrel_sql.plugins.SybaseASE.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TriggerSourceTab extends FormattedSourceTab
{

    
    private final static ILogger s_log =
        LoggerController.createLogger(TriggerSourceTab.class);
                
	
	private static String SQL =
        "SELECT trigger_defs.text " +
        "FROM sysobjects tables , sysobjects triggers, syscomments trigger_defs " +
        "where triggers.type = 'TR' " +
        "and triggers.id = trigger_defs.id " +
        "and triggers.deltrig = tables.id " +
        "and tables.loginame = ? " +
        
        
        
        "and triggers.name = ? ";
        
	public TriggerSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setCompressWhitespace(true);
        super.setupFormatter(stmtSep, null);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+SQL);
            s_log.debug("Binding for param 1: "+doi.getCatalogName());
            s_log.debug("Binding for param 2: "+doi.getSimpleName());
        }
        
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getCatalogName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
