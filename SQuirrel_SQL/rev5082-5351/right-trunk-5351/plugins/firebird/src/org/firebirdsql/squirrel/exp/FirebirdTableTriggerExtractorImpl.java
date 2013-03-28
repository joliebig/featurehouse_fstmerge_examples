
package org.firebirdsql.squirrel.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class FirebirdTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(FirebirdTableTriggerExtractorImpl.class);
                
    
    private static String SQL = 
        "select " +
        "cast(rdb$trigger_name as varchar(31)) as rdb$trigger_name " +
        "from rdb$triggers " +
        "where rdb$relation_name = ? ";
    
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as first bind value");            
        }        
        pstmt.setString(1, dbo.getSimpleName());        
    }


    
    public String getTableTriggerQuery() {
        return SQL;
    }

}
