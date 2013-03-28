
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


public class DB2Dialect extends org.hibernate.dialect.DB2Dialect 
                        implements HibernateDialect {

    public DB2Dialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, 254, "char($l) for bit data");
        registerColumnType(Types.BINARY, "blob");
        registerColumnType(Types.BIT, "smallint");
        
        registerColumnType(Types.BLOB, 1073741823, "blob($l)");
        registerColumnType(Types.BLOB, "blob(1073741823)");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.CHAR, 254, "char($l)");
        registerColumnType(Types.CHAR, 4000, "varchar($l)");
        registerColumnType(Types.CHAR, 32700, "long varchar");
        registerColumnType(Types.CHAR, 1073741823, "clob($l)");
        registerColumnType(Types.CHAR, "clob(1073741823)");
        
        registerColumnType(Types.CLOB, 1073741823, "clob($l)");
        registerColumnType(Types.CLOB, "clob(1073741823)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");
        registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
        registerColumnType(Types.LONGVARBINARY, 1073741823, "blob($l)");
        registerColumnType(Types.LONGVARBINARY, "blob(1073741823)");
        registerColumnType(Types.LONGVARCHAR, 32700, "long varchar");
        
        registerColumnType(Types.LONGVARCHAR, 1073741823, "clob($l)");
        registerColumnType(Types.LONGVARCHAR, "clob(1073741823)");
        registerColumnType(Types.NUMERIC, "bigint");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, 254, "varchar($l) for bit data");
        registerColumnType(Types.VARBINARY, "blob");
        
        registerColumnType(Types.VARCHAR, 3924, "varchar($l)");
        registerColumnType(Types.VARCHAR, 32700, "long varchar");
        
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
            return 31;
        }
    }

    
    public int getMaxScale(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            
            
            return 0;
        } else {
            return getMaxPrecision(dataType);
        }
    }
    
    
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }
    
    
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }

    
    public String getDisplayName() {
        return "DB2";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("DB2")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        ArrayList<String> result = new ArrayList<String>();
        
        StringBuffer addColumn = new StringBuffer();
        addColumn.append("ALTER TABLE ");
        addColumn.append(info.getTableName());
        addColumn.append(" ADD ");
        addColumn.append(info.getColumnName());
        addColumn.append(" ");
        addColumn.append(getTypeName(info.getDataType(), 
                                  info.getColumnSize(), 
                                  info.getColumnSize(), 
                                  info.getDecimalDigits()));
        if (info.getDefaultValue() != null) {
            addColumn.append(" WITH DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                addColumn.append(info.getDefaultValue());
            } else {
                addColumn.append("'");
                addColumn.append(info.getDefaultValue());
                addColumn.append("'");                
            }
        }
        result.add(addColumn.toString());
        
        if (info.isNullable() == "NO") {
        
        
            StringBuffer notnull = new StringBuffer();
            notnull.append("ALTER TABLE ");
            notnull.append(info.getTableName());
            notnull.append(" ADD CONSTRAINT ");
            notnull.append(info.getColumnName());
            notnull.append(" CHECK (");
            notnull.append(info.getColumnName());
            notnull.append(" IS NOT NULL)");            
            result.add(notnull.toString());
        } 
        
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }
        
        return result.toArray(new String[result.size()]);
        
     }

    
    public String getColumnCommentAlterSQL(String tableName, 
                                           String columnName, 
                                           String comment) 
        throws UnsupportedOperationException 
    {
        return DialectUtils.getColumnCommentAlterSQL(tableName, 
                                                     columnName, 
                                                     comment);
    }

    
    public boolean supportsDropColumn() {
        return false;
    }
    
    
    public String getColumnDropSQL(String tableName, String columnName) {
        int featureId = DialectUtils.COLUMN_DROP_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
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
        return true;
    }    
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        return DialectUtils.getColumnCommentAlterSQL(info);
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
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to) 
    {
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
        ArrayList<String> result = new ArrayList<String>();
        StringBuffer tmp = new StringBuffer();
        tmp.append("ALTER TABLE ");
        tmp.append(from.getTableName());
        tmp.append(" ALTER COLUMN ");
        tmp.append(from.getColumnName());
        tmp.append(" SET DATA TYPE ");
        tmp.append(DialectUtils.getTypeName(to, this));
        result.add(tmp.toString());
        return result;
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
