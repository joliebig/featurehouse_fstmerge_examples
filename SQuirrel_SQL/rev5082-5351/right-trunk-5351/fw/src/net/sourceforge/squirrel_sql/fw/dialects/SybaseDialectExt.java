
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class SybaseDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class SybaseDialectHelper extends org.hibernate.dialect.SybaseDialect {
		public SybaseDialectHelper() {
			super();
			registerColumnType(Types.BIGINT, "numeric($p)");
			registerColumnType(Types.BINARY, "image");
			registerColumnType(Types.BIT, "tinyint");
			registerColumnType(Types.BLOB, "image");
			registerColumnType(Types.BOOLEAN, "tinyint");
			registerColumnType(Types.CHAR, 255, "char($l)");
			registerColumnType(Types.CHAR, "text");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "image");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "datetime");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, "image");
			registerColumnType(Types.VARCHAR, 255, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");			
		}
	}
	
	
	private SybaseDialectHelper _dialect = new SybaseDialectHelper();

	
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
		return "datalength";
	}

	
	@Override
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 48;
		} else
		{
			return 38;
		}
	}

	
	@Override
	public String getDisplayName()
	{
		return "Sybase";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		String lname = databaseProductName.trim().toLowerCase();
		if (lname.startsWith("sybase") || lname.startsWith("adaptive") || lname.startsWith("sql server"))
		{
			
			return true;
		}
		return false;
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

		
		List<String> dropTableSQL =
			DialectUtils.getTableDropSQL(iTableInfo,
				false,
				cascadeConstraints,
				false,
				DialectUtils.CASCADE_CLAUSE,
				false, qualifier, prefs, this);
		if (cascadeConstraints)
		{
			ArrayList<String> result = new ArrayList<String>();
			ForeignKeyInfo[] fks = iTableInfo.getExportedKeys();
			if (fks != null && fks.length > 0)
			{
				for (int i = 0; i < fks.length; i++)
				{
					ForeignKeyInfo info = fks[i];
					String fkName = info.getForeignKeyName();
					String fkTable = info.getForeignKeyTableName();
					StringBuilder tmp = new StringBuilder();
					tmp.append("ALTER TABLE ");
					tmp.append(fkTable);
					tmp.append(" DROP CONSTRAINT ");
					tmp.append(fkName);
					result.add(tmp.toString());
				}
			}
			result.addAll(dropTableSQL);
			return result;
		} else
		{
			return dropTableSQL;
		}
	}

	
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs, this) };
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, false, qualifier, prefs) };
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
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
	}

	
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
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
		return DialectType.SYBASEASE;
	}

	
	@Override
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "UNIQUE", "NON-UNIQUE" };
	}

	
	@Override
	public String[] getIndexStorageOptions()
	{
		return new String[] { "NONCLUSTERED", "CLUSTERED" };
	}

	
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ADD_AUTO_INCREMENT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = true;
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

		return result.toArray(new String[result.size()]);
	}

	
	@Override
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> fkValuesMap = new HashMap<String, String>();
		fkValuesMap.put("childTableName", localTableName);
		fkValuesMap.put("constraint", "CONSTRAINT");
		fkValuesMap.put("constraintName", constraintName);
		fkValuesMap.put("parentTableName", refTableName);

		
		StringTemplate childIndexST = null;
		HashMap<String, String> ckIndexValuesMap = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(st,
			fkValuesMap,
			childIndexST,
			ckIndexValuesMap,
			localRefColumns,
			qualifier,
			prefs,
			this);

	}

	
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO);

		HashMap<String, String> valuesMap = new HashMap<String, String>();

		valuesMap.put(ST_TABLE_NAME_KEY, tableName);
		valuesMap.put(ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st,
			valuesMap,
			columns,
			qualifier,
			prefs,
			this) };
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
		StringTemplate st = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);

		HashMap<String, String> valuesMap = new HashMap<String, String>();
		if (unique)
		{
			valuesMap.put(ST_UNIQUE_KEY, "UNIQUE");
		}

		
		

		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		return DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
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
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("viewName", viewName);
		valuesMap.put("selectStatement", definition);
		if (checkOption != null && !"".equals(checkOption))
		{
			valuesMap.put("with", "WITH");
			valuesMap.put("checkOption", "CHECK OPTION");
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
	}

	
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_TWO);
		st.setAttribute(ST_INDEX_NAME_KEY, indexName);
		st.setAttribute(ST_TABLE_NAME_KEY, tableName);
		return st.toString();
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
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	@Override
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	
	@Override
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_SP_RENAME_STYLE_ONE);
		HashMap<String, String> valuesMap = new HashMap<String, String>();

		valuesMap.put(ST_OLD_OBJECT_NAME_KEY, oldTableName);
		valuesMap.put(ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_SP_RENAME_STYLE_ONE);
		HashMap<String, String> valuesMap = new HashMap<String, String>();

		valuesMap.put(ST_OLD_OBJECT_NAME_KEY, oldViewName);
		valuesMap.put(ST_NEW_OBJECT_NAME_KEY, newViewName);

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
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

	
	@Override
	public boolean supportsAccessMethods()
	{
		return true;
	}

	
	@Override
	public boolean supportsAddForeignKeyConstraint()
	{
		return true;
	}

	
	@Override
	public boolean supportsAddUniqueConstraint()
	{
		return true;
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
		return true;
	}

	
	@Override
	public boolean supportsCreateIndex()
	{
		return true;
	}

	
	@Override
	public boolean supportsCreateSequence()
	{
		return false;
	}

	
	@Override
	public boolean supportsCreateTable()
	{
		return true;
	}

	
	@Override
	public boolean supportsCreateView()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropConstraint()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropIndex()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropSequence()
	{
		return false;
	}

	
	@Override
	public boolean supportsDropView()
	{
		return true;
	}

	
	@Override
	public boolean supportsEmptyTables()
	{
		return false;
	}

	
	@Override
	public boolean supportsIndexes()
	{
		return true;
	}

	
	@Override
	public boolean supportsInsertInto()
	{
		return true;
	}

	
	@Override
	public boolean supportsMultipleRowInserts()
	{
		
		return true;
	}

	
	@Override
	public boolean supportsRenameTable()
	{
		return true;
	}

	
	@Override
	public boolean supportsRenameView()
	{
		return true;
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
		return true;
	}

	
	@Override
	public boolean supportsAddColumn()
	{
		return true;
	}

	
	@Override
	public boolean supportsViewDefinition() {
		return true;
	}	
	
	
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		String sql =
			"select text from sysobjects inner join syscomments on syscomments.id = sysobjects.id "
				+ "where (loginame = '$catalogName$' or loginame is null) " +
						"and name = '$viewName$' and text not like '%--%'";		

		StringTemplate st = new StringTemplate(sql);
		st.setAttribute(ST_CATALOG_NAME_KEY, qualifier.getCatalog());
		st.setAttribute(ST_VIEW_NAME_KEY, viewName);

		return st.toString();
	}

	
	@Override
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	
	@Override
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}
	
}
