package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;


import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpanderFactory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;


public class SchemaExpander implements INodeExpander
{

	INodeExpanderFactory _inodeFactory = null;

	DatabaseObjectType _dbObjType = null;

	
	public SchemaExpander(INodeExpanderFactory inodeExpFactory, DatabaseObjectType dbObjType)
	{
		super();
		this._inodeFactory = inodeExpFactory;
		this._dbObjType = dbObjType;
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

		IDatabaseObjectInfo seqInfo =
			new DatabaseObjectInfo(catalogName, schemaName, _inodeFactory.getParentLabelForType(_dbObjType),
				_dbObjType, md);
		ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
		node.addExpander(_inodeFactory.createExpander(_dbObjType));
		childNodes.add(node);

		return childNodes;
	}

}
