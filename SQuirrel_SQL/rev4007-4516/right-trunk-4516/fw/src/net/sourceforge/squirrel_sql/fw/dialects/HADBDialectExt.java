
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


public class HADBDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class HADBDialectHelper extends Dialect
	{
		public HADBDialectHelper()
		{
			registerColumnType(Types.BIGINT, "double integer");
			registerColumnType(Types.BINARY, 8000, "binary($l)");
			registerColumnType(Types.BINARY, "binary(8000)");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.CHAR, 8000, "char($l)");
			registerColumnType(Types.CHAR, "char(8000)");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "blob");
			registerColumnType(Types.LONGVARCHAR, "clob");
			registerColumnType(Types.NUMERIC, "decimal($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 8000, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "varbinary(8000)");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(8000)");
		}
	}

	
	private HADBDialectHelper _dialect = new HADBDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		return true;
	}

	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	public String[] getColumnAddSQL(TableColumnInfo info) throws HibernateException,
		UnsupportedOperationException
	{
		
		return null;
	}

	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public int getColumnLength(int columnSize, int dataType)
	{
		
		if (dataType == Types.CLOB || dataType == Types.BLOB)
		{
			return Integer.MAX_VALUE; 
		}
		return columnSize;
	}

	
	public boolean supportsRenameColumn()
	{
		
		return true;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" RENAME COLUMN ");
		result.append(from.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		return result.toString();
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDisplayName()
	{
		return "Sun HADB";
	}

	
	public String getLengthFunction(int dataType)
	{
		return "char_length";
	}

	
	public String getMaxFunction()
	{
		return "max";
	}

	
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.FLOAT)
		{
			return 52;
		}
		if (dataType == Types.DECIMAL || dataType == Types.NUMERIC)
		{
			return 31;
		}
		return 0;
	}

	
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
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

	
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	public boolean supportsDropColumn()
	{
		return false;
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		String prodName = "sun java system high availability";
		if (databaseProductName.trim().toLowerCase().startsWith(prodName))
		{
			
			return true;
		}
		return false;
	}

	public boolean supportsSchemasInTableDefinition()
	{
		
		return false;
	}

	
	public boolean supportsAlterColumnNull()
	{
		
		return false;
	}

	
	public boolean supportsAlterColumnDefault()
	{
		
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
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
		return DialectType.HADB;
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
		
		return null;
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
