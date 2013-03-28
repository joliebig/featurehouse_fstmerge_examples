
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class HSQLDialect extends org.hibernate.dialect.HSQLDialect 
                         implements HibernateDialect {
    
    public HSQLDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "binary");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, "longvarbinary");
        
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.CHAR, "char($l)");
        registerColumnType(Types.CLOB, "longvarchar");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, "longvarbinary");
        registerColumnType(Types.LONGVARCHAR, "longvarchar");
        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, "varbinary");
        registerColumnType(Types.VARCHAR, "varchar");        
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
        return "HSQL";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("HSQL")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        
        
        
        String addSQL = DialectUtils.getColumnAddSQL(info, 
                                                     this, 
                                                     false, 
                                                     true, true);
        if (info.getDefaultValue() != null 
                && !"".equals(info.getDefaultValue()))
        {
            StringBuffer defaultSQL = new StringBuffer();
            defaultSQL.append("ALTER TABLE ");
            defaultSQL.append(info.getTableName());
            defaultSQL.append(" ALTER COLUMN ");
            defaultSQL.append(info.getColumnName());
            defaultSQL.append(" SET DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                defaultSQL.append(info.getDefaultValue());
            } else {
                defaultSQL.append("'");
                defaultSQL.append(info.getDefaultValue());
                defaultSQL.append("'");
            }
            return new String[] { addSQL, defaultSQL.toString() };
        } else {
            return new String[] { addSQL };
        }
    }

    
    public boolean supportsColumnComment() {
        return false;
    }    
    
    
    @SuppressWarnings("unused")
    public String getColumnCommentAlterSQL(String tableName, String columnName, String comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HSQLDB doesn't support adding comments to columns");
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
                                        TableColumnInfo[] columns, ITableInfo ti) 
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(ti.getQualifiedName());
        result.append(" ADD CONSTRAINT ");
        result.append(pkName);
        result.append(" PRIMARY KEY (");
        for (int i = 0; i < columns.length; i++) {
            result.append(columns[i].getColumnName());
            if (i + 1 < columns.length) {
                result.append(", ");
            }
        }
        result.append(")");
        return new String[] { result.toString() };
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("HSQLDB doesn't support column comments");
    }
 
        
    public boolean supportsAlterColumnNull() {
        return true;
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        result.append(" SET ");
        if (info.isNullable().equalsIgnoreCase("YES")) {
            result.append(" NULL");
        } else {
            result.append(" NOT NULL");
        }
        return result.toString();
    }

    
    public boolean supportsRenameColumn() {
        return true;
    }   
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause);
    
    }
    
    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String setClause = "";
        return DialectUtils.getColumnTypeAlterSQL(this, 
                                                  alterClause, 
                                                  setClause, 
                                                  false, 
                                                  from, 
                                                  to);
    }
    
    
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause, false, defaultClause);
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
