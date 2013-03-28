
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class InformixDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class InformixDialectHelper extends org.hibernate.dialect.InformixDialect {
		public InformixDialectHelper() {
			super();
			registerColumnType(Types.BIGINT, "integer");
			registerColumnType(Types.BINARY, "byte");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BLOB, "byte");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 32511, "char($l)");
			registerColumnType(Types.CHAR, "char(32511)");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, 15, "float($l)");
			registerColumnType(Types.DOUBLE, "float(15)");
			registerColumnType(Types.FLOAT, 15, "float($l)");
			registerColumnType(Types.FLOAT, "float(15)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "byte");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "datetime hour to second");
			registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "byte");
			registerColumnType(Types.VARCHAR, 255, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");			
		}
	}
	
	
	private InformixDialectHelper _dialect = new InformixDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}	

	
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		if (info.getDatabaseObjectType() == DatabaseObjectType.SCHEMA)
		{
			return true;
		} else
		{
			return false;
		}
	}

	
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.DECIMAL || dataType == Types.NUMERIC)
		{
			return 32;
		}
		if (dataType == Types.DOUBLE || dataType == Types.DOUBLE)
		{
			return 16;
		}
		return 32;
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
		return "Informix";
	}

	
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.toLowerCase().contains("informix"))
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

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		

		return new String[] { DialectUtils.getAddIndexSQL(pkName, true, columns, qualifier, prefs, this),
				DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, true, qualifier, prefs, this) };
	}

	
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs) };
	}

	
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnRenameSQL(from, to, qualifier, prefs, this);
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String setClause = null;
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
	}

	
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String defaultClause = DialectUtils.DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, true, defaultClause, qualifier, prefs);
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
		return DialectType.INFORMIX;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "NORMAL", "CLUSTERED" };

	}
	
	public String[] getIndexStorageOptions()
	{
		
		return null;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		
		
		
		
		
		StringBuilder result = new StringBuilder();
		result.append(DialectUtils.ALTER_TABLE_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs, this));
		result.append(" ");
		result.append(DialectUtils.MODIFY_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeIdentifier(column.getColumnName(), prefs, this));
		result.append(" SERIAL");
		return new String[] { result.toString() };
	}

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		return new String[] { sql };
	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		
		String[] utilSql =
			DialectUtils.getAddForeignKeyConstraintSQL(localTableName,
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

		String indexSql = null;
		if (utilSql.length == 2)
		{
			indexSql = utilSql[1];
		}

		
		StringBuilder result = new StringBuilder();
		result.append(DialectUtils.ALTER_TABLE_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeQualifiableIdentifier(localTableName, qualifier, prefs, this));
		result.append(" ADD CONSTRAINT FOREIGN KEY ");

		result.append(" (");

		ArrayList<String> localColumns = new ArrayList<String>();
		StringBuilder refColumns = new StringBuilder();
		for (String[] columns : localRefColumns)
		{
			result.append(DialectUtils.shapeIdentifier(columns[0], prefs, this));
			result.append(", ");
			localColumns.add(columns[0]);
			refColumns.append(DialectUtils.shapeIdentifier(columns[1], prefs, this));
			refColumns.append(", ");
		}
		result.setLength(result.length() - 2); 
		refColumns.setLength(refColumns.length() - 2); 

		result.append(")\n REFERENCES ");
		result.append(DialectUtils.shapeQualifiableIdentifier(refTableName, qualifier, prefs, this));
		result.append(" (");
		result.append(refColumns.toString());
		result.append(")\n");

		result.append(" CONSTRAINT ");
		result.append(constraintName);

		String[] retVal = null;
		if (indexSql != null)
		{
			retVal = new String[] { result.toString(), indexSql };
		} else
		{
			retVal = new String[] { result.toString() };
		}

		return retVal;
	}

	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		

		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		sql.append("\n");

		sql.append(" ");
		sql.append(DialectUtils.ADD_CONSTRAINT_CLAUSE);

		sql.append(" ");
		sql.append(DialectUtils.UNIQUE_CLAUSE);
		sql.append(" (");
		for (TableColumnInfo column : columns)
		{
			sql.append(DialectUtils.shapeIdentifier(column.getColumnName(), prefs, this));
			sql.append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); 
		sql.append(")");

		sql.append(" CONSTRAINT ");
		sql.append(DialectUtils.shapeIdentifier(constraintName, prefs, this));

		return new String[] { sql.toString() };

	}

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NOCYCLE_CLAUSE;
		}

		return new String[] { DialectUtils.getAlterSequenceSQL(sequenceName,
			increment,
			minimum,
			maximum,
			restart,
			cache,
			cycleClause,
			qualifier,
			prefs,
			this) };
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String access = (accessMethod == null || accessMethod.equalsIgnoreCase("NORMAL")) ? null : "CLUSTER";

		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_CLAUSE + " ");
		if (unique)
		{
			sql.append(DialectUtils.UNIQUE_CLAUSE + " ");
		} else
		{
			if (access != null)
			{
				sql.append(access);
			}
		}
		sql.append(" ");
		sql.append(DialectUtils.INDEX_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeIdentifier(indexName, prefs, this));
		sql.append(" ON ").append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));

		sql.append("(");
		for (String column : columns)
		{
			sql.append(DialectUtils.shapeIdentifier(column, prefs, this)).append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); 
		sql.append(")");

		return sql.toString();
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NOCYCLE_CLAUSE;
		}

		String minimumClause = DialectUtils.MINVALUE_CLAUSE;
		if (minimum == null || "".equals(DialectUtils.NOMINVALUE_CLAUSE))
		{
			minimumClause = DialectUtils.NOMINVALUE_CLAUSE;
		}

		String maximumClause = DialectUtils.MAXVALUE_CLAUSE;
		if (maximum == null || "".equals(maximum))
		{
			maximumClause = DialectUtils.NOMAXVALUE_CLAUSE;
		}

		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minimumClause,
			minimum,
			maximumClause,
			maximum,
			start,
			cache,
			cycleClause,
			qualifier,
			prefs,
			this);
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
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropIndexSQL(indexName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropSequenceSQL(sequenceName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		result.append("RENAME TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		result.append(" TO ");
		result.append(DialectUtils.shapeQualifiableIdentifier(newTableName, qualifier, prefs, this));
		return result.toString();
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		return null;
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringBuilder result = new StringBuilder();
		result.append("SELECT  ");
		result.append(sequenceName);
		result.append(".CURRVAL");
		result.append(" as last_value, ");
		result.append("T1.max_val   AS max_value, ");
		result.append("T1.min_val   AS min_value, ");
		result.append("T1.cache     AS cache_size, ");
		result.append("T1.inc_val   AS increment_by, ");
		result.append("T1.cycle 	 AS is_cycled ");
		result.append("FROM    informix.syssequences AS T1, informix.systables AS T2 ");
		result.append("WHERE   T2.tabid     = T1.tabid ");
		result.append("and T2.owner = ");
		result.append("'");
		result.append(qualifier.getSchema());
		result.append("'");
		result.append("and T2.tabname =");
		result.append("'");
		result.append(sequenceName);
		result.append("'");

		return result.toString();
	}

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		






		String templateStr = "";
		
		if (fromTables != null) {
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_TWO;
		} else {
			templateStr = ST_UPDATE_STYLE_ONE;
		}
			
		StringTemplate st = new StringTemplate(templateStr);

		return DialectUtils.getUpdateSQL(st, tableName,
			setColumns,
			setValues,
			fromTables,
			whereColumns,
			whereValues,
			qualifier,
			prefs,
			this);
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

	
	public boolean supportsViewDefinition() {
		return true;
	}
	
	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		
		StringBuilder result = new StringBuilder();
		result.append("SELECT viewtext ");

		result.append("FROM informix.systables AS T1, informix.sysviews AS T2 ");
		result.append("WHERE tabname = '");
		result.append(viewName);
		result.append("' ");
		result.append("AND T2.tabid = T1.tabid");
		return result.toString();
	}

	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		StringBuilder result = new StringBuilder();
		if (prefs.isQualifyTableNames()) {
			String catalog = qualifier.getCatalog();
			String schema = qualifier.getSchema();
		
			if (catalog != null && schema != null) {
				result.append(catalog);
				result.append(":");
				result.append(schema);
				result.append(".");
				result.append(identifier);
			}	
		} else {
			result.append(identifier);
		}
		return result.toString();
	}
	
	
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}
	
}
