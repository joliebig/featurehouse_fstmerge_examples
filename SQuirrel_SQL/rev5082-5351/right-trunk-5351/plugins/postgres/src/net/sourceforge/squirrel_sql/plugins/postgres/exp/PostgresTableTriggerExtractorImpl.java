
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class PostgresTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(PostgresTableTriggerExtractorImpl.class);
                
    
    private static final String triggerQuery = 
        "select tr.tgname " +
        "from pg_catalog.pg_trigger tr, pg_catalog.pg_proc p, " +
        "     pg_class c, pg_namespace n " +
        "where tr.tgfoid  =  p.oid " +
        "and tr.tgrelid = c.oid " +
        "and c.relnamespace = n.oid " +
        "and n.nspname = ? " +
        "and c.relname = ? ";
    
    
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding schema name "+dbo.getSchemaName()+
                        " as first bind value");
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as second bind value");            
        }                
        pstmt.setString(1, dbo.getSchemaName());
        pstmt.setString(2, dbo.getSimpleName());
    }

    
    public String getTableTriggerQuery() {
        return triggerQuery;
    }

}
