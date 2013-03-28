
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.hibernate.HibernateException;


public class DerbyDialectExt extends DB2DialectExt implements HibernateDialect
{

	
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DerbyDialectExt.class);

	private static interface i18n
	{
		
		
		String TYPE_MESSAGE = s_stringMgr.getString("DerbyDialect.typeMessage");

		
		
		String VARCHAR_MESSAGE = s_stringMgr.getString("DerbyDialect.varcharMessage");

		
		
		String COLUMN_LENGTH_MESSAGE = s_stringMgr.getString("DerbyDialect.columnLengthMessage");
	}

	private class DerbyDialectHelper extends org.hibernate.dialect.DB2Dialect {
		public DerbyDialectHelper() {
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
			registerColumnType(Types.DECIMAL, "decimal($p)");
			
			
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
			registerColumnType(Types.VARBINARY, 254, "long varchar for bit data");
			registerColumnType(Types.VARBINARY, "blob");
			registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
			registerColumnType(Types.VARCHAR, 32700, "long varchar");
			
			registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.VARCHAR, "clob(1073741823)");			
		}
	}
	
	
	private DerbyDialectHelper _dialect = new DerbyDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	
	@Override
	public boolean canPasteTo(final IDatabaseObjectInfo info)
	{
		
		return true;
	}

	
	@Override
	public int getMaxPrecision(final int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 48;
		} else
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
	public int getColumnLength(final int columnSize, final int dataType)
	{
		return columnSize;
	}

	
	@Override
	public String getDisplayName()
	{
		return "Derby";
	}

	
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().startsWith("Apache Derby"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public String[] getAddColumnSQL(final TableColumnInfo column, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
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

	
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	@Override
	public String getColumnDropSQL(final String tableName, final String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(final String pkName, final TableColumnInfo[] colInfos,
		final ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;

		
		
		DialectUtils.getMultiColNotNullSQL(colInfos, this, alterClause, false, result, qualifier, prefs);

		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false, qualifier, prefs, this));

		return result.toArray(new String[result.size()]);
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@Override
	public String getColumnCommentAlterSQL(final TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final boolean specifyColumnType = false;
		return new String[] {
			DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, specifyColumnType, qualifier, prefs)
		};
	}

	
	@Override
	public boolean supportsRenameColumn()
	{
		return false;
	}

	
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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
		if (from.getDataType() != to.getDataType())
		{
			throw new UnsupportedOperationException(i18n.TYPE_MESSAGE);
		}
		if (from.getDataType() != Types.VARCHAR)
		{
			throw new UnsupportedOperationException(i18n.VARCHAR_MESSAGE);
		}
		if (from.getColumnSize() > to.getColumnSize())
		{
			throw new UnsupportedOperationException(i18n.COLUMN_LENGTH_MESSAGE);
		}
		return super.getColumnTypeAlterSQL(from, to, qualifier, prefs);
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
	}

	
	@Override
	public String getDropForeignKeySQL(final String fkName, final String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.DERBY;
	}

	
	@Override
	public boolean supportsCreateSequence()
	{
		return false;
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
	public boolean supportsDropSequence()
	{
		return false;
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
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public boolean supportsAlterSequence()
	{
		return false;
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
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> columnNotNullAlters = new ArrayList<String>();
		
		final boolean specifyColumnType = false;
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		for (TableColumnInfo column : columns)
		{
			if (column.isNullable().equalsIgnoreCase("YES"))
			{
				columnNotNullAlters.add(DialectUtils.getColumnNullableAlterSQL(column,
					false,
					this,
					alterClause,
					specifyColumnType, qualifier, prefs));
			}
		}
		result.addAll(columnNotNullAlters);

		result.add(DialectUtils.getAddUniqueConstraintSQL(tableName,
			constraintName,
			columns,
			qualifier,
			prefs,
			this));

		return result.toArray(new String[result.size()]);
	}

	
	public boolean supportsViewDefinition() {
		return true;
	}
	
	
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		StringBuilder result = new StringBuilder();
		result.append("select v.VIEWDEFINITION ");
		result.append("from sys.SYSVIEWS v, sys.SYSTABLES t, sys.SYSSCHEMAS s ");
		result.append("where v.TABLEID = t.TABLEID ");
		result.append("and s.SCHEMAID = t.SCHEMAID ");
		result.append("and UPPER(t.TABLENAME) = '");
		result.append(viewName.toUpperCase());
		result.append("' and UPPER(s.SCHEMANAME) = '");
		result.append(qualifier.getSchema().toUpperCase());
		result.append("'");
		return result.toString();
	}

	
	@Override
	public boolean supportsAutoIncrement()
	{
		return false;
	}

}
