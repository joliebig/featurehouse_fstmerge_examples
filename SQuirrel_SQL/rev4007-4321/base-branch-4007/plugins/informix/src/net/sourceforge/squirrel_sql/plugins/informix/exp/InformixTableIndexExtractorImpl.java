
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class InformixTableIndexExtractorImpl implements ITableIndexExtractor {

    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(InformixTableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "SELECT  T1.idxname " +
        "FROM informix.sysindices AS T1, informix.systables AS T2 " +
        "WHERE T1.tabid = T2.tabid " +
        "and t1.owner = ? "+
        "and T2.tabname = ? ";
    
    
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
