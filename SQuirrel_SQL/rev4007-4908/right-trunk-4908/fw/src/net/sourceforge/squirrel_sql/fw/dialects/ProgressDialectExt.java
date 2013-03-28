
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;


public class ProgressDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class ProgressDialectHelper extends Dialect
	{
		public ProgressDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "integer");
			registerColumnType(Types.BINARY, 2000, "binary($l)");
			registerColumnType(Types.BINARY, 31982, "varbinary($l)");
			registerColumnType(Types.BINARY, "lvarbinary($l)");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, "lvarbinary($l)");
			registerColumnType(Types.BOOLEAN, "bit");
			registerColumnType(Types.CHAR, 2000, "char($l)");
			registerColumnType(Types.CHAR, "char(2000)");
			
			registerColumnType(Types.CLOB, "varchar($l)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "numeric($p,2)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, 999999999, "lvarbinary($l)");
			registerColumnType(Types.LONGVARBINARY, "lvarbinary(999999999)");
			
			registerColumnType(Types.LONGVARCHAR, "varchar($l)");
			registerColumnType(Types.NUMERIC, "numeric($p,2)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "date");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 31982, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "varbinary(31982)");
			registerColumnType(Types.VARCHAR, 31982, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(31982)");
		}
	}

	
	private final ProgressDialectHelper _dialect = new ProgressDialectHelper();

	
	@Override
	public String getTypeName(final int code, final int length, final int precision, final int scale)
		throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean canPasteTo(final IDatabaseObjectInfo info)
	{
		return true;
	}

	
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	
	@Override
	public String getLengthFunction(final int dataType)
	{
		return "length";
	}

	@Override
	public String getMaxFunction()
	{
		return "max";
	}

	
	@Override
	public int getMaxPrecision(final int dataType)
	{
		if (dataType == Types.FLOAT)
		{
			return 15;
		}
		else
		{
			return 32;
		}
	}

	
	@Override
	public int getMaxScale(final int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	@Override
	public int getPrecisionDigits(final int columnSize, final int dataType)
	{
		return columnSize;
	}

	
	@Override
	public int getColumnLength(final int columnSize, final int dataType)
	{
		return columnSize;
	}

	
	@Override
	public String getDisplayName()
	{
		return "Progress";
	}

	
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().toLowerCase().startsWith("progress")
			|| databaseProductName.trim().toLowerCase().startsWith("openedge"))
		{
			
			return true;
		}
		return false;
	}

	
	public String[] getColumnAddSQL(final TableColumnInfo info) throws UnsupportedOperationException
	{
		return new String[] { "Column add not yet supported" };
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnDropSQL(final String tableName, final String columnName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(final String pkName, final TableColumnInfo[] columns,
		final ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs,
			this) };
	}

	
	@Override
	public String getColumnCommentAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		final StringTemplate st = new StringTemplate(ST_ALTER_COLUMN_NULL_STYLE_ONE);
		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, info.getTableName(), ST_COLUMN_NAME_KEY,
				info.getColumnName());

		if (info.isNullable().equalsIgnoreCase("YES"))
		{
			valuesMap.put(ST_NULLABLE_KEY, "NULL");
		}
		else
		{
			valuesMap.put(ST_NULLABLE_KEY, "NOT NULL");
		}
		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		final StringTemplate st = new StringTemplate(ST_ALTER_COLUMN_NAME_STYLE_ONE);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, from.getTableName(), ST_OLD_COLUMN_NAME_KEY,
				from.getColumnName(), ST_NEW_COLUMN_NAME_KEY, to.getColumnName());

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public boolean supportsAlterColumnType()
	{
		return false;
	}

	
	@Override
	public List<String> getColumnTypeAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		

		StringTemplate st = null;
		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, info.getTableName(), ST_COLUMN_NAME_KEY,
				info.getColumnName());

		if (info.getDefaultValue() != null)
		{
			
			
			
			st = new StringTemplate(ST_ALTER_COLUMN_SET_DEFAULT_STYLE_ONE);
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				valuesMap.put(ST_DEFAULT_VALUE_KEY, info.getDefaultValue());
			}
			else
			{
				valuesMap.put(ST_DEFAULT_VALUE_KEY, "'" + info.getDefaultValue() + "'");
			}
		}
		else
		{
			
			
			
			st = new StringTemplate(ST_ALTER_COLUMN_DROP_DEFAULT_STYLE_ONE);

		}
		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_PRIMARY_KEY_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropForeignKeySQL(final String fkName, final String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getCreateTableSQL(final List<ITableInfo> tables, final ISQLDatabaseMetaData md,
		final CreateScriptPreferences prefs, final boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.PROGRESS;
	}

	
	@Override
	public String[] getIndexAccessMethodsTypes()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String[] getIndexStorageOptions()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String[] getAddAutoIncrementSQL(final TableColumnInfo column,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ALTER_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String[] getAddColumnSQL(final TableColumnInfo column, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = false;
		final boolean addNullClause = true;

		return new String[] { DialectUtils.getAddColumSQL(column, this, addDefaultClause,
			supportsNullQualifier, addNullClause, qualifier, prefs) };
	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(final String localTableName, final String refTableName,
		final String constraintName, final Boolean deferrable, final Boolean initiallyDeferred,
		final Boolean matchFull, final boolean autoFKIndex, final String fkIndexName,
		final Collection<String[]> localRefColumns, final String onUpdateAction, final String onDeleteAction,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		Boolean deferrableNotSupported = null;
		Boolean initiallyDeferredNotSupported = null;
		Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(final String tableName, final String constraintName,
		final TableColumnInfo[] columns, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		

		
		

		String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO;

		StringTemplate st = new StringTemplate(templateStr);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st, valuesMap, columns, qualifier, prefs,
			this) };
	}

	
	@Override
	public String[] getAlterSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String restart, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getSimulatedAlterSequenceSQL(sequenceName, increment, minimum, maximum, minimum,
			cache, cycle, qualifier, prefs, this);
	}

	
	@Override
	public String getCreateIndexSQL(final String indexName, final String tableName, final String accessMethod,
		final String[] columns, final boolean unique, final String tablespace, final String constraints,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
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

	
	@Override
	public String getCreateSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String start, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		
		
		

		

		
		

		String templateStr = ST_CREATE_SEQUENCE_STYLE_TWO;

		StringTemplate startClauseTemplate = new StringTemplate("START WITH $startWith$ ,");
		OptionalSqlClause startClause = new OptionalSqlClause(startClauseTemplate, ST_START_WITH_KEY, start);

		StringTemplate incrementByClauseTemplate = new StringTemplate("INCREMENT BY $incrementBy$ ,");
		OptionalSqlClause incrementClause =
			new OptionalSqlClause(incrementByClauseTemplate, ST_INCREMENT_BY_KEY, increment);

		StringTemplate maxClauseTemplate = new StringTemplate("MAXVALUE $maximum$ ,");
		OptionalSqlClause maxClause = new OptionalSqlClause(maxClauseTemplate, ST_MAXIMUM_KEY, maximum);

		StringTemplate minClauseTemplate = new StringTemplate("MINVALUE $minimum$ ,");
		OptionalSqlClause minClause = new OptionalSqlClause(minClauseTemplate, ST_MINIMUM_KEY, minimum);

		StringTemplate st = new StringTemplate(templateStr);

		st.setAttribute(ST_SEQUENCE_NAME_KEY, "PUB." + sequenceName);
		st.setAttribute(ST_START_WITH_KEY, startClause.toString());
		st.setAttribute(ST_INCREMENT_KEY, incrementClause.toString());
		st.setAttribute(ST_MAXIMUM_KEY, maxClause.toString());
		st.setAttribute(ST_MINIMUM_KEY, minClause.toString());

		if (cycle)
		{
			st.setAttribute(ST_CYCLE_KEY, "CYCLE");
		}
		else
		{
			st.setAttribute(ST_CYCLE_KEY, "NOCYCLE");
		}

		return st.toString();
	}

	
	@Override
	public String getCreateViewSQL(final String viewName, final String definition, final String checkOption,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		

		
		
		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		if (checkOption != null)
		{
			valuesMap.put(ST_WITH_CHECK_OPTION_KEY, "WITH CHECK OPTION");
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropConstraintSQL(final String tableName, final String constraintName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
	}

	
	@Override
	public String getDropIndexSQL(final String tableName, final String indexName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_ONE);
		HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);
		return DialectUtils.bindAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropSequenceSQL(final String sequenceName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		

		DatabaseObjectQualifier pubQualifier = new DatabaseObjectQualifier(qualifier.getCatalog(), "PUB");
		SqlGenerationPreferences pubPrefs = new SqlGenerationPreferences();
		pubPrefs.setQualifyTableNames(true);

		return DialectUtils.getDropSequenceSQL(sequenceName, null, pubQualifier, pubPrefs, this);
	}

	
	@Override
	public String getDropViewSQL(final String viewName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;

		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	@Override
	public String getRenameTableSQL(final String oldTableName, final String newTableName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_RENAME_OBJECT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldTableName, ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String[] getRenameViewSQL(final String oldViewName, final String newViewName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return new String[] { "blah" };
	}

	
	@Override
	public String getSequenceInformationSQL(final String sequenceName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return "SELECT PUB." + sequenceName + ".CURRVAL, \"SEQ-MAX\", \"SEQ-MIN\", \"USER-MISC\", "
			+ "\"SEQ-INCR\", " + "case \"CYCLE-OK\" " + "when 0 then 0 " + "else 1 " + "end as CYCLE_FLAG "
			+ "FROM SYSPROGRESS.SYSSEQUENCES " + "where \"SEQ-NAME\" = '" + sequenceName + "' ";

	}

	
	@Override
	public String[] getUpdateSQL(final String tableName, final String[] setColumns, final String[] setValues,
		final String[] fromTables, final String[] whereColumns, final String[] whereValues,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.UPDATE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsAccessMethods()
	{
		return false;
	}

	
	@Override
	public boolean supportsAddForeignKeyConstraint()
	{
		return true;
	}

	
	@Override
	public boolean supportsAddUniqueConstraint()
	{
		return true;
	}

	
	@Override
	public boolean supportsAlterSequence()
	{
		return false;
	}

	@Override
	public boolean supportsAutoIncrement()
	{
		return false;
	}

	
	@Override
	public boolean supportsCheckOptionsForViews()
	{
		return true;
	}

	
	@Override
	public boolean supportsCreateIndex()
	{
		return true;
	}

	
	@Override
	public boolean supportsCreateSequence()
	{
		return true;
	}

	
	@Override
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
		return false;
	}

	
	public boolean supportsIndexes()
	{
		return true;
	}

	
	public boolean supportsMultipleRowInserts()
	{
		return true;
	}

	
	public boolean supportsRenameTable()
	{
		return true;
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
		return true;
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

	
	public boolean supportsViewDefinition()
	{
		return true;
	}

	
	public String getViewDefinitionSQL(final String viewName, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		
		
		

		final String templateStr =
			"SELECT 'CREATE VIEW $viewName$ AS ' || VIEWTEXT " + "FROM SYSPROGRESS.SYSVIEWS "
				+ "where VIEWNAME = '$viewName$' and OWNER = '$schemaName$' ";

		final StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_VIEW_NAME_KEY, viewName);
		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());

		return st.toString();
	}

	
	public String getQualifiedIdentifier(final String identifier, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		return qualifier.getSchema() + "." + identifier;
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		return false;
	}

	
	@Override
	public boolean supportsDropPrimaryKey()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsSubSecondTimestamps()
	{
		return false;
	}

}
