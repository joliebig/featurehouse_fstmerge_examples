package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;


public class SequenceParentExpander implements INodeExpander
{
	
	private static final String SQL =
		"select sequence_name"
			+ " from all_sequences"
			+ " where sequence_owner = ?"
			+ " and sequence_name like ?";

	
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
		try
		{
			pstmt.setString(1, schemaName);
			pstmt.setString(2, objFilter != null && objFilter.length() > 0 ? objFilter :"%"); 
			ResultSet rs = pstmt.executeQuery();
			try
			{
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
				rs.close();
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}
}
