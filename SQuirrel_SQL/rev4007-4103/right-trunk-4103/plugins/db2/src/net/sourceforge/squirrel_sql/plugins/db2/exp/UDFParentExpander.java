package net.sourceforge.squirrel_sql.plugins.db2.exp;

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
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class UDFParentExpander implements INodeExpander
{
	
	private static final String SQL =
	    "SELECT name " +
	    "FROM SYSIBM.SYSFUNCTIONS " +
	    "WHERE schema = ? " +
	    "AND name like ? " +
	    "AND implementation is null";
	
	
	private static final String OS_400_SQL = 
	    "select routine_name " +
	    "from QSYS2.SYSFUNCS " +
	    "where routine_schema = ? " +
	    "and routine_name like ? ";	    
	
	
	private boolean isOS400 = false;
	
	
	public UDFParentExpander(boolean isOS400)
	{
		super();
		this.isOS400 = isOS400;
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


		String sql = SQL;
		if (isOS400) {
		    sql = OS_400_SQL;
		}
		final PreparedStatement pstmt = conn.prepareStatement(sql);
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
												DatabaseObjectType.UDF, md);
					childNodes.add(new ObjectTreeNode(session, si));
				}
		}
		finally
		{
		    SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(pstmt);
		}
		return childNodes;
	}
}
