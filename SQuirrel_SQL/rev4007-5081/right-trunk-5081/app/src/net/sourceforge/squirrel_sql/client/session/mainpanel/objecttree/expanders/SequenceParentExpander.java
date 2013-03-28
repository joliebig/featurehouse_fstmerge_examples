package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;


public class SequenceParentExpander implements INodeExpander
{

	
	private ISequenceParentExtractor extractor = null;

	
	public SequenceParentExpander()
	{
		super();
	}

	
	public void setExtractor(ISequenceParentExtractor extractor)
	{
		this.extractor = extractor;
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
		final ObjFilterMatcher filterMatcher = new ObjFilterMatcher(session.getProperties());

		final PreparedStatement pstmt = conn.prepareStatement(extractor.getSequenceParentQuery());
		ResultSet rs = null;
		try
		{
			extractor.bindParameters(pstmt, parentDbinfo, filterMatcher);

			rs = pstmt.executeQuery();
			while (rs.next())
			{
				final IDatabaseObjectInfo si =
					new DatabaseObjectInfo(catalogName, schemaName, rs.getString(1), DatabaseObjectType.SEQUENCE,
						md);
				if (filterMatcher.matches(si.getSimpleName()))
				{
					childNodes.add(new ObjectTreeNode(session, si));
				}
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
