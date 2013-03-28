package net.sourceforge.squirrel_sql.plugins.mysql.expander;

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

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

public class UserParentExpander implements INodeExpander
{
	
	private static final String SQL = "select concat(user, '@', host) from mysql.user";

	



	
	@SuppressWarnings("unused")
    private final MysqlPlugin _plugin;

	
	public UserParentExpander(MysqlPlugin plugin)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}

		_plugin = plugin;
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String schemaName = parentDbinfo.getSchemaName();

		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo doi = new DatabaseObjectInfo(null, schemaName,
											rs.getString(1), DatabaseObjectType.USER, md);
				childNodes.add(new ObjectTreeNode(session, doi));
			}
		}
		finally
		{
			pstmt.close();
		}
		return childNodes;
	}
}
