package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class UDTTypeExpander implements INodeExpander
{
	
	public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();

      ObjFilterMatcher filterMatcher = new ObjFilterMatcher(session.getProperties());

      IUDTInfo[] udts =
         conn.getSQLMetaData().getUDTs(catalogName, schemaName, filterMatcher.getSqlLikeMatchString(), null);

		for (int i = 0; i < udts.length; ++i)
		{
         if(filterMatcher.matches(udts[i].getSimpleName()))
         {
            ObjectTreeNode child = new ObjectTreeNode(session, udts[i]);
            childNodes.add(child);
         }
      }

		return childNodes;
	}
}
