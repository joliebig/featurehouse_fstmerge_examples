
package net.sourceforge.squirrel_sql.fw.dialects;


public class SequencePropertyMutability
{
	private boolean _restart = true;
	private boolean _startWith = true;
	private boolean _minValue = true;
	private boolean _maxValue = true;
	private boolean _cycle = true;
	private boolean _cache = true;
		
	
	public boolean isRestart()
	{
		return _restart;
	}
	
	public void setRestart(boolean restart)
	{
		this._restart = restart;
	}
	
	public boolean isStartWith()
	{
		return _startWith;
	}
	
	public void setStartWith(boolean startWith)
	{
		this._startWith = startWith;
	}
	
	public boolean isMinValue()
	{
		return _minValue;
	}
	
	public void setMinValue(boolean minValue)
	{
		this._minValue = minValue;
	}
	
	public boolean isMaxValue()
	{
		return _maxValue;
	}
	
	public void setMaxValue(boolean maxValue)
	{
		this._maxValue = maxValue;
	}
	
	public boolean isCycle()
	{
		return _cycle;
	}
	
	public void setCycle(boolean cycle)
	{
		this._cycle = cycle;
	}
	
	public boolean isCache()
	{
		return _cache;
	}
	
	public void setCache(boolean cache)
	{
		this._cache = cache;
	}
	
	
}
