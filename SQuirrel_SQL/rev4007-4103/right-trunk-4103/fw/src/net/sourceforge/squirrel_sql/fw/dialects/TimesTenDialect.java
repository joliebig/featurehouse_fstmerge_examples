
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class TimesTenDialect extends org.hibernate.dialect.TimesTenDialect 
                             implements HibernateDialect {
    
    public TimesTenDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, 8300,"binary($l)");
        registerColumnType(Types.BINARY, 4194304,"varbinary($l)");
        registerColumnType(Types.BINARY, "varbinary(4194304)");
        registerColumnType(Types.BIT, "tinyint");
        registerColumnType(Types.BLOB, 4194304, "varbinary($l)");
        registerColumnType(Types.BLOB, "varbinary(4194304)");
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 8300, "char($l)");
        registerColumnType(Types.CHAR, 4194304, "varchar($l)");
        registerColumnType(Types.CHAR, "varchar(4194304)");        
        registerColumnType(Types.CLOB, 4194304, "varchar($l)");
        registerColumnType(Types.CLOB, "varchar(4194304)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, 4194304,"varbinary($l)");
        registerColumnType(Types.LONGVARBINARY, "varbinary(4194304)");
        registerColumnType(Types.LONGVARCHAR, 4194304, "varchar($l)");
        registerColumnType(Types.LONGVARCHAR, "varchar(4194304)");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "float");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 4194304, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "varbinary(4194304)");
        registerColumnType(Types.VARCHAR, 4194304,"varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(4194304)");
      
    }    
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
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
    	int result = Integer.MAX_VALUE;
    	if (dataType == Types.DECIMAL || dataType == Types.NUMERIC) {
    		result = 40;
    	}
    	if (dataType == Types.FLOAT) {
    		result = 53;
    	}
    	return result;
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
        return "TimesTen";
    }    
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("timesten")) {
    		
    		return true;
    	}
		return false;
	}
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
    	return new String[] {
    		DialectUtils.getColumnAddSQL(info, this, true, false, false)
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
                                      TableColumnInfo[] columnNames, ITableInfo ti) 
    {
        int featureId = DialectUtils.ADD_PRIMARY_KEY_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);            	
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
       return DialectType.TIMESTEN;
    }
    
}
