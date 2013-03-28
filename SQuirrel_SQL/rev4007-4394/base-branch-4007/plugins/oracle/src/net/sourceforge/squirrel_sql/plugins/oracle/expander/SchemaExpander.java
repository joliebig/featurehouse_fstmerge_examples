package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;

public class SchemaExpander implements INodeExpander
{

	
	public SchemaExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
											schemaName, "PACKAGE",
											IObjectTypes.PACKAGE_PARENT, md);
		ObjectTreeNode child = new ObjectTreeNode(session, dbinfo);
		child.addExpander(new PackageParentExpander());
		childNodes.add(child);

		ObjectType objType;
		objType = new ObjectType(IObjectTypes.CONSUMER_GROUP_PARENT, "CONSUMER GROUP",
										IObjectTypes.CONSUMER_GROUP);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.FUNCTION_PARENT, "FUNCTION",
									DatabaseObjectType.FUNCTION);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.INDEX_PARENT, "INDEX", DatabaseObjectType.INDEX);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		objType = new ObjectType(IObjectTypes.LOB_PARENT, "LOB", IObjectTypes.LOB);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName,
										schemaName, "SEQUENCE",
										IObjectTypes.SEQUENCE_PARENT, md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(new SequenceParentExpander());
		childNodes.add(node);

		objType = new ObjectType(IObjectTypes.TYPE_PARENT, "TYPE", IObjectTypes.TYPE);
		childNodes.add(createObjectTypeNode(session, catalogName, schemaName,
											md, objType));

		return childNodes;
	}

	private ObjectTreeNode createObjectTypeNode(ISession session,
										String catalogName, String schemaName,
										SQLDatabaseMetaData md, ObjectType objType)
	{
		IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
										schemaName, objType._objectTypeColumnData,
										objType._dboType, md);
		ObjectTreeNode node = new ObjectTreeNode(session, dbinfo);
		node.addExpander(new ObjectTypeExpander(objType));
		return node;
	}
}
