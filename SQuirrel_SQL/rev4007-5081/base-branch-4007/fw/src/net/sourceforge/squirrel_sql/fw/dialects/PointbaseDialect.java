
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class PointbaseDialect extends org.hibernate.dialect.PointbaseDialect 
                              implements HibernateDialect {
    
    public PointbaseDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "blob(8K)");
        registerColumnType(Types.BIT, "smallint");
        registerColumnType(Types.BLOB, 2000000000, "blob($l)");
        registerColumnType(Types.BLOB, "blob(2000000000)");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.CHAR, 4054, "char($l)");
        registerColumnType(Types.CHAR, 2000000000, "clob($l)");
        registerColumnType(Types.CHAR, "clob(2000000000)");
        registerColumnType(Types.CLOB, 2000000000, "clob($l)");
        registerColumnType(Types.CLOB, "clob(2000000000)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");        
        registerColumnType(Types.LONGVARBINARY, 2000000000, "blob($l)");
        registerColumnType(Types.LONGVARBINARY, "blob(2000000000)");
        registerColumnType(Types.LONGVARCHAR, 2000000000, "clob($l)");
        registerColumnType(Types.LONGVARCHAR, "clob(2000000000)");
        registerColumnType(Types.NUMERIC, "bigint");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, 2000000000, "blob($l)");
        registerColumnType(Types.VARBINARY, "blob(2000000000)");
        registerColumnType(Types.VARCHAR, 4054, "varchar($l)");        
        registerColumnType(Types.VARCHAR, 2000000000, "clob($l)");
        registerColumnType(Types.VARCHAR, "clob(2000000000)");
    }

    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }
    
    
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    
    public String getLengthFunction(int dataType) {
        return "length";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            return 48;
        } else {
            return 31;
        }
    }

    
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }    
    
    
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }

    
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }

    
    public String getDisplayName() {
        return "Pointbase";
    }    
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("pointbase")) {
    		
    		return true;
    	}
		return false;
	}  
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        return new String[] {
            DialectUtils.getColumnAddSQL(info, this, true, false, true)
        };
    }

    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {   
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        
        return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] columns, 
                                        ITableInfo ti) 
    {
        return new String[] {
            DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false)
        };
    }
    
    
    public boolean supportsColumnComment() {
        return false;
    }    
        
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
    }
    
        
    public boolean supportsAlterColumnNull() {
        return false;
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
    }
    
    
    public boolean supportsRenameColumn() {
        return false;
    }    
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);                
    }
    
    
    public boolean supportsAlterColumnType() {
        return false;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        int featureId = DialectUtils.COLUMN_TYPE_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
    }
   
    
    public boolean supportsAlterColumnDefault() {
        return false;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
    }
    
    
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false);
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
    
}
