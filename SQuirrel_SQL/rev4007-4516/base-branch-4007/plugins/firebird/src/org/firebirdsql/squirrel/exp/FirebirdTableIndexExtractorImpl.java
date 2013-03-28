
package org.firebirdsql.squirrel.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class FirebirdTableIndexExtractorImpl implements ITableIndexExtractor {

    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(FirebirdTableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "SELECT " +
        "RDB$INDEX_NAME " +
        "FROM RDB$INDICES " +
        "WHERE RDB$RELATION_NAME = ? ";
    
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding tablename name "+dbo.getSchemaName()+
                        " as first bind value");
        }                        
        pstmt.setString(1, dbo.getSimpleName());
    }

    
    public String getTableIndexQuery() {
        return query;
    }

}
