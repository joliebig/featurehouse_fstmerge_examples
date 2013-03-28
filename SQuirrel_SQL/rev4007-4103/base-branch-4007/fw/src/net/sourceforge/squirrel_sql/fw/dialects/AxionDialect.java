
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class AxionDialect extends org.hibernate.dialect.HSQLDialect 
                          implements HibernateDialect {
    
    public AxionDialect() {
        super();
        
        
        
        
        
        
        
        
        
        
        
        
        registerColumnType(Types.BIGINT, "numeric($p,0)");
        registerColumnType(Types.BINARY, "binary($l)");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.BOOLEAN, "bit");
        registerColumnType(Types.CHAR, "char($l)");
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "numeric($p,$s)");
        registerColumnType(Types.DOUBLE, "numeric($p,$s)");
        registerColumnType(Types.FLOAT, "numeric($p,$s)");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, "longvarbinary");
        registerColumnType(Types.LONGVARCHAR, "longvarchar");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        
        
        
        
        
        
        
        registerColumnType(Types.REAL, "numeric($p,$s)");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "varbinary($l)");
        registerColumnType(Types.VARCHAR, "varchar($l)");        
    }    
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
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
        return 38;
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
        return "Axion";
    }

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("Axion")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        return new String[] {
            DialectUtils.getColumnAddSQL(info, this, false, false, true)
        };
    }
    
    
    public boolean supportsDropColumn() {
        return true;
    }
    
    
    public String getColumnDropSQL(String tableName, String columnName) {   
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        String cascadeClause = DialectUtils.CASCADE_CLAUSE;
        return DialectUtils.getTableDropSQL(iTableInfo, 
                                            false, 
                                            cascadeConstraints, 
                                            false, 
                                            cascadeClause, false);
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
        return true;
    }
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, 
                                                  to, 
                                                  alterClause, 
                                                  renameToClause);
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
        String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause, 
                                                     false, 
                                                     defaultClause);
    }
   
    
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, 
                                                 tableName, 
                                                 false, 
                                                 false);
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
