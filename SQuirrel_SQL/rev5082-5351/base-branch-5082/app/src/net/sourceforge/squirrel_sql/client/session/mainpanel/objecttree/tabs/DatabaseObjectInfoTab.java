package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.JavabeanDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DatabaseObjectInfoTab extends BaseDataSetTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DatabaseObjectInfoTab.class);
	
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("DatabaseObjectInfoTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("DatabaseObjectInfoTab.hint");
	}

	protected IDataSet createDataSet() throws DataSetException
	{
		return new JavabeanDataSet(getDatabaseObjectInfo());
	}
}
