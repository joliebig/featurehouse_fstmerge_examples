
package net.sourceforge.squirrel_sql.plugins.derby.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DerbyTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(DerbyTableTriggerExtractorImpl.class);
                
    
    private static String SQL = 
        "select tr.TRIGGERNAME " +
        "from SYS.SYSTRIGGERS tr, SYS.SYSTABLES t, SYS.SYSSCHEMAS s " +
        "where tr.TABLEID = t.TABLEID " +
        "and s.SCHEMAID = t.SCHEMAID " +
        "and t.TABLENAME = ? " +
        "and s.SCHEMANAME = ? ";
    
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo) 
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as first bind value");            
            s_log.debug("Binding schema name "+dbo.getSchemaName()+
                        " as second bind value");
        }        
        pstmt.setString(1, dbo.getSimpleName());
        pstmt.setString(2, dbo.getSchemaName());        
    }

    
    public String getTableTriggerQuery() {
        return SQL;
    }

}
