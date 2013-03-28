
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class SQLServerDialectExt extends SybaseDialectExt implements HibernateDialect
{
	private class SQLServerDialectHelper extends org.hibernate.dialect.SybaseDialect
	{
		public SQLServerDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, "image");
			registerColumnType(Types.BIT, "tinyint");
			registerColumnType(Types.BLOB, "image");
			registerColumnType(Types.BOOLEAN, "tinyint");
			registerColumnType(Types.CHAR, 8000, "char($l)");
			registerColumnType(Types.CHAR, "text");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "datetime");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "image");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "datetime");
			registerColumnType(Types.TIMESTAMP, "datetime");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 8000, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "image");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");
		}
	}

	
	private SQLServerDialectHelper _dialect = new SQLServerDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database") || type.getName().equalsIgnoreCase("catalog"))
		{
			result = false;
		}
		return result;
	}

	
	@Override
	public String getLengthFunction(int dataType)
	{
		return "len";
	}

	
	@Override
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
		return "MS SQLServer";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("microsoft"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, "DROP COLUMN", false, null, qualifier, prefs, this);
	}

	
	@Override
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

	
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		
		
		DialectUtils.getMultiColNotNullSQL(colInfos, this, alterClause, true, result, qualifier, prefs);

		String pkSQL = DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false, qualifier, prefs, this);
		result.add(pkSQL);

		return result.toArray(new String[result.size()]);
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
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs) };
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("exec sp_rename ");
		result.append("'");
		result.append(from.getTableName());
		result.append(".");
		result.append(from.getColumnName());
		result.append("'");
		result.append(", ");
		result.append(to.getColumnName());
		result.append(", 'COLUMN'");
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
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(from.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(to, this));
		list.add(result.toString());
		return list;
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" ADD CONSTRAINT ");
		result.append(info.getTableName()).append("_").append(info.getColumnName()).append("_default");
		result.append(" DEFAULT ");
		if (JDBCTypeMapper.isNumberType(info.getDataType()))
		{
			result.append(info.getDefaultValue());
		} else
		{
			result.append("'");
			result.append(info.getDefaultValue());
			result.append("'");
		}
		result.append(" FOR ");
		result.append(info.getColumnName());
		return result.toString();
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.MSSQL;
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = false;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		result.add(sql);

		if (column.getDefaultValue() != null)
		{
			result.add(getColumnDefaultAlterSQL(column, qualifier, prefs));
		}

		return result.toArray(new String[result.size()]);
	}

	
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_ONE);
		HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);
		return DialectUtils.bindAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_TABLE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return "sp_helptext " + viewName;
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
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsSubSecondTimestamps() {
		return false;
	}
	
}
