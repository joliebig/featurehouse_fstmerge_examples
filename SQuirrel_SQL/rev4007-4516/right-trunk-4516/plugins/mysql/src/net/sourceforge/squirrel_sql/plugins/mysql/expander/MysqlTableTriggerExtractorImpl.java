
package net.sourceforge.squirrel_sql.plugins.mysql.expander;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class MysqlTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(MysqlTableTriggerExtractorImpl.class); 
    
    
    private static String SQL = 
        "select TRIGGER_NAME " +
        "from information_schema.triggers " +
        "where EVENT_OBJECT_SCHEMA = ? " +
        "and EVENT_OBJECT_TABLE = ? ";
    
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding catalog name "+dbo.getCatalogName()+
                        " as first bind value");
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as second bind value");            
        }
        pstmt.setString(1, dbo.getCatalogName());
        pstmt.setString(2, dbo.getSimpleName());        
    }

    
    public String getTableTriggerQuery() {
        return SQL;
    }

}
