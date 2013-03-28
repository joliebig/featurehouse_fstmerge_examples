package net.sourceforge.squirrel_sql.plugins.mysql.expander;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.mysql.IObjectTypes;

public class SessionExpander implements INodeExpander
{
	



	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

		
		DatabaseObjectInfo dboInfo = new DatabaseObjectInfo(null, null, "USERS",
											IObjectTypes.USER_PARENT, md);
		ObjectTreeNode node = new ObjectTreeNode(session, dboInfo);
		childNodes.add(node);

		return childNodes;
	}
}
