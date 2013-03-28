package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public interface IDataSet {
	int getColumnCount() throws DataSetException;
	DataSetDefinition getDataSetDefinition() throws DataSetException;
	boolean next(IMessageHandler msgHandler) throws DataSetException;
	Object get(int columnIndex) throws DataSetException;
}