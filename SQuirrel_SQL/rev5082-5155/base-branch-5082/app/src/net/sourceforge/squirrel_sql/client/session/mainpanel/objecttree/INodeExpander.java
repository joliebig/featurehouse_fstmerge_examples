package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import java.sql.SQLException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface INodeExpander
{
	
	List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode node)
			throws SQLException;
}
