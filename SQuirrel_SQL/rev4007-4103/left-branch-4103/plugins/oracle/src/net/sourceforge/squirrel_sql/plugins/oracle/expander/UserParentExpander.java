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
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;


public class UserParentExpander implements INodeExpander
{
	
	private static final String SQL_ADMIN = "select username from dba_users order by username";
	
	private static final String SQL_USER = "select username from user_users order by username";

	
	final protected boolean isAdmin;

	public UserParentExpander(final ISession session) {
		isAdmin=OraclePlugin.checkObjectAccessible(session, SQL_ADMIN);
	}
	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String schemaName = parentDbinfo.getSchemaName();

		final PreparedStatement pstmt = conn.prepareStatement(isAdmin?SQL_ADMIN:SQL_USER);
		try
		{
			ResultSet rs = pstmt.executeQuery();
			try
			{
				while (rs.next())
				{
					IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, schemaName,
												rs.getString(1), DatabaseObjectType.USER, md);
					childNodes.add(new ObjectTreeNode(session, doi));
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
