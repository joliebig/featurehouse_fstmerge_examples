package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TablePriviligesTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(TablePriviligesTab.class);
        		
    private int[] columnIndices = new int[] { 5, 6, 7, 4 };
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("TablePriviligesTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("TablePriviligesTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final SQLDatabaseMetaData md = 
            getSession().getSQLConnection().getSQLMetaData();
        ITableInfo ti = getTableInfo();
        return md.getTablePrivilegesDataSet(ti, columnIndices, true);
	}
}
