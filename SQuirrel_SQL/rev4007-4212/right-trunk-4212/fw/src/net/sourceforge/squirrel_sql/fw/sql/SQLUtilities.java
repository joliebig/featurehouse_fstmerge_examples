
package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SQLUtilities
{

	
	private final static ILogger s_log = LoggerController.createLogger(SQLUtilities.class);

	
	public static String quoteIdentifier(String s)
	{
		if (s == null)
		{
			return null;
		}
		StringBuilder buff = null;
		buff = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '"' && i != 0 && i != s.length() - 1)
			{
				buff.append(c);
			}
			buff.append(c);
		}
		String result = buff.toString();
		return result;
	}

	
	public static List<ITableInfo> getDeletionOrder(List<ITableInfo> tables, SQLDatabaseMetaData md,
		ProgressCallBack callback) throws SQLException
	{
		List<ITableInfo> insertionOrder = getInsertionOrder(tables, md, callback);
		Collections.reverse(insertionOrder);
		return insertionOrder;
	}

	
	public static List<ITableInfo> getInsertionOrder(List<ITableInfo> tables, SQLDatabaseMetaData md,
		ProgressCallBack callback) throws SQLException
	{
		List<ITableInfo> result = new ArrayList<ITableInfo>();
		
		List<ITableInfo> unattached = new ArrayList<ITableInfo>();
		
		List<ITableInfo> children = new ArrayList<ITableInfo>();
		
		List<ITableInfo> parents = new ArrayList<ITableInfo>();
		
		List<ITableInfo> sandwiches = new ArrayList<ITableInfo>();
		ITableInfo lastTable = null;
		try
		{
			for (ITableInfo table : tables)
			{
				lastTable = table;
				callback.currentlyLoading(table.getSimpleName());
				ForeignKeyInfo[] importedKeys = getImportedKeys(table, md);
				ForeignKeyInfo[] exportedKeys = getExportedKeys(table, md);

				if (importedKeys != null && importedKeys.length == 0 && exportedKeys.length == 0)
				{
					unattached.add(table);
					continue;
				}
				if (exportedKeys != null && exportedKeys.length > 0)
				{
					if (importedKeys != null && importedKeys.length > 0)
					{
						sandwiches.add(table);
					} else
					{
						parents.add(table);
					}
					continue;
				}
				if (importedKeys != null && importedKeys.length > 0)
				{
					children.add(table);
				}
			}
			reorderTables(sandwiches);

			for (ITableInfo info : unattached)
			{
				result.add(info);
			}
			for (ITableInfo info : parents)
			{
				result.add(info);
			}
			for (ITableInfo info : sandwiches)
			{
				result.add(info);
			}
			for (ITableInfo info : children)
			{
				result.add(info);
			}
			if (result.size() != tables.size())
			{
				s_log.error("getInsertionOrder(): failed to obtain a result table list " + "(" + result.size()
					+ ") that is the same size as the input table " + "list (" + tables.size()
					+ ") - returning the original unordered " + "list");
				result = tables;
			}
		} catch (Exception e)
		{
			if (lastTable != null)
			{
				String tablename = lastTable.getSimpleName();
				s_log.error("Unexpected exception while getting foreign key info for " + "table " + tablename, e);
			} else
			{
				s_log.error("Unexpected exception while getting foreign key info ", e);
			}
			result = tables;
		}
		return result;
	}

	public static ForeignKeyInfo[] getImportedKeys(ITableInfo ti, SQLDatabaseMetaData md) throws SQLException
	{
		ForeignKeyInfo[] result = ti.getImportedKeys();
		if (result == null)
		{
			result = md.getImportedKeysInfo(ti);
			
			ti.setImportedKeys(result);
		}
		return result;
	}

	
	public static ForeignKeyInfo[] getExportedKeys(ITableInfo ti, SQLDatabaseMetaData md) throws SQLException
	{
		ForeignKeyInfo[] result = ti.getExportedKeys();
		if (result == null)
		{
			result = md.getExportedKeysInfo(ti);
			
			ti.setExportedKeys(result);
		}
		return result;
	}

	
	private static void reorderTables(List<ITableInfo> sandwiches)
	{
		Collections.sort(sandwiches, new TableComparator());
	}

	
	public static List<String> getExtFKParents(SQLDatabaseMetaData md, List<ITableInfo> tables)
		throws SQLException
	{
		List<String> result = new ArrayList<String>();
		HashSet<String> tableNames = new HashSet<String>();

		for (ITableInfo table : tables)
		{
			tableNames.add(table.getSimpleName());
		}

		for (ITableInfo table : tables)
		{
			ForeignKeyInfo[] importedKeys = md.getImportedKeysInfo(table);
			for (int i = 0; i < importedKeys.length; i++)
			{
				ForeignKeyInfo info = importedKeys[i];
				String pkTable = info.getPrimaryKeyTableName();
				if (!tableNames.contains(pkTable))
				{
					result.add(pkTable);
				}
			}
		}
		return result;
	}

	
	public static List<String> getExtFKChildren(SQLDatabaseMetaData md, List<ITableInfo> tables)
		throws SQLException
	{
		List<String> result = new ArrayList<String>();
		HashSet<String> tableNames = new HashSet<String>();

		for (ITableInfo table : tables)
		{
			tableNames.add(table.getSimpleName());
		}

		for (ITableInfo table : tables)
		{
			ForeignKeyInfo[] exportedKeys = md.getExportedKeysInfo(table);
			for (int i = 0; i < exportedKeys.length; i++)
			{
				ForeignKeyInfo info = exportedKeys[i];
				String fkTable = info.getForeignKeyTableName();
				if (!tableNames.contains(fkTable))
				{
					result.add(fkTable);
				}
			}
		}
		return result;
	}

	
	public static void closeResultSet(ResultSet rs)
	{
		closeResultSet(rs, false);
	}

	
	public static void closeResultSet(ResultSet rs, boolean closeStatement)
	{
		if (rs == null)
		{
			return;
		}
		
		try
		{
			rs.close();
		} catch (SQLException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("Unexpected exception while closing ResultSet: " + e.getMessage(), e);
			}
		}
		if (closeStatement)
		{
			
			

			try
			{
				Statement stmt = rs.getStatement();
				if (stmt != null)
				{
					stmt.close();
				}
			} catch (SQLException e)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Unexpected exception while closing " + "Statement: " + e.getMessage(), e);
				}
			}
		}
	}

	
	public static void closeStatement(Statement stmt)
	{
		if (stmt == null)
		{
			return;
		}
		try
		{
			stmt.close();
		} catch (SQLException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.error("Unexpected exception while closing Statement: " + e.getMessage(), e);
			}
		}
	}

	
	private static class TableComparator implements Comparator<ITableInfo>, Serializable
	{

		private static final long serialVersionUID = 1L;

		public int compare(ITableInfo t1, ITableInfo t2)
		{
			ForeignKeyInfo[] t1ImportedKeys = t1.getImportedKeys();
			for (int i = 0; i < t1ImportedKeys.length; i++)
			{
				ForeignKeyInfo info = t1ImportedKeys[i];
				if (info.getPrimaryKeyTableName().equals(t2.getSimpleName()))
				{
					
					return 1;
				}
			}
			ForeignKeyInfo[] t2ImportedKeys = t2.getImportedKeys();
			for (int i = 0; i < t2ImportedKeys.length; i++)
			{
				ForeignKeyInfo info = t2ImportedKeys[i];
				if (info.getPrimaryKeyTableName().equals(t1.getSimpleName()))
				{
					
					return -1;
				}
			}
			if (t1.getImportedKeys().length > t2ImportedKeys.length)
			{
				return 1;
			}
			if (t1.getImportedKeys().length < t2ImportedKeys.length)
			{
				return -1;
			}
			return 0;
		}

	}

}
