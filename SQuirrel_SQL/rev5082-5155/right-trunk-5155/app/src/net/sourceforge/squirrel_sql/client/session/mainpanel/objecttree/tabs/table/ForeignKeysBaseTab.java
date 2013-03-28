
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static java.sql.DatabaseMetaData.importedKeyCascade;
import static java.sql.DatabaseMetaData.importedKeyNoAction;
import static java.sql.DatabaseMetaData.importedKeyRestrict;
import static java.sql.DatabaseMetaData.importedKeySetDefault;
import static java.sql.DatabaseMetaData.importedKeySetNull;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.FilterDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public abstract class ForeignKeysBaseTab extends BaseTableTab
{

	
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ForeignKeysBaseTab.class);

	public ForeignKeysBaseTab()
	{
		super();
	}

	
	public String getTitle()
	{
		return s_stringMgr.getString(getTitleKey());
	}

	protected abstract String getTitleKey();
	
	
	public String getHint()
	{ 
		return s_stringMgr.getString(getHintKey());
	}
	
	protected abstract String getHintKey();

	
	protected IDataSet createDataSet() throws DataSetException
	{
		
		Map<Integer, Map<String,String>> replacements = new HashMap<Integer, Map<String,String>>(20);
		HashMap<String,String> replacementMap = new HashMap<String,String>();
		replacementMap.put(""+importedKeyCascade, importedKeyCascade + " (CASCADE)");
		replacementMap.put(""+importedKeyRestrict, importedKeyRestrict + " (RESTRICT)");
		replacementMap.put(""+importedKeySetNull, importedKeySetNull + " (SET NULL)");
		replacementMap.put(""+importedKeyNoAction, importedKeyNoAction + " (NO ACTION)");
		replacementMap.put(""+importedKeySetDefault, importedKeySetDefault + " (SET DEFAULT)");
		replacements.put(9, replacementMap);
		replacements.put(10, replacementMap);
		
		final ISQLConnection conn = getSession().getSQLConnection();
		
		IDataSet orig = getUnfilteredDataSet(conn.getSQLMetaData(), getTableInfo()); 
	   return new FilterDataSet(orig, replacements);
	}

	protected abstract IDataSet getUnfilteredDataSet(SQLDatabaseMetaData md, ITableInfo tableInfo)
		throws DataSetException;
		
	
}