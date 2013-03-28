
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


public class InterbaseDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class InterbaseDialectHelper extends org.hibernate.dialect.InterbaseDialect {
		public InterbaseDialectHelper() {
			super();
			
			
			registerColumnType( Types.BIT, "smallint" );
			registerColumnType( Types.BIGINT, "numeric(18,0)" );
			registerColumnType( Types.SMALLINT, "smallint" );
			registerColumnType( Types.TINYINT, "smallint" );
			registerColumnType( Types.INTEGER, "integer" );
			registerColumnType( Types.CHAR, "char(1)" );
			registerColumnType( Types.VARCHAR, "varchar($l)" );
			registerColumnType( Types.FLOAT, "float" );
			registerColumnType( Types.DOUBLE, "double precision" );
			registerColumnType( Types.DATE, "date" );
			registerColumnType( Types.TIME, "time" );
			registerColumnType( Types.TIMESTAMP, "timestamp" );
			registerColumnType( Types.VARBINARY, "blob" );
			registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
			registerColumnType( Types.BLOB, "blob" );
			registerColumnType( Types.CLOB, "blob sub_type 1" );			
		}
	}
	
	
	private InterbaseDialectHelper _dialect = new InterbaseDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}	

	
	@Override
	public boolean canPasteTo(IDatabaseObjectInfo info) {
		return true;
	}

	
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	
	@Override
	public String getLengthFunction(int dataType)
	{
		return "length";
	}

	
	@Override
	public String getMaxFunction()
	{
		return "max";
	}

	
	@Override
	public int getMaxPrecision(int dataType)
	{
		return 0;
	}

	
	@Override
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	@Override
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	@Override
	public int getColumnLength(int columnSize, int dataType)
	{
		return columnSize;
	}

	
	@Override
	public String getDisplayName()
	{
		return "Interbase";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("interbase"))
		{
			
			return true;
		}
		return false;
	}

	
	public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("This database dialect doesn't support adding columns to tables");
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@SuppressWarnings("unused")
	public String getColumnCommentAlterSQL(String tableName, String columnName, String comment)
		throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("This database dialect doesn't support adding comments to columns");
	}

	
	@Override
	public boolean supportsDropColumn()
	{
		
		return true;
	}

	
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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
			false, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columnNames, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("getAddPrimaryKeySQL not implemented");
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		
		return true;
	}

	
	@Override
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public boolean supportsAlterColumnType()
	{
		
		return true;
	}

	
	@Override
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		
		throw new UnsupportedOperationException("Not Yet Implemented");
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
	}

	
	@Override
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
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
		return DialectType.INTERBASE;
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
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		
		return null;
	}

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	@Override
	public boolean supportsAccessMethods()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsAddForeignKeyConstraint()
	{
		
		return false;
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
		
		return false;
	}

	
	@Override
	public boolean supportsCheckOptionsForViews()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsCreateIndex()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsCreateSequence()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsCreateTable()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsCreateView()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsDropConstraint()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsDropIndex()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsDropSequence()
	{
		
		return false;
	}

	
	@Override
	public boolean supportsDropView()
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
		
		return false;
	}

	
	@Override
	public boolean supportsInsertInto()
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
		
		return false;
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
		
		return false;
	}

	
	@Override
	public boolean supportsUpdate()
	{
		
		return false;
	}

	
	public boolean supportsAddColumn()
	{
		
		return true;
	}

	
	public boolean supportsViewDefinition() {
		
		return false;
	}	
	
	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		return null;
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
