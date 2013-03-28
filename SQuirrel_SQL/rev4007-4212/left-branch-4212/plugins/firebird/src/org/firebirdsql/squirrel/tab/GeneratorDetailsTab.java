package org.firebirdsql.squirrel.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class GeneratorDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GeneratorDetailsTab.class);


	
	private interface i18n
	{

		
		String TITLE = s_stringMgr.getString("firebird.genDetails");
		
		String HINT = s_stringMgr.getString("firebird.seqDetails");
	}

	
	private final static ILogger s_log =
		LoggerController.createLogger(GeneratorDetailsTab.class);

	public GeneratorDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        
        String sql = "SELECT CAST('" + doi.getSimpleName() + "' AS VARCHAR(31)) as generator_name, " +
            "gen_id(" + doi.getSimpleName() + ", 0) as current_value " +
            "from rdb$database";
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Preparing SQL: "+sql);
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		return pstmt;
	}
}
