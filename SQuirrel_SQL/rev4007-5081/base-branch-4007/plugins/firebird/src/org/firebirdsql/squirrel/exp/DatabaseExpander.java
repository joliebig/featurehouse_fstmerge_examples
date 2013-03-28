package org.firebirdsql.squirrel.exp;

import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.firebirdsql.squirrel.IObjectTypes;
import org.firebirdsql.squirrel.util.IndexParentInfo;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class DatabaseExpander implements INodeExpander {
    



    private FirebirdPlugin _plugin;
    
    public DatabaseExpander(FirebirdPlugin plugin) {
        this._plugin = plugin;
    }
    
    
    public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
    {
        final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
        final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

        final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
        final String catalogName = parentDbinfo.getCatalogName();
        final String schemaName = parentDbinfo.getSimpleName();
        ObjectTreeNode node;
        IDatabaseObjectInfo seqInfo = new DatabaseObjectInfo(catalogName,
                schemaName, "GENERATORS", IObjectTypes.GENERATOR_PARENT, md);
        node = new ObjectTreeNode(session, seqInfo);
        node.addExpander(new GeneratorParentExpander(_plugin));
        childNodes.add(node);
        
        seqInfo = new DatabaseObjectInfo(catalogName,
                schemaName, "DOMAINS", IObjectTypes.DOMAIN_PARENT, md);
        node = new ObjectTreeNode(session, seqInfo);
        node.addExpander(new DomainParentExpander(_plugin));
        childNodes.add(node);

        seqInfo = new IndexParentInfo(null, md);
        node = new ObjectTreeNode(session, seqInfo);
        childNodes.add(node);

        return childNodes;
    }

}
