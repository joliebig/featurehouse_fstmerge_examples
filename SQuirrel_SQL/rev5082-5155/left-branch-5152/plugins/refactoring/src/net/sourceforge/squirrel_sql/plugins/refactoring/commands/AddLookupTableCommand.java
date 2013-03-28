package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.AddLookupTableDialog;

public class AddLookupTableCommand extends AbstractRefactoringCommand
{
	
	private final static ILogger s_log = LoggerController.createLogger(AddLookupTableCommand.class);

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddLookupTableCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddLookupTableCommand.sqlDialogTitle");
	}

	protected AddLookupTableDialog _customDialog;

	public AddLookupTableCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
			return;

		showCustomDialog();
	}

	
	@Override
	protected String[] generateSQLStatements() throws Exception
	{
		ArrayList<String> results = new ArrayList<String>();

		ISQLDatabaseMetaData md = _session.getMetaData();

		String catalog = _info[0].getCatalogName();
		String schema = _info[0].getSchemaName();

		String lookupTableName = _customDialog.getLookupTableName();
		String lookupPrimaryKey = _customDialog.getLookupPrimaryKey();

		TableColumnInfo sourceColumn = null;
		for (TableColumnInfo info : md.getColumnInfo((ITableInfo) _info[0]))
		{
			if (info.getColumnName().equals(_customDialog.getSourceColumn()))
			{
				sourceColumn = info;
				break;
			}
		}
		if (sourceColumn == null)
			throw new IllegalStateException("The selected source column was not found.");
		String sourceTableName = sourceColumn.getTableName();
		String sourceColumnName = sourceColumn.getColumnName();

		
		ArrayList<TableColumnInfo> columns = new ArrayList<TableColumnInfo>();
		ArrayList<TableColumnInfo> primaryKeys = new ArrayList<TableColumnInfo>();

		if (_customDialog.getMode() == AddLookupTableDialog.MODE_KEEP)
		{
			TableColumnInfo pk =
				new TableColumnInfo(	catalog,
											schema,
											lookupTableName,
											lookupPrimaryKey,
											sourceColumn.getDataType(),
											sourceColumn.getTypeName(),
											sourceColumn.getColumnSize(),
											sourceColumn.getDecimalDigits(),
											sourceColumn.getRadix(),
											sourceColumn.isNullAllowed(),
											sourceColumn.getRemarks(),
											sourceColumn.getDefaultValue(),
											sourceColumn.getOctetLength(),
											1,
											sourceColumn.isNullable(),
											md);
			columns.add(pk);
			primaryKeys.add(pk);
		} else if (_customDialog.getMode() == AddLookupTableDialog.MODE_REPLACE)
		{
			TableColumnInfo pk =
				new TableColumnInfo(	catalog,
											schema,
											lookupTableName,
											lookupPrimaryKey,
											Types.INTEGER,
											JDBCTypeMapper.getJdbcTypeName(Types.INTEGER),
											0,
											0,
											0,
											0,
											null,
											null,
											0,
											1,
											"NO",
											md);

			TableColumnInfo second =
				new TableColumnInfo(	catalog,
											schema,
											lookupTableName,
											_customDialog.getLookupSecondColumn(),
											sourceColumn.getDataType(),
											sourceColumn.getTypeName(),
											sourceColumn.getColumnSize(),
											sourceColumn.getDecimalDigits(),
											sourceColumn.getRadix(),
											0,
											sourceColumn.getRemarks(),
											sourceColumn.getDefaultValue(),
											sourceColumn.getOctetLength(),
											2,
											"NO",
											md);

			columns.add(pk);
			columns.add(second);
			primaryKeys.add(pk);
		}
		results.add(_dialect.getCreateTableSQL(lookupTableName, columns, primaryKeys, _sqlPrefs, _qualifier));

		if (_customDialog.getMode() == AddLookupTableDialog.MODE_KEEP)
		{
			
			ArrayList<String> insertColumns = new ArrayList<String>();
			insertColumns.add(lookupPrimaryKey);
			String dataQuery = getDataQuery(schema, sourceTableName, sourceColumnName);

			results.add(_dialect.getInsertIntoSQL(lookupTableName,
				insertColumns,
				dataQuery,
				_qualifier,
				_sqlPrefs));

			
			String constraintName = _customDialog.getForeignKeyName();
			ArrayList<String[]> refs = new ArrayList<String[]>();
			refs.add(new String[] { sourceColumnName, lookupPrimaryKey });

			String[] fkSQLs =
				_dialect.getAddForeignKeyConstraintSQL(sourceTableName,
					lookupTableName,
					constraintName,
					false,
					false,
					false,
					false,
					null,
					refs,
					"NO ACTION",
					"NO ACTION",
					_qualifier,
					_sqlPrefs);

			results.addAll(Arrays.asList(fkSQLs));

		} else if (_customDialog.getMode() == AddLookupTableDialog.MODE_REPLACE)
		{
			
			String dataQuery = getDataQuery(schema, sourceTableName, sourceColumnName);
			List<String> data = executeStringQuery(dataQuery);

			
			ArrayList<String> insertColumns = new ArrayList<String>();
			insertColumns.add(lookupPrimaryKey);
			insertColumns.add(_customDialog.getLookupSecondColumn());

			for (int i = 0; i < data.size(); i++)
			{
				
				
				
				
				
				
				String valuesPart = " VALUES ( " + i + ", '" + data.get(i) + "' )";
				results.add(_dialect.getInsertIntoSQL(lookupTableName,
					insertColumns,
					valuesPart,
					_qualifier,
					_sqlPrefs));
			}

			
			TableColumnInfo tempColumn =
				new TableColumnInfo(	catalog,
											schema,
											sourceTableName,
											sourceColumnName + "_temp",
											sourceColumn.getDataType(),
											sourceColumn.getTypeName(),
											sourceColumn.getColumnSize(),
											sourceColumn.getDecimalDigits(),
											sourceColumn.getRadix(),
											sourceColumn.isNullAllowed(),
											sourceColumn.getRemarks(),
											sourceColumn.getDefaultValue(),
											sourceColumn.getOctetLength(),
											sourceColumn.getOrdinalPosition(),
											sourceColumn.isNullable(),
											md);

			results.add(_dialect.getColumnNameAlterSQL(sourceColumn, tempColumn, _qualifier, _sqlPrefs));

			
			TableColumnInfo newColumn =
				new TableColumnInfo(	catalog,
											schema,
											sourceTableName,
											sourceColumnName,
											Types.INTEGER,
											JDBCTypeMapper.getJdbcTypeName(Types.INTEGER),
											0,
											0,
											0,
											1,
											sourceColumn.getRemarks(),
											null,
											0,
											sourceColumn.getOrdinalPosition(),
											"YES",
											md);
			String[] addColumnResults = _dialect.getAddColumnSQL(newColumn, _qualifier, _sqlPrefs);
			for (String addColumnResult : addColumnResults)
			{
				results.add(addColumnResult);
			}

			
			String constraintName = _customDialog.getForeignKeyName();
			ArrayList<String[]> refs = new ArrayList<String[]>();
			refs.add(new String[] { sourceColumnName, lookupPrimaryKey });

			String[] fkSQLs =
				_dialect.getAddForeignKeyConstraintSQL(sourceTableName,
					lookupTableName,
					constraintName,
					false,
					false,
					false,
					false,
					null,
					refs,
					"NO ACTION",
					"NO ACTION",
					_qualifier,
					_sqlPrefs);

			results.addAll(Arrays.asList(fkSQLs));

			
			for (int i = 0; i < data.size(); i++)
			{
				
				
				results.addAll(Arrays.asList(_dialect.getUpdateSQL(sourceTableName,
					new String[] { sourceColumnName },
					new String[] { String.valueOf(i) },
					null,
					new String[] { sourceColumnName + "_temp" },
					new String[] { "'" + data.get(i) + "'" },
					_qualifier,
					_sqlPrefs)));
			}

			
			
			if (sourceColumn.isNullAllowed() == 0)
			{
				TableColumnInfo newColumnNotNull =
					new TableColumnInfo(	catalog,
												schema,
												sourceTableName,
												newColumn.getColumnName(),
												newColumn.getDataType(),
												newColumn.getTypeName(),
												newColumn.getColumnSize(),
												newColumn.getDecimalDigits(),
												newColumn.getRadix(),
												0,
												newColumn.getRemarks(),
												newColumn.getDefaultValue(),
												newColumn.getOctetLength(),
												newColumn.getOrdinalPosition(),
												"NO",
												md);
				results.addAll(Arrays.asList(_dialect.getColumnNullableAlterSQL(newColumnNotNull,
					_qualifier,
					_sqlPrefs)));
			}

			
			String dropStmt = _dialect.getColumnDropSQL(sourceTableName, sourceColumnName + "_temp", _qualifier, _sqlPrefs);
			if (_customDialog.getDropCascade())
			{
				dropStmt += " CASCADE";
			}
			results.add(dropStmt);
		}
		return results.toArray(new String[results.size()]);
	}

	
	@Override
	protected void executeScript(String script)
	{
		try
		{
			NoAutoCommitCommandExecHandler handler = new NoAutoCommitCommandExecHandler(_session);
			SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
			executer.run(); 

			_session.getApplication().getThreadPool().addTask(new Runnable()
			{
				public void run()
				{
					GUIUtils.processOnSwingEventThread(new Runnable()
					{
						public void run()
						{
							_customDialog.setVisible(false);
							_session.getSchemaInfo().reloadAll();
						}
					});
				}
			});
		} catch (SQLException e)
		{
			_session.showErrorMessage(e);
			s_log.error("Unexpected exception " + e.getMessage(), e);
		}
	}

	
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		boolean result = true;
		
		
		result = result && dialect.supportsCreateTable();
		
		result = result && dialect.supportsInsertInto();
		
		result = result && dialect.supportsAddForeignKeyConstraint();
		
		result = result && dialect.supportsRenameColumn();
		
		result = result && dialect.supportsAddColumn();		
		
		result = result && dialect.supportsUpdate();
		
		result = result && dialect.supportsAlterColumnNull();
		
		result = result && dialect.supportsDropColumn();

		return result;
	}

	private void showCustomDialog() throws SQLException
	{
		ISQLDatabaseMetaData md = _session.getMetaData();
		ITableInfo selectedTable = (ITableInfo) _info[0];
		TableColumnInfo[] tableColumnInfos = md.getColumnInfo(selectedTable);
		ForeignKeyInfo[] exportedKeys = md.getExportedKeysInfo(selectedTable);
		ForeignKeyInfo[] importedKeys = md.getImportedKeysInfo(selectedTable);

		_customDialog =
			new AddLookupTableDialog(selectedTable.getSimpleName(), getColumnNames(tableColumnInfos,
				exportedKeys,
				importedKeys));
		_customDialog.addExecuteListener(new ExecuteListener());
		_customDialog.addEditSQLListener(new EditSQLListener(_customDialog));
		_customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, _customDialog));
		_customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
		_customDialog.setVisible(true);
	}

	private List<String> executeStringQuery(String sql)
	{
		ArrayList<String> result = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = _session.getSQLConnection().createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String value = rs.getString(1);
				if (!rs.wasNull())
					result.add(value);
			}
		} catch (SQLException e)
		{
			s_log.error("executeStringQuery: unexpected exception while executing query ( " + sql + " ): "
				+ e.getMessage(), e);
		} finally
		{
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}
		return result;
	}

	private String[] getColumnNames(TableColumnInfo[] infos, ForeignKeyInfo[] exportedKeys,
		ForeignKeyInfo[] importedKeys)
	{
		ArrayList<String> columnNames = new ArrayList<String>();
		for (TableColumnInfo info : infos)
		{
			columnNames.add(info.getColumnName());
		}
		for (ForeignKeyInfo exportedKey : exportedKeys)
		{
			columnNames.remove(exportedKey.getPrimaryKeyColumnName());
		}
		for (ForeignKeyInfo importedKey : importedKeys)
		{
			columnNames.remove(importedKey.getForeignKeyColumnName());
		}
		return columnNames.toArray(new String[] {});
	}

	private String getDataQuery(String schema, String table, String column)
	{
		StringBuilder result = new StringBuilder();
		result.append("SELECT DISTINCT \"").append(column).append("\" FROM ");
		if (_sqlPrefs.isQualifyTableNames())
		{
			result.append("\"").append(schema).append("\".\"").append(table).append("\"");
		} else
		{
			result.append(table);
		}
		return result.toString();
	}

	
	protected class NoAutoCommitCommandExecHandler extends CommandExecHandler
	{
		protected boolean _origAutoCommit;

		public NoAutoCommitCommandExecHandler(ISession session) throws SQLException
		{
			super(session);

			_origAutoCommit = _session.getSQLConnection().getAutoCommit();
			_session.getSQLConnection().setAutoCommit(false);
		}

		public void sqlCloseExecutionHandler()
		{
			super.sqlCloseExecutionHandler();
			if (_origAutoCommit)
			{
				if (exceptionEncountered())
					_session.rollback();
				else
					_session.commit();
				try
				{
					_session.getSQLConnection().setAutoCommit(true);
				} catch (SQLException e)
				{
					_session.showErrorMessage(e);
				}
			}
		}
	}

}
