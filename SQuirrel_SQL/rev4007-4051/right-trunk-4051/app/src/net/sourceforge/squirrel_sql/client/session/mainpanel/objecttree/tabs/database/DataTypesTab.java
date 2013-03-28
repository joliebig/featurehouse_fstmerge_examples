package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DataTypesTab extends BaseDataSetTab
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DataTypesTab.class);
                
	
	public String getTitle()
	{
        
		return s_stringMgr.getString("DataTypesTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("DataTypesTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		
		
		final ISession session = getSession();
        return session.getSQLConnection().getSQLMetaData().getTypesDataSet();
	}
}
