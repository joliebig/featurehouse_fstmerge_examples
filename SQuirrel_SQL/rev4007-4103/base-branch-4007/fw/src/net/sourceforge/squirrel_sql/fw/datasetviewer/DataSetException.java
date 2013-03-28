package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.BaseException;


public class DataSetException extends BaseException
{
	
	public DataSetException(String msg)
	{
		super(msg);
	}

	
	public DataSetException(Throwable wrapee)
	{
		super(wrapee);
	}
}