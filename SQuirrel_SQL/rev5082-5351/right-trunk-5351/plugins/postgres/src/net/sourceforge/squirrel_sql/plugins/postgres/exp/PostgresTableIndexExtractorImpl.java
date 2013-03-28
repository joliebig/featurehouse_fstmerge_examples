
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class PostgresTableIndexExtractorImpl implements ITableIndexExtractor {

    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(PostgresTableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "select indexname " +
        "from pg_catalog.pg_indexes " +
        "where schemaname = ? " +
        "and tablename = ? ";
    
    
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

    
    public String getTableIndexQuery() {
        return query;
    }

}
