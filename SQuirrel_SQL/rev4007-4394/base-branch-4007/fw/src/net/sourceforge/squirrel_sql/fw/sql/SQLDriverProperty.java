package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.sql.DriverPropertyInfo;

public class SQLDriverProperty implements Cloneable, Serializable
{
    static final long serialVersionUID = -5150608132930417454L;
    
	
	public interface IPropertyNames
	{
		
		String NAME = "name";

		
		String VALUE = "value";

		
		String IS_SPECIFIED = "isSpecified";
	}

    
	private String _name;

	
	private String _value;

	
	private boolean _isSpecified;

	private transient DriverPropertyInfo _driverPropInfo;

	
	public SQLDriverProperty()
	{
		super();
	}

	
	public SQLDriverProperty(DriverPropertyInfo parm)
	{
		super();
		if (parm == null)
		{
			throw new IllegalArgumentException("DriverPropertyInfo == null");
		}
	
		setName(parm.name);
		setValue(parm.value);
		setDriverPropertyInfo(parm);	
	}

	
	public SQLDriverProperty(String name, String value)
	{
		super();
		_name = name;
		_value = value;
	}

	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public String getName()
	{
		return _name;
	}

	
	public String getValue()
	{
		return _value;
	}

	public boolean isSpecified()
	{
		return _isSpecified;
	}

	public DriverPropertyInfo getDriverPropertyInfo()
	{
		return _driverPropInfo;
	}

	
	public synchronized void setName(String name)
	{
		_name = name;
		if (_driverPropInfo != null)
		{
			_driverPropInfo.name = name;
		}
	}

	
	public synchronized void setValue(String value)
	{
		_value = value;
		if (_driverPropInfo != null)
		{
			_driverPropInfo.value = value;
		}
	}

	public void setIsSpecified(boolean value)
	{
		_isSpecified = value;
	}

	public void setDriverPropertyInfo(DriverPropertyInfo parm)
	{
		if (parm != null)
		{
			if (!parm.name.equals(getName()))
			{
				throw new IllegalArgumentException("DriverPropertyInfo.name != my name");
			}
		}
		_driverPropInfo = parm;
		_driverPropInfo.value = _value;
	}
}

