
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class DerbyDialect extends DB2Dialect 
                          implements HibernateDialect {

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DerbyDialect.class);   
    
    private static interface i18n {
        
        
        String TYPE_MESSAGE = s_stringMgr.getString("DerbyDialect.typeMessage"); 
        
        
        
        String VARCHAR_MESSAGE = 
            s_stringMgr.getString("DerbyDialect.varcharMessage");
        
        
        
        String COLUMN_LENGTH_MESSAGE = 
            s_stringMgr.getString("DerbyDialect.columnLengthMessage");
    }
    
    public DerbyDialect() {
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
        registerColumnType(Types.DECIMAL, "decimal($p)");
        
        
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
        registerColumnType(Types.VARBINARY, 254, "long varchar for bit data");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
        registerColumnType(Types.VARCHAR, 32700, "long varchar");
        
        registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
        registerColumnType(Types.VARCHAR, "clob(1073741823)");
        
    }

    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        
        return true;
    }    
    
    
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            return 48;
        } else {
            return 31;
        }
    }

    
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }
    
    
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }

    
    public String getDisplayName() {
        return "Derby";
    }
    
    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("Apache Derby")) {
    		
    		return true;
    	}
		return false;
	}    
    
    
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        return new String[] {
            DialectUtils.getColumnAddSQL(info, this, true, false, true)
        };
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
                                        TableColumnInfo[] colInfos, 
                                        ITableInfo ti) 
    {
        ArrayList<String> result = new ArrayList<String>();
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        
        
        
        DialectUtils.getMultiColNotNullSQL(colInfos, 
                                           this, 
                                           alterClause, 
                                           false, 
                                           result);
        
        result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false));
        
        return result.toArray(new String[result.size()]);
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
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      false);
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
        ArrayList<String> list = new ArrayList<String>();
        if (from.getDataType() != to.getDataType()) {
            throw new UnsupportedOperationException(i18n.TYPE_MESSAGE);
        }
        if (from.getDataType() != Types.VARCHAR) {
            throw new UnsupportedOperationException(i18n.VARCHAR_MESSAGE);
        }
        if (from.getColumnSize() > to.getColumnSize()) {
            throw new UnsupportedOperationException(i18n.COLUMN_LENGTH_MESSAGE);
        }
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(to.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(to.getColumnName());
        result.append(" SET DATA TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
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
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
    }
    
    
    public String getDropForeignKeySQL(String fkName, String tableName) {
        return DialectUtils.getDropForeignKeySQL(fkName, tableName);
    }
    
}
