
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
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


public class IngresDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class IngresDialectHelper extends org.hibernate.dialect.IngresDialect
	{
		public IngresDialectHelper()
		{
			registerColumnType(Types.BIGINT, "bigint");
			
			
			
			
			
			
			registerColumnType(Types.BINARY, 8000, "byte($l)");
			registerColumnType(Types.BINARY, "long byte");
			registerColumnType(Types.BIT, "tinyint");
			registerColumnType(Types.BLOB, "long byte");
			registerColumnType(Types.BOOLEAN, "tinyint");
			registerColumnType(Types.CHAR, 2000, "char($l)");
			registerColumnType(Types.CHAR, "long varchar");
			registerColumnType(Types.CLOB, "long varchar");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p, $s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "long byte");
			registerColumnType(Types.LONGVARCHAR, "long varchar");
			registerColumnType(Types.NUMERIC, "numeric($p, $s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "date");
			registerColumnType(Types.TIMESTAMP, "date");
			registerColumnType(Types.TINYINT, "tinyint");
			
			
			
			
			
			registerColumnType(Types.VARBINARY, "long byte");
			
			
			
			
			
			registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "long varchar");
		}
	}

	
	private IngresDialectHelper _dialect = new IngresDialectHelper();

	
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

	
	public int getMaxPrecision(int dataType)
	{
		
		
		
		
		if (dataType == Types.FLOAT)
		{
			return 53;
		} else
		{
			return 31;
		}
	}

	
	public String getDisplayName()
	{
		return "Ingres";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("ingres"))
		{
			
			return true;
		}
		return false;
	}

	
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String dropClause = DialectUtils.DROP_COLUMN_CLAUSE;
		
		String constraintClause = "CASCADE";

		return DialectUtils.getColumnDropSQL(tableName,
			columnName,
			dropClause,
			true,
			constraintClause,
			qualifier,
			prefs,
			this);
	}

	
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		for (int i = 0; i < columns.length; i++)
		{
			TableColumnInfo info = columns[i];
			String notNullSQL = DialectUtils.getColumnNullableAlterSQL(info, false, this, alterClause, true, qualifier, prefs);
			result.add(notNullSQL);
		}
		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs, this));
		return result.toArray(new String[result.size()]);
	}

	
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, null, null, null);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs) };
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
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, true, defaultClause, qualifier, prefs);
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, true, qualifier, prefs, this);
	}

	
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	public DialectType getDialectType()
	{
		return DialectType.INGRES;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return null;
	}

	
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String msg = DialectUtils.getUnsupportedMessage(this, DialectUtils.ADD_AUTO_INCREMENT_TYPE);
		throw new UnsupportedOperationException(msg);
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
		boolean addNullClause = false;

		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		result.add(sql);

		StringTemplate st = new StringTemplate(ST_MODIFY_TABLE_TO_RECONSTRUCT);

		result.add(DialectUtils.bindTemplateAttributes(this, st, DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
			column.getTableName()), qualifier, prefs));

		
		
		if (column.isNullable().equals("NO"))
		{
			result.add(getColumnDefaultAlterSQL(column, qualifier, prefs));
			result.addAll(Arrays.asList(getColumnNullableAlterSQL(column, qualifier, prefs)));
		}
		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, null, null));
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

		return DialectUtils.getAddForeignKeyConstraintSQL(fkST,
			fkValuesMap,
			childIndexST,
			ckIndexValuesMap,
			localRefColumns,
			qualifier,
			prefs,
			this);
	}

	
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

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		

		StringTemplate st = new StringTemplate(ST_ALTER_SEQUENCE_STYLE_TWO);

		OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_BY_CLAUSE, increment);
		OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY,
				sequenceName,
				ST_INCREMENT_KEY,
				incClause,
				ST_MINIMUM_KEY,
				minClause,
				ST_MAXIMUM_KEY,
				maxClause,
				ST_CACHE_KEY,
				cacheClause);
		if (cycle)
		{
			valuesMap.put(ST_CYCLE_KEY, "CYCLE");
		}

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };

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
		

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_TWO);

		OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_BY_CLAUSE, increment);
		OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY,
				sequenceName,
				ST_INCREMENT_KEY,
				incClause,
				ST_MINIMUM_KEY,
				minClause,
				ST_MAXIMUM_KEY,
				maxClause,
				ST_CACHE_KEY,
				cacheClause);
		if (cycle)
		{
			valuesMap.put(ST_CYCLE_KEY, "CYCLE");
		}

		return DialectUtils.getCreateSequenceSQL(st, valuesMap, qualifier, prefs, this);
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		
		

		
		

		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		if (checkOption != null)
		{
			valuesMap.put(ST_WITH_CHECK_OPTION_KEY, DialectUtils.WITH_CHECK_OPTION_CLAUSE);
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
		int featureId = DialectUtils.RENAME_TABLE_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.RENAME_VIEW_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String result =
			"SELECT next_value-1, max_value, min_value, cache_size, " + "increment_value, " + "case "
				+ "    when cycle_flag = 'Y' " + "    then 1 " + "    else 0 " + "end " + "FROM iisequences "
				+ "where seq_name = ?";

		return result;
	}

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_UPDATE_CORRELATED_QUERY_STYLE_TWO);

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
		return true;
	}

	
	public boolean supportsAutoIncrement()
	{
		return false;
	}

	
	public boolean supportsCheckOptionsForViews()
	{
		return true;
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
		return false;
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
		
		
		StringBuilder result = new StringBuilder();
		result.append("SELECT TEXT_SEGMENT FROM IIVIEWS ");
		result.append("WHERE TABLE_NAME = '");
		result.append(viewName);
		result.append("' AND TABLE_OWNER = '");
		result.append(qualifier.getSchema());
		result.append("'");

		return result.toString();
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
