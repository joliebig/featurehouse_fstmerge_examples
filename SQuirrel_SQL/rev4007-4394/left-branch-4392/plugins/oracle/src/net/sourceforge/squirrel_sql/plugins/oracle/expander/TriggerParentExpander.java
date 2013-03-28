package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class TriggerParentExpander implements INodeExpander
{
	private static String SQL =
		"select owner, trigger_name from sys.all_triggers where table_owner = ?" +
		" and table_name = ?";

	
	public TriggerParentExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final IDatabaseObjectInfo tableInfo = ((TriggerParentInfo)parentDbinfo).getTableInfo();
		final PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{
			pstmt.setString(1, tableInfo.getSchemaName());
			pstmt.setString(2, tableInfo.getSimpleName());
			ResultSet rs = pstmt.executeQuery();
			try
			{
				while (rs.next())
				{
					DatabaseObjectInfo doi = new DatabaseObjectInfo(null,
												rs.getString(1), rs.getString(2),
												DatabaseObjectType.TRIGGER, md);
					childNodes.add(new ObjectTreeNode(session, doi));
				}
			}
			finally
			{
				rs.close();
			}
		}
		finally
		{
			pstmt.close();
		}


		return childNodes;
	}
}
