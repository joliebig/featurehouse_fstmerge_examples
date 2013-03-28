package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class IndexesTab extends BaseTableTab
{
	
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(IndexesTab.class);
	
    private static final int[] indexIndices = 
                                new int[] {5, 6, 8, 9, 10, 4, 7, 11, 12, 13 };
    
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("IndexesTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("IndexesTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
        final SQLDatabaseMetaData dmd = conn.getSQLMetaData();
        
        ITableInfo ti = getTableInfo();
        if (! "TABLE".equalsIgnoreCase(ti.getType())) {
      	  
      	  if (!DialectFactory.isFrontBase(dmd)) {
      		  return null;  
      	  }
        }
        ResultSetDataSet rsds = 
            dmd.getIndexInfo(getTableInfo(), indexIndices, true);
        rsds.next(null);
        String indexName = (String)rsds.get(1);
        if (indexName == null) {
            rsds.removeRow(0);
        }
        rsds.resetCursor();
        return rsds;
	}
	
}
