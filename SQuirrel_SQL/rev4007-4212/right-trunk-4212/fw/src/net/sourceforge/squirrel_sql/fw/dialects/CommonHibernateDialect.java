
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class CommonHibernateDialect implements HibernateDialect, StringTemplateConstants
{
	
	protected String DROP_COLUMN_SQL_TEMPLATE = ST_DROP_COLUMN_STYLE_ONE;

	
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		return true;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
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

		return new String[] { sql };
	}

	
	public String getAddColumnString()
	{
		return "ADD";
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false) };
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ALTER_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		
		return null;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		
		return null;
	}

	
	public String getColumnDropSQL(String tableName, String columnName) throws UnsupportedOperationException
	{
		StringTemplate st = new StringTemplate(DROP_COLUMN_SQL_TEMPLATE);

		
		
		
		
		
		
		st.setAttribute(ST_TABLE_NAME_KEY, tableName);
		st.setAttribute(ST_COLUMN_NAME_KEY, columnName);
		
		return st.toString();
	}

	
	public int getColumnLength(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		
		return null;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		
		return null;
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
		
		return null;
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

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public DialectType getDialectType()
	{
		
		return DialectType.GENERIC;
	}

	
	public String getDisplayName()
	{
		return "Generic";
	}

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_CONSTRAINT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String getDropForeignKeySQL(String fkName, String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
	}

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{                        
		
      StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_THREE);

      HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_INDEX_NAME_KEY, indexName);

      return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName)
	{
		
		return null;
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return null;
	}

	
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
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
		return Integer.MAX_VALUE;
	}

	
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	public String getNullColumnString()
	{
		return "";
	}

	
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public List<String> getTableDropSQL(ITableInfo tableInfo, boolean cascadeConstraints,
		boolean isMaterializedView)
	{
		return DialectUtils.getTableDropSQL(tableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false);
	}

	
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		
		throw new UnsupportedOperationException("Common dialect doesn't register column types");
	}

	
	public String getTypeName(int code) throws HibernateException
	{
		
		throw new UnsupportedOperationException("Common dialect doesn't register column types");
	}

	
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

	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public boolean supportsAccessMethods()
	{
		
		return false;
	}

	
	public boolean supportsAddColumn()
	{
		
		return false;
	}

	
	public boolean supportsAddForeignKeyConstraint()
	{
		
		return false;
	}

	
	public boolean supportsAddUniqueConstraint()
	{
		
		return false;
	}

	
	public boolean supportsAlterColumnDefault()
	{
		
		return false;
	}

	
	public boolean supportsAlterColumnNull()
	{
		
		return false;
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public boolean supportsAlterSequence()
	{
		
		return false;
	}

	
	public boolean supportsAutoIncrement()
	{
		
		return false;
	}

	
	public boolean supportsCheckOptionsForViews()
	{
		
		return false;
	}

	
	public boolean supportsColumnComment()
	{
		
		return false;
	}

	
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

	
	public boolean supportsCreateIndex()
	{
		
		return false;
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

	
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	public boolean supportsDropConstraint()
	{
		
		return false;
	}

	
	public boolean supportsDropIndex()
	{
		
		return false;
	}

	
	public boolean supportsDropSequence()
	{
		
		return false;
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
		return false;
	}

	
	public boolean supportsInsertInto()
	{
		return true;
	}

	
	public boolean supportsMultipleRowInserts()
	{
		return false;
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		return false;
	}

	
	public boolean supportsRenameColumn()
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

	
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
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
		return false;
	}

	
	public boolean supportsUpdate()
	{
		return true;
	}

	
	public boolean supportsViewDefinition()
	{
		return false;
	}

	
	public char closeQuote()
	{
		return '"';
	}

	
	public char openQuote()
	{
		return '"';
	}

	
	public SequencePropertyMutability getSequencePropertyMutability() {
		return new SequencePropertyMutability();
	}

	
	public boolean supportsSubSecondTimestamps() {
		return true;
	}
}
