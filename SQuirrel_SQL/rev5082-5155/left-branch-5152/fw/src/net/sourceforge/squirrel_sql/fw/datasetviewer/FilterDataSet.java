
package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;


public class FilterDataSet implements IDataSet {

	private final IDataSet _toBeFiltered;
	private final Map<Integer, Map<String, String>> _replacements;
	
	public FilterDataSet(IDataSet toBeFiltered, Map<Integer, Map<String, String>> replacements) {
		this._toBeFiltered = toBeFiltered;
		this._replacements = replacements;
		
	}
	
	@Override
	public Object get(int columnIndex) throws DataSetException
	{
		Object result = _toBeFiltered.get(columnIndex);
		if (result == null) {
			return result;
		}
		
		Map<String, String> replacementMap = _replacements.get(columnIndex);
		if (replacementMap != null) {
			String value = replacementMap.get(result.toString());
			if (value != null) {
				result = value;
			}
		}
		return result; 
	}

	@Override
	public int getColumnCount() throws DataSetException
	{
		return _toBeFiltered.getColumnCount();
	}

	@Override
	public DataSetDefinition getDataSetDefinition() throws DataSetException
	{
		return _toBeFiltered.getDataSetDefinition();
	}

	@Override
	public boolean next(IMessageHandler msgHandler) throws DataSetException
	{
		return _toBeFiltered.next(msgHandler);
	}
	
}