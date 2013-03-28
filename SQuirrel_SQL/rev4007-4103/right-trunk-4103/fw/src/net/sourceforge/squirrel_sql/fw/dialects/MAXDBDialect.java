
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

import org.hibernate.dialect.SAPDBDialect;

public class MAXDBDialect extends SAPDBDialect 
                          implements HibernateDialect {
    
    public MAXDBDialect() {
        super();
        registerColumnType( Types.BIGINT, "fixed(19,0)" );
        registerColumnType( Types.BINARY, 8000, "char($l) byte" );
        registerColumnType( Types.BINARY, "long varchar byte" );
        registerColumnType( Types.BIT, "boolean" );
        registerColumnType( Types.BLOB, "long byte" );
        registerColumnType( Types.BOOLEAN, "boolean" );
        registerColumnType( Types.CLOB, "long varchar" );
        registerColumnType( Types.CHAR, 8000, "char($l) ascii" );
        registerColumnType( Types.CHAR, "long varchar ascii" );
        registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
        registerColumnType( Types.DOUBLE, "double precision" );
        registerColumnType( Types.DATE, "date" );
        registerColumnType( Types.FLOAT, "float($p)" );
        registerColumnType( Types.INTEGER, "int" );
        registerColumnType( Types.LONGVARBINARY, 8000, "varchar($l) byte");
        registerColumnType( Types.LONGVARBINARY, "long byte");
        registerColumnType( Types.LONGVARCHAR, "long ascii");
        registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
        registerColumnType( Types.REAL, "float($p)");
        registerColumnType( Types.SMALLINT, "smallint" );
        registerColumnType( Types.TIME, "time" );
        registerColumnType( Types.TIMESTAMP, "timestamp" );
        registerColumnType( Types.TINYINT, "fixed(3,0)" );
        registerColumnType( Types.VARBINARY, "long byte" );
        registerColumnType( Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType( Types.VARCHAR, "long ascii");
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

    public String getMaxFunction() {
        return "max";
    }

    public String getLengthFunction(int dataType) {
        return "length";
    }

    public int getMaxPrecision(int dataType) {
        return 38;
    }

    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize * 2;
    }

    
    public int getColumnLength(int columnSize, int dataType) {
        
        if (dataType == Types.LONGVARBINARY) {
            return Integer.MAX_VALUE;
        }
        return columnSize;
    }
    
    
    public String getDisplayName() {
        return "MaxDB";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	String lname = databaseProductName.trim().toLowerCase();
    	if (lname.startsWith("sap") || lname.startsWith("maxdb")) {
    		
    		return true;
    	}
		return false;
	}        

    
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        ArrayList<String> result = new ArrayList<String>();
        result.add(DialectUtils.getColumnAddSQL(info, this, true, false, true));
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }
        return result.toArray(new String[result.size()]);
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
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < columns.length; i++) {
            TableColumnInfo info = columns[i];
            result.add(getColumnNullableAlterSQL(info, false));
        }
        result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false));
        return result.toArray(new String[result.size()]);
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
        return true;
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        boolean nullable = info.isNullable().equalsIgnoreCase("YES");
        return getColumnNullableAlterSQL(info, nullable);
    }
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info, 
                                            boolean nullable) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" COLUMN ");
        result.append(info.getColumnName());
        if (nullable) {
            result.append(" DEFAULT NULL");
        } else {
            result.append(" NOT NULL");
        }
        return result.toString();
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
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        return DialectUtils.getColumnTypeAlterSQL(this, 
                                                  alterClause, 
                                                  "", 
                                                  false, 
                                                  from, 
                                                  to);
    }
    
    
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.COLUMN_CLAUSE;
        String newDefault = info.getDefaultValue();
        String defaultClause = null;
        if (newDefault != null && !"".equals(newDefault)) {
            defaultClause = DialectUtils.ADD_DEFAULT_CLAUSE;
        } else {
            defaultClause = DialectUtils.DROP_DEFAULT_CLAUSE;
        }
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

    
    public DialectType getDialectType() {
       return DialectType.MAXDB;
    }
    
}
