
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


public class PointbaseDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class PointbaseDialectHelper extends org.hibernate.dialect.PointbaseDialect
	{
		public PointbaseDialectHelper()
		{
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, "blob(8K)");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BLOB, 2000000000, "blob($l)");
			registerColumnType(Types.BLOB, "blob(2000000000)");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 4054, "char($l)");
			registerColumnType(Types.CHAR, 2000000000, "clob($l)");
			registerColumnType(Types.CHAR, "clob(2000000000)");
			registerColumnType(Types.CLOB, 2000000000, "clob($l)");
			registerColumnType(Types.CLOB, "clob(2000000000)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, 2000000000, "blob($l)");
			registerColumnType(Types.LONGVARBINARY, "blob(2000000000)");
			registerColumnType(Types.LONGVARCHAR, 2000000000, "clob($l)");
			registerColumnType(Types.LONGVARCHAR, "clob(2000000000)");
			registerColumnType(Types.NUMERIC, "bigint");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 2000000000, "blob($l)");
			registerColumnType(Types.VARBINARY, "blob(2000000000)");
			registerColumnType(Types.VARCHAR, 4054, "varchar($l)");
			registerColumnType(Types.VARCHAR, 2000000000, "clob($l)");
			registerColumnType(Types.VARCHAR, "clob(2000000000)");
		}
	}

	
	private final PointbaseDialectHelper _dialect = new PointbaseDialectHelper();

	
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
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 48;
		}
		else
		{
			return 31;
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
		return "Pointbase";
	}

	
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().toLowerCase().startsWith("pointbase"))
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
	public String getColumnDropSQL(final String tableName, final String columnName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false,
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
	public boolean supportsColumnComment()
	{
		return false;
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
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return false;
	}

	
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		
		
		

		

		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier,
			prefs);
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
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
		return DialectType.POINTBASE;
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
		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = false;
		final boolean addNullClause = true;

		final String sql =
			DialectUtils.getAddColumSQL(column, this, addDefaultClause, supportsNullQualifier, addNullClause,
				qualifier, prefs);

		return new String[] { sql };
	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(final String localTableName, final String refTableName,
		final String constraintName, final Boolean deferrable, final Boolean initiallyDeferred,
		final Boolean matchFull, final boolean autoFKIndex, final String fkIndexName,
		final Collection<String[]> localRefColumns, final String onUpdateAction, final String onDeleteAction,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final Boolean deferrableNotSupported = null;
		final Boolean initiallyDeferredNotSupported = null;
		final Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(final String tableName, final String constraintName,
		final TableColumnInfo[] columns, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		

		
		

		final String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO;

		final StringTemplate st = new StringTemplate(templateStr);

		final String quotedConstraint = DialectUtils.shapeIdentifier(constraintName, prefs, this);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, quotedConstraint);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st, valuesMap, columns, qualifier, prefs,
			this) };
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
		return DialectUtils.getCreateIndexSQL(indexName, tableName, accessMethod, columns, unique, tablespace,
			constraints, qualifier, prefs, this);
	}

	
	@Override
	public String getCreateSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String start, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.CREATE_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getCreateTableSQL(final String tableName, final List<TableColumnInfo> columns,
		final List<TableColumnInfo> primaryKeys, final SqlGenerationPreferences prefs,
		final DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	@Override
	public String getCreateViewSQL(final String viewName, final String definition, final String checkOption,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		

		
		
		final StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_TWO);

		final HashMap<String, String> valuesMap =
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
		final StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_TWO);
		st.setAttribute(ST_INDEX_NAME_KEY, indexName);
		st.setAttribute(ST_TABLE_NAME_KEY, tableName);
		return st.toString();
	}

	
	@Override
	public String getDropSequenceSQL(final String sequenceName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getInsertIntoSQL(final String tableName, final List<String> columns,
		final String valuesPart, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	@Override
	public String getRenameTableSQL(final String oldTableName, final String newTableName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		
		final StringTemplate st = new StringTemplate(ST_RENAME_OBJECT_STYLE_ONE);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldTableName, ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String[] getRenameViewSQL(final String oldViewName, final String newViewName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getSequenceInformationSQL(final String sequenceName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
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
		return false;
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
		return false;
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
	public boolean supportsInsertInto()
	{
		return true;
	}

	
	@Override
	public boolean supportsMultipleRowInserts()
	{
		return true;
	}

	
	@Override
	public boolean supportsRenameTable()
	{
		return true;
	}

	
	@Override
	public boolean supportsRenameView()
	{
		return false;
	}

	
	@Override
	public boolean supportsSequence()
	{
		return false;
	}

	
	@Override
	public boolean supportsSequenceInformation()
	{
		return false;
	}

	
	@Override
	public boolean supportsTablespace()
	{
		return true;
	}

	
	@Override
	public boolean supportsUpdate()
	{
		return true;
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

	
	@Override
	public String getViewDefinitionSQL(final String viewName, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		
		
		
		
		

		final StringBuilder result = new StringBuilder();
		result.append("select 'CREATE VIEW ' || VIEWNAME || ' AS ' || VIEWDEFINITION ");
		result.append("from POINTBASE.SYSVIEWS v, POINTBASE.SYSSCHEMATA s ");
		result.append("where v.SCHEMAID = s.SCHEMAID ");
		result.append("and v.VIEWNAME = '").append(viewName).append("' ");
		result.append("and s.SCHEMANAME = '").append(qualifier.getSchema()).append("' ");
		return result.toString();
	}

	
	@Override
	public String getQualifiedIdentifier(final String identifier, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	
	@Override
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

	
	@Override
	public int getTimestampMaximumFractionalDigits()
	{
		return 9;
	}

}
