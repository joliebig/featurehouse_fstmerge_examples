
package net.sourceforge.squirrel_sql.fw.dialects;

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NO_CYCLE_CLAUSE;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;


public class PostgreSQLDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class PostgreSQLDialectHelper extends org.hibernate.dialect.PostgreSQLDialect {
		public PostgreSQLDialectHelper() {
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, "bytea");
			
			
			
			
			
			
			
			
			
			
			
			registerColumnType(Types.BIT, "bool");
			registerColumnType(Types.BLOB, "bytea");
			registerColumnType(Types.BOOLEAN, "bool");
			registerColumnType(Types.CHAR, 8000, "char($l)");
			registerColumnType(Types.CHAR, "text");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,2)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "bytea");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "int");
			registerColumnType(Types.VARBINARY, "bytea");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");			
		}
	}
	
	
	private PostgreSQLDialectHelper _dialect = new PostgreSQLDialectHelper();

	
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
		return true;
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
		} else
		{
			return 38;
		}
	}

	
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		if (columnSize == 2)
		{
			return 5;
		}
		if (columnSize == 4)
		{
			return 10;
		}
		return 19;
	}

	
	public int getColumnLength(int columnSize, int dataType)
	{
		if (dataType == Types.VARCHAR && columnSize == -1)
		{
			
			return 2000;
		}
		return columnSize;
	}

	
	public String getDisplayName()
	{
		return "PostgreSQL";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("postgresql"))
		{
			
			return true;
		}
		return false;
	}

	











	
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false, qualifier, prefs, this) };
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		prefs.setQuoteColumnNames(false);
		return DialectUtils.getColumnCommentAlterSQL(info.getTableName(),
			info.getColumnName(),
			info.getRemarks(), qualifier, prefs, this);

	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(info.getColumnName());
		if (info.isNullable().equalsIgnoreCase("YES"))
		{
			result.append(" DROP NOT NULL");
		} else
		{
			result.append(" SET NOT NULL");
		}
		return new String[] { result.toString() };
	}

	
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.RENAME_COLUMN_CLAUSE;
		String toClause = DialectUtils.TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, toClause, qualifier, prefs, this);
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(to.getColumnName());
		result.append(" TYPE ");
		result.append(DialectUtils.getTypeName(to, this));
		list.add(result.toString());
		return list;
	}

	
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		result.append(" ALTER COLUMN ");
		result.append(info.getColumnName());
		String defVal = info.getDefaultValue();
		if (defVal == null || "".equals(defVal))
		{
			result.append(" DROP DEFAULT");
		} else
		{
			result.append(" SET DEFAULT ");
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				result.append(defVal);
			} else
			{
				result.append("'");
				result.append(defVal);
				result.append("'");
			}
		}
		return result.toString();
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
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

	
	public DialectType getDialectType()
	{
		return DialectType.POSTGRES;
	}

	
	private static final String[] ACCESS_METHODS = { "btree", "hash", "gist", "gin" };

	
	private String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.shapeQualifiableIdentifier(identifier, qualifier, prefs, this);
	}

	
	private String shapeIdentifier(String identifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.shapeIdentifier(identifier, prefs, this);
	}

	
	public boolean supportsSequence()
	{
		return _dialect.supportsSequences();
	}

	
	public boolean supportsCheckOptionsForViews()
	{
		return false;
	}

	
	public boolean supportsIndexes()
	{
		return true;
	}

	
	public boolean supportsTablespace()
	{
		return true;
	}

	
	public boolean supportsAccessMethods()
	{
		return true;
	}

	
	public boolean supportsDropView()
	{
		return true;
	}

	
	public boolean supportsRenameView()
	{
		return true;
	}

	
	public boolean supportsAutoIncrement()
	{
		return true;
	}

	
	public boolean supportsEmptyTables()
	{
		return true;
	}

	
	public boolean supportsMultipleRowInserts()
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

	
	public boolean supportsInsertInto()
	{
		return true;
	}

	
	public boolean supportsRenameTable()
	{
		return true;
	}

	
	public boolean supportsSequenceInformation()
	{
		return true;
	}

	
	public boolean supportsUpdate()
	{
		return true;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return ACCESS_METHODS;
	}

	
	public String[] getIndexStorageOptions()
	{
		return null;
	}
	
	
	public String getCreateTableSQL(String simpleName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(simpleName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		sql.append(" RENAME TO ");
		
		
		
		
		
		
		
		

		sql.append(DialectUtils.shapeIdentifier(newTableName, prefs, this));
		
		return sql.toString();
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs, this);
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return new String[] { 
			getRenameTableSQL(oldViewName, newViewName, qualifier, prefs) 
		};
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		
		String result = "";
		if (unique && accessMethod.equalsIgnoreCase("hash")) {
			String uniqueHashAccessMethodNotSupported = null;
			
			result =
				DialectUtils.getAddIndexSQL(this,
					indexName,
					tableName,
					uniqueHashAccessMethodNotSupported,
					columns,
					unique,
					tablespace,
					constraints,
					qualifier,
					prefs);			
		} else {
			result =
				DialectUtils.getAddIndexSQL(this,
					indexName,
					tableName,
					accessMethod,
					columns,
					unique,
					tablespace,
					constraints,
					qualifier,
					prefs);
		}
		return result;
	}

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return DialectUtils.getDropIndexSQL(indexName, cascade, qualifier, prefs, this);
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleCause = cycle ? CYCLE_CLAUSE : NO_CYCLE_CLAUSE;
		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minimum,
			maximum,
			start,
			cache,
			cycleCause,
			qualifier,
			prefs,
			this);
	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = cycle ? CYCLE_CLAUSE : NO_CYCLE_CLAUSE;
		String sql =
			DialectUtils.getAlterSequenceSQL(sequenceName,
				increment,
				minimum,
				maximum,
				restart,
				cache,
				cycleClause,
				qualifier,
				prefs,
				this);
		return new String[] { sql };
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs));

		return sql.toString();
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropSequenceSQL(sequenceName, cascade, qualifier, prefs, this);
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName,
			refTableName,
			constraintName,
			deferrable,
			initiallyDeferred,
			matchFull,
			autoFKIndex,
			fkIndexName,
			localRefColumns,
			onUpdateAction,
			onDeleteAction,
			qualifier,
			prefs,
			this);
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddUniqueConstraintSQL(tableName,
			constraintName,
			columns,
			qualifier,
			prefs,
			this) };
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		ArrayList<String> result = new ArrayList<String>();

		
		
		
		

		
		String sequenceName = column.getTableName() + "_" + column.getColumnName() + "_seq";
		String sequenceSQL =
			getCreateSequenceSQL(sequenceName, null, null, null, null, null, false, qualifier, prefs);
		result.add(sequenceSQL);

		StringBuilder sql = new StringBuilder();
		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

		sql.append(" ");
		sql.append(DialectUtils.ALTER_COLUMN_CLAUSE);
		sql.append(" ");
		sql.append(shapeIdentifier(column.getColumnName(), prefs)).append("\n");
		sql.append(" ");
		sql.append(DialectUtils.SET_DEFAULT_CLAUSE + " nextval('");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("')");
		result.add(sql.toString());

		sql = new StringBuilder();
		sql.append(DialectUtils.ALTER_SEQUENCE_CLAUSE + " ");
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");
		sql.append(" OWNED BY ");
		sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs));
		sql.append(".");
		sql.append(shapeIdentifier(column.getColumnName(), prefs));
		result.add(sql.toString());

		return result.toArray(new String[result.size()]);
	}

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String query,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, query, qualifier, prefs, this);
	}

	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		if ((setColumns == null && setValues == null)
			|| (setColumns != null && setValues != null && setColumns.length == 0 && setValues.length == 0))
		{
			return new String[] {};
		}
		if ((setColumns != null && setValues != null && setColumns.length != setValues.length)
			|| setColumns == null || setValues == null)
		{
			throw new IllegalArgumentException("The amount of SET columns and values must be the same!");
		}
		if ((whereColumns != null && whereValues != null && whereColumns.length != whereValues.length)
			|| (whereColumns == null && whereValues != null) || (whereColumns != null && whereValues == null))
		{
			throw new IllegalArgumentException("The amount of WHERE columns and values must be the same!");
		}
		
		
		
		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.UPDATE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
		sql.append(" " + DialectUtils.SET_CLAUSE + " ");
		for (int i = 0; i < setColumns.length; i++)
		{
			sql.append(shapeIdentifier(setColumns[i], prefs));
			if (setValues[i] == null)
				sql.append(" = NULL");
			else
				sql.append(" = ").append(setValues[i]);
			sql.append(", ");
		}
		sql.setLength(sql.length() - 2);

		if (fromTables != null)
		{
			sql.append("\n " + DialectUtils.FROM_CLAUSE + " ");
			for (String from : fromTables)
			{
				sql.append(shapeQualifiableIdentifier(from, qualifier, prefs)).append(", ");
			}
			sql.setLength(sql.length() - 2);
		}

		if (whereColumns != null && whereColumns.length != 0)
		{
			sql.append("\n " + DialectUtils.WHERE_CLAUSE + " ");
			for (int i = 0; i < whereColumns.length; i++)
			{
				sql.append(shapeIdentifier(whereColumns[i], prefs));
				if (whereValues[i] == null)
					sql.append(" IS NULL");
				else
					sql.append(" = ").append(whereValues[i]);
				sql.append(" " + DialectUtils.AND_CLAUSE + " ");
			}
			sql.setLength(sql.length() - 5);
		}

		return new String[] { sql.toString() };
	}

	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();		
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;
		
		result.add(DialectUtils.getAddColumSQL(column,
			this,
			addDefaultClause,
			supportsNullQualifier,
			addNullClause,
			qualifier,
			prefs));
		

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, qualifier, prefs));
		}		
		return result.toArray(new String[result.size()]);
	}

	
	public boolean supportsAddColumn()
	{
		return true;
	}

	
	public boolean supportsViewDefinition() {
		return true;
	}	
	
	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		
      StringBuilder result = new StringBuilder();
      result.append("select view_definition from information_schema.views where table_schema = '");
      result.append(qualifier.getSchema());
      result.append("' and table_name =  '");
      result.append(viewName);
      result.append("'");
      return result.toString();
	}
	
	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		if (prefs.isQualifyTableNames()) {
			if (prefs.isQuoteIdentifiers()) {
				result.append(this.openQuote());
			}
			result.append(qualifier.getSchema());
			if (prefs.isQuoteIdentifiers()) {
				result.append(this.closeQuote());
			} 
			result.append(".");
		}
		if (prefs.isQuoteIdentifiers()) {
			result.append(this.openQuote());
		}
		result.append(identifier);
		if (prefs.isQuoteIdentifiers()) {
			result.append(this.closeQuote());
		}
		return result.toString();
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		return false;
	}

	
	@Override
	public int getJavaTypeForNativeType(String nativeColumnTypeName)
	{
		if ("character_data".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.CHAR;
		}
		if ("cardinal_number".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.INTEGER;
		}
		if ("xml".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.VARCHAR;
		}
		return super.getJavaTypeForNativeType(nativeColumnTypeName);
	}
	
}
