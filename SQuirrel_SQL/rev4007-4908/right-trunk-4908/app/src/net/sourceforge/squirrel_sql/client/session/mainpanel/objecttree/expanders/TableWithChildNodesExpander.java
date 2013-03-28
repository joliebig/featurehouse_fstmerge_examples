package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;


public class TableWithChildNodesExpander implements INodeExpander {

     
    private ITableTriggerExtractor triggerExtractor = null;
    
    
    private ITableIndexExtractor indexExtractor = null;
    
    
    public TableWithChildNodesExpander() {
        super();
    }

    
    public void setTableTriggerExtractor(ITableTriggerExtractor extractor) {
        this.triggerExtractor = extractor;
    }    

    
    public void setTableIndexExtractor(ITableIndexExtractor extractor) {
        this.indexExtractor = extractor;
    }        
    
    
    public List<ObjectTreeNode> createChildren(ISession session, 
                                               ObjectTreeNode parentNode)
        throws SQLException 
    {
        final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
        final IDatabaseObjectInfo parentDbinfo = parentNode
                .getDatabaseObjectInfo();
        final SQLDatabaseMetaData md = session.getSQLConnection()
                .getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();

        if (triggerExtractor != null) {
            IDatabaseObjectInfo triggerParentInfo = 
                new TriggerParentInfo(parentDbinfo, schemaName, md);
            ObjectTreeNode triggerChild = 
                new ObjectTreeNode(session, triggerParentInfo);
            TriggerParentExpander expander = new TriggerParentExpander();
            expander.setTableTriggerExtractor(triggerExtractor);
            triggerChild.addExpander(expander);
            childNodes.add(triggerChild);
        }
        if (indexExtractor != null) {
            IDatabaseObjectInfo triggerParentInfo = 
                new IndexParentInfo(parentDbinfo, schemaName, md);
            ObjectTreeNode triggerChild = 
                new ObjectTreeNode(session, triggerParentInfo);
            IndexParentExpander expander = new IndexParentExpander();
            expander.setTableIndexExtractor(indexExtractor);
            triggerChild.addExpander(expander);
            childNodes.add(triggerChild);            
        }
        return childNodes;
    }
}