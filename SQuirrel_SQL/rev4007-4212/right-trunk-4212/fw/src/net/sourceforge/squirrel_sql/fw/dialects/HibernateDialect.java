
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;


public interface HibernateDialect extends StringTemplateConstants
{

	
	String getTypeName(int code, int length, int precision, int scale) throws HibernateException;

	
	public String getTypeName(int code) throws HibernateException;
	
	
	
	boolean canPasteTo(IDatabaseObjectInfo info);

	
	boolean supportsSchemasInTableDefinition();

	
	String getNullColumnString();

	
	String getMaxFunction();

	
	String getLengthFunction(int dataType);

	
	int getMaxPrecision(int dataType);

	
	int getMaxScale(int dataType);

	
	int getPrecisionDigits(int columnSize, int dataType);

	
	int getColumnLength(int columnSize, int dataType);

	
	boolean supportsProduct(String databaseProductName, String databaseProductVersion);

	
	String getDisplayName();

	
	boolean supportsColumnComment();

	
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException;

	
	boolean supportsDropColumn();

	
	boolean supportsAlterColumnNull();

	
	String getColumnDropSQL(String tableName, String columnName) throws UnsupportedOperationException;

	
	List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView);

	
	String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti);

	
	String getAddColumnString();

	
	String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	boolean supportsRenameColumn();

	
	String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to);

	
	boolean supportsAlterColumnType();

	
	List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException;

	
	boolean supportsAlterColumnDefault();

	
	String getColumnDefaultAlterSQL(TableColumnInfo info);

	
	String getDropPrimaryKeySQL(String pkName, String tableName);

	
	String getDropForeignKeySQL(String fkName, String tableName);

	
	List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException;

	
	DialectType getDialectType();

	
	public boolean supportsSequence();

	
	public boolean supportsTablespace();

	
	public boolean supportsIndexes();

	
	public boolean supportsAccessMethods();

	
	public boolean supportsAutoIncrement();

	
	public boolean supportsCheckOptionsForViews();

	
	public boolean supportsEmptyTables();

	
	public boolean supportsMultipleRowInserts();

	
	public boolean supportsAddColumn();

	
	public boolean supportsAddForeignKeyConstraint();

	
	public boolean supportsAddUniqueConstraint();

	
	public boolean supportsAlterSequence();

	
	public boolean supportsCreateIndex();

	
	public boolean supportsCreateSequence();

	
	public boolean supportsCreateTable();

	
	public boolean supportsCreateView();

	
	public boolean supportsDropConstraint();

	
	public boolean supportsDropIndex();

	
	public boolean supportsDropSequence();

	
	public boolean supportsDropView();

	
	public boolean supportsInsertInto();

	
	public boolean supportsRenameTable();

	
	public boolean supportsRenameView();

	
	public boolean supportsSequenceInformation();

	
	public boolean supportsUpdate();

	
	public String[] getIndexAccessMethodsTypes();
	

		
	public String[] getIndexStorageOptions();
	
	
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier);

	
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	public char closeQuote();

	
	public char openQuote();

	
	public boolean supportsViewDefinition();
	
	
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);
	
	
	
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs);

	
	
	public boolean supportsCorrelatedSubQuery();

	
	SequencePropertyMutability getSequencePropertyMutability();
	
	
	
	boolean supportsSubSecondTimestamps();
	
	
	
}
