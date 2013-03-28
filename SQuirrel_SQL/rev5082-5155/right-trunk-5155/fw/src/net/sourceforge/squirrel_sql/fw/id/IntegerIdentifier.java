package net.sourceforge.squirrel_sql.fw.id;

import java.io.Serializable;

public class IntegerIdentifier implements IIdentifier, Serializable
{
	private static final long serialVersionUID = 7129424482184532913L;

	public interface IPropertyNames
	{
		String STRING = "string";
	}

	private int _id;

	public IntegerIdentifier(int value)
	{
		super();
		_id = value;
	}

	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((IntegerIdentifier)rhs).toString().equals(toString());
		}
		return rc;
	}

	public synchronized int hashCode()
	{
		return _id;
	}

	public String toString()
	{
		return Integer.toString(_id);
	}

	
	public void setString(String value)
	{
		_id = Integer.parseInt(value);
	}
}
