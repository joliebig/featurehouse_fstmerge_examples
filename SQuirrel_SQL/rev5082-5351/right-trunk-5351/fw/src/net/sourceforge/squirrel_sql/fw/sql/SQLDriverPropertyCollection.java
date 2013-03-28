package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.sql.DriverPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class SQLDriverPropertyCollection implements Serializable
{
    private static final long serialVersionUID = 1L;

    
	public interface IPropertyNames
	{
		String DRIVER_PROPERTIES = "driverProperties";
	}

	
	private final Map<String, SQLDriverProperty> _objectsIndexMap = 
        new TreeMap<String, SQLDriverProperty>();

	
	private final List<SQLDriverProperty> _objectsList = 
        new ArrayList<SQLDriverProperty>();

	
	public SQLDriverPropertyCollection()
	{
		super();
	}

	
	public synchronized void clear()
	{
		_objectsIndexMap.clear();
		_objectsList.clear();
	}

	
	public int size()
	{
		return _objectsList.size();
	}

	public synchronized void applyTo(Properties props)
	{
		for (int i = 0, limit = size(); i < limit; ++i)
		{
			SQLDriverProperty sdp = getDriverProperty(i);
			if (sdp.isSpecified())
			{
				final String value = sdp.getValue();
				if (value != null)
				{
					props.put(sdp.getName(), value);
				}
			}
		}
	}

	
	public synchronized SQLDriverProperty[] getDriverProperties()
	{
		SQLDriverProperty[] ar = new SQLDriverProperty[_objectsList.size()];
		return _objectsList.toArray(ar);
	}

	public synchronized SQLDriverProperty getDriverProperty(int idx)
	{
		return _objectsList.get(idx);
	}

	public synchronized void setDriverProperties(SQLDriverProperty[] values)
	{
		_objectsIndexMap.clear();
		_objectsList.clear();
		for (int i = 0; i < values.length; ++i)
		{
			_objectsList.add(values[i]);
			_objectsIndexMap.put(values[i].getName(), values[i]);
		}
	}

	public synchronized void addDriverProperty(SQLDriverProperty value) {
		_objectsList.add(value);
		_objectsIndexMap.put(value.getName(), value);		
	}
	
	public synchronized void removeDriverProperty(String name) {
		SQLDriverProperty prop = _objectsIndexMap.remove(name);
		_objectsList.remove(prop);
	}
	
	
	public synchronized void setDriverProperty(int idx, SQLDriverProperty value)
	{
		_objectsList.add(idx, value);
		_objectsIndexMap.put(value.getName(), value);
	}

	public synchronized void applyDriverPropertynfo(DriverPropertyInfo[] infoAr)
	{
		if (infoAr == null || infoAr.length == 0)
		{
			infoAr = new DriverPropertyInfo[1];
            infoAr[0] = new DriverPropertyInfo("remarksReporting", "true");
            infoAr[0].required = false;
            infoAr[0].description = "Set to true in order to table/column comments";
		}
		for (int i = 0; i < infoAr.length; ++i)
		{
			SQLDriverProperty sdp = _objectsIndexMap.get(infoAr[i].name);
			if (sdp == null)
			{
				sdp = new SQLDriverProperty(infoAr[i]);
				_objectsIndexMap.put(sdp.getName(), sdp);
				_objectsList.add(sdp);
			}
			sdp.setDriverPropertyInfo(infoAr[i]);
		}
	}
}
