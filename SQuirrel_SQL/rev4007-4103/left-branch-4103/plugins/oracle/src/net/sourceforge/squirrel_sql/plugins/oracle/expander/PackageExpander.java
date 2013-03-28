package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

public class PackageExpander implements INodeExpander
{
	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String schemaName = parentDbinfo.getSchemaName();
		final String packageName = parentDbinfo.getSimpleName();
		return createProcedureNodes(session, packageName, schemaName);
	}

	private List<ObjectTreeNode> createProcedureNodes(ISession session, String catalogName,
										String schemaName)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		IProcedureInfo[] procs = null;
		final String objFilter = session.getProperties().getObjectFilter();
      
      String procNamePattern = objFilter != null && objFilter.length() > 0 ? objFilter : "%";
      procs = session.getSchemaInfo().getStoredProceduresInfos(catalogName, schemaName, procNamePattern);
		for (int i = 0; i < procs.length; ++i)
		{
			childNodes.add(new ObjectTreeNode(session, procs[i]));
		}
		return childNodes;
	}
}
