package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ColumnsTab extends BaseTableTab
{
    private static int[] columnIndices = 
        new int[] { 4, 6, 18, 9, 7, 13, 12, 5, 8, 10, 11, 14, 15, 16, 17 };
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ColumnsTab.class);    
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("ColumnsTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("ColumnsTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
        SQLDatabaseMetaData md = conn.getSQLMetaData();
        return md.getColumns(getTableInfo(), columnIndices, true);
	}
}
