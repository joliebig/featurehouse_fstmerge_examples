
package net.sourceforge.squirrel_sql.fw.dialects;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;


public interface HibernateDialect {
       
        
    String getTypeName(int code, int length, 
                       int precision, int scale) throws HibernateException;
    
    
    boolean canPasteTo(IDatabaseObjectInfo info);
    
    
    boolean supportsSchemasInTableDefinition();
    
    
    String getNullColumnString();
    
    
    String getMaxFunction();
    
    
    String getLengthFunction(int dataType);
    
    
    int getMaxPrecision(int dataType);
    
    
    int getMaxScale(int dataType);
    
    
    int getPrecisionDigits(int columnSize, int dataType);
    
    
    int getColumnLength(int columnSize, int dataType);
    
    
    boolean supportsProduct(String databaseProductName, 
    					    String databaseProductVersion);
    
        
    String getDisplayName();
    
    
    String[] getColumnAddSQL(TableColumnInfo info) 
        throws HibernateException, UnsupportedOperationException;
    
    
    boolean supportsColumnComment();
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) throws UnsupportedOperationException; 
    

    
    boolean supportsDropColumn();

    
    boolean supportsAlterColumnNull();
    
    
    
    String getColumnDropSQL(String tableName, String columnName)
        throws UnsupportedOperationException;
    
    
    List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView);
    
    
    String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti);
 
    
    String getAddColumnString();
    
    
    String getColumnNullableAlterSQL(TableColumnInfo info);
        
    
    boolean supportsRenameColumn();    
    
    
    String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to);
    
    
    boolean supportsAlterColumnType();
    
    
    
    List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
        throws UnsupportedOperationException;
    
    
    boolean supportsAlterColumnDefault();
    
    
    String getColumnDefaultAlterSQL(TableColumnInfo info);
    

    
    String getDropPrimaryKeySQL(String pkName, String tableName);
    
    
    String getDropForeignKeySQL(String fkName, String tableName);
    
    
    List<String> getCreateTableSQL(List<ITableInfo> tables, 
                                   ISQLDatabaseMetaData md,
                                   CreateScriptPreferences prefs,
                                   boolean isJdbcOdbc) throws SQLException;   
    
    
    DialectType getDialectType();

}

