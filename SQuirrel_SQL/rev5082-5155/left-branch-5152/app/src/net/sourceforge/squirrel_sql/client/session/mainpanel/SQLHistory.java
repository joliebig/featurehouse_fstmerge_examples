package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLHistory
{
	private List<SQLHistoryItem> _history = new ArrayList<SQLHistoryItem>();

	public SQLHistory()
	{
		super();
	}

	public synchronized SQLHistoryItem[] getData()
	{
		SQLHistoryItem[] data = new SQLHistoryItem[_history.size()];
		return _history.toArray(data);
	}

	public synchronized void setData(SQLHistoryItem[] data)
	{
		_history.clear();
		_history.addAll(Arrays.asList(data));
	}


   public synchronized void add(SQLHistoryItem obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("SQLHistoryItem == null");
		}

		
		while (_history.remove(obj))
		{
			
		}

		_history.add(obj);
	}
}
