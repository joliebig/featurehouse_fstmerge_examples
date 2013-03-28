
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class HSQLDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class HSQLDialectHelper extends org.hibernate.dialect.HSQLDialect
	{
		public HSQLDialectHelper()
		{
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, "binary");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, "longvarbinary");
			
			registerColumnType(Types.BOOLEAN, "boolean");
			registerColumnType(Types.CHAR, "char($l)");
			registerColumnType(Types.CLOB, "longvarchar");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal");
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
			registerColumnType(Types.VARBINARY, "varbinary");
			registerColumnType(Types.VARCHAR, "varchar");
		}
	}

	
	private HSQLDialectHelper _dialect = new HSQLDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
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

	
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	
	@Override
	public String getDisplayName()
	{
		return "HSQL";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().startsWith("HSQL"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public boolean supportsColumnComment()
	{
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
			true,
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
		

		return getAddPrimaryKeySQL(pkName, columns, ti.getQualifiedName());
	}

	private String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, String tableName)
	{

		

		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(tableName);
		result.append(" ADD CONSTRAINT ");
		result.append(pkName);
		result.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			result.append(columns[i].getColumnName());
			if (i + 1 < columns.length)
			{
				result.append(", ");
			}
		}
		result.append(")");
		return new String[] { result.toString() };
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("HSQLDB doesn't support column comments");
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_ALTER_COLUMN_NULL_STYLE_ONE);
		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				info.getTableName(),
				ST_COLUMN_NAME_KEY,
				info.getColumnName());

		if (info.isNullable().equalsIgnoreCase("YES"))
		{
			valuesMap.put(ST_NULLABLE_KEY, "NULL");
		} else
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
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause, qualifier, prefs, this);

	}

	
	@Override
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this,
			info,
			alterClause,
			false,
			defaultClause,
			qualifier,
			prefs);
	}

	
	@Override
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.HSQLDB;
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
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		
		StringTemplate st = new StringTemplate(ST_ADD_AUTO_INCREMENT_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				column.getTableName(),
				ST_COLUMN_NAME_KEY,
				column.getColumnName());

		String[] pkSQL =
			getAddPrimaryKeySQL("PK_" + column.getTableName() + "_" + column.getColumnName(),
				new TableColumnInfo[] { column },
				column.getTableName());

		ArrayList<String> result = new ArrayList<String>();

		result.add("-- Column must be a primary key and an integer type");
		result.addAll(Arrays.asList(pkSQL));
		result.add(DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs));

		return result.toArray(new String[result.size()]);
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = false;
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

		result.add(sql);

		if (column.getDefaultValue() != null && !"".equals(column.getDefaultValue()))
		{
			StringBuilder defaultSQL = new StringBuilder();
			defaultSQL.append("ALTER TABLE ");
			defaultSQL.append(column.getTableName());
			defaultSQL.append(" ALTER COLUMN ");
			defaultSQL.append(column.getColumnName());
			defaultSQL.append(" SET DEFAULT ");
			if (JDBCTypeMapper.isNumberType(column.getDataType()))
			{
				defaultSQL.append(column.getDefaultValue());
			} else
			{
				defaultSQL.append("'");
				defaultSQL.append(column.getDefaultValue());
				defaultSQL.append("'");
			}
		}

		return result.toArray(new String[result.size()]);
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
		

		
		
		StringTemplate st = new StringTemplate(ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO);

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
		

		
		
		
		StringTemplate st = new StringTemplate(ST_ALTER_SEQUENCE_STYLE_ONE);

		st.setAttribute(ST_SEQUENCE_NAME_KEY, sequenceName);
		st.setAttribute(ST_RESTART_WITH_KEY, "RESTART WITH");
		st.setAttribute(ST_START_VALUE_KEY, restart);

		return new String[] { st.toString() };
	}

	
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		

		
		

		StringTemplate st = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_INDEX_NAME_KEY, indexName, ST_TABLE_NAME_KEY, tableName);

		if (unique)
		{
			valuesMap.put(ST_UNIQUE_KEY, "UNIQUE");
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, columns, qualifier, prefs);
	}

	
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		
		

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_TWO);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		OptionalSqlClause startWithClause = new OptionalSqlClause("START WITH", start);
		OptionalSqlClause incrementByClause = new OptionalSqlClause("INCREMENT BY", increment);

		valuesMap.put(ST_START_WITH_KEY, startWithClause.toString());
		valuesMap.put(ST_INCREMENT_KEY, incrementByClause.toString());

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		

		
		
		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("viewName", viewName);
		valuesMap.put("selectStatement", definition);

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
		st.setAttribute(ST_INDEX_NAME_KEY, indexName);

		return st.toString();
	}

	
	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_TABLE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		

		String templateStr =
			"select start_with as last_value, MAXIMUM_VALUE, MINIMUM_VALUE, 0 as CACHE_VALUE, INCREMENT, "
				+ "case INCREMENT " + "when 'NO' then 0 " + "else 1 " + "end as INCREMENT_BY "
				+ "from INFORMATION_SCHEMA.SYSTEM_SEQUENCES " + "where SEQUENCE_SCHEMA = '$sequenceSchema$' "
				+ "and SEQUENCE_NAME = '$sequenceName$' ";

		StringTemplate st = new StringTemplate(templateStr);

		st.setAttribute(ST_SEQUENCE_NAME_KEY, sequenceName);
		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());

		return st.toString();
	}

	
	@Override
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String templateStr = "";

		if (fromTables != null)
		{
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_ONE;
		} else
		{
			templateStr = ST_UPDATE_STYLE_ONE;
		}

		StringTemplate st = new StringTemplate(templateStr);

		return DialectUtils.getUpdateSQL(st,
			tableName,
			setColumns,
			setValues,
			fromTables,
			whereColumns,
			whereValues,
			qualifier,
			prefs,
			this);

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
		
		return false;
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
	public boolean supportsDropView()
	{
		return true;
	}

	
	@Override
	public boolean supportsEmptyTables()
	{
		return false;
	}

	
	public boolean supportsIndexes()
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
		String templateStr =
			"SELECT VIEW_DEFINITION " + "FROM INFORMATION_SCHEMA.SYSTEM_VIEWS "
				+ "WHERE TABLE_NAME = '$viewName$' " + "and TABLE_SCHEMA = '$schemaName$' ";

		StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_VIEW_NAME_KEY, viewName);
		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());

		return st.toString();
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
	public SequencePropertyMutability getSequencePropertyMutability()
	{
		SequencePropertyMutability result = new SequencePropertyMutability();
		result.setCache(false);
		result.setCycle(false);
		result.setMaxValue(false);
		result.setMinValue(false);
		result.setRestart(true);
		result.setStartWith(true);
		return result;
	}

}
