
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public class FirebirdDialect extends org.hibernate.dialect.FirebirdDialect 
                             implements HibernateDialect {
    
    public FirebirdDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "blob sub_type 0");
        registerColumnType(Types.BIT, "char(1)");
        registerColumnType(Types.BLOB, "blob sub_type -1");
        registerColumnType(Types.BOOLEAN, "char(1)");
        registerColumnType(Types.CHAR, 32767, "char($l)");
        registerColumnType(Types.CHAR, "char(32767)");
        registerColumnType(Types.CLOB, "blob sub_type text");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "double precision");
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, "blob sub_type 0");
        registerColumnType(Types.LONGVARCHAR, "blob sub_type 1");
        registerColumnType(Types.NUMERIC, 18, "numeric($p,$s)");
        registerColumnType(Types.NUMERIC, "double precision");
        registerColumnType(Types.REAL, "double precision");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "blob sub_type -1");
        registerColumnType(Types.VARCHAR, 32765, "varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(32765)");
    }

    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        
        return true;
    }

    
    public boolean supportsSchemasInTableDefinition() {
        return false;
    }

    
    public String getLengthFunction(int dataType) {
        return "strlen";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        return 18;
    }

    
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }    
    
    
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize * 2;
    }

    
    public int getColumnLength(int columnSize, int dataType) {
        if (dataType == Types.BIGINT 
                || dataType == Types.DECIMAL 
                || dataType == Types.DOUBLE
                || dataType == Types.FLOAT
                || dataType == Types.NUMERIC
                || dataType == Types.REAL) 
        {
            return getMaxPrecision(dataType);
        }
        if (dataType == Types.BLOB 
                || dataType == Types.LONGVARBINARY 
                || dataType == Types.LONGVARCHAR)
        {
            return 2147483647;
        }        
        return columnSize;
    }

    
    public String getDisplayName() {
        return "Firebird";
    }

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("Firebird")) {
    		
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
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                      TableColumnInfo[] columns, 
                                      ITableInfo ti) 
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
        String renameToClause = DialectUtils.TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause);
    }

    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(from.getColumnName());
        result.append(" TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
        ArrayList<String> list = new ArrayList<String>();
        list.add(result.toString());
        return list;
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
    
}
