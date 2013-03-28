
package net.sourceforge.squirrel_sql.plugins.db2.exp;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DB2TableIndexExtractorImpl implements ITableIndexExtractor {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(DB2TableIndexExtractorImpl.class);
                
    
    private static final String query = 
        "select INDNAME from SYSCAT.INDEXES " +
        "where TABSCHEMA = ? " +
        "and TABNAME = ? ";
    
    
    private static final String OS_400_SQL = 
        "select " +
        "index_name " +
        "from qsys2.sysindexes " +
        "where table_schema = ? " +
        "and table_name = ? ";        
    
    
    private boolean isOS400 = false;        
    
    
    public DB2TableIndexExtractorImpl(boolean isOS400) {
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

    
    public String getTableIndexQuery() {
        if (isOS400) {
            return OS_400_SQL;
        }
        return query;
    }

}
