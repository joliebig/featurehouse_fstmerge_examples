
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class ProgressDialect extends org.hibernate.dialect.Dialect 
                             implements HibernateDialect {
    
    public ProgressDialect() {
        super();
        registerColumnType(Types.BIGINT, "integer");
        registerColumnType(Types.BINARY, 2000,"binary($l)");
        registerColumnType(Types.BINARY, 31982, "varbinary($l)");
        registerColumnType(Types.BINARY, "lvarbinary($l)");        
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, "lvarbinary($l)");
        registerColumnType(Types.BOOLEAN, "bit");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CHAR, "char(2000)");
        
        registerColumnType(Types.CLOB, "varchar($l)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "numeric($p,2)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, 999999999, "lvarbinary($l)");
        registerColumnType(Types.LONGVARBINARY, "lvarbinary(999999999)");
        
        registerColumnType(Types.LONGVARCHAR, "varchar($l)");
        registerColumnType(Types.NUMERIC, "numeric($p,2)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 31982, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "varbinary(31982)");
        registerColumnType(Types.VARCHAR, 31982,"varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(31982)");
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
        if (dataType == Types.FLOAT) {
            return 15;
        } else {
            return 32;
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
        return "Progress";
    }    

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("progress")) {
    		
    		return true;
    	}
		return false;
	}
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This database dialect doesn't support adding columns to tables");
    }

    
    public boolean supportsColumnComment() {
        return false;
    }    
    
    
    @SuppressWarnings("unused")
    public String getColumnCommentAlterSQL(String tableName, String columnName, 
    String comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This database dialect doesn't support adding comments to columns");
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
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public boolean supportsRenameColumn() {
        
        return true;
    }
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    
    public boolean supportsAlterColumnType() {
        
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        
        throw new UnsupportedOperationException("Not Yet Implemented");
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
       return DialectType.PROGRESS;
    }
    
}
