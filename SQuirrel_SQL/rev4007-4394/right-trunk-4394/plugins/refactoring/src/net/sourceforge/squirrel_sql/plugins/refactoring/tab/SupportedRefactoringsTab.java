package net.sourceforge.squirrel_sql.plugins.refactoring.tab;


import java.lang.reflect.Method;
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseDataSetTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SupportedRefactoringsTab extends BaseDataSetTab
{
	
	private static final ILogger s_log = LoggerController.createLogger(SupportedRefactoringsTab.class);
	
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SupportedRefactoringsTab.class);

	
	HashMap<String, String> refactorings = new HashMap<String, String>();

	
	MapDataSet dataSet = null;

	
	private static final String SUPPORTS_RENAME_VIEW_METHOD_NAME = "supportsRenameView";
	
	private static final String SUPPORTS_VIEW_DEF_METHOD_NAME = "supportsViewDefinition";
	
	
	public SupportedRefactoringsTab(ISession session)
	{
		try {
			HibernateDialect dialect = DialectFactory.getDialect(session.getMetaData());
			Method[] methods = dialect.getClass().getMethods();
			for (Method method : methods)
			{
				if (isRefactoringSupportMethodName(method.getName()))
				{
						Boolean supported = (Boolean) method.invoke(dialect, (Object[])null);
						refactorings.put(method.getName(), supported.toString());
				}
			}
			if (refactorings.containsKey(SUPPORTS_RENAME_VIEW_METHOD_NAME) 
					&& refactorings.containsKey(SUPPORTS_VIEW_DEF_METHOD_NAME)) 
			{
				String supportsRenameView = refactorings.get(SUPPORTS_RENAME_VIEW_METHOD_NAME);
				String supportsViewDefinition = refactorings.get(SUPPORTS_VIEW_DEF_METHOD_NAME);
				
				if (supportsRenameView.equalsIgnoreCase("false") 
						&&  supportsViewDefinition.equalsIgnoreCase("true")) 
				{
					refactorings.put(SUPPORTS_RENAME_VIEW_METHOD_NAME, "true");
				}
				refactorings.remove(SUPPORTS_VIEW_DEF_METHOD_NAME);
			}
			dataSet = new MapDataSet(refactorings);
		} catch (Exception e)
		{
			s_log.error("SupportedRefactoringsTab.init: unexpected exception "+e.getMessage(), e);
		}
	}

	
	private boolean isRefactoringSupportMethodName(String methodName)
	{

		if (methodName.startsWith("supportsAdd") || methodName.startsWith("supportsCreate")
			|| methodName.startsWith("supportsAlter") || methodName.startsWith("supportsDrop")
			|| methodName.startsWith("supportsRename") || methodName.equals("supportsViewDefinition"))
		{
			return true;
		}
		return false;
	}

	
	public String getTitle()
	{
		
		return s_stringMgr.getString("SupportedRefactoringsTab.title");
	}

	
	public String getHint()
	{
		
		return s_stringMgr.getString("SupportedRefactoringsTab.hint");
	}

	
	protected IDataSet createDataSet() throws DataSetException
	{
		return dataSet;
	}
}
