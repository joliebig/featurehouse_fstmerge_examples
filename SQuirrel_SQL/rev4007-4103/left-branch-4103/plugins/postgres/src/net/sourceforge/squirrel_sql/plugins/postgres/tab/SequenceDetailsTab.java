package net.sourceforge.squirrel_sql.plugins.postgres.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SequenceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SequenceDetailsTab.class);

    
    private final static ILogger s_log =
        LoggerController.createLogger(SequenceDetailsTab.class);
	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("SequenceDetailsTab.title");
		
		String HINT = s_stringMgr.getString("SequenceDetailsTab.hint");
	}

	
	private static final String SQL =
        "SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled " +
        "FROM  ";
    
	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        String sql = getSQL();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sequence details SQL: "+sql);
            s_log.debug("Sequence schema: "+doi.getSchemaName());
            s_log.debug("Sequence name: "+doi.getSimpleName());
        }

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		return pstmt;
	}
    
    private String getSQL() {
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        StringBuffer result = new StringBuffer();
        result.append(SQL);
        result.append(doi.getSchemaName());
        result.append(".");
        result.append(doi.getSimpleName());
        return result.toString();
    }
}
