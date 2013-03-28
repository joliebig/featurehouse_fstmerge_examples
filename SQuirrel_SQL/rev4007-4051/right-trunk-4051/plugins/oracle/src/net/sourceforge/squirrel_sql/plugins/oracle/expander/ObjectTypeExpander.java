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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class ObjectTypeExpander implements INodeExpander
{
	
	private static String SQL =
		"select object_name from sys.all_objects where object_type = ?" +
		" and owner = ? and object_name like ? order by object_name";

	
	private ObjectType _objectType;

	
	ObjectTypeExpander(ObjectType objectType)
	{
		super();
		if (objectType == null)
		{
			throw new IllegalArgumentException("ObjectType == null");
		}
		_objectType = objectType;
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
		throws SQLException
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final String catalogName = parentDbinfo.getCatalogName();
		final String schemaName = parentDbinfo.getSchemaName();
		childNodes.addAll(createNodes(session, catalogName, schemaName));
		return childNodes;
	}

	private List<ObjectTreeNode> createNodes(ISession session, String catalogName,
											String schemaName)
		throws SQLException
	{
		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = conn.getSQLMetaData();
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		String objFilter =  session.getProperties().getObjectFilter();

		
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		try
		{	
			pstmt.setString(1, _objectType._objectTypeColumnData);
			pstmt.setString(2, schemaName);
			pstmt.setString(3, objFilter != null && objFilter.length() > 0 ? objFilter :"%");
			ResultSet rs = pstmt.executeQuery();
			try
			{
				while (rs.next())
				{
					IDatabaseObjectInfo dbinfo = new DatabaseObjectInfo(catalogName,
											schemaName, rs.getString(1),
											_objectType._childDboType, md);
					childNodes.add(new ObjectTreeNode(session, dbinfo));
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
