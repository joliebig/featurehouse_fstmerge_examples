package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DataSetViewerTextFileDestination
	extends BaseDataSetViewerDestination
{
	private final ILogger s_log =
		LoggerController.createLogger(DataSetViewerTextFileDestination.class);

	private File _outFile;

	private PrintWriter _outFileWtr;

	private int _rowCount = 0;

	public DataSetViewerTextFileDestination(File outFile)
	{
		super();
		if (outFile == null)
		{
			throw new IllegalArgumentException("File == null");
		}
		_outFile = outFile;
	}

	public java.awt.Component getComponent()
	{
		throw new UnsupportedOperationException("DataSetViewerTextFileDestination.getComponent()");
	}


	
	public void clear()
	{
	}

	
	protected void addRow(Object[] row) throws DataSetException
	{
		PrintWriter wtr = getWriter();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < row.length; ++i)
		{
			buf.append("\"").append(row[i] != null ? row[i].toString() : "null").append("\"");
			if (i < (row.length - 1))
			{
				buf.append(",");
			}
		}
		wtr.println(buf.toString());
		++_rowCount;
	}

	
	protected void allRowsAdded() throws DataSetException
	{
		closeWriter();
	}

	
	public void moveToTop()
	{
	}

	
	public int getRowCount()
	{
		return _rowCount;
	}

	private PrintWriter getWriter() throws DataSetException
	{
		if (_outFileWtr == null)
		{
			try
			{
				_outFileWtr = new PrintWriter(new FileWriter(_outFile));
			}
			catch (IOException ex)
			{
				s_log.error("Error closing file writer", ex);
				throw new DataSetException(ex);
			}
		}
		return _outFileWtr;
	}

	private void closeWriter() throws DataSetException
	{
		getWriter().close();
		_outFileWtr = null;
		_rowCount = 0;
	}
}
