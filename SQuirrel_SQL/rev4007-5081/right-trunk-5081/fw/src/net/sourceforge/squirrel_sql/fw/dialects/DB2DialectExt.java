
package net.sourceforge.squirrel_sql.fw.dialects;

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NO_CYCLE_CLAUSE;

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
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class DB2DialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class DB2DialectHelper extends org.hibernate.dialect.DB2Dialect
	{
		public DB2DialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 254, "char($l) for bit data");
			registerColumnType(Types.BINARY, "blob");
			registerColumnType(Types.BIT, "smallint");
			
			registerColumnType(Types.BLOB, 1073741823, "blob($l)");
			registerColumnType(Types.BLOB, "blob(1073741823)");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 254, "char($l)");
			registerColumnType(Types.CHAR, 4000, "varchar($l)");
			registerColumnType(Types.CHAR, 32700, "long varchar");
			registerColumnType(Types.CHAR, 1073741823, "clob($l)");
			registerColumnType(Types.CHAR, "clob(1073741823)");
			
			registerColumnType(Types.CLOB, 1073741823, "clob($l)");
			registerColumnType(Types.CLOB, "clob(1073741823)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
			registerColumnType(Types.LONGVARBINARY, 1073741823, "blob($l)");
			registerColumnType(Types.LONGVARBINARY, "blob(1073741823)");
			registerColumnType(Types.LONGVARCHAR, 32700, "long varchar");
			
			registerColumnType(Types.LONGVARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.LONGVARCHAR, "clob(1073741823)");
			registerColumnType(Types.NUMERIC, "bigint");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 254, "varchar($l) for bit data");
			registerColumnType(Types.VARBINARY, "blob");
			
			registerColumnType(Types.VARCHAR, 3924, "varchar($l)");
			registerColumnType(Types.VARCHAR, 32700, "long varchar");
			
			registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.VARCHAR, "clob(1073741823)");

			

			
			registerColumnType(-8, "int");
			
			registerColumnType(-9, 1073741823, "clob($l)");
			registerColumnType(-9, "clob(1073741823)");

			
			registerColumnType(-15, "char($l)");
			
			registerColumnType(-16, "longvarchar");
			
			registerColumnType(2009, "clob");
			
			registerColumnType(2011, "clob");

		}
	}

	
	private final DB2DialectHelper _dialect = new DB2DialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		final DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
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
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 53;
		}
		else
		{
			return 31;
		}
	}

	
	public int getMaxScale(int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			
			
			return 0;
		}
		else
		{
			return getMaxPrecision(dataType);
		}
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
		return "DB2";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().startsWith("DB2"))
		{
			
			return true;
		}
		return false;
	}

	
	public String[] getAddColumnSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final String qualifedTableName =
			DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this);
		final String shapedColumnName = DialectUtils.shapeIdentifier(info.getColumnName(), prefs, this);

		final ArrayList<String> result = new ArrayList<String>();

		final StringBuffer addColumn = new StringBuffer();
		addColumn.append("ALTER TABLE ");
		addColumn.append(qualifedTableName);
		addColumn.append(" ADD ");
		addColumn.append(shapedColumnName);
		addColumn.append(" ");
		addColumn.append(getTypeName(info.getDataType(), info.getColumnSize(), info.getColumnSize(),
			info.getDecimalDigits()));
		if (info.getDefaultValue() != null)
		{
			addColumn.append(" WITH DEFAULT ");
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				addColumn.append(info.getDefaultValue());
			}
			else
			{
				addColumn.append("'");
				addColumn.append(info.getDefaultValue());
				addColumn.append("'");
			}
		}
		result.add(addColumn.toString());

		if (info.isNullable() == "NO")
		{
			
			
			final StringBuffer notnull = new StringBuffer();
			notnull.append("ALTER TABLE ");
			notnull.append(qualifedTableName);
			notnull.append(" ADD CONSTRAINT ");
			
			
			notnull.append(shapedColumnName);
			notnull.append(" CHECK (");
			notnull.append(shapedColumnName);
			notnull.append(" IS NOT NULL)");
			result.add(notnull.toString());
		}

		if (info.getRemarks() != null && !"".equals(info.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(info, qualifier, prefs));
		}

		return result.toArray(new String[result.size()]);

	}

	
	public String getColumnCommentAlterSQL(String tableName, String columnName, String comment,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
		throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(tableName, columnName, comment, qualifier, prefs, dialect);
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
		return true;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();

		final boolean nullable = info.isNullable().equalsIgnoreCase("yes");
		result.addAll(Arrays.asList(getColumnNullableAlterSQL(info, nullable, qualifier, prefs)));

		
		final StringBuilder reorgSql = new StringBuilder();
		reorgSql.append("CALL SYSPROC.ADMIN_CMD('REORG TABLE ");
		reorgSql.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		reorgSql.append("')");

		result.add(reorgSql.toString());
		return result.toArray(new String[result.size()]);
	}

	
	private String[] getColumnNullableAlterSQL(TableColumnInfo info, boolean nullable,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> sql = new ArrayList<String>();

		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		result.append(" ");
		result.append(DialectUtils.ALTER_COLUMN_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeIdentifier(info.getColumnName(), prefs, this));
		result.append(" SET ");
		if (nullable)
		{
			result.append("NULL");
		}
		else
		{
			result.append("NOT NULL");
		}
		sql.add(result.toString());
		sql.add(getTableReorgSql(info.getTableName(), qualifier, prefs));
		return sql.toArray(new String[sql.size()]);
	}

	
	public boolean supportsRenameColumn()
	{
		return false;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{

		
		

		final String templateString = ST_ALTER_COLUMN_SET_DATA_TYPE_STYLE_ONE;
		final StringTemplate st = new StringTemplate(templateString);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, from.getTableName());
		valuesMap.put(ST_COLUMN_NAME_KEY, from.getColumnName());
		valuesMap.put(ST_DATA_TYPE_KEY, DialectUtils.getTypeName(to, this));

		final ArrayList<String> result = new ArrayList<String>();
		result.add(DialectUtils.bindAttributes(this, st, valuesMap, qualifier, prefs));
		return result;
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
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
		return DialectType.DB2;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] {};
	}

	
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, String sequenceName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();
		
		final String tableName = column.getTableName();
		final String columnName = column.getColumnName();

		result.add(getCreateSequenceSQL(sequenceName.toString(), "1", "1", null, "1", null, false, qualifier,
			prefs));

		final StringBuilder triggerSql = new StringBuilder();
		triggerSql.append("CREATE TRIGGER ");
		triggerSql.append(columnName);
		triggerSql.append("_trigger \n");
		triggerSql.append("NO CASCADE BEFORE INSERT ON ");
		triggerSql.append(tableName);
		triggerSql.append(" REFERENCING NEW AS n \n");
		triggerSql.append("FOR EACH ROW \n");
		triggerSql.append("SET n.");
		triggerSql.append(columnName);
		triggerSql.append(" = NEXTVAL FOR ");
		triggerSql.append(sequenceName);

		result.add(triggerSql.toString());

		return result.toArray(new String[result.size()]);

	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String tableName = column.getTableName();
		final String columnName = column.getColumnName();
		final StringBuilder sequenceName = new StringBuilder();
		sequenceName.append(tableName.toUpperCase()).append("_");
		sequenceName.append(columnName.toUpperCase()).append("_SEQ");

		return getAddAutoIncrementSQL(column, sequenceName.toString(), qualifier, prefs);
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final Boolean deferrableNotSupported = null;
		final Boolean initiallyDeferredNotSupported = null;
		final Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	private String getTableReorgSql(String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		final StringBuilder reorgSql = new StringBuilder();
		reorgSql.append("CALL SYSPROC.ADMIN_CMD('REORG TABLE ");
		reorgSql.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		reorgSql.append("')");
		return reorgSql.toString();
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();

		
		for (final TableColumnInfo column : columns)
		{
			if (column.isNullable().equalsIgnoreCase("YES"))
			{
				result.addAll(Arrays.asList(getColumnNullableAlterSQL(column, false, qualifier, prefs)));
			}
		}

		result.add(DialectUtils.getAddUniqueConstraintSQL(tableName, constraintName, columns, qualifier, prefs,
			this));

		return result.toArray(new String[result.size()]);
	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = NO_CYCLE_CLAUSE;
		if (cycle == true)
		{
			cycleClause = CYCLE_CLAUSE;
		}
		return new String[] {

		DialectUtils.getAlterSequenceSQL(sequenceName, increment, minimum, maximum, restart, cache,
			cycleClause, qualifier, prefs, this) };
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateIndexSQL(indexName, tableName, accessMethod, columns, unique, tablespace,
			constraints, qualifier, prefs, this);
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateSequenceSQL(sequenceName, increment, minimum, maximum, start, cache, null,
			qualifier, prefs, this);
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
		final Boolean cascadeNotSupported = null;
		return DialectUtils.getDropIndexSQL(indexName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropSequenceSQL(sequenceName, false, qualifier, prefs, this);
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final Boolean cascadeNotSupported = null;

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
		
		final StringBuilder sql = new StringBuilder();

		sql.append("RENAME TABLE ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		sql.append(" ");
		sql.append(" TO ");
		sql.append(DialectUtils.shapeIdentifier(newTableName, prefs, this));

		return sql.toString();
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsViewDefinition()
	{
		return true;
	}

	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		

		final StringBuilder createViewSql = new StringBuilder();
		createViewSql.append("SELECT TEXT ");
		createViewSql.append(" FROM SYSCAT.VIEWS ");
		createViewSql.append("WHERE VIEWSCHEMA = '");
		createViewSql.append(qualifier.getSchema());
		createViewSql.append("' AND UPPER(VIEWNAME) = '");
		createViewSql.append(viewName.toUpperCase());
		createViewSql.append("'");
		return createViewSql.toString();
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		

		final StringBuilder result = new StringBuilder();
		result.append("SELECT NEXTCACHEFIRSTVALUE, MAXVALUE, MINVALUE, CACHE, INCREMENT, CYCLE ");
		result.append("FROM SYSCAT.SEQUENCES ");
		result.append("WHERE ");
		if (qualifier.getSchema() != null)
		{
			result.append("SEQSCHEMA = upper('" + qualifier.getSchema() + "') AND ");
		}
		
		result.append("SEQNAME = '");
		result.append(sequenceName);
		result.append("'");
		return result.toString();
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

	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String schema = qualifier.getSchema();
		final String catalog = qualifier.getCatalog();
		final StringBuilder result = new StringBuilder();
		if (!StringUtilities.isEmpty(catalog))
		{
			result.append(DialectUtils.shapeIdentifier(catalog, prefs, this));
			result.append(".");
		}
		if (!StringUtilities.isEmpty(schema))
		{
			result.append(DialectUtils.shapeIdentifier(schema, prefs, this));
			result.append(".");
		}
		result.append(DialectUtils.shapeIdentifier(identifier, prefs, this));
		return result.toString();
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

}
