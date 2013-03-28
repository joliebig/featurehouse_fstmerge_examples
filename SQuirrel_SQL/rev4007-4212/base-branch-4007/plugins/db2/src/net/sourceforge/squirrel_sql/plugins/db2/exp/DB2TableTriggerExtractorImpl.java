
package net.sourceforge.squirrel_sql.plugins.db2.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DB2TableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(DB2TableTriggerExtractorImpl.class);
                
    
    private final static String SQL = 
        "select TRIGNAME from SYSCAT.TRIGGERS " +
        "where TABSCHEMA = ? " +
        "and TABNAME = ? ";
                
    
    private final static String OS400_SQL = 
        "select trigger_name " +
        "from qsys2.systriggers " +
        "where trigger_schema = ? " +
        "and event_object_table = ? ";
    
    
    private boolean isOS400 = false;            
    
        
    public DB2TableTriggerExtractorImpl(boolean isOS400) {
        this.isOS400 = isOS400;
    }
    
    
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
        String result = SQL;
        if (isOS400) {
            result = OS400_SQL;
        } 
        return result;
    }

}
