
package net.sourceforge.squirrel_sql.plugins.h2.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class H2TableIndexExtractorImpl implements ITableIndexExtractor {

    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(H2TableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "SELECT INDEX_NAME " +
        "FROM INFORMATION_SCHEMA.INDEXES " +
        "WHERE TABLE_SCHEMA = ? " +
        "AND TABLE_NAME = ? ";
    
    
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
