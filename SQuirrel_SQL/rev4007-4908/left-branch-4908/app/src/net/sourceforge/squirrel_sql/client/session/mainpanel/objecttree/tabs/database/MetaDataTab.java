package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.MetaDataDecoratorDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class MetaDataTab extends BaseDataSetTab
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(MetaDataTab.class);  

	
	public String getTitle()
	{
        
		return s_stringMgr.getString("MetaDataTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("MetaDataTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
		try
		{
            DatabaseMetaData md = conn.getSQLMetaData().getJDBCMetaData();
			return new MetaDataDecoratorDataSet(md, getSession().getDriver().getDriverClassName(), getSession().getDriver().getJarFileNames());
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
