package net.sourceforge.squirrel_sql.plugins.h2.exp;

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

public class SequenceParentExpander implements INodeExpander
{
	
	private static final String SQL =
        "select SEQUENCE_NAME " +
        "from INFORMATION_SCHEMA.SEQUENCES " +
        "where SEQUENCE_SCHEMA = ? " +
        "and SEQUENCE_NAME like ? ";
    
	
	public SequenceParentExpander()
	{
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();
		final String objFilter =  session.getProperties().getObjectFilter();


		final PreparedStatement pstmt = conn.prepareStatement(SQL);
        ResultSet rs = null;
		try
		{
			pstmt.setString(1, schemaName);
			pstmt.setString(2, objFilter != null && objFilter.length() > 0 ? objFilter :"%"); 
			rs = pstmt.executeQuery();
				while (rs.next())
				{
					IDatabaseObjectInfo si = new DatabaseObjectInfo(catalogName,
												schemaName, rs.getString(1),
												DatabaseObjectType.SEQUENCE, md);
					childNodes.add(new ObjectTreeNode(session, si));
				}
		}
		finally
		{
            if (rs != null) {
                try {rs.close();} catch (SQLException e) {}
            }
            if (pstmt != null) {
                try {pstmt.close();} catch (SQLException e) {}
            }
		}
		return childNodes;
	}
}
