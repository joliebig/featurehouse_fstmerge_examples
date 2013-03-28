
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class SQLServerDialect extends org.hibernate.dialect.SQLServerDialect 
                              implements HibernateDialect {    
        
    public SQLServerDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "image");
        registerColumnType(Types.BIT, "tinyint");
        registerColumnType(Types.BLOB, "image");
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 8000, "char($l)");
        registerColumnType(Types.CHAR, "text");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.DATE, "datetime");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");
        registerColumnType(Types.LONGVARBINARY, "image");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.NUMERIC, "numeric($p,s$)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "datetime");
        registerColumnType(Types.TIMESTAMP, "datetime");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 8000, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "image");
        registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
	registerColumnType(Types.VARCHAR, "text");
    }    
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")
                || type.getName().equalsIgnoreCase("catalog")) {
            result = false;
        }
        return result;
    }    
    
    
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    
    public String getLengthFunction(int dataType) {
        return "len";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT) 
        {
            return 53;
        } else {
            return 38;
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
        return "MS SQLServer";
    }    
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("microsoft")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        if (info.getDefaultValue() != null) {
            return new String[] {
                    DialectUtils.getColumnAddSQL(info, this, false, true, true),
                    getColumnDefaultAlterSQL(info)
                };            
        } else {
            return new String[] {
                    DialectUtils.getColumnAddSQL(info, this, false, true, true),
                };                        
        }
    }

    
    public boolean supportsColumnComment() {
        return false;
    }
     
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {   
        throw new UnsupportedOperationException("SQL-Server doesn't support column comments");
    }
        
    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {
        
        return DialectUtils.getColumnDropSQL(tableName, columnName, "DROP COLUMN", false, null);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] colInfos, 
                                        ITableInfo ti) 
    {
        ArrayList<String> result = new ArrayList<String>();
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        
        
        DialectUtils.getMultiColNotNullSQL(colInfos, this, alterClause, true, result);

        String pkSQL = DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false);
        result.add(pkSQL);
        
        return result.toArray(new String[result.size()]);
    }
    
        
    public boolean supportsAlterColumnNull() {
        return true;
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      true);
    }
    
    
    public boolean supportsRenameColumn() {
        return true;
    }
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        StringBuffer result = new StringBuffer();
        result.append("exec sp_rename ");
        result.append("'");
        result.append(from.getTableName());
        result.append(".");
        result.append(from.getColumnName());
        result.append("'");
        result.append(", ");
        result.append(to.getColumnName());
        result.append(", 'COLUMN'");
        return result.toString();
    }
    
    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(from.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(to, this));
        list.add(result.toString());
        return list;
    }
    
    
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ADD CONSTRAINT ");
        result.append(info.getTableName())
              .append("_")
              .append(info.getColumnName())
              .append("_default");
        result.append(" DEFAULT ");
        if (JDBCTypeMapper.isNumberType(info.getDataType())) {
            result.append(info.getDefaultValue());
        } else {
            result.append("'");
            result.append(info.getDefaultValue());
            result.append("'");
        }
        result.append(" FOR ");
        result.append(info.getColumnName());
        return result.toString();
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

