
package net.sourceforge.squirrel_sql.plugins.h2.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class H2TableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(H2TableTriggerExtractorImpl.class);
                
    
    private static String query = 
        "select trigger_name " +
        "from INFORMATION_SCHEMA.TRIGGERS " +
        "where TABLE_SCHEMA = ? " +
        "and TABLE_NAME = ? ";
        
            
    
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
        return query;
    }

}
