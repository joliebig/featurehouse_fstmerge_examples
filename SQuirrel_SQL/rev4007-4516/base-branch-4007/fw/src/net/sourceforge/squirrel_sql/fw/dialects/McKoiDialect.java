
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class McKoiDialect extends org.hibernate.dialect.HSQLDialect 
                          implements HibernateDialect {
    
    public McKoiDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, 2000000000, "binary($l)");
        registerColumnType(Types.BINARY, "binary(2000000000)");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, 2000000000, "blob");
        registerColumnType(Types.BLOB, "blob(2000000000)");
        registerColumnType(Types.BOOLEAN, "bit");
        registerColumnType(Types.CHAR, 255, "char($l)");
        registerColumnType(Types.CHAR, 1000000000, "varchar($l)");
        registerColumnType(Types.CHAR, "varchar(1000000000)");
        registerColumnType(Types.CLOB, 1000000000, "clob($l)");
        registerColumnType(Types.CLOB, "clob(1000000000)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, 2000000000, "longvarbinary($l)");
        registerColumnType(Types.LONGVARBINARY, "longvarbinary(2000000000)");
        registerColumnType(Types.LONGVARCHAR, 1000000000, "longvarchar($l)");
        registerColumnType(Types.LONGVARCHAR, "longvarchar(1000000000)");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        
        
        
        
        
        
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, 2000000000, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "varbinary(2000000000)");
        registerColumnType(Types.VARCHAR, 1000000000, "varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(1000000000)");
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
        return false;
    }

    
    public String getLengthFunction(int dataType) {
        return "length";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        return Integer.MAX_VALUE;
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
        return "McKoi";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("mckoi")) {
    		
    		return true;
    	}
		return false;
	}
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        return new String[] {
            DialectUtils.getColumnAddSQL(info, this, true, true, true)
        };
    }

    
    public boolean supportsDropColumn() {
        
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {
        
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }

    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                      TableColumnInfo[] columns, ITableInfo ti) 
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
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String defaultClause = DialectUtils.SET_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause);
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
    
}

