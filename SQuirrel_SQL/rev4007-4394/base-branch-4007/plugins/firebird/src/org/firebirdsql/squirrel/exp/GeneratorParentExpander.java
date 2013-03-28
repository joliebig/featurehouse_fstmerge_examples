package org.firebirdsql.squirrel.exp;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.squirrel.FirebirdPlugin;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;


public class GeneratorParentExpander implements INodeExpander {

    
    private static final String SQL = "select cast(rdb$generator_name as varchar(31)) as rdb$generator_name"
            + " from rdb$generators where rdb$system_flag is null";

    
    @SuppressWarnings("unused")
    private static final ILogger s_log = LoggerController
            .createLogger(GeneratorParentExpander.class);

    
    @SuppressWarnings("unused")
    private final FirebirdPlugin _plugin;

    
    GeneratorParentExpander(FirebirdPlugin plugin) {
        super();
        if (plugin == null) { throw new IllegalArgumentException(
                "FirebirdPlugin == null"); }

        _plugin = plugin;
    }

    
    public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
            throws SQLException {
        final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
        final IDatabaseObjectInfo parentDbinfo = parentNode
                .getDatabaseObjectInfo();
        final ISQLConnection conn = session.getSQLConnection();
        final SQLDatabaseMetaData md = session.getSQLConnection()
                .getSQLMetaData();
        final String catalogName = parentDbinfo.getCatalogName();
        final String schemaName = parentDbinfo.getSchemaName();

        PreparedStatement pstmt = conn.prepareStatement(SQL);
        try {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                IDatabaseObjectInfo si = new DatabaseObjectInfo(catalogName,
                        schemaName, rs.getString(1),
                        DatabaseObjectType.SEQUENCE, md);
                childNodes.add(new ObjectTreeNode(session, si));
            }
        } finally {
            pstmt.close();
        }
        return childNodes;
    }
}