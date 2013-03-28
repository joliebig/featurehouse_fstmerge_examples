package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RowCountTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RowCountTab.class);
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("RowCountTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("RowCountTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final ResultSet rs = stmt.executeQuery("select count(*) from "
												+ getTableInfo().getQualifiedName());
				try
				{
					final ResultSetDataSet rsds = new ResultSetDataSet();
					rsds.setResultSet(rs, getDialectType());
					return rsds;
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
