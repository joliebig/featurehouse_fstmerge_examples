
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

import org.hibernate.MappingException;


public class ColTypeMapper {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(ColTypeMapper.class);
            
    
    public static String mapColType(ISession sourceSession, 
                                    ISession destSession,
                                    TableColumnInfo colInfo,
                                    String sourceTableName,
                                    String destTableName) 
        throws UserCancelledOperationException, MappingException  
    {
        int colJdbcType = colInfo.getDataType();

        
        
        colJdbcType = DBUtil.replaceOtherDataType(colInfo, sourceSession);
        
        
        colJdbcType = DBUtil.replaceDistinctDataType(colInfo, sourceSession);
        
        
        
        
        
        
        
        
        if (DialectFactory.isOracle(sourceSession.getMetaData())
                && colJdbcType ==Types.DECIMAL) 
        {
            
            
            
            if (colInfo.getDecimalDigits() == 0) {
                colJdbcType = Types.BIGINT;
            }
        }
        
        
        int size = getColumnLength(sourceSession, colInfo, sourceTableName);
                        
        if (DialectFactory.isPointbase(destSession.getMetaData()) && size <= 0) {
            if (DBUtil.isBinaryType(colInfo)) { 
                
                
                
                
                size = 16777215; 
            } else {
                size = 20; 
            }
        }
        if (DialectFactory.isFirebird(destSession.getMetaData())) {
            if (colJdbcType == java.sql.Types.DECIMAL) {
                if (size > 18) {
                    size = 18;
                }
            }
        }
        String result = null;
        JFrame mainFrame = destSession.getApplication().getMainFrame();
        HibernateDialect destDialect = 
            DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                      mainFrame, 
                                      destSession.getMetaData());

        if (s_log.isDebugEnabled()) {
            s_log.debug(
                    "ColTypeMapper.mapColType: using dialect type: "+
                    destDialect.getClass().getName()+" to find name for column "+
                    colInfo.getColumnName()+" in table "+destTableName+
                    " with type id="+colJdbcType+" ("+
                    JDBCTypeMapper.getJdbcTypeName(colJdbcType)+")");
        }
        if (destDialect != null) {
            HibernateDialect sourceDialect = 
                DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                          mainFrame, 
                                          sourceSession.getMetaData());
            
            int precision = sourceDialect.getPrecisionDigits(size, colJdbcType);
            
            if (precision > destDialect.getMaxPrecision(colJdbcType)) {
                precision = destDialect.getMaxPrecision(colJdbcType);
            }
            int scale = colInfo.getDecimalDigits();
            if (scale > destDialect.getMaxScale(colJdbcType)) {
                scale = destDialect.getMaxScale(colJdbcType);
            }
            
            
            
            
            
            
            
            
            
            
            
            
            if (precision <= scale) {
                if (precision < scale) {
                    precision = scale;
                }
                scale = precision / 2;
                s_log.debug(
                    "Precision == scale ("+precision+") for the destination " +
                    "database column def.  This is most likely incorrect, so " +
                    "setting the scale to a more reasonable value: "+scale);
                
            }
            
            
            if (scale < 0) {
                scale = precision / 2;
                s_log.debug(
                        "scale is less than 0 for the destination " +
                        "database column def.  This is most likely incorrect, so " +
                        "setting the scale to a more reasonable value: "+scale);                
            }
            result = destDialect.getTypeName(colJdbcType, size, precision, scale);
        } 
        return result;
    }
    
    
    public static int getColumnLength(ISession sourceSession, 
                                      TableColumnInfo colInfo,
                                      String tableName) 
        throws UserCancelledOperationException
    {
        if (colInfo.getDataType() == Types.TIMESTAMP
                || colInfo.getDataType() == Types.DATE
                || colInfo.getDataType() == Types.TIME) 
        {
            
            
            return 10;
        }
        
        
        
        
        
        
        
        if (DialectFactory.isOracle(sourceSession.getMetaData())
                && (colInfo.getDataType() == Types.CLOB 
                        || colInfo.getDataType() == Types.BLOB))
        {
            return getColumnLengthBruteForce(sourceSession, colInfo, tableName, 4000);
        }
        int length = getColumnLength(sourceSession, colInfo);
        
        
        if (length <= 0) {
            length = getColumnLengthBruteForce(sourceSession, colInfo, tableName, 10);            
        }
        return length;
    }
    
    private static int getColumnLength(ISession sourceSession, 
                                       TableColumnInfo colInfo) 
        throws UserCancelledOperationException
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                      sourceSession.getApplication().getMainFrame(), 
                                      sourceSession.getMetaData());
        int length = colInfo.getColumnSize();
        int type = colInfo.getDataType();
        length = dialect.getColumnLength(length, type); 
        return length;
    }
    
    private static int getColumnLengthBruteForce(ISession sourceSession, 
                                                 TableColumnInfo colInfo,
                                                 String tableName,
                                                 int defaultLength) 
        throws UserCancelledOperationException
    {
        int length = defaultLength;
        String sql = 
            DBUtil.getMaxColumnLengthSQL(sourceSession, 
                                         colInfo, 
                                         tableName, 
                                         true);
        ResultSet rs = null;
        try {
            rs = DBUtil.executeQuery(sourceSession, sql);
            if (rs.next()) {
                length = rs.getInt(1);
            }
            if (length <= 0) {
                length = defaultLength;
            }
        } catch (SQLException e) {
            s_log.error("ColTypeMapper.getColumnLengthBruteForce: encountered " +
                        "unexpected SQLException - "+e.getMessage());
        } finally {
            SQLUtilities.closeResultSet(rs);
        }        
        return length;
    }
}
