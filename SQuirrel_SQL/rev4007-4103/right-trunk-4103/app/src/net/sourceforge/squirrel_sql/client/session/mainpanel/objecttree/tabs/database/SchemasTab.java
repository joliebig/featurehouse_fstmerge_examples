package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ObjectArrayDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;

public class SchemasTab extends BaseDataSetTab
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SchemasTab.class);
    
	
	public String getTitle()
	{
        
		return s_stringMgr.getString("SchemasTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("SchemasTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
      return new ObjectArrayDataSet(getSession().getSchemaInfo().getSchemas());
	}
}
