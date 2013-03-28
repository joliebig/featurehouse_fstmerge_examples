package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class TableExpander implements INodeExpander
{
	
	public TableExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String schemaName = parentDbinfo.getSchemaName();

		IDatabaseObjectInfo dbinfo = new TriggerParentInfo(parentDbinfo,
											schemaName, md);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		childNodes.add(child);

		return childNodes;
	}
}
