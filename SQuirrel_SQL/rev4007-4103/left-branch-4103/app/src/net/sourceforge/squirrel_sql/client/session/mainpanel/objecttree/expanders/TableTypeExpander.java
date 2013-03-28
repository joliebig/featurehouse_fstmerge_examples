package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class TableTypeExpander implements INodeExpander
{
	
	private static ILogger s_log =
		LoggerController.createLogger(TableTypeExpander.class);

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		Statement stmt = null;
		try
		{
			final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
			final ISQLConnection conn = session.getSQLConnection();
			final String catalogName = parentDbinfo.getCatalogName();
			final String schemaName = parentDbinfo.getSchemaName();
         final String tableType = parentDbinfo.getSimpleName();


         final String objFilter = session.getProperties().getObjectFilter();
         String tableNamePattern = objFilter != null && objFilter.length() > 0 ? objFilter : "%";
         String[] types = tableType != null ? new String[]{tableType} : null;
         session.getSchemaInfo().waitTillTablesLoaded();
         final ITableInfo[] tables = session.getSchemaInfo().getITableInfos(catalogName, schemaName, tableNamePattern, types);

         if (session.getProperties().getShowRowCount())
         {
            stmt = conn.createStatement();
         }

         for (int i = 0; i < tables.length; ++i)
			{
				ObjectTreeNode child = new ObjectTreeNode(session, tables[i]);
				child.setUserObject(getNodeDisplayText(stmt, tables[i]));
				childNodes.add(child);
			}
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (SQLException ex)
				{
					s_log.error("Error closing Statement", ex);
				}
			}
		}

		return childNodes;
	}

	private String getNodeDisplayText(Statement rowCountStmt, IDatabaseObjectInfo dbinfo)
	{
		if (rowCountStmt != null)
		{
			try
			{
				ResultSet rs = rowCountStmt.executeQuery("select count(*) from "
										+ dbinfo.getQualifiedName());
				try
				{
					long nbrRows = 0;
					if (rs.next())
					{
						nbrRows = rs.getLong(1);
					}
					StringBuffer buf = new StringBuffer(dbinfo.getSimpleName());
					buf.append(" (")
						.append(nbrRows)
						.append(")");
					return buf.toString();
				}
				finally
				{
					rs.close();
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Error retrieving row count for: " + dbinfo.getQualifiedName(), ex);
				return dbinfo.getSimpleName();
			}
		}
		else
		{
			return dbinfo.getSimpleName();
		}
	}

}
