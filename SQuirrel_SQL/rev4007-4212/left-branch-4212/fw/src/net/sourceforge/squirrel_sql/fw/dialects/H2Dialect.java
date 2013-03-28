
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

import org.hibernate.Hibernate;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class H2Dialect extends Dialect implements HibernateDialect {

    
    static final String DEFAULT_BATCH_SIZE = "15";
    
    public H2Dialect() {
        super();
        registerColumnType(Types.ARRAY, "array");
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "binary");
        registerColumnType(Types.BIT, "boolean");
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.CHAR, "varchar($l)");
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.LONGVARBINARY, "longvarbinary");
        registerColumnType(Types.LONGVARCHAR, "longvarchar");
        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.VARBINARY, "binary($l)");
        registerColumnType(Types.VARCHAR, "varchar($l)");        
        
        
        


        registerFunction("acos", new StandardSQLFunction("acos", Hibernate.DOUBLE));
        registerFunction("asin", new StandardSQLFunction("asin", Hibernate.DOUBLE));
        registerFunction("atan", new StandardSQLFunction("atan", Hibernate.DOUBLE));
        registerFunction("atan2", new StandardSQLFunction("atan2", Hibernate.DOUBLE));
        registerFunction("bitand", new StandardSQLFunction("bitand", Hibernate.INTEGER));
        registerFunction("bitor", new StandardSQLFunction("bitor", Hibernate.INTEGER));
        registerFunction("bitxor", new StandardSQLFunction("bitxor", Hibernate.INTEGER));
        registerFunction("ceiling", new StandardSQLFunction("ceiling", Hibernate.DOUBLE));
        registerFunction("cos", new StandardSQLFunction("cos", Hibernate.DOUBLE));
        registerFunction("cot", new StandardSQLFunction("cot", Hibernate.DOUBLE));
        registerFunction("degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE));
        registerFunction("exp", new StandardSQLFunction("exp", Hibernate.DOUBLE));
        registerFunction("floor", new StandardSQLFunction("floor", Hibernate.DOUBLE));
        registerFunction("log", new StandardSQLFunction("log", Hibernate.DOUBLE));
        registerFunction("log10", new StandardSQLFunction("log10", Hibernate.DOUBLE));

        registerFunction("pi", new NoArgSQLFunction("pi", Hibernate.DOUBLE));
        registerFunction("power", new StandardSQLFunction("power", Hibernate.DOUBLE));
        registerFunction("radians", new StandardSQLFunction("radians", Hibernate.DOUBLE));
        registerFunction("rand", new NoArgSQLFunction("rand", Hibernate.DOUBLE));
        registerFunction("round", new StandardSQLFunction("round", Hibernate.DOUBLE));
        registerFunction("roundmagic", new StandardSQLFunction("roundmagic", Hibernate.DOUBLE));
        registerFunction("sign", new StandardSQLFunction("sign", Hibernate.INTEGER));
        registerFunction("sin", new StandardSQLFunction("sin", Hibernate.DOUBLE));

        registerFunction("tan", new StandardSQLFunction("tan", Hibernate.DOUBLE));
        registerFunction("truncate", new StandardSQLFunction("truncate", Hibernate.DOUBLE));

        registerFunction("compress", new StandardSQLFunction("compress", Hibernate.BINARY));
        registerFunction("expand", new StandardSQLFunction("compress", Hibernate.BINARY));
        registerFunction("decrypt", new StandardSQLFunction("decrypt", Hibernate.BINARY));
        registerFunction("encrypt", new StandardSQLFunction("encrypt", Hibernate.BINARY));
        registerFunction("hash", new StandardSQLFunction("hash", Hibernate.BINARY));

        registerFunction("ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER));

        registerFunction("char", new StandardSQLFunction("char", Hibernate.CHARACTER));
        registerFunction("concat", new VarArgsSQLFunction(Hibernate.STRING, "(", "||", ")"));
        registerFunction("difference", new StandardSQLFunction("difference", Hibernate.INTEGER));
        registerFunction("hextoraw", new StandardSQLFunction("hextoraw", Hibernate.STRING));
        registerFunction("lower", new StandardSQLFunction("lower", Hibernate.STRING));
        registerFunction("insert", new StandardSQLFunction("lower", Hibernate.STRING));
        registerFunction("left", new StandardSQLFunction("left", Hibernate.STRING));



        registerFunction("lcase", new StandardSQLFunction("lcase", Hibernate.STRING));
        registerFunction("ltrim", new StandardSQLFunction("ltrim", Hibernate.STRING));
        registerFunction("octet_length", new StandardSQLFunction("octet_length", Hibernate.INTEGER));
        registerFunction("position", new StandardSQLFunction("position", Hibernate.INTEGER));
        registerFunction("rawtohex", new StandardSQLFunction("rawtohex", Hibernate.STRING));
        registerFunction("repeat", new StandardSQLFunction("repeat", Hibernate.STRING));
        registerFunction("replace", new StandardSQLFunction("replace", Hibernate.STRING));
        registerFunction("right", new StandardSQLFunction("right", Hibernate.STRING));
        registerFunction("rtrim", new StandardSQLFunction("rtrim", Hibernate.STRING));
        registerFunction("soundex", new StandardSQLFunction("soundex", Hibernate.STRING));
        registerFunction("space", new StandardSQLFunction("space", Hibernate.STRING));
        registerFunction("stringencode", new StandardSQLFunction("stringencode", Hibernate.STRING));
        registerFunction("stringdecode", new StandardSQLFunction("stringdecode", Hibernate.STRING));


        registerFunction("ucase", new StandardSQLFunction("ucase", Hibernate.STRING));

        registerFunction("stringtoutf8", new StandardSQLFunction("stringtoutf8", Hibernate.BINARY));
        registerFunction("utf8tostring", new StandardSQLFunction("utf8tostring", Hibernate.STRING));

        registerFunction("current_date", new NoArgSQLFunction("current_date", Hibernate.DATE));
        registerFunction("current_time", new NoArgSQLFunction("current_time", Hibernate.TIME));
        registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP));
        registerFunction("datediff", new NoArgSQLFunction("datediff", Hibernate.INTEGER));
        registerFunction("dayname", new StandardSQLFunction("dayname", Hibernate.STRING));
        registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", Hibernate.INTEGER));
        registerFunction("dayofweek", new StandardSQLFunction("dayofweek", Hibernate.INTEGER));
        registerFunction("dayofyear", new StandardSQLFunction("dayofyear", Hibernate.INTEGER));



        registerFunction("monthname", new StandardSQLFunction("monthname", Hibernate.STRING));
        registerFunction("quater", new StandardSQLFunction("quater", Hibernate.INTEGER));

        registerFunction("week", new StandardSQLFunction("week", Hibernate.INTEGER));


        registerFunction("curdate", new NoArgSQLFunction("curdate", Hibernate.DATE));
        registerFunction("curtime", new NoArgSQLFunction("curtime", Hibernate.TIME));
        registerFunction("curtimestamp", new NoArgSQLFunction("curtimestamp", Hibernate.TIME));
        registerFunction("now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP));

        registerFunction("database", new NoArgSQLFunction("database", Hibernate.STRING));
        registerFunction("user", new NoArgSQLFunction("user", Hibernate.STRING));

        getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);

    }

    public String getAddColumnString() {
        return "add column";
    }

    public boolean supportsIdentityColumns() {
        return true;
    }

    public String getIdentityColumnString() {
        return "generated by default as identity"; 
    }

    public String getIdentitySelectString() {
        return "call identity()";
    }

    public String getIdentityInsertString() {
        return "null";
    }

    public String getForUpdateString() {
        return " for update";
    }

    public boolean supportsUnique() {
        return true;
    }

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuffer(sql.length() + 20).
            append(sql).
            append(hasOffset ? " limit ? offset ?" : " limit ?").
            toString();
    }
    
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }    

    public boolean bindLimitParametersFirst() {
        return false;
    }

    public boolean supportsIfExistsAfterTableName() {
        return true;
    }

    public String[] getCreateSequenceStrings(String sequenceName) {
        return new String[] {
                "create sequence " + sequenceName
        };
    }

    public String[] getDropSequenceStrings(String sequenceName) {
        return new String[] {
                "drop sequence " + sequenceName
        };
    }

    public String getSelectSequenceNextValString(String sequenceName) {
        return "next value for " + sequenceName;
    }

    public String getSequenceNextValString(String sequenceName) {
        return "call next value for " + sequenceName;
    }

    public String getQuerySequencesString() {
        return "select name from information_schema.sequences";
    }

    public boolean supportsSequences() {
        return true;
    }

    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {

        
        public String extractConstraintName(SQLException sqle) {
            String constraintName = null;
            
            
            if(sqle.getSQLState().startsWith("23")) {
                String message = sqle.getMessage();
                int idx = message.indexOf("violation: ");
                if(idx > 0) {
                    constraintName = message.substring(idx + "violation: ".length());
                }
            }
            return constraintName;
        }

    };

    public boolean supportsTemporaryTables() {
        return true;
    }
    
    public String getCreateTemporaryTableString() {
        return "create temporary table if not exists";
    }

    public boolean supportsCurrentTimestampSelection() {
        return true;
    }
    
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }
    
    public String getCurrentTimestampSelectString() {
        return "call current_timestamp()";
    }    
    
    public boolean supportsUnionAll() {
        return true;
    }

    
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }

    
    public String getLengthFunction(int dataType) {
        return "length";
    }

    
    public String getMaxFunction() {
        return "max";
    }

    
    public int getMaxPrecision(int dataType) {
        return Integer.MAX_VALUE;
    }

    
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }

    
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }
 
    
    public String getDisplayName() {
        return "H2";
    }

    
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().startsWith("H2")) {
    		
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
        return DialectUtils.getColumnDropSQL(tableName, columnName, "DROP COLUMN", false, null);
    }
    
    
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] columns, 
                                        ITableInfo ti) 
    {
        ArrayList<String> result = new ArrayList<String>();
        StringBuffer addPKSQL = new StringBuffer();
        addPKSQL.append("ALTER TABLE ");
        addPKSQL.append(ti.getQualifiedName());
        addPKSQL.append(" ADD CONSTRAINT ");
        addPKSQL.append(pkName);
        addPKSQL.append(" PRIMARY KEY (");
        for (int i = 0; i < columns.length; i++) {
            TableColumnInfo info = columns[i];
            if (info.isNullable().equals("YES")) {
                result.add(getColumnNullableAlterSQL(info, false));
            }
            addPKSQL.append(info.getColumnName());
            if (i + 1 < columns.length) {
                addPKSQL.append(", ");
            }
        }
        addPKSQL.append(")");
        result.add(addPKSQL.toString());
        return result.toArray(new String[result.size()]);
    }
    
    
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {       
        return DialectUtils.getColumnCommentAlterSQL(info);
    }
 
    
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      true);
    }

        
    public boolean supportsAlterColumnNull() {
        return true;
    }

        
    private String getColumnNullableAlterSQL(TableColumnInfo info, 
                                             boolean isNullable) 
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(DialectUtils.getTypeName(info, this));
        if (isNullable) {
            result.append(" NULL ");
        } else {
            result.append(" NOT NULL ");
        }
        return result.toString();        
        
    }
    
    
    public boolean supportsRenameColumn() {
        return true;
    }    
    
    
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, 
                                                  to, 
                                                  alterClause, 
                                                  renameToClause);
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
        String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
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
    
}

