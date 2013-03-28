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

public class DomainDetailsTab extends BasePreparedStatementTab
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DomainDetailsTab.class);

	 
    private static String SQL =
        "select rdb$field_name, " +
        "rdb$validation_source, " +
        "rdb$computed_source, " +
        "rdb$default_source, " +
        "rdb$field_length, " +
        "rdb$field_scale, " +
        "rdb$field_type, " +
        "rdb$field_sub_type, " +
        "rdb$missing_source, " +
        "rdb$edit_string, " +
        "rdb$character_length, " +
        "rdb$collation_name, " +
        "rdb$character_set_name, " +
        "rdb$field_precision " +
        "rdb$description " +
        "from rdb$fields f " +
        "left outer join rdb$character_sets cs on cs.rdb$character_set_id = f.rdb$character_set_id " +
        "left outer join rdb$collations cl on (cl.rdb$collation_id = f.rdb$collation_id and cl.rdb$character_set_id = f.rdb$character_set_id) " +
        "where " +
        "  rdb$field_name = ?";
    
	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("firebird.details");
		
		String HINT = s_stringMgr.getString("firebird.domainDetails");
	}

	
	private final static ILogger s_log =
		LoggerController.createLogger(DomainDetailsTab.class);

	public DomainDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Preparing SQL: "+SQL);
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        if (s_log.isDebugEnabled()) {
            s_log.debug("setString param: "+doi.getSimpleName());
        }                
        pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
