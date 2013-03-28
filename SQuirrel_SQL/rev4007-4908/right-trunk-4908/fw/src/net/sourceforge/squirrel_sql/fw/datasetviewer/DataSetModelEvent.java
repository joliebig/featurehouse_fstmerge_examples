package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.EventObject;


public class DataSetModelEvent extends EventObject {
	private IDataSetModel _src;

	DataSetModelEvent(IDataSetModel src) throws IllegalArgumentException {
		super(validateSource(src));
		_src = src;
	}

	private static IDataSetModel validateSource(IDataSetModel src)
			throws IllegalArgumentException {
		if (src == null) {
			throw new IllegalArgumentException("Null IDataSetModel passed");
		}
		return src;
	}
}
