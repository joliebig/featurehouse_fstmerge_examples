package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

public class TableTypesTab extends BaseDataSetTab
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(TableTypesTab.class);
                
	
	public String getTitle()
	{
        
		return s_stringMgr.getString("TableTypesTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("TableTypesTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		try
		{
			final ISQLConnection conn = session.getSQLConnection();
			final String[] tableTypes = conn.getSQLMetaData().getTableTypes();
			return new ObjectArrayDataSet(tableTypes);
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}
}
