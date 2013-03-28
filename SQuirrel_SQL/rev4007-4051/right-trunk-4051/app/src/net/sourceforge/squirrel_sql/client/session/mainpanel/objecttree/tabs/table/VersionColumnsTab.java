package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class VersionColumnsTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(VersionColumnsTab.class);
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("VersionColumnsTab.title");
	}

	
	public String getHint()
	{
		
		
		return s_stringMgr.getString("VersionColumnsTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final SQLDatabaseMetaData md = 
            getSession().getSQLConnection().getSQLMetaData();
		return md.getVersionColumnsDataSet(getTableInfo()); 
	}
}
