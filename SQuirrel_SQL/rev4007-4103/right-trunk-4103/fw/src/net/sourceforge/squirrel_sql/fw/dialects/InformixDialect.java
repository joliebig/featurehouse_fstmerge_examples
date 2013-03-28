
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class InformixDialect extends org.hibernate.dialect.InformixDialect
        implements HibernateDialect {

    public InformixDialect() {
        super();
        registerColumnType(Types.BIGINT, "integer");
        registerColumnType(Types.BINARY, "byte");
        registerColumnType(Types.BIT, "smallint");
        registerColumnType(Types.BLOB, "byte");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.CHAR, 32511, "char($l)");
        registerColumnType(Types.CHAR, "char(32511)");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, 15, "float($l)");
        registerColumnType(Types.DOUBLE, "float(15)");
        registerColumnType(Types.FLOAT, 15, "float($l)");
        registerColumnType(Types.FLOAT, "float(15)");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, "byte");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "datetime hour to second");
        registerColumnType(Types.TIMESTAMP, "datetime year to fraction");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "byte");
        registerColumnType(Types.VARCHAR, 255, "varchar($l)");
        registerColumnType(Types.VARCHAR, "text");
    }

    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        if (info.getDatabaseObjectType() == DatabaseObjectType.SCHEMA) {
            return true;
        } else {
            return false;
        }
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
        if (dataType == Types.DECIMAL || dataType == Types.NUMERIC) {
            return 32;
        }
        if (dataType == Types.DOUBLE || dataType == Types.DOUBLE) {
            return 16;
        }
        return 32;
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
        return "Informix";
    }

    
    public boolean supportsProduct(String databaseProductName,
            String databaseProductVersion) {
        if (databaseProductName == null) {
            return false;
        }
        if (databaseProductName.toLowerCase().contains("informix")) {
            
            return true;
        }
        return false;
    }

    
    public String[] getColumnAddSQL(TableColumnInfo info)
            throws UnsupportedOperationException {
        return new String[] { DialectUtils.getColumnAddSQL(info, this, true,
                false, true) };
    }

    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }

    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView) {
        return DialectUtils
                .getTableDropSQL(iTableInfo, true, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }

    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] columns, 
                                        ITableInfo ti) 
    {
        
        
        return new String[] { 
            DialectUtils.getAddIndexSQL(pkName, true, columns),
            DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, true) 
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
                                                      true);
    }

    
    public boolean supportsRenameColumn() {
        return true;
    }

    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        return DialectUtils.getColumnRenameSQL(from, to);
    }

    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
            throws UnsupportedOperationException {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        String setClause = null;
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
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        String defaultClause = DialectUtils.DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause,
                                                     true, 
                                                     defaultClause);   
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
       return DialectType.INFORMIX;
    }
    
}
