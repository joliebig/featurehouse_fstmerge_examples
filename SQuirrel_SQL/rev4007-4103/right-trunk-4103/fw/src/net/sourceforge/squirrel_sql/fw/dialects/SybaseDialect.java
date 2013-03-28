
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class SybaseDialect extends org.hibernate.dialect.SybaseDialect 
                           implements HibernateDialect {    
        
    public SybaseDialect() {
        super();
        registerColumnType(Types.BIGINT, "numeric($p)");
        registerColumnType(Types.BINARY, "image");
        registerColumnType(Types.BIT, "tinyint");
        registerColumnType(Types.BLOB, "image");
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 255, "char($l)");
        registerColumnType(Types.CHAR, "text");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.DATE, "datetime");
        registerColumnType(Types.DECIMAL, "decimal($p,2)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");
        registerColumnType(Types.LONGVARBINARY, "image");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.NUMERIC, "numeric($p)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "datetime");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, "image");
        registerColumnType(Types.VARCHAR, 255, "varchar($l)");
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
        return "datalength";
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
        return "Sybase";
    }    

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	String lname = databaseProductName.trim().toLowerCase();
    	if (lname.startsWith("sybase") 
                || lname.startsWith("adaptive")
                || lname.startsWith("sql server")) 
        {
    		
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
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, 
                                        boolean cascadeConstraints, 
                                        boolean isMaterializedView){
    
        
        List<String> dropTableSQL = 
            DialectUtils.getTableDropSQL(iTableInfo, 
                                         false, 
                                         cascadeConstraints, 
                                         false, 
                                         DialectUtils.CASCADE_CLAUSE, 
                                         false);
        if (cascadeConstraints) {
            ArrayList<String> result = new ArrayList<String>();
            ForeignKeyInfo[] fks = iTableInfo.getExportedKeys();
            if (fks != null && fks.length > 0) {
                for (int i = 0; i < fks.length; i++) {
                    ForeignKeyInfo info = fks[i];
                    String fkName = info.getForeignKeyName();
                    String fkTable = info.getForeignKeyTableName();
                    StringBuilder tmp = new StringBuilder();
                    tmp.append("ALTER TABLE ");
                    tmp.append(fkTable);
                    tmp.append(" DROP CONSTRAINT ");
                    tmp.append(fkName);
                    result.add(tmp.toString());
                }
            }
            result.addAll(dropTableSQL);
            return result;
        } else {
            return dropTableSQL;
        }
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
        return true;
    }

    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      false);
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
        return result.toString();        
    }
    
    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        String setClause = "";
        return DialectUtils.getColumnTypeAlterSQL(this, 
                                                  alterClause, 
                                                  setClause, 
                                                  false, 
                                                  from, 
                                                  to);        
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

    
    public DialectType getDialectType() {
       return DialectType.SYBASEASE;
    }
    
}


