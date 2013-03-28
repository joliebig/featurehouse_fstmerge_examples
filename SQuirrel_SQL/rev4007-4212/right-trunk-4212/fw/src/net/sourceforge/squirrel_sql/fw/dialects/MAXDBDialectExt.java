
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
import org.hibernate.dialect.SAPDBDialect;

public class MAXDBDialectExt extends CommonHibernateDialect implements HibernateDialect
{
	private class MAXDBDialectHelper extends SAPDBDialect
	{
		public MAXDBDialectHelper()
		{
			registerColumnType(Types.BIGINT, "fixed(19,0)");
			registerColumnType(Types.BINARY, 8000, "char($l) byte");
			registerColumnType(Types.BINARY, "long varchar byte");
			registerColumnType(Types.BIT, "boolean");
			registerColumnType(Types.BLOB, "long byte");
			registerColumnType(Types.BOOLEAN, "boolean");
			registerColumnType(Types.CLOB, "long varchar");
			registerColumnType(Types.CHAR, 8000, "char($l) ascii");
			registerColumnType(Types.CHAR, "long varchar ascii");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, 8000, "varchar($l) byte");
			registerColumnType(Types.LONGVARBINARY, "long byte");
			registerColumnType(Types.LONGVARCHAR, "long ascii");
			registerColumnType(Types.NUMERIC, "fixed($p,$s)");
			registerColumnType(Types.REAL, "float($p)");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "fixed(3,0)");
			registerColumnType(Types.VARBINARY, "long byte");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "long ascii");
		}
	}

	
	private final MAXDBDialectHelper _dialect = new MAXDBDialectHelper();

	
	@Override
	public String getTypeName(final int code, final int length, final int precision, final int scale)
		throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean canPasteTo(final IDatabaseObjectInfo info)
	{
		boolean result = true;
		final DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
	}

	
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	@Override
	public int getMaxPrecision(final int dataType)
	{
		return 38;
	}

	
	@Override
	public int getPrecisionDigits(final int columnSize, final int dataType)
	{
		return columnSize * 2;
	}

	
	@Override
	public int getColumnLength(final int columnSize, final int dataType)
	{
		
		if (dataType == Types.LONGVARBINARY)
		{
			return Integer.MAX_VALUE;
		}
		return columnSize;
	}

	
	@Override
	public String getDisplayName()
	{
		return "MaxDB";
	}

	
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		final String lname = databaseProductName.trim().toLowerCase();
		if (lname.startsWith("sap") || lname.startsWith("maxdb"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnDropSQL(final String tableName, final String columnName)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName);
	}

	
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(final String pkName, final TableColumnInfo[] columns,
		final ITableInfo ti)
	{
		final ArrayList<String> result = new ArrayList<String>();
		for (final TableColumnInfo info : columns)
		{
			result.add(getColumnNullableAlterSQL(info, false));
		}
		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false));
		return result.toArray(new String[result.size()]);
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	@Override
	public String getColumnCommentAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, null, null, null);
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final boolean nullable = info.isNullable().equalsIgnoreCase("YES");
		return new String[] { getColumnNullableAlterSQL(info, nullable) };
	}

	
	public String getColumnNullableAlterSQL(final TableColumnInfo info, final boolean nullable)
	{
		final StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" COLUMN ");
		result.append(info.getColumnName());
		if (nullable)
		{
			result.append(" DEFAULT NULL");
		} else
		{
			result.append(" NOT NULL");
		}
		return result.toString();
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to)
	{
		return DialectUtils.getColumnRenameSQL(from, to);
	}

	
	@Override
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	@Override
	public List<String> getColumnTypeAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final String alterClause = DialectUtils.MODIFY_CLAUSE;
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, "", false, from, to);
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info)
	{
		final String alterClause = DialectUtils.COLUMN_CLAUSE;
		final String newDefault = info.getDefaultValue();
		String defaultClause = null;
		if (newDefault != null && !"".equals(newDefault))
		{
			defaultClause = DialectUtils.ADD_DEFAULT_CLAUSE;
		} else
		{
			defaultClause = DialectUtils.DROP_DEFAULT_CLAUSE;
		}
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause);
	}

	
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
	}

	
	@Override
	public String getDropForeignKeySQL(final String fkName, final String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
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
		return DialectType.MAXDB;
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
	public String[] getAddAutoIncrementSQL(final TableColumnInfo column,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ADD_AUTO_INCREMENT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);		
	}

	
	@Override
	public String[] getAddColumnSQL(final TableColumnInfo column, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();

		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = false;
		final boolean addNullClause = true;

		final String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		result.add(sql);

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, null, null));
		}

		return result.toArray(new String[result.size()]);

	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(final String localTableName, final String refTableName,
		final String constraintName, final Boolean deferrable, final Boolean initiallyDeferred,
		final Boolean matchFull, final boolean autoFKIndex, final String fkIndexName,
		final Collection<String[]> localRefColumns, final String onUpdateAction, final String onDeleteAction,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
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
	public String[] getAddUniqueConstraintSQL(final String tableName, final String constraintName,
		final TableColumnInfo[] columns, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ADD_UNIQUE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);		
	}

	
	@Override
	public String[] getAlterSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String restart, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ALTER_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getCreateIndexSQL(final String indexName, final String tableName, final String accessMethod,
		final String[] columns, final boolean unique, final String tablespace, final String constraints,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
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

	
	@Override
	public String getCreateSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String start, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		

		

		final StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_TWO);

		final OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_BY_CLAUSE, increment);
		final OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		final OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		final OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		final HashMap<String, String> valuesMap =
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

	
	@Override
	public String getCreateViewSQL(final String viewName, final String definition, final String checkOption,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		

		
		
		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		if (checkOption != null && !"".equals(checkOption))
		{
			valuesMap.put(ST_WITH_CHECK_OPTION_KEY, "WITH CHECK OPTION");
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropConstraintSQL(final String tableName, final String constraintName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_CONSTRAINT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropIndexSQL(final String tableName, final String indexName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		
		
      StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_ONE);
      HashMap<String, String> valuesMap = new HashMap<String, String>();
      valuesMap.put(ST_INDEX_NAME_KEY, indexName);
      valuesMap.put(ST_TABLE_NAME_KEY, tableName);
      return DialectUtils.getDropIndexSQL(st, valuesMap, qualifier, prefs, this);
	}

	
	@Override
	public String getDropSequenceSQL(final String sequenceName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		final StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		final HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropViewSQL(final String viewName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_DROP_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getRenameTableSQL(final String oldTableName, final String newTableName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_RENAME_TABLE_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldTableName, ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String[] getRenameViewSQL(final String oldViewName, final String newViewName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		
		

		
		StringTemplate st = new StringTemplate(ST_RENAME_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldViewName, ST_NEW_OBJECT_NAME_KEY, newViewName);

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
	}

	
	@Override
	public String getSequenceInformationSQL(final String sequenceName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return "SELECT LAST_NUMBER as last_value, MAX_VALUE, MIN_VALUE, CACHE_SIZE as cache_value, "
			+ "INCREMENT_BY, CYCLE_FLAG as is_cycled " + "FROM DOMAIN.SEQUENCES";
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
		return false;
	}

	
	@Override
	public boolean supportsAlterSequence()
	{
		return false;
	}

	
	@Override
	public boolean supportsAutoIncrement()
	{
		return false ;
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
	public boolean supportsCreateView()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropConstraint()
	{
		return false;
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

	@Override
	public boolean supportsIndexes()
	{
		
		return false;
	}

	@Override
	public boolean supportsMultipleRowInserts()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsRenameTable()
	{
		return true;
	}

	
	@Override
	public boolean supportsRenameView()
	{
		return true;
	}

	
	public boolean supportsSequence()
	{
		return true;
	}

	
	@Override
	public boolean supportsSequenceInformation()
	{
		return true;
	}

	
	public boolean supportsTablespace()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsAddColumn()
	{
		return true;
	}

	
	@Override
	public boolean supportsViewDefinition()
	{
		return true;
	}

	
	public String getViewDefinitionSQL(final String viewName, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		return null;
	}

	
	public String getQualifiedIdentifier(final String identifier, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		return identifier;
	}

}
