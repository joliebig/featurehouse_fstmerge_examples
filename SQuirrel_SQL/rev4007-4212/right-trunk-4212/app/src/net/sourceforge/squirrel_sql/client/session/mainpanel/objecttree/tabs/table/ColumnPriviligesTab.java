package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ColumnPriviligesTab extends BaseTableTab
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnPriviligesTab.class);	
	
    private int[] columnIndices = new int[] { 4, 6, 7, 5, 8 };
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("ColumnPriviligesTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("ColumnPriviligesTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final SQLDatabaseMetaData md = 
            getSession().getSQLConnection().getSQLMetaData();
        final ITableInfo ti = getTableInfo();
        return md.getColumnPrivilegesDataSet(ti,columnIndices, true);
	}
}
