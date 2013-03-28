package org.firebirdsql.squirrel.tab;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

import org.firebirdsql.squirrel.util.IndexInfo;
import org.firebirdsql.squirrel.util.SystemTables;


public class IndexInfoTab extends BaseDataSetTab
{
	
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(IndexInfoTab.class);

	
	private String SQL = "SELECT " + SystemTables.IIndexTable.COL_NAME + ","
	      + SystemTables.IIndexTable.COL_DESCRIPTION + "," + SystemTables.IIndexTable.COL_ID + ","
	      + SystemTables.IIndexTable.COL_RELATION_NAME + "," + SystemTables.IIndexTable.COL_UNIQUE + ","
	      + SystemTables.IIndexTable.COL_SEGMENT_COUNT + "," + SystemTables.IIndexTable.COL_INACTIVE + ","
	      + SystemTables.IIndexTable.COL_SYSTEM + "," + SystemTables.IIndexTable.COL_FOREIGN_KEY + ","
	      + SystemTables.IIndexTable.COL_EXPRESSION_SOURCE + " FROM " + SystemTables.IIndexTable.TABLE_NAME
	      + " WHERE " + SystemTables.IIndexTable.COL_NAME + " = ?";

	
	public String getTitle()
	{
		return s_stringMgr.getString("IndexInfoTab.title");
	}

	
	public String getHint()
	{
		return s_stringMgr.getString("IndexInfoTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		return new JavabeanDataSet(createIndexInfo());
	}

	private IndexInfo createIndexInfo() throws DataSetException
	{
		final ISession session = getSession();
		final ISQLConnection conn = session.getSQLConnection();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			final IDatabaseObjectInfo doi = getDatabaseObjectInfo();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, doi.getSimpleName());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				return new IndexInfo(
				   rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6),
				   rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getString(10));
			}
			String msg = s_stringMgr.getString("IndexInfoTab.err.noindex", doi.getSimpleName());
			throw new DataSetException(msg);
		} catch (SQLException ex)
		{
			throw new DataSetException(ex);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
	}
}
