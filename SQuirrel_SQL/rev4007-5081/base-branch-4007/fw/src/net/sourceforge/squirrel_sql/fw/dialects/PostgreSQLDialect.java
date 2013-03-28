
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



public class PostgreSQLDialect 
    extends org.hibernate.dialect.PostgreSQLDialect 
    implements HibernateDialect {

    public PostgreSQLDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "bytea");
        
        
        
        
        
        
        
        
        
        
        
        registerColumnType(Types.BIT, "bool");
        registerColumnType(Types.BLOB, "bytea");
        registerColumnType(Types.BOOLEAN, "bool");
        registerColumnType(Types.CHAR, 8000,  "char($l)");
        registerColumnType(Types.CHAR, "text");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,2)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int"); 
        registerColumnType(Types.LONGVARBINARY, "bytea");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.NUMERIC, "numeric($p)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "int");
        registerColumnType(Types.VARBINARY, "bytea");
        registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType(Types.VARCHAR, "text");
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
            return 53;
        } else {
            return 38;
        }
    }

    
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    
    public int getPrecisionDigits(int columnSize, int dataType) {
        if (columnSize == 2) {
            return 5;
        }
        if (columnSize == 4) {
            return 10;
        }
        return 19;
    }
    
    
    public int getColumnLength(int columnSize, int dataType) {
        if (dataType == Types.VARCHAR && columnSize == -1) { 
            
            return 2000;
        } 
        return columnSize;
    }

    
    public String getDisplayName() {
        return "PostgreSQL";
    }    
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("postgresql")) {
    		
    		return true;
    	}
		return false;
	}
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        ArrayList<String> result = new ArrayList<String>();
        result.add(DialectUtils.getColumnAddSQL(info, this, true, true, true));
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }
        return result.toArray(new String[result.size()]);
    }

    
    public boolean supportsColumnComment() {
        return true;
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
                                        TableColumnInfo[] colInfos, 
                                        ITableInfo ti) 
    {
        return new String[] { 
            DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false)
        };
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        return DialectUtils.getColumnCommentAlterSQL(info.getTableName(), 
                                                     info.getColumnName(), 
                                                     info.getRemarks());

    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        if (info.isNullable().equalsIgnoreCase("YES")) {
            result.append(" DROP NOT NULL");
        } else {
            result.append(" SET NOT NULL");
        }
        return result.toString();
    }

    
    public boolean supportsRenameColumn() {
        
        return true;
    }
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        String alterClause = DialectUtils.RENAME_COLUMN_CLAUSE;        
        String toClause = DialectUtils.TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, toClause);
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
        result.append(to.getColumnName());
        result.append(" TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
        list.add(result.toString());
        return list;
    }

        
    public boolean supportsAlterColumnNull() {
        return true;
    }
    
    
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        String defVal = info.getDefaultValue();
        if (defVal == null || "".equals(defVal)) {
            result.append(" DROP DEFAULT");
        } else {
            result.append(" SET DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                result.append(defVal);
            } else {
                result.append("'");
                result.append(defVal);
                result.append("'");
            }
        }
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

