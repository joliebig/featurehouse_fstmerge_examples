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
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;


public class DomainParentExpander implements INodeExpander
{

	
	private static final String SQL = "select cast(rdb$field_name as varchar(31)) as rdb$domain_name"
	      + " from rdb$fields where not (rdb$field_name like 'RDB$%')";

	
	@SuppressWarnings("unused")
	private static final ILogger s_log = LoggerController.createLogger(DomainParentExpander.class);

	
	@SuppressWarnings("unused")
	private final FirebirdPlugin _plugin;

	
	DomainParentExpander(FirebirdPlugin plugin) {
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("FirebirdPlugin == null");
		}

		_plugin = plugin;
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

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				IDatabaseObjectInfo si = new DatabaseObjectInfo(
				   catalogName, schemaName, rs.getString(1), DatabaseObjectType.DATATYPE, md);
				childNodes.add(new ObjectTreeNode(session, si));
			}
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return childNodes;
	}
}