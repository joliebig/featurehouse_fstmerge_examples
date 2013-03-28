package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class PrimaryKeyTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PrimaryKeyTab.class);
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("PrimaryKeyTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("PrimaryKeyTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
        IDataSet result = null;
        SQLDatabaseMetaData md = conn.getSQLMetaData();
        result = md.getPrimaryKey(getTableInfo(),
                                  new int[] { 6, 5, 4 },
                                  true);
        return result;
	}
}
