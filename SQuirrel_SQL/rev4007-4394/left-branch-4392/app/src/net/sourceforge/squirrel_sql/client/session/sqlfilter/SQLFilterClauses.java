package net.sourceforge.squirrel_sql.client.session.sqlfilter;

import java.io.Serializable;
import java.util.HashMap;

public class SQLFilterClauses implements Serializable
{
    private static final long serialVersionUID = 1L;

    
	HashMap<String, HashMap<String, String>> _sqlClauseInformation;

	
	public SQLFilterClauses()
	{
		_sqlClauseInformation = new HashMap<String, HashMap<String, String>>();
	}

	
	public String get(String clauseName, String tableName)
	{
		HashMap<String, String> filterData = _sqlClauseInformation.get(tableName);
		return (filterData == null) ? null : filterData.get(clauseName);
	}

	
	public void put(String clauseName, String tableName,
						String clauseInformation)
	{
		HashMap<String, String> filterData = _sqlClauseInformation.get(tableName);
		if (filterData == null)
		{
			filterData = new HashMap<String, String>();
		}
		filterData.put(clauseName, clauseInformation);
		_sqlClauseInformation.put(tableName, filterData);
	}
}
