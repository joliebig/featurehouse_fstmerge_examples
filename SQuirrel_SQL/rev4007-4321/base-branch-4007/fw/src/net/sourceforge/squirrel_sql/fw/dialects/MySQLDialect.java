
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;



public class MySQLDialect extends org.hibernate.dialect.MySQLDialect 
                          implements HibernateDialect {
    
    public MySQLDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, 255, "binary($l)");
        registerColumnType(Types.BINARY, 65532, "blob");
        registerColumnType(Types.BINARY, "longblob");
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BLOB, 65532, "blob");
        registerColumnType(Types.BLOB, "longblob");
        registerColumnType(Types.BOOLEAN, "bool");
        registerColumnType(Types.CHAR, 255, "char($l)");
        registerColumnType(Types.CHAR, 65532, "text");
        registerColumnType(Types.CHAR, "longtext");
        registerColumnType(Types.CLOB, "longtext");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");        
        registerColumnType(Types.LONGVARBINARY, "longblob");
        registerColumnType(Types.LONGVARCHAR, "longtext");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, 255, "varbinary($l)");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.VARCHAR, "text");        
    }    
    
    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
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
        if (dataType == Types.FLOAT) {
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
        return "MySQL";
    }    

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("mysql")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        ArrayList<String> returnVal = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ADD COLUMN ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(info, this));
        result.append(" ");
        DialectUtils.appendDefaultClause(info, result);
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.append(" COMMENT ");
            result.append("'");
            result.append(info.getRemarks());
            result.append("'");
        }
        returnVal.add(result.toString());
        if (info.isNullable().equals("NO")) {
            String setNullSQL = 
                getModifyColumnNullabilitySQL(info.getTableName(), info, false);
            returnVal.add(setNullSQL);
        } 
        
        
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) 
        {
            returnVal.add(getColumnCommentAlterSQL(info));
        }        
        
        
        
        if (info.getDefaultValue() != null 
                && !"".equals(info.getDefaultValue()))
        {
            returnVal.add(getColumnDefaultAlterSQL(info));
        }   
        
        return returnVal.toArray(new String[returnVal.size()]);
    }

    public String getModifyColumnNullabilitySQL(String tableName, 
                                                TableColumnInfo info,
                                                boolean nullable) 
    {
        StringBuilder result = new StringBuilder();
        result.append(" ALTER TABLE ");
        result.append(tableName);
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(info, this));
        if (nullable) {
            result.append(" NULL ");
        } else {
            result.append(" NOT NULL ");
        }
        return result.toString();
    }
    
    
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    public String getColumnDefaultAlterSQL(TableColumnInfo info)
        throws UnsupportedOperationException 
    {   
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(info, this));
        DialectUtils.appendDefaultClause(info, result);
        return result.toString();
    }
    
    
    public boolean supportsColumnComment() {
        return true;
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(info, this));
        result.append(" COMMENT '");
        result.append(info.getRemarks());
        result.append("'");
        return result.toString();
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
                                        TableColumnInfo[] colInfos, ITableInfo ti) 
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(ti.getQualifiedName());
        result.append(" ADD CONSTRAINT ");
        result.append(pkName);
        result.append(" PRIMARY KEY (");
        for (int i = 0; i < colInfos.length; i++) {
            result.append(colInfos[i].getColumnName());
            if (i + 1 < colInfos.length) {
                result.append(", ");
            }
        }
        result.append(")");
        return new String[] { result.toString() };
    }
    
        
    public boolean supportsAlterColumnNull() {
        return true;
    }
        
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.MODIFY_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      true); 
    }

    
    public boolean supportsRenameColumn() {
        return true;
    }
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" CHANGE ");
        result.append(from.getColumnName());
        result.append(" ");
        result.append(to.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(from, this));
        return result.toString();
    }
    
    
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" CHANGE ");
        
        result.append(to.getColumnName());
        result.append(" ");
        result.append(to.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(to, this));
        ArrayList<String> list = new ArrayList<String>();
        list.add(result.toString());
        return list;
    }
    
    
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
    }
    
    
    public String getDropForeignKeySQL(String fkName, String tableName) {
        StringBuilder tmp = new StringBuilder();
        tmp.append("ALTER TABLE ");
        tmp.append(tableName);
        tmp.append(" DROP FOREIGN KEY ");
        tmp.append(fkName);
        return tmp.toString();
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
