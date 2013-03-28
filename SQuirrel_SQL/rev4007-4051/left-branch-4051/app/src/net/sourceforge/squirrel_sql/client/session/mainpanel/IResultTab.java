
package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public interface IResultTab {

    void reInit(IDataSetUpdateableTableModel creator, SQLExecutionInfo exInfo);

    
    void showResults(ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds,
            SQLExecutionInfo exInfo) throws DataSetException;

    
    void clear();

    
    String getSqlString();

    
    String getViewableSqlString();

    
    String getTitle();

    
    void closeTab();

    void returnToTabbedPane();

    Component getOutputComponent();

    void reRunSQL();

    
    IIdentifier getIdentifier();

}