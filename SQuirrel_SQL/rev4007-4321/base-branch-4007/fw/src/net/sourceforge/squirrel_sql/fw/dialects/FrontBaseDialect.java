
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class FrontBaseDialect extends org.hibernate.dialect.FrontBaseDialect 
                             implements HibernateDialect {
    
    public FrontBaseDialect() {
        super();
        registerColumnType(Types.BIGINT, "longint");
        
        
        
        
        
        
        registerColumnType(Types.BINARY, "bit varying(2147000000)");
        registerColumnType(Types.BIT, 2147000000, "bit($l)");
        registerColumnType(Types.BIT, "bit(2147000000)");
        
        
        registerColumnType(Types.BLOB, "bit varying(2147000000)");
        
        
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 2147000000, "char($l)");
        registerColumnType(Types.CHAR, "char(2147000000)");
        registerColumnType(Types.CLOB, 2147000000, "varchar($l)");
        registerColumnType(Types.CLOB, "varchar(2147000000)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,2)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");       
        
        
        
        
        
        
        
        registerColumnType(Types.LONGVARBINARY, "bit varying(2147000000)");
        registerColumnType(Types.LONGVARCHAR, 2147000000, "varchar($l)");
        registerColumnType(Types.LONGVARCHAR, "varchar(2147000000)");
        registerColumnType(Types.NUMERIC, 19, "numeric($p,$s)");
        registerColumnType(Types.NUMERIC, "double precision");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        
        
        registerColumnType(Types.VARBINARY, "bit varying(2147000000)");
        registerColumnType(Types.VARCHAR, 2147000000,"varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(2147000000)");
    }    
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("catalog")
                || type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }
    
    
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    
    public String getLengthFunction(int dataType) {
        return "character_length";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.NUMERIC
                || dataType == Types.FLOAT) 
        {
            return 19;
        }
        return 36;
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
        return "FrontBase";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("FrontBase")) {
    		
    		return true;
    	}
		return false;
	}        
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        return new String[] { 
                DialectUtils.getColumnAddSQL(info, this, true, false, true)
        };
    }

    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {
        String dropClause = DialectUtils.DROP_COLUMN_CLAUSE;
        return DialectUtils.getColumnDropSQL(tableName, 
                                             columnName, 
                                             dropClause, 
                                             true, 
                                             "CASCADE");
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
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER COLUMN ");
        result.append(from.getTableName());
        result.append(".");
        result.append(from.getColumnName());
        result.append(" TO ");
        result.append(DialectUtils.getTypeName(to, this));
        ArrayList<String> list = new ArrayList<String>();
        list.add(result.toString());
        return list;
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
        int featureId = DialectUtils.DROP_PRIMARY_KEY_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);                
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
