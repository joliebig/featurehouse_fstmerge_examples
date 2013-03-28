package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo.IndexType;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo.SortOrder;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class IndexParentExpander implements INodeExpander
{
	
	private static final ILogger s_log = LoggerController.createLogger(IndexParentExpander.class);

	
	private ITableIndexExtractor extractor = null;

	
	public IndexParentExpander() {
		super();
	}

	
	public void setTableIndexExtractor(ITableIndexExtractor extractor)
	{
		this.extractor = extractor;
	}

	
	public List<ObjectTreeNode> createChildren(ISession session, ObjectTreeNode parentNode)
	{
		final List<ObjectTreeNode> childNodes = new ArrayList<ObjectTreeNode>();
		final IDatabaseObjectInfo parentDbinfo = parentNode.getDatabaseObjectInfo();
		final IDatabaseObjectInfo tableInfo = ((IndexParentInfo) parentDbinfo).getTableInfo();

		final ISQLConnection conn = session.getSQLConnection();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			String query = extractor.getTableIndexQuery();
			if (s_log.isDebugEnabled())
			{
				s_log.debug("Running query for index extraction: " + query);
			}
			pstmt = conn.prepareStatement(query);
			extractor.bindParamters(pstmt, tableInfo);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				String indexName = rs.getString(1);
				String cat = parentDbinfo.getCatalogName();
				String schema = parentDbinfo.getSchemaName();
				String tableName = tableInfo.getSimpleName();
				
				
				
				String columnName = null;
				boolean nonUnique = true;
				String indexQualifier = null;
				IndexType indexType = null;
				short ordinalPosition = 0;
				SortOrder sortOrder = null;
				int cardinality = 0;
				int pages = 0;
				String filterCondition = null;
				IndexInfo doi = new IndexInfo(
				   cat, schema, indexName, tableName, columnName, nonUnique, indexQualifier, indexType,
				   ordinalPosition, sortOrder, cardinality, pages, filterCondition, md);
				childNodes.add(new ObjectTreeNode(session, doi));
			}
		} catch (SQLException e)
		{
			session.showErrorMessage(e);
			s_log.error(
			   "Unexpected exception while extracting indexes for " + "parent dbinfo: " + parentDbinfo, e);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}

		return childNodes;
	}
}
