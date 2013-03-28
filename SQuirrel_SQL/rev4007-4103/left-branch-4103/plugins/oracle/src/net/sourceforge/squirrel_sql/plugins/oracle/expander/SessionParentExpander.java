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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;

public class SessionParentExpander implements INodeExpander
{

	
	private static String SQL =
		"select sid||' ('||schemaname||')' from sys.v_$session";

	
	public SessionParentExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String schemaName = parentDbinfo.getSchemaName();

		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{
			ResultSet rs = pstmt.executeQuery();
			try
			{
				while (rs.next())
				{
					IDatabaseObjectInfo doi = new DatabaseObjectInfo(null,
													schemaName, rs.getString(1),
													IObjectTypes.SESSION, md);
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
