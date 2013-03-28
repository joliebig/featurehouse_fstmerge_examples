package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;

public class PackageParentExpander implements INodeExpander
{
	
	PackageParentExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String schemaName = parentDbinfo.getSchemaName();

		
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(null, schemaName,
												"", IObjectTypes.PACKAGE, md);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.setUserObject("Standalone");
		childNodes.add(child);

		
		ObjectType objType = new ObjectType(IObjectTypes.PACKAGE, "PACKAGE",
												IObjectTypes.PACKAGE);
		INodeExpander exp = new ObjectTypeExpander(objType);
		childNodes.addAll(exp.createChildren(session, parentNode));

		return childNodes;
	}
}
