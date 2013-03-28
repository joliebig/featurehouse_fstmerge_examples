package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.EventListener;


public interface IDataSetModelListener extends EventListener {
	
	void allRowsAdded(DataSetModelEvent evt);

	
	void moveToTop(DataSetModelEvent evt);
}
