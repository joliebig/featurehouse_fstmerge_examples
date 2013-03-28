
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;



public class McKoiDialectExt extends CommonHibernateDialect implements HibernateDialect
{
	private class McKoiDialectHelper extends Dialect {
		public McKoiDialectHelper() {
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 2000000000, "binary($l)");
			registerColumnType(Types.BINARY, "binary(2000000000)");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, 2000000000, "blob");
			registerColumnType(Types.BLOB, "blob(2000000000)");
			registerColumnType(Types.BOOLEAN, "bit");
			registerColumnType(Types.CHAR, 255, "char($l)");
			registerColumnType(Types.CHAR, 1000000000, "varchar($l)");
			registerColumnType(Types.CHAR, "varchar(1000000000)");
			registerColumnType(Types.CLOB, 1000000000, "clob($l)");
			registerColumnType(Types.CLOB, "clob(1000000000)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, 2000000000, "longvarbinary($l)");
			registerColumnType(Types.LONGVARBINARY, "longvarbinary(2000000000)");
			registerColumnType(Types.LONGVARCHAR, 1000000000, "longvarchar($l)");
			registerColumnType(Types.LONGVARCHAR, "longvarchar(1000000000)");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			
			
			
			
			
			
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 2000000000, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "varbinary(2000000000)");
			registerColumnType(Types.VARCHAR, 1000000000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(1000000000)");			
		}
	}
	
	
	private McKoiDialectHelper _dialect = new McKoiDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}	
	
	
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
	}

	
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	
	public String getLengthFunction(int dataType)
	{
		return "length";
	}

	
	public String getMaxFunction()
	{
		return "max";
	}

	
	public int getMaxPrecision(int dataType)
	{
		return Integer.MAX_VALUE;
	}

	
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	public int getColumnLength(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	public String getDisplayName()
	{
		return "McKoi";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("mckoi"))
		{
			
			return true;
		}
		return false;
	}

	
	public boolean supportsDropColumn()
	{
		
		return true;
	}

	
	public String getColumnDropSQL(String tableName, String columnName)
	{
		
		return DialectUtils.getColumnDropSQL(tableName, columnName);
	}

	
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView)
	{
		
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false) };
	}

	
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsRenameColumn()
	{
		return false;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnType()
	{
		return false;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_TYPE_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause);
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
	}

	
	public String getDropForeignKeySQL(String fkName, String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
	}

	
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	
	public DialectType getDialectType()
	{
		return DialectType.MCKOI;
	}

	public String[] getIndexAccessMethodsTypes()
	{
		
		return null;
	}

	
	public String[] getIndexStorageOptions()
	{
		
		return null;
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		return new String[] { sql };
	}

	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		
		return null;
	}

	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public boolean supportsAccessMethods()
	{
		
		return false;
	}

	public boolean supportsAddForeignKeyConstraint()
	{
		
		return false;
	}

	public boolean supportsAddUniqueConstraint()
	{
		
		return false;
	}

	public boolean supportsAlterSequence()
	{
		
		return false;
	}

	public boolean supportsAutoIncrement()
	{
		
		return false;
	}

	public boolean supportsCheckOptionsForViews()
	{
		
		return false;
	}

	public boolean supportsCreateIndex()
	{
		
		return false;
	}

	public boolean supportsCreateSequence()
	{
		
		return false;
	}

	public boolean supportsCreateTable()
	{
		
		return false;
	}

	public boolean supportsCreateView()
	{
		
		return false;
	}

	public boolean supportsDropConstraint()
	{
		
		return false;
	}

	public boolean supportsDropIndex()
	{
		
		return false;
	}

	public boolean supportsDropSequence()
	{
		
		return false;
	}

	public boolean supportsDropView()
	{
		
		return false;
	}

	public boolean supportsEmptyTables()
	{
		
		return false;
	}

	public boolean supportsIndexes()
	{
		
		return false;
	}

	public boolean supportsInsertInto()
	{
		
		return false;
	}

	public boolean supportsMultipleRowInserts()
	{
		
		return false;
	}

	public boolean supportsRenameTable()
	{
		
		return false;
	}

	public boolean supportsRenameView()
	{
		
		return false;
	}

	public boolean supportsSequence()
	{
		
		return false;
	}

	public boolean supportsSequenceInformation()
	{
		
		return false;
	}

	public boolean supportsTablespace()
	{
		
		return false;
	}

	public boolean supportsUpdate()
	{
		
		return false;
	}

	
	public boolean supportsAddColumn()
	{
		
		return true;
	}

	
	public boolean supportsViewDefinition() {
		
		return false;
	}	
	
	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		return null;
	}
	
	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		
		return false;
	}
	
}
