package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ImportedKeysTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ImportedKeysTab.class);
	
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("ImportedKeysTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("ImportedKeysTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
        return conn.getSQLMetaData().getImportedKeysDataSet(getTableInfo());
	}
}
