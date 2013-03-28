
package net.sourceforge.squirrel_sql.fw.dialects;

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NOCYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.RENAME_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.TO_CLAUSE;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;


public class OracleDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class OracleDialectHelper extends org.hibernate.dialect.Oracle9Dialect {
		public OracleDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "number($p)");
			registerColumnType(Types.BINARY, 2000, "raw($l)");
			registerColumnType(Types.BINARY, "blob");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 2000, "char($l)");
			registerColumnType(Types.CHAR, 4000, "varchar2($l)");
			registerColumnType(Types.CHAR, "clob");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "blob");
			registerColumnType(Types.LONGVARCHAR, 4000, "varchar2($l)");
			registerColumnType(Types.LONGVARCHAR, "clob");
			registerColumnType(Types.NUMERIC, "number($p)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "date");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "blob");
			registerColumnType(Types.VARCHAR, 4000, "varchar2($l)");
			registerColumnType(Types.VARCHAR, "clob");
			
			registerColumnType(Types.OTHER, 4000, "varchar2(4000)");
			registerColumnType(Types.OTHER, "clob");

		}
		
	}
	
	
	private OracleDialectHelper _dialect = new OracleDialectHelper();
	
	
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
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
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
	public String getDisplayName()
	{
		return "Oracle";
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("oracle"))
		{
			
			return true;
		}
		return false;
	}

	
	@Override
	public boolean supportsColumnComment()
	{
		return true;
	}

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
	}

	
	public boolean supportsDropColumn()
	{
		return true;
	}

	
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(tableName);
		result.append(" DROP COLUMN ");
		result.append(columnName);
		return result.toString();
	}

	
	public List<String> getTableDropSQL(ITableInfo ti, boolean cascadeConstraints, boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String cascadeClause = "";
		if (!isMaterializedView)
		{
			cascadeClause = DialectUtils.CASCADE_CONSTRAINTS_CLAUSE;
		}

		return DialectUtils.getTableDropSQL(ti,
			true,
			cascadeConstraints,
			true,
			cascadeClause,
			isMaterializedView, qualifier, prefs, this);
	}

	
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(ti.getQualifiedName());
		result.append(" ADD CONSTRAINT ");
		result.append(pkName);
		result.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			result.append(columns[i].getColumnName());
			if (i + 1 < columns.length)
			{
				result.append(", ");
			}
		}
		result.append(")");
		return new String[] { result.toString() };
	}

	
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		if (info.isNullable().equals("YES"))
		{
			result.append(" NULL");
		} else
		{
			result.append(" NOT NULL");
		}
		return new String[] { result.toString() };
	}

	
	public boolean supportsRenameColumn()
	{
		return true;
	}

	
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		StringTemplate st = new StringTemplate(ST_ALTER_COLUMN_NAME_STYLE_ONE);
		
		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				from.getTableName(),
				ST_OLD_COLUMN_NAME_KEY,
				from.getColumnName(),
				ST_NEW_COLUMN_NAME_KEY,
				to.getColumnName());
		
		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		ArrayList<String> result = new ArrayList<String>();

		
		if ((from.getDataType() == Types.VARCHAR && to.getDataType() == Types.CLOB)
			|| (from.getDataType() == Types.CLOB && to.getDataType() == Types.VARCHAR))
		{
			
			TableColumnInfo newInfo = DialectUtils.getRenamedColumn(to, to.getColumnName() + "_2");

			String[] addSQL = this.getAddColumnSQL(newInfo, qualifier, prefs);
			for (int i = 0; i < addSQL.length; i++)
			{
				result.add(addSQL[i]);
			}

			
			StringBuilder updateSQL = new StringBuilder();
			updateSQL.append("update ");
			updateSQL.append(from.getTableName());
			updateSQL.append(" set ");
			updateSQL.append(newInfo.getColumnName());
			updateSQL.append(" = ");
			updateSQL.append(from.getColumnName());
			result.add(updateSQL.toString());

			
			String dropSQL = getColumnDropSQL(from.getTableName(), from.getColumnName(), qualifier, prefs);
			result.add(dropSQL);

			
			String renameSQL = this.getColumnNameAlterSQL(newInfo, to, qualifier, prefs);
			result.add(renameSQL);
		} else
		{
			StringBuffer tmp = new StringBuffer();
			tmp.append("ALTER TABLE ");
			tmp.append(from.getTableName());
			tmp.append(" MODIFY (");
			tmp.append(from.getColumnName());
			tmp.append(" ");
			tmp.append(DialectUtils.getTypeName(to, this));
			tmp.append(")");
			result.add(tmp.toString());
		}
		return result;
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
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
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
		return result.toString();
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
		return DialectType.ORACLE;
	}

	
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "default", "unique", "bitmap" };
	}

	
	public String[] getIndexStorageOptions()
	{
		
		return null;
	}

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		
		

		

		
		
		
		String seqName = column.getColumnName() + "_AUTOINC_SEQ";
		String sequenceSql = getCreateSequenceSQL(seqName, "1", "1", null, "1", null, false, qualifier, prefs);
		
		
		String tableName = column.getTableName();
		String trigName = column.getColumnName() + "_AUTOINC_TRIG";
		String triggerTemplateStr = 
			"CREATE OR REPLACE TRIGGER $triggerName$ \n" +
			"BEFORE INSERT ON $tableName$ \n" +
			"FOR EACH ROW \n" +
			"DECLARE \n" +
			"    nextid number(8) := 0; \n" +
			"BEGIN \n" +
			"    SELECT $sequenceName$.nextval into nextid from dual; \n" +
			"    :new.$columnName$ := nextid; \n" +
			"END; ";

		StringTemplate st = new StringTemplate(triggerTemplateStr);
		st.setAttribute("triggerName", trigName);
		st.setAttribute("tableName", tableName);
		st.setAttribute("sequenceName", seqName);
		st.setAttribute("columnName", column.getColumnName());
		
		return new String[] { sequenceSql, st.toString() };
	}

	
	public String[] getAddColumnSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		String addColumnSql =
			DialectUtils.getAddColumSQL(info,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		if (info.getRemarks() != null && !"".equals(info.getRemarks()))
		{
			return new String[] { addColumnSql,
					DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this) };
		} else
		{
			return new String[] { addColumnSql };
		}

	}

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		
		String onUpdateNotSupported = null;
		
		String onDeleteNoAction = null;
		
		Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName,
			refTableName,
			constraintName,
			deferrable,
			initiallyDeferred,
			matchFullNotSupported,
			autoFKIndex,
			fkIndexName,
			localRefColumns,
			onUpdateNotSupported,
			onDeleteNoAction,
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

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String[] result = null;
		Boolean cascadeNotSupported = null;
		String cycleClause = cycle ? CYCLE_CLAUSE : NOCYCLE_CLAUSE;

		if (restart != null)
		{
			String comment = "-- Oracle cannot change the start value of a sequence.";
			String comment2 = "-- Must drop and re-create.";
			String dropSql =
				DialectUtils.getDropSequenceSQL(sequenceName, cascadeNotSupported, qualifier, prefs, this);
			String createSql =
				DialectUtils.getCreateSequenceSQL(sequenceName,
					increment,
					minimum,
					maximum,
					restart,
					cache,
					cycleClause,
					qualifier,
					prefs,
					this);
			result = new String[] { comment, comment2, dropSql, createSql };
		} else
		{
			String restartNotSupported = null;
			String sql =
				DialectUtils.getAlterSequenceSQL(sequenceName,
					increment,
					minimum,
					maximum,
					restartNotSupported,
					cache,
					cycleClause,
					qualifier,
					prefs,
					this);
			result = new String[] { sql };
		}
		return result;
	}

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		result.append("CREATE ");

		
		if (unique && ! accessMethod.equalsIgnoreCase("bitmap") )
		{
			result.append("UNIQUE ");
		}
		if (accessMethod != null && accessMethod.equalsIgnoreCase("bitmap"))
		{
			result.append(accessMethod);
			result.append(" ");
		}
		result.append("INDEX ");
		result.append(DialectUtils.shapeQualifiableIdentifier(indexName, qualifier, prefs, this));
		result.append(" ON ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		result.append("(");
		for (String column : columns)
		{
			result.append(column);
			result.append(",");
		}
		result.setLength(result.length() - 1);
		result.append(")");
		return result.toString();
	}

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String minValue = minimum;
		String minClause = DialectUtils.MINVALUE_CLAUSE;
		if (minValue == null || "".equals(minValue)) {
			minValue = DialectUtils.NOMINVALUE_CLAUSE;
			minClause = "";
		}
		String maxValue = maximum; 
		String maxClause = DialectUtils.MAXVALUE_CLAUSE;
		if (maxValue == null || "".equals(maxValue)) {
			maxValue = DialectUtils.NOMAXVALUE_CLAUSE;
			maxClause = "";
		}
		
		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minClause,
			minValue,
			maxClause,
			maxValue,
			start,
			cache,
			null,
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
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String query,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, query, qualifier, prefs, this);
	}

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs, this);
	}

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getRenameViewSQL(RENAME_CLAUSE,
			TO_CLAUSE,
			oldViewName,
			newViewName,
			qualifier,
			prefs,
			this) };
	}

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		
		

		StringBuilder result = new StringBuilder();
		result.append("SELECT last_number, max_value, min_value, cache_size, increment_by, ");
		result.append("case cycle_flag when 'N' then 0 else 1 end as cycle_flag ");
		result.append("FROM USER_SEQUENCES ");
		result.append("WHERE sequence_name = upper(?)");
		return result.toString();
	}

	
	public String[] getUpdateSQL(String destTableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String templateStr = "";
		
		if (fromTables != null) {
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_ONE;
		} else {
			templateStr = ST_UPDATE_STYLE_ONE;
		}
			
		StringTemplate st = new StringTemplate(templateStr);
		
		return DialectUtils.getUpdateSQL(st,
			destTableName,
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
		return true;
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
		return true;
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
		return true;
	}
	
	
}
