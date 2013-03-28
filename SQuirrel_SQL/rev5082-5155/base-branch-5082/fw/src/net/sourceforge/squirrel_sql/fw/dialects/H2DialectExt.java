package net.sourceforge.squirrel_sql.fw.dialects;


import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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


public class H2DialectExt extends CommonHibernateDialect implements HibernateDialect
{

	
	private class H2DialectHelper extends org.hibernate.dialect.Dialect
	{

		public H2DialectHelper()
		{
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

		}

	}

	
	private H2DialectHelper _dialect = new H2DialectHelper();

	public H2DialectExt()
	{
		
		super.DROP_COLUMN_SQL_TEMPLATE = ST_DROP_COLUMN_STYLE_TWO;
	}

	
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

	
	public String getDisplayName()
	{
		return "H2";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().startsWith("H2"))
		{
			
			return true;
		}
		return false;
	}

	
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		StringBuffer addPKSQL = new StringBuffer();
		addPKSQL.append("ALTER TABLE ");
		addPKSQL.append(DialectUtils.shapeQualifiableIdentifier(ti.getSimpleName(), qualifier, prefs, this));
		addPKSQL.append(" ADD CONSTRAINT ");
		addPKSQL.append(pkName);
		addPKSQL.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			TableColumnInfo info = columns[i];
			if (info.isNullable().equals("YES"))
			{
				String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
				String notNullSql =
					DialectUtils.getColumnNullableAlterSQL(info, false, this, alterClause, true, qualifier, prefs);

				result.add(notNullSql);
			}
			addPKSQL.append(DialectUtils.shapeIdentifier(info.getColumnName(), prefs, this));
			if (i + 1 < columns.length)
			{
				addPKSQL.append(", ");
			}
		}
		addPKSQL.append(")");

		result.add(addPKSQL.toString());
		return result.toArray(new String[result.size()]);
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier,
			prefs) };
	}

	
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause, qualifier, prefs, this);
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier,
			prefs);
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause, qualifier,
			prefs);
	}

	
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		StringTemplate st = new StringTemplate(DROP_COLUMN_SQL_TEMPLATE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_COLUMN_NAME_KEY, columnName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
		return DialectType.H2;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "DEFAULT", "HASH" };
	}

	
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_ADD_AUTO_INCREMENT_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, column.getTableName(), ST_COLUMN_NAME_KEY,
				column.getColumnName());

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column, this, addDefaultClause, supportsNullQualifier, addNullClause,
				qualifier, prefs);

		result.add(sql);

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, qualifier, prefs));
		}

		return result.toArray(new String[result.size()]);
	}

	
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

		return DialectUtils.getAddForeignKeyConstraintSQL(fkST, fkValuesMap, childIndexST, ckIndexValuesMap,
			localRefColumns, qualifier, prefs, this);
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		

		StringTemplate st = new StringTemplate(ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_KEY, "CONSTRAINT",
				ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, columns, qualifier,
			prefs) };

	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		
		
		

		StringTemplate st = new StringTemplate(ST_ALTER_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);
		if (DialectUtils.isNotEmptyString(restart))
		{
			valuesMap.put(ST_RESTART_WITH_KEY, "RESTART WITH");
			valuesMap.put(ST_START_VALUE_KEY, restart);
		}
		if (DialectUtils.isNotEmptyString(increment))
		{
			valuesMap.put(ST_INCREMENT_BY_KEY, "INCREMENT BY");
			valuesMap.put(ST_INCREMENT_VALUE_KEY, increment);
		}

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		StringTemplate st = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
		
		

		HashMap<String, String> valuesMap = new HashMap<String, String>();

		if (unique)
		{
			valuesMap.put(ST_UNIQUE_KEY, "UNIQUE");
			if (accessMethod != null && "HASH".equalsIgnoreCase(accessMethod))
			{
				valuesMap.put(ST_STORAGE_OPTION_KEY, "HASH");
			}
		}
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		return DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		

		
		

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);
		if (DialectUtils.isNotEmptyString(cache))
		{
			valuesMap.put(ST_CACHE_KEY, "CACHE");
			valuesMap.put(ST_CACHE_VALUE_KEY, cache);
		}
		if (DialectUtils.isNotEmptyString(increment))
		{
			valuesMap.put(ST_INCREMENT_VALUE_KEY, increment);
		}
		if (DialectUtils.isNotEmptyString(start))
		{
			valuesMap.put(ST_START_VALUE_KEY, start);
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		
		

		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_THREE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_INDEX_NAME_KEY, indexName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
		
		StringTemplate st = new StringTemplate(ST_DROP_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_RENAME_OBJECT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldTableName, ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String msg = DialectUtils.getUnsupportedMessage(this, DialectUtils.RENAME_VIEW_TYPE);
		throw new UnsupportedOperationException(msg);
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		String templateStr =
			"SELECT CURRENT_VALUE, 'NONE', 'NONE', CACHE, INCREMENT, 0 " + "FROM INFORMATION_SCHEMA.SEQUENCES "
				+ "WHERE SEQUENCE_SCHEMA = '$schemaName$' " + "AND SEQUENCE_NAME = '$sequenceName$' "
				+ "AND SEQUENCE_CATALOG = '$catalogName$'";
		StringTemplate st = new StringTemplate(templateStr);

		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());
		st.setAttribute(ST_CATALOG_NAME_KEY, qualifier.getCatalog());
		st.setAttribute(ST_SEQUENCE_NAME_KEY, sequenceName);

		return st.toString();

	}

	
	public boolean supportsAccessMethods()
	{
		return true;
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
		return true;
	}

	
	public boolean supportsAutoIncrement()
	{
		return true;
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
			"select view_definition from information_schema.views "
				+ "where table_schema = '$schemaName$' and UPPER(table_name) = UPPER('$viewName$') ";

		StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());
		st.setAttribute(ST_VIEW_NAME_KEY, viewName);

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

}
