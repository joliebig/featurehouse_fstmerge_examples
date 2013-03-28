package net.sourceforge.squirrel_sql.plugins.db2.tab;

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

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("SequenceDetailsTab.title");
		
		String HINT = s_stringMgr.getString("SequenceDetailsTab.hint");
	}

	
	private static final String SQL =
        "SELECT  T1.OWNER     AS sequence_owner, " +
        "        T1.DEFINER   AS sequence_definer, " +
        "       T1.SEQNAME   AS sequence_name, " +
        "       T2.TYPENAME AS data_type, " +
        "       T1.MINVALUE   AS min_value, " +
        "       T1.MAXVALUE   AS max_value, " +
        "       T1.INCREMENT   AS increment_by, " +
        "       case T1.CYCLE " +
        "         when 'Y' then 'CYCLE' " +
        "         else 'NOCYCLE' " +
        "       end AS cycle_flag, " +
        "       case T1.ORDER " +
        "         when 'Y' then 'ORDERED' " +
        "         else 'UNORDERED' " +
        "        end AS order_flag, " +
        "       T1.CACHE AS cache_size, " +
        "       T1.CREATE_TIME AS create_time, " +
        "       T1.ALTER_TIME AS last_alter_time, " +
        "       case T1.ORIGIN " +
        "         when 'U' then 'User' " +
        "         when 'S' then 'System' " +
        "       end AS origin, " +
        "       T1.REMARKS AS comment " +
        "FROM    SYSCAT.SEQUENCES AS T1, " +
        "        SYSCAT.DATATYPES AS T2 " +
        "WHERE T1.DATATYPEID = T2.TYPEID " +
        "and T1.SEQSCHEMA = ? " +
        "and T1.SEQNAME = ? ";
	
	
	private static final String OS_400_SQL = 
	    "select sequence_schema, " +
	    "sequence_name, " +
	    "sequence_definer, " +
	    "data_type as type_name, " +
	    "minimum_value as min_value, " +
	    "maximum_value as max_value, " +
	    "increment as increment_by, " +
	    "case cycle_option " +
	    " when 'YES' then 'CYCLE' " +
	    " else 'NOCYCLE' " +
	    "end as cycle_flag, " +
	    "case order " +
	    " when 'YES' then 'ORDERED' " +
	    " else 'UNORDERED' " +
	    "end as order_flag, " +
	    "cache as cache_size, " +
	    "sequence_created as create_time, " +
	    "last_altered_timestamp as last_alter_time, " +
	    "long_comment as comment " +
	    "from qsys2.syssequences " +
	    "where sequence_schema = ? " +
	    "and sequence_name = ?";	    
	
    
    private final static ILogger s_log =
        LoggerController.createLogger(SequenceDetailsTab.class);

    
    private boolean isOS400 = false;
	
	
	public SequenceDetailsTab(boolean isOS400)
	{
		super(i18n.TITLE, i18n.HINT, true);
		this.isOS400 = isOS400;
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{        
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		String sql = SQL;
        if (isOS400) {
            sql = OS_400_SQL;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sequence details SQL: "+sql);
            s_log.debug("Sequence schema: "+doi.getSchemaName());
            s_log.debug("Sequence name: "+doi.getSimpleName());
        }		
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
