
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;


public class TimesTenDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class TimesTenDialectHelper extends org.hibernate.dialect.TimesTenDialect
	{
		public TimesTenDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 8300, "binary($l)");
			registerColumnType(Types.BINARY, 4194304, "varbinary($l)");
			registerColumnType(Types.BINARY, "varbinary(4194304)");
			registerColumnType(Types.BIT, "tinyint");
			registerColumnType(Types.BLOB, 4194304, "varbinary($l)");
			registerColumnType(Types.BLOB, "varbinary(4194304)");
			registerColumnType(Types.BOOLEAN, "tinyint");
			registerColumnType(Types.CHAR, 8300, "char($l)");
			registerColumnType(Types.CHAR, 4194304, "varchar($l)");
			registerColumnType(Types.CHAR, "varchar(4194304)");
			registerColumnType(Types.CLOB, 4194304, "varchar($l)");
			registerColumnType(Types.CLOB, "varchar(4194304)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double");
			registerColumnType(Types.FLOAT, "float");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, 4194304, "varbinary($l)");
			registerColumnType(Types.LONGVARBINARY, "varbinary(4194304)");
			registerColumnType(Types.LONGVARCHAR, 4194304, "varchar($l)");
			registerColumnType(Types.LONGVARCHAR, "varchar(4194304)");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "float");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 4194304, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "varbinary(4194304)");
			registerColumnType(Types.VARCHAR, 4194304, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(4194304)");
		}
	}

	
	private TimesTenDialectHelper _dialect = new TimesTenDialectHelper();

	
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
		return true;
	}

	
	public int getMaxPrecision(int dataType)
	{
		int result = Integer.MAX_VALUE;
		if (dataType == Types.DECIMAL || dataType == Types.NUMERIC)
		{
			result = 40;
		}
		if (dataType == Types.FLOAT)
		{
			result = 53;
		}
		return result;
	}

	
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
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
		return "TimesTen";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("timesten"))
		{
			
			return true;
		}
		return false;
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

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columnNames, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.ADD_PRIMARY_KEY_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		return false;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		return false;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
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
		return DialectType.TIMESTEN;
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
		
		return null;
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

		return result.toArray(new String[result.size()]);
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
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

	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		
		return null;
	}

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
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
		
		return null;
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public boolean supportsAccessMethods()
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
		
		return false;
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
		
		return false;
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
		
		return false;
	}

	
	public boolean supportsInsertInto()
	{
		
		return false;
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
		
		return false;
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
