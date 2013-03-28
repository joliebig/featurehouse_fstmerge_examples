package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

public class CatalogsTab extends BaseDataSetTab
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CatalogsTab.class);
                
	
	public String getTitle()
	{
        
		return s_stringMgr.getString("CatalogsTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("CatalogsTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		try
		{
			final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
			return new ObjectArrayDataSet(md.getCatalogs());
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
