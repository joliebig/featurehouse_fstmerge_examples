
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


public class MySQLDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	
	class MySQLDialectHelper extends org.hibernate.dialect.MySQLDialect
	{
		public MySQLDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 255, "binary($l)");
			registerColumnType(Types.BINARY, 65532, "blob");
			registerColumnType(Types.BINARY, "longblob");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, 65532, "blob");
			registerColumnType(Types.BLOB, "longblob");
			registerColumnType(Types.BOOLEAN, "bool");
			registerColumnType(Types.CHAR, 255, "char($l)");
			registerColumnType(Types.CHAR, 65532, "text");
			registerColumnType(Types.CHAR, "longtext");
			registerColumnType(Types.CLOB, "longtext");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "longblob");
			registerColumnType(Types.LONGVARCHAR, "longtext");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 255, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "blob");
			registerColumnType(Types.VARCHAR, "text");
		}
	}

	
	private final MySQLDialectHelper _dialect = new MySQLDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.FLOAT)
		{
			return 53;
		}
		else
		{
			return 38;
		}
	}

	
	@Override
	public String getDisplayName()
	{
		return "MySQL";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().toLowerCase().startsWith("mysql")
			&& !databaseProductVersion.startsWith("5")) { return true; }
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getModifyColumnNullabilitySQL(String tableName, TableColumnInfo info, boolean nullable)
	{
		final StringBuilder result = new StringBuilder();
		result.append(" ALTER TABLE ");
		result.append(tableName);
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		if (nullable)
		{
			result.append(" NULL ");
		}
		else
		{
			result.append(" NOT NULL ");
		}
		return result.toString();
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		DialectUtils.appendDefaultClause(info, result);
		return result.toString();
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		result.append(" COMMENT '");
		result.append(info.getRemarks());
		result.append("'");
		return result.toString();
	}

	
	@Override
	public boolean supportsDropColumn()
	{
		return true;
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
		final String alterClause = DialectUtils.MODIFY_COLUMN_CLAUSE;
		
		
		prefs.setQuoteColumnNames(false);
		
		String columnNullableAlterSql =
			DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs);
		
		return new String[] { columnNullableAlterSql };
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
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" CHANGE ");
		result.append(from.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(from, this));
		return result.toString();
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
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" CHANGE ");
		
		result.append(to.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(to, this));
		final ArrayList<String> list = new ArrayList<String>();
		list.add(result.toString());
		return list;
	}

	
	@Override
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
	}

	
	@Override
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final StringBuilder tmp = new StringBuilder();
		tmp.append("ALTER TABLE ");
		tmp.append(tableName);
		tmp.append(" DROP FOREIGN KEY ");
		tmp.append(fkName);
		return tmp.toString();
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
		return DialectType.MYSQL;
	}

	
	@Override
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "UNIQUE", "FULLTEXT", "SPATIAL" };
	}

	
	@Override
	public String[] getIndexStorageOptions()
	{
		return new String[] { "BTREE", "HASH" };
	}

	
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		final String templateStr = ST_ADD_AUTO_INCREMENT_STYLE_ONE;
		final StringTemplate st = new StringTemplate(templateStr);

		final HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_TABLE_NAME_KEY, column.getTableName());
		valuesMap.put(ST_COLUMN_NAME_KEY, column.getColumnName());

		String addAutoIncrementSql = DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
		addAutoIncrementSql = 
			DialectUtils.stripQuotesFromIdentifier(this, column.getColumnName(), addAutoIncrementSql);
		return new String[] { addAutoIncrementSql };
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = true;
		final boolean addNullClause = true;

		
		prefs.setQuoteColumnNames(false);
		
		String addColumnSql =
			DialectUtils.getAddColumSQL(column, this, addDefaultClause, supportsNullQualifier, addNullClause,
				qualifier, prefs);

		return new String[] { addColumnSql };
	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		

		final String fkTemplateStr = ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE;

		final StringTemplate fkst = new StringTemplate(fkTemplateStr);
		final HashMap<String, String> fkValuesMap = new HashMap<String, String>();
		fkValuesMap.put(ST_CHILD_TABLE_KEY, localTableName);
		if (constraintName != null)
		{
			fkValuesMap.put(ST_CONSTRAINT_KEY, "CONSTRAINT");
			fkValuesMap.put(ST_CONSTRAINT_NAME_KEY, constraintName);
		}
		fkValuesMap.put(ST_PARENT_TABLE_KEY, refTableName);

		StringTemplate ckIndexSt = null;
		HashMap<String, String> ckIndexValuesMap = null;

		if (autoFKIndex)
		{
			
			

			ckIndexSt = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
			ckIndexValuesMap = new HashMap<String, String>();
			ckIndexValuesMap.put(ST_INDEX_NAME_KEY, "fk_child_idx");
		}

		return DialectUtils.getAddForeignKeyConstraintSQL(fkst, fkValuesMap, ckIndexSt, ckIndexValuesMap,
			localRefColumns, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		

		final String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE;

		final StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_TABLE_NAME_KEY, tableName);
		if (constraintName != null)
		{
			st.setAttribute(ST_CONSTRAINT_KEY, "CONSTRAINT");
			st.setAttribute(ST_CONSTRAINT_NAME_KEY, constraintName);
		}

		
		
		
		
		

		
		
		
		

		for (final TableColumnInfo columnInfo : columns)
		{
			st.setAttribute(ST_COLUMN_NAME_KEY, columnInfo.getColumnName());
		}

		return new String[] { st.toString() };
	}

	
	@Override
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ALTER_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		

		final String templateStr = ST_CREATE_INDEX_STYLE_ONE;

		final StringTemplate st = new StringTemplate(templateStr);

		final HashMap<String, String> valuesMap = new HashMap<String, String>();

		if (accessMethod != null && !accessMethod.toLowerCase().equals("default"))
		{
			valuesMap.put(ST_ACCESS_METHOD_KEY, accessMethod);
		}
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		
		
		
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		String addIndexSql = DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
		for (String column : columns) {
			addIndexSql = DialectUtils.stripQuotesFromIdentifier(this, column, addIndexSql);
		}
		
		return addIndexSql;
	}

	
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.CREATE_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		
		prefs.setQuoteColumnNames(false);
		
		return  DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.CREATE_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_CONSTRAINT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final Boolean cascadeNotSupported = null;
		return DialectUtils.getDropIndexSQL(tableName, indexName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String renameTableSql =
			DialectUtils.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs, this);

		return DialectUtils.stripQuotesFromIdentifier(this, newTableName, renameTableSql);
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		String templateStr = "";

		if (fromTables != null)
		{
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_TWO;
		}
		else
		{
			templateStr = ST_UPDATE_STYLE_ONE;
		}

		final StringTemplate st = new StringTemplate(templateStr);

		return DialectUtils.getUpdateSQL(st, tableName, setColumns, setValues, fromTables, whereColumns,
			whereValues, qualifier, prefs, this);
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
		return false;
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
		return false;
	}

	
	public boolean supportsCreateTable()
	{
		return true;
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
		return true;
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
		return false;
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
		return true;
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
		throw new UnsupportedOperationException("getViewDefinitionSQL: MySQL 4 and below doesn't support views");
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
	public boolean supportsSubSecondTimestamps()
	{
		return false;
	}

	
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		
		prefs.setQuoteColumnNames(false);
		
		prefs.setQuoteConstraintNames(false);
		
		return super.getColumnDropSQL(tableName, columnName, qualifier, prefs);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{		
		
		prefs.setQuoteColumnNames(false);
		
		prefs.setQuoteConstraintNames(false);
		
		return super.getAddPrimaryKeySQL(pkName, colInfos, ti, qualifier, prefs);
	}

}
