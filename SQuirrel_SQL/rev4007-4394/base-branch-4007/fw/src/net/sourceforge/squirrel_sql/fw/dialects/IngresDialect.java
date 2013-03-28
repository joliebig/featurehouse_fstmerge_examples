
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


public class IngresDialect extends org.hibernate.dialect.IngresDialect 
                             implements HibernateDialect {
    
    public IngresDialect() {
        super();
        
        registerColumnType(Types.BIGINT, "bigint");
        
        
        
        
        
        
        registerColumnType(Types.BINARY, 8000,"byte($l)");
        registerColumnType(Types.BINARY, "long byte" );
        registerColumnType(Types.BIT, "tinyint" );
        registerColumnType(Types.BLOB, "long byte");
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CHAR, "long varchar");
        registerColumnType(Types.CLOB, "long varchar");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p, $s)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)" );
        registerColumnType(Types.INTEGER, "integer");        
        registerColumnType(Types.LONGVARBINARY, "long byte" );
        registerColumnType(Types.LONGVARCHAR, "long varchar" );
        registerColumnType(Types.NUMERIC, "numeric($p, $s)" );
        registerColumnType(Types.REAL, "real" );
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "date");
        registerColumnType(Types.TINYINT, "tinyint");
        
        
        
        
        
        registerColumnType(Types.VARBINARY, "long byte" );        
        
        
        
        
        
        registerColumnType(Types.VARCHAR, 4000, "varchar($l)" );
        registerColumnType(Types.VARCHAR, "long varchar" );
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
            return 53;
        } else {
            return 31;
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
        return "Ingres";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("ingres")) {
    		
    		return true;
    	}
		return false;
	}
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        ArrayList<String> result = new ArrayList<String>();
        
        result.add(DialectUtils.getColumnAddSQL(info, this, false, false, false));
        
        
        
        if (info.isNullable().equals("NO")) {
            result.add(getColumnDefaultAlterSQL(info));
            result.add(getColumnNullableAlterSQL(info));
        }
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }        
        return result.toArray(new String[result.size()]);
    }

    
    public boolean supportsDropColumn() {
        return true;
    }

    
    public String getColumnDropSQL(String tableName, String columnName) {
        String dropClause = DialectUtils.DROP_COLUMN_CLAUSE;
        
        String constraintClause = "CASCADE";
        
        return DialectUtils.getColumnDropSQL(tableName, 
                                             columnName, 
                                             dropClause, 
                                             true,
                                             constraintClause);
    }
  
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] columns, ITableInfo ti) 
    {
        ArrayList<String> result = new ArrayList<String>();
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        for (int i = 0; i < columns.length; i++) {
            TableColumnInfo info = columns[i];
            String notNullSQL = 
                DialectUtils.getColumnNullableAlterSQL(info, 
                                                       false, 
                                                       this, 
                                                       alterClause, 
                                                       true);
            result.add(notNullSQL);
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
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true);
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
        String defaultClause = DialectUtils.DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause, 
                                                     true, 
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

}
