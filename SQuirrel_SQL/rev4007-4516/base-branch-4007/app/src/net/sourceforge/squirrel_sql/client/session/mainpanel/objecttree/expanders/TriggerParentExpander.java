package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class TriggerParentExpander implements INodeExpander {

    
    private static final ILogger s_log = LoggerController
            .createLogger(TriggerParentExpander.class);
    
    private ITableTriggerExtractor triggerExtractor = null;
    
    
    public TriggerParentExpander() {
        super();
    }

    
    public void setTableTriggerExtractor(ITableTriggerExtractor extractor) {
        this.triggerExtractor = extractor;
    }
    
    
    public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
            throws SQLException {
        final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
        final IDatabaseObjectInfo parentDbinfo = parentNode
                .getDatabaseObjectInfo();
        final ISQLConnection conn = session.getSQLConnection();
        final SQLDatabaseMetaData md = 
            session.getSQLConnection().getSQLMetaData();
        final String schemaName = parentDbinfo.getSchemaName();
        final String catalogName = parentDbinfo.getCatalogName();
        final IDatabaseObjectInfo tableInfo = ((TriggerParentInfo) parentDbinfo)
                .getTableInfo();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String tableName = tableInfo.getSimpleName();
            String query = triggerExtractor.getTableTriggerQuery();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Getting triggers for table "+tableName+
                            " in schema "+ schemaName + " and catalog " +
                            catalogName + " - Running query: " + query);
            }
            pstmt = conn.prepareStatement(query);
            triggerExtractor.bindParamters(pstmt, tableInfo);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DatabaseObjectInfo doi = 
                    new DatabaseObjectInfo(catalogName, 
                                           schemaName, 
                                           rs.getString(1),
                                           DatabaseObjectType.TRIGGER, md);
                childNodes.add(new ObjectTreeNode(session, doi));
            }
        } catch (SQLException e) {
            session.showErrorMessage(e);
            s_log.error("Unexpected exception while extracting triggers for " +
                        "parent dbinfo: "+parentDbinfo, e);            
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e){}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e){}
        }

        return childNodes;
    }

}