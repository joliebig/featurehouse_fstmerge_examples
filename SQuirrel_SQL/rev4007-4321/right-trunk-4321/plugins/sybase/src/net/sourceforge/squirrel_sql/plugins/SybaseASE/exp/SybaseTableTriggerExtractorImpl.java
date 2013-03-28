
package net.sourceforge.squirrel_sql.plugins.SybaseASE.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SybaseTableTriggerExtractorImpl implements ITableTriggerExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(SybaseTableTriggerExtractorImpl.class);
                
    
    private static String query = 
        "SELECT triggers.name " +
        "FROM sysobjects tables , sysobjects triggers " +
        "where triggers.type = 'TR' " +
        "and triggers.deltrig = tables.id " +
        "and tables.loginame = ? " +
        "and tables.name = ? ";
    
    






            
    
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
        return query;
    }

}
