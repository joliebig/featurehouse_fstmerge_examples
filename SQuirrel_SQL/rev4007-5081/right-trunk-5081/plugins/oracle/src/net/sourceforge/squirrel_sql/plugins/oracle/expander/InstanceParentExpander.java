package net.sourceforge.squirrel_sql.plugins.oracle.expander;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;


public class InstanceParentExpander implements INodeExpander
{
	
	private static String SQL = "select instance_number, instance_name, host_name, version,"
	      + " startup_time, status, parallel, thread#, archiver, log_switch_wait,"
	      + " logins, shutdown_pending, database_status, instance_role" + " from sys.v_$instance";

	
	public InstanceParentExpander() {
		super();
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	      throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo doi = new DatabaseObjectInfo(
				   null, null, rs.getString(1), IObjectTypes.INSTANCE, md);
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				childNodes.add(new ObjectTreeNode(session, doi));
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return childNodes;
	}
}
