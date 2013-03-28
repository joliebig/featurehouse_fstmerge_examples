
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

import org.hibernate.dialect.Oracle9Dialect;



public class Oracle9iDialect extends Oracle9Dialect 
                             implements HibernateDialect {

    public Oracle9iDialect() {
        super();
        registerColumnType(Types.BIGINT, "number($p)");
        registerColumnType(Types.BINARY, 2000, "raw($l)");
        registerColumnType(Types.BINARY, "blob");
        registerColumnType(Types.BIT, "smallint");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CHAR, 4000, "varchar2($l)");
        registerColumnType(Types.CHAR, "clob");
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");        
        registerColumnType(Types.LONGVARBINARY, "blob");
        registerColumnType(Types.LONGVARCHAR, 4000, "varchar2($l)");
        registerColumnType(Types.LONGVARCHAR, "clob");
        registerColumnType(Types.NUMERIC, "number($p)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.VARCHAR, 4000, "varchar2($l)");
        registerColumnType(Types.VARCHAR, "clob");
        
        registerColumnType(Types.OTHER, 4000, "varchar2(4000)");
        registerColumnType(Types.OTHER, "clob");
        
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
        return columnSize;
    }

    
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }

    
    public String getDisplayName() {
        return "Oracle";
    }    
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("oracle")) {
    		
    		return true;
    	}
		return false;
	}

    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            return new String[] {
                DialectUtils.getColumnAddSQL(info, this, true, true, true),
                DialectUtils.getColumnCommentAlterSQL(info)
            };            
        } else {
            return new String[] {
                DialectUtils.getColumnAddSQL(info, this, true, true, true)
            };            
        }
    }

    
    public boolean supportsColumnComment() {
        return true;
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        return DialectUtils.getColumnCommentAlterSQL(info);
    }
    
    
    public boolean supportsDropColumn() {
        return true;
    }
    
    
    public String getColumnDropSQL(String tableName, String columnName) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" DROP COLUMN ");
        result.append(columnName);
        return result.toString();
    }
        
    
    public List<String> getTableDropSQL(ITableInfo ti, 
                                  boolean cascadeConstraints, 
                                  boolean isMaterializedView)
    {
        String cascadeClause = "";
        if (!isMaterializedView) {
            cascadeClause = DialectUtils.CASCADE_CONSTRAINTS_CLAUSE;            
        }

        return DialectUtils.getTableDropSQL(ti,  
                                            true, 
                                            cascadeConstraints, 
                                            true, 
                                            cascadeClause, 
                                            isMaterializedView);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName,
                                        TableColumnInfo[] columns, ITableInfo ti) {
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
    
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        if (info.isNullable().equals("YES")) {
            result.append(" NULL");
        } else {
            result.append(" NOT NULL");
        }
        return result.toString();
    }
    
    
    public boolean supportsRenameColumn() {
        return true;
    }    
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" RENAME COLUMN ");
        result.append(from.getColumnName());
        result.append(" TO ");
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
        ArrayList<String> result = new ArrayList<String>();
        
        
        if ( (from.getDataType() == Types.VARCHAR && to.getDataType() == Types.CLOB)
                || (from.getDataType() == Types.CLOB && to.getDataType() == Types.VARCHAR) ) 
        {
            
            TableColumnInfo newInfo = 
                DialectUtils.getRenamedColumn(to, to.getColumnName()+"_2");
            
            String[] addSQL = this.getColumnAddSQL(newInfo);
            for (int i = 0; i < addSQL.length; i++) {
                result.add(addSQL[i]);
            }
            
            
            StringBuilder updateSQL = new StringBuilder();
            updateSQL.append("update ");
            updateSQL.append(from.getTableName());
            updateSQL.append(" set ");
            updateSQL.append(newInfo.getColumnName());
            updateSQL.append(" = ");
            updateSQL.append(from.getColumnName());
            result.add(updateSQL.toString());
            
            
            String dropSQL = 
                getColumnDropSQL(from.getTableName(), from.getColumnName());
            result.add(dropSQL);
            
            
            String renameSQL = this.getColumnNameAlterSQL(newInfo, to);
            result.add(renameSQL);
        } 
        else 
        {
            StringBuffer tmp = new StringBuffer();
            tmp.append("ALTER TABLE ");
            tmp.append(from.getTableName());
            tmp.append(" MODIFY (");
            tmp.append(from.getColumnName());
            tmp.append(" ");
            tmp.append(DialectUtils.getTypeName(to, this));
            tmp.append(")");
            result.add(tmp.toString());
        }
        return result;
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
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        result.append(" DEFAULT ");
        if (JDBCTypeMapper.isNumberType(info.getDataType())) {
            result.append(info.getDefaultValue());
        } else {
            result.append("'");
            result.append(info.getDefaultValue());
            result.append("'");
        }
        return result.toString();
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
