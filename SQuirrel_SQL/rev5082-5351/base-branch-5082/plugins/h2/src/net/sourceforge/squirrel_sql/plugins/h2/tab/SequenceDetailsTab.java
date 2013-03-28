package net.sourceforge.squirrel_sql.plugins.h2.tab;

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
        "SELECT SEQUENCE_CATALOG,SEQUENCE_SCHEMA, " +
        "CURRENT_VALUE,INCREMENT,IS_GENERATED,REMARKS " +
        "FROM INFORMATION_SCHEMA.SEQUENCES " +
        "WHERE SEQUENCE_SCHEMA = ? " +
        "AND SEQUENCE_NAME = ? ";
    
	public SequenceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Sequence details SQL: "+SQL);
            s_log.debug("Sequence schema: "+doi.getSchemaName());
            s_log.debug("Sequence name: "+doi.getSimpleName());
        }

		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
        pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
    
}
