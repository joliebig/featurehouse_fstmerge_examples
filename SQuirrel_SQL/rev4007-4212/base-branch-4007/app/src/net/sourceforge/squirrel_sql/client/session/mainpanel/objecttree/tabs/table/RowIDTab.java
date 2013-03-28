package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter.AdapterFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RowIDTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RowIDTab.class);
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("RowIDTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("RowIDTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		try
		{
			final ISQLConnection conn = getSession().getSQLConnection();
			final SQLDatabaseMetaData md = conn.getSQLMetaData();
			final ITableInfo ti = getTableInfo();
			final BestRowIdentifier[] bris = md.getBestRowIdentifier(ti);
			return new JavabeanArrayDataSet(AdapterFactory.getInstance().createBestRowIdentifierAdapter(bris));
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}



















	}
}
