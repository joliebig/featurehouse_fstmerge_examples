package net.sourceforge.squirrel_sql.plugins.db2.exp;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;


public class SchemaExpander implements INodeExpander
{

    
    private boolean isOS400 = false;
    
	
	public SchemaExpander(boolean isOS400)
	{
		super();
		this.isOS400 = isOS400;
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSimpleName();

        IDatabaseObjectInfo seqInfo = 
            new DatabaseObjectInfo(catalogName,
                                   schemaName, 
                                   "SEQUENCE",
                                   DatabaseObjectType.SEQUENCE_TYPE_DBO, 
                                   md);
        ObjectTreeNode node = new ObjectTreeNode(session, seqInfo);
        node.addExpander(new SequenceParentExpander(isOS400));
        childNodes.add(node);

        IDatabaseObjectInfo udfInfo = 
            new DatabaseObjectInfo(catalogName,
                                   schemaName, 
                                   "UDF",
                                   DatabaseObjectType.UDF_TYPE_DBO, 
                                   md);
        ObjectTreeNode udfnode = new ObjectTreeNode(session, udfInfo);
        udfnode.addExpander(new UDFParentExpander(isOS400));
        childNodes.add(udfnode);
        
        
		return childNodes;
	}

}
