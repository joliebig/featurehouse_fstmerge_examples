
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;



public class AxionDialectExt extends CommonHibernateDialect implements HibernateDialect
{
	private class AxionDialectHelper extends Dialect
	{
		public AxionDialectHelper()
		{
			
			
			
			
			
			
			
			
			
			
			
			
			registerColumnType(Types.BIGINT, "numeric($p,0)");
			registerColumnType(Types.BINARY, "binary($l)");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.BOOLEAN, "bit");
			registerColumnType(Types.CHAR, "char($l)");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "numeric($p,$s)");
			registerColumnType(Types.DOUBLE, "numeric($p,$s)");
			registerColumnType(Types.FLOAT, "numeric($p,$s)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "longvarbinary");
			registerColumnType(Types.LONGVARCHAR, "longvarchar");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			
			
			
			
			
			
			
			registerColumnType(Types.REAL, "numeric($p,$s)");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "varbinary($l)");
			registerColumnType(Types.VARCHAR, "varchar($l)");
		}
	}

	
	private AxionDialectHelper _dialect = new AxionDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		return true;
	}

	
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	
	public int getMaxPrecision(int dataType)
	{
		return 38;
	}

	
	public String getDisplayName()
	{
		return "Axion";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().startsWith("Axion"))
		{
			
			return true;
		}
		return false;
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
		String cascadeClause = DialectUtils.CASCADE_CLAUSE;
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, cascadeClause, false,
			qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs,
			this) };
	}

	
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		return false;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_TYPE_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		return DialectType.AXION;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public boolean supportsCreateTable()
	{

		return false;
	}

	
	public boolean supportsDropView()
	{

		return false;
	}

	
	public boolean supportsInsertInto()
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

	
	public boolean supportsUpdate()
	{

		return false;
	}

	
	public boolean supportsViewDefinition()
	{
		return false;
	}

	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	
	public boolean supportsCorrelatedSubQuery()
	{

		return false;
	}

}
