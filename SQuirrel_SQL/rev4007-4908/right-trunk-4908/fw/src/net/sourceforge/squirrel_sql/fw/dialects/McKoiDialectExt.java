
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;



public class McKoiDialectExt extends CommonHibernateDialect implements HibernateDialect
{
	private class McKoiDialectHelper extends Dialect
	{
		public McKoiDialectHelper()
		{
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
		if (databaseProductName == null) { return false; }
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

	
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs,
			this) };
	}

	
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsRenameColumn()
	{
		return false;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause, qualifier,
			prefs);
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
	}

	
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
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
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getIndexStorageOptions()
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ADD_AUTO_INCREMENT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column, this, addDefaultClause, supportsNullQualifier, addNullClause,
				qualifier, prefs);

		return new String[] { sql };
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		Boolean deferrableNotSupported = null;
		Boolean initiallyDeferredNotSupported = null;
		Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		

		
		

		String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO;

		StringTemplate st = new StringTemplate(templateStr);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st, valuesMap, columns, qualifier, prefs,
			this) };
	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getSimulatedAlterSequenceSQL(sequenceName, increment, minimum, maximum, minimum,
			cache, cycle, qualifier, prefs, this);
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		result.append("CREATE ");

		if (unique)
		{
			result.append("UNIQUE ");
		}
		result.append(" INDEX ");
		result.append(DialectUtils.shapeQualifiableIdentifier(indexName, qualifier, prefs, this));
		result.append(" ON ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		result.append("(");
		for (String column : columns)
		{
			result.append(column);
			result.append(",");
		}
		result.setLength(result.length() - 1);
		result.append(")");
		return result.toString();
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_THREE);

		OptionalSqlClause startWithClause = new OptionalSqlClause("START", start);
		OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_CLAUSE, increment);
		OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName, ST_START_WITH_KEY, startWithClause,
				ST_INCREMENT_KEY, incClause, ST_MINIMUM_KEY, minClause, ST_MAXIMUM_KEY, maxClause, ST_CACHE_KEY,
				cacheClause);
		if (cycle)
		{
			valuesMap.put(ST_CYCLE_KEY, "CYCLE");
		}

		return DialectUtils.getCreateSequenceSQL(st, valuesMap, qualifier, prefs, this);

	}

	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs, this);
	}

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
	}

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		

		final StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_ONE);
		final HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);
		return DialectUtils.bindAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_TABLE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return new String[] { "foo" };
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.UPDATE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAccessMethods()
	{
		return false;
	}

	public boolean supportsAddForeignKeyConstraint()
	{
		
		return true;
	}

	public boolean supportsAddUniqueConstraint()
	{
		
		return true;
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
		
		return true;
	}

	
	public boolean supportsCreateSequence()
	{
		return true;
	}

	public boolean supportsCreateTable()
	{
		
		return true;
	}

	public boolean supportsCreateView()
	{
		
		return true;
	}

	public boolean supportsDropConstraint()
	{
		
		return true;
	}

	public boolean supportsDropIndex()
	{
		
		return true;
	}

	public boolean supportsDropSequence()
	{
		
		return true;
	}

	public boolean supportsDropView()
	{
		
		return true;
	}

	public boolean supportsEmptyTables()
	{
		
		return true;
	}

	public boolean supportsIndexes()
	{
		
		return true;
	}

	public boolean supportsInsertInto()
	{
		
		return true;
	}

	public boolean supportsMultipleRowInserts()
	{
		
		return true;
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
		return true;
	}

	
	public boolean supportsSequenceInformation()
	{
		return false;
	}

	public boolean supportsTablespace()
	{
		
		return true;
	}

	
	public boolean supportsUpdate()
	{
		return false;
	}

	
	public boolean supportsAddColumn()
	{
		
		return true;
	}

	
	public boolean supportsViewDefinition()
	{
		return false;
	}

	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return "foo";
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

	
	@Override
	public boolean supportsSubSecondTimestamps()
	{
		return false;
	}

}
