
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.hibernate.dialect.Dialect;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class DaffodilDialect extends Dialect 
                             implements HibernateDialect {
    
    public DaffodilDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, 4192, "binary($l)");
        registerColumnType(Types.BINARY, 1073741823, "long varbinary($l)");
        registerColumnType(Types.BINARY, "long varbinary(1073741823)");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, 1073741823, "blob($l)");
        registerColumnType(Types.BLOB, "blob(1073741823)");
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.CHAR, 4192, "char($l)");
        registerColumnType(Types.CHAR, 1073741823, "clob($l)");
        registerColumnType(Types.CHAR, "clob(1073741823)");
        registerColumnType(Types.CLOB, 1073741823, "clob($l)");
        registerColumnType(Types.CLOB, "clob(1073741823)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, 1073741823, "long varbinary($l)");
        registerColumnType(Types.LONGVARBINARY, "long varbinary(1073741823)");
        registerColumnType(Types.LONGVARCHAR, 1073741823, "long varchar($l)");
        registerColumnType(Types.LONGVARCHAR, "long varchar(1073741823)");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 4192, "varbinary($l)");
        registerColumnType(Types.VARBINARY, 1073741823, "long varbinary($l)");
        registerColumnType(Types.VARBINARY, "long varbinary(1073741823)");
        registerColumnType(Types.VARCHAR, 4192, "varchar($l)");
        registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
        registerColumnType(Types.VARCHAR, "clob(1073741823)");
        
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
        if (dataType == Types.FLOAT)
        {
            return 15;
        }
        if (dataType == Types.NUMERIC
                || dataType ==  Types.DECIMAL) {
            return 38;
        }
        return 0;
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
        return "Daffodil";
    }

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("Daffodil")) {
    		
    		return true;
    	}
		return false;
	}    

    public String getAddColumnString() {
        return "ADD COLUMN";
    }
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        return new String[] {
            DialectUtils.getColumnAddSQL(info, this, true, false, true)
        };
    }

    
    public boolean supportsColumnComment() {
        return false;
    }    
    
    
    @SuppressWarnings("unused")
    public String getColumnCommentAlterSQL(String tableName, 
                                           String columnName, 
                                           String comment) 
        throws UnsupportedOperationException 
    {
        int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);
    }
    
    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) 
        throws UnsupportedOperationException {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" DROP COLUMN ");
        result.append(columnName);
        result.append(" CASCADE");
        return result.toString();        
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
        String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause, 
                                                     false, 
                                                     defaultClause);
    }
    
    
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, true);
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
       return DialectType.DAFFODIL;
    }
    
}
