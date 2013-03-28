package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public interface IDataSetViewer
{
	
	int MIN_COLUMN_WIDTH = 3;

	
	int MAX_COLUMN_WIDTH = 50;

	
	public void init(IDataSetUpdateableModel updateableObject);

	
	int getRowCount();

	
	void clear();

	
	void setColumnDefinitions(ColumnDisplayDefinition[] hdgs);

	
	ColumnDisplayDefinition[] getColumnDefinitions();

	
	void showHeadings(boolean show);

	
	boolean getShowHeadings();

	void show(IDataSet ds) throws DataSetException;

	void show(IDataSet ds, IMessageHandler msgHandler) throws DataSetException;

	
	void moveToTop();

	
	Component getComponent();

	
	IDataSetUpdateableModel getUpdateableModelReference();
}
