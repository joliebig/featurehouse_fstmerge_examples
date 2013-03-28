package net.sourceforge.squirrel_sql.client.session;

import java.util.Calendar;
import java.util.Date;

public class SQLExecutionInfo
{
	
	private int _idx;

	
	private Date _sqlExecutionStart;

	
	private Date _resultsProcessingStart;

	
	private Date _resultsProcessingEnd;

	
	private String _sql;

	
	private final int _maxRows;
   private Integer _numberResultRowsRead;

   





	





	











	
	public SQLExecutionInfo(int idx, String sql, int maxRows)
	{
		super();
		if (sql == null)
		{
			throw new IllegalArgumentException("SQL script == null");
		}
		_idx = idx;
		_sql = sql;
		_maxRows = maxRows;
		_sqlExecutionStart = Calendar.getInstance().getTime();
	}

	
	public void sqlExecutionComplete()
	{
		_resultsProcessingStart = Calendar.getInstance().getTime();
	}

	
	public void resultsProcessingComplete()
	{
		_resultsProcessingEnd = Calendar.getInstance().getTime();
	}

	
	public int getQueryIndex()
	{
		return _idx;
	}

	
	public String getSQL()
	{
		return _sql;
	}

	
	public Date getSQLExecutionStartTime()
	{
		return _sqlExecutionStart;
	}

	
	public void setSQLExecutionStartTime(Date value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("SQL Execution start time == null");
		}
		_sqlExecutionStart = value;
	}

	
	public long getSQLExecutionElapsedMillis()
	{
		long results = 0;
		if (_resultsProcessingStart != null)
		{
			results = _resultsProcessingStart.getTime() - _sqlExecutionStart.getTime();
		}
		return results;
	}

	
	public long getResultsProcessingElapsedMillis()
	{
		long results = 0;
		if (_resultsProcessingEnd != null && _resultsProcessingStart != null)
		{
			results = (_resultsProcessingEnd.getTime() - _resultsProcessingStart.getTime());
		}
		return results;
	}

	
	public long getTotalElapsedMillis()
	{
		return getSQLExecutionElapsedMillis() + getResultsProcessingElapsedMillis();
	}

	
	public int getMaxRows()
	{
		return _maxRows;
	}

   public void setNumberResultRowsRead(int numberResultRowsRead)
   {
      _numberResultRowsRead = numberResultRowsRead;
   }

   public Integer getNumberResultRowsRead()
   {
      return _numberResultRowsRead;
   }
}
