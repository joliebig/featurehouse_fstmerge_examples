package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;




public class ImportedKeysTab extends ForeignKeysBaseTab
{

	@Override
	protected String getTitleKey()
	{
		return "ImportedKeysTab.title";
	}

	@Override
	protected String getHintKey()
	{
		return "ImportedKeysTab.hint";
	}

	@Override
	protected IDataSet getUnfilteredDataSet(SQLDatabaseMetaData md, ITableInfo tableInfo)
		throws DataSetException
	{
		return md.getImportedKeysDataSet(tableInfo);
	}
}
