package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

public class HADBDialect extends Dialect implements HibernateDialect {

    public HADBDialect() {
        super();

        registerColumnType(Types.BIGINT, "double integer");
        registerColumnType(Types.BINARY, 8000, "binary($l)");
        registerColumnType(Types.BINARY, "binary(8000)");
        registerColumnType(Types.BIT, "smallint");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.CHAR, 8000, "char($l)");
        registerColumnType(Types.CHAR, "char(8000)");
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, "blob");
        registerColumnType(Types.LONGVARCHAR, "clob");
        registerColumnType(Types.NUMERIC, "decimal($p,$s)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, 8000, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "varbinary(8000)");
        registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(8000)");
        
    }
    
        
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
    }

    public String[] getAddPrimaryKeySQL(String pkName,
            TableColumnInfo[] colInfos, ITableInfo ti) {
        
        return null;
    }

    public String[] getColumnAddSQL(TableColumnInfo info)
            throws HibernateException, UnsupportedOperationException {
        
        return null;
    }

    public String getColumnCommentAlterSQL(TableColumnInfo info)
        throws UnsupportedOperationException 
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

        
    public String getColumnDropSQL(String tableName, String columnName)
        throws UnsupportedOperationException 
    {
        throw new UnsupportedOperationException("Not yet implemented");    
    }

        
    public int getColumnLength(int columnSize, int dataType) {
        
        if (dataType == Types.CLOB || dataType == Types.BLOB) {
            return Integer.MAX_VALUE; 
        }
        return columnSize;
    }

    
    public boolean supportsRenameColumn() {
        
        return true;
    }    
    
        
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" RENAME COLUMN ");
        result.append(from.getColumnName());
        result.append(" ");
        result.append(to.getColumnName());
        return result.toString();
    }

    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
        
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

            
    public String getDisplayName() {
        return "Sun HADB";
    }

        
    public String getLengthFunction(int dataType) {
        return "char_length";
    }

        
    public String getMaxFunction() {
        return "max";
    }

        
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.FLOAT) {
            return 52;
        } 
        if (dataType == Types.DECIMAL
                || dataType == Types.NUMERIC) {
            return 31;
        }
        return 0;
    }

        
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

        
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }

        
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView) {
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }

        
    public boolean supportsColumnComment() {
        return false;
    }

        
    public boolean supportsDropColumn() {
        return false;
    }

        
    public boolean supportsProduct(String databaseProductName,
                                   String databaseProductVersion) {
        if (databaseProductName == null) {
            return false;
        }
        String prodName = "sun java system high availability";
        if (databaseProductName.trim().toLowerCase().startsWith(prodName)) {
            
            return true;
        }
        return false;
    }

    public boolean supportsSchemasInTableDefinition() {
        
        return false;
    }

        
    public boolean supportsAlterColumnNull() {
        
        return false;
    }
    
    
    public boolean supportsAlterColumnDefault() {
        
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
    }
    
    
    public String getDropForeignKeySQL(String fkName, String tableName) {
        return DialectUtils.getDropForeignKeySQL(fkName, tableName);
    }
    
    
    public List<String> getCreateTableSQL(List<ITableInfo> tables, 
                                          ISQLDatabaseMetaData md,
                                          CreateScriptPreferences prefs,
                                          boolean isJdbcOdbc)
        throws SQLException
    {
        return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
    }

    
    public DialectType getDialectType() {
       return DialectType.HADB;
    }
    
}
