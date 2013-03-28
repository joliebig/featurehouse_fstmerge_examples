
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class InformixTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(InformixTableTriggerExtractorImpl.class);
                
    
    private static String query = 
        "select T1.trigname " +
        "from informix.systriggers AS T1, informix.systables AS T2 " +
        "where T2.tabid = T1.tabid " +
        "and T2.tabname = ? ";
        
            
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding table name "+dbo.getSchemaName()+
                        " as first bind value");            
        }        
        pstmt.setString(1, dbo.getSimpleName());
    }

    
    public String getTableTriggerQuery() {
        return query;
    }

}
