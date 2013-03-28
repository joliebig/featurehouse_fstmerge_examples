
package net.sourceforge.squirrel_sql.plugins.SybaseASE.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SybaseTableIndexExtractorImpl implements ITableIndexExtractor {

    
    
    private final static ILogger s_log = 
        LoggerController.createLogger(SybaseTableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "SELECT sysindexes.name " +
        "FROM dbo.sysindexes " +
        "inner join sysobjects on  sysindexes.id = sysobjects.id " +
        "where sysobjects.loginame = ? and " +
        "sysobjects.name = ? and " +
        "sysindexes.name != ? ";
        
    
    public void bindParamters(PreparedStatement pstmt, IDatabaseObjectInfo dbo)
        throws SQLException 
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Binding schema name "+dbo.getCatalogName()+
                        " as first bind value");
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as second bind value");
            s_log.debug("Binding table name "+dbo.getSimpleName()+
                        " as third bind value");            
            
        }                        
        pstmt.setString(1, dbo.getCatalogName());
        pstmt.setString(2, dbo.getSimpleName());
        pstmt.setString(3, dbo.getSimpleName());
    }

    
    public String getTableIndexQuery() {
        return query;
    }

}
