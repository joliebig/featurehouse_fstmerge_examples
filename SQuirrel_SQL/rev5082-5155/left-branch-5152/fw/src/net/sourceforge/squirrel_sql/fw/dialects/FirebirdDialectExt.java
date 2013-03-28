
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class FirebirdDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class FirebirdDialectHelper extends org.hibernate.dialect.FirebirdDialect
	{
		public FirebirdDialectHelper()
		{
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
	}

	
	private FirebirdDialectHelper _dialect = new FirebirdDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	
	@Override
	public String getLengthFunction(int dataType)
	{
		return "strlen";
	}

	
	@Override
	public int getMaxPrecision(int dataType)
	{
		return 18;
	}

	
	@Override
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize * 2;
	}

	
	@Override
	public int getColumnLength(int columnSize, int dataType)
	{
		if (dataType == Types.BIGINT || dataType == Types.DECIMAL || dataType == Types.DOUBLE
			|| dataType == Types.FLOAT || dataType == Types.NUMERIC || dataType == Types.REAL)
		{
			return getMaxPrecision(dataType);
		}
		if (dataType == Types.BLOB || dataType == Types.LONGVARBINARY || dataType == Types.LONGVARCHAR)
		{
			return 2147483647;
		}
		return columnSize;
	}

	
	@Override
	public String getDisplayName()
	{
		return "Firebird";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().startsWith("Firebird"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false,
			qualifier,
			prefs,
			this);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(ti.getSimpleName(), qualifier, prefs, this));
		result.append(" ADD CONSTRAINT ");
		result.append(pkName);
		result.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			String shapedColumn = DialectUtils.shapeIdentifier(columns[i].getColumnName(), prefs, this);
			result.append(shapedColumn);
			if (i + 1 < columns.length)
			{
				result.append(", ");
			}
		}
		result.append(")");
		return new String[] { result.toString() };
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		
		
		return false;
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause, qualifier, prefs, this);
	}

	
	@Override
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	@Override
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		StringBuilder result = new StringBuilder();
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

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
	}

	
	@Override
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.FIREBIRD;
	}

	
	@Override
	public String[] getIndexAccessMethodsTypes()
	{
		
		return null;
	}

	
	@Override
	public String[] getIndexStorageOptions()
	{
		
		return null;
	}

	
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		String incrementNotSupported = null;
		String minValueNotSupported = null;
		String maxValueNotSupported = null;
		String startValueNotSupported = null;
		String cacheNotSupported = null;
		boolean cycle = false;

		
		String sequenceName = column.getColumnName() + "_AUTOINC_SEQ";

		String generatorSql =
			getCreateSequenceSQL(sequenceName,
				incrementNotSupported,
				minValueNotSupported,
				maxValueNotSupported,
				startValueNotSupported,
				cacheNotSupported,
				cycle,
				qualifier,
				prefs);

		String triggerName = "CREATE_" + column.getColumnName().toUpperCase();
		
		String trigTemplate =
			"CREATE TRIGGER $triggerName$ FOR $tableName$ BEFORE INSERT POSITION 0 AS "
				+ "BEGIN NEW.$columnName$ = GEN_ID($sequenceName$, 1); END";

		StringTemplate st = new StringTemplate(trigTemplate);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_COLUMN_NAME_KEY, column.getColumnName());

		valuesMap.put(ST_TABLE_NAME_KEY, column.getTableName());
		valuesMap.put(ST_SEQUENCE_NAME_KEY, sequenceName);
		valuesMap.put(ST_TRIGGER_NAME_KEY, triggerName);
		
		String trigSql = DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);

		return new String[] { generatorSql, trigSql.toString() };
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
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

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		

		StringTemplate fkST = new StringTemplate(ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE);
		HashMap<String, String> fkValuesMap = DialectUtils.getValuesMap(ST_CHILD_TABLE_KEY, localTableName);
		fkValuesMap.put(ST_CONSTRAINT_KEY, "CONSTRAINT");
		fkValuesMap.put(ST_CONSTRAINT_NAME_KEY, constraintName);
		fkValuesMap.put(ST_PARENT_TABLE_KEY, refTableName);

		StringTemplate childIndexST = null;
		HashMap<String, String> ckIndexValuesMap = null;

		if (autoFKIndex)
		{
			
			

			childIndexST = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
			ckIndexValuesMap = new HashMap<String, String>();
			ckIndexValuesMap.put(ST_INDEX_NAME_KEY, "fk_child_idx");
		}

		return DialectUtils.getAddForeignKeyConstraintSQL(fkST,
			fkValuesMap,
			childIndexST,
			ckIndexValuesMap,
			localRefColumns,
			qualifier,
			prefs,
			this);
	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		

		
		

		String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO;

		StringTemplate st = new StringTemplate(templateStr);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st,
			valuesMap,
			columns,
			qualifier,
			prefs,
			this) };
	}

	
	@Override
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate("SET GENERATOR $generatorName$ TO $value$");

		st.setAttribute(ST_GENERATOR_NAME_KEY, sequenceName);
		st.setAttribute(ST_VALUE_KEY, minimum);

		return new String[] { st.toString() };
	}

	
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		StringTemplate st = new StringTemplate(ST_CREATE_INDEX_STYLE_THREE);
		
		

		HashMap<String, String> valuesMap = new HashMap<String, String>();

		if (unique)
		{
			valuesMap.put(ST_UNIQUE_KEY, "UNIQUE");
		}
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		return DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
	}

	
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate("CREATE GENERATOR $generatorName$");

		st.setAttribute("generatorName", sequenceName);

		return st.toString();
	}

	
	@Override
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_THREE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_INDEX_NAME_KEY, indexName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate("DROP GENERATOR $generatorName$");

		st.setAttribute("generatorName", sequenceName);

		return st.toString();
	}

	
	@Override
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.RENAME_TABLE_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.RENAME_VIEW_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
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
		return true;
	}

	
	@Override
	public boolean supportsAutoIncrement()
	{
		return true;
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

	
	@Override
	public boolean supportsCreateView()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropConstraint()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropIndex()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropSequence()
	{
		return true;
	}

	
	@Override
	public boolean supportsEmptyTables()
	{
		return false;
	}

	
	@Override
	public boolean supportsIndexes()
	{
		return true;
	}

	
	@Override
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
		return true;
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
		return true;
	}

	
	public boolean supportsAddColumn()
	{
		return true;
	}

	
	public boolean supportsViewDefinition()
	{
		return true;
	}

	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return "select rdb$view_source from rdb$relations where rdb$relation_name = '" + viewName + "'";
	}

	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

	
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public boolean supportsDropView()
	{
		return true;
	}

	
	@Override
	public boolean supportsSubSecondTimestamps()
	{
		return false;
	}

}
