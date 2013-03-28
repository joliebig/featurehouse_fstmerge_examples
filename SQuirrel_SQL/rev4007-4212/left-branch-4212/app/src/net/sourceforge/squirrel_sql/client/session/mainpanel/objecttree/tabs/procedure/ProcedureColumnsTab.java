package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ProcedureColumnsTab extends BaseProcedureTab
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ProcedureColumnsTab.class);
	
	
	public String getTitle()
	{
		
		return s_stringMgr.getString("ProcedureColumnsTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("ProcedureColumnsTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISQLConnection conn = getSession().getSQLConnection();
        return conn.getSQLMetaData().getProcedureColumnsDataSet(getProcedureInfo());
	}
}
