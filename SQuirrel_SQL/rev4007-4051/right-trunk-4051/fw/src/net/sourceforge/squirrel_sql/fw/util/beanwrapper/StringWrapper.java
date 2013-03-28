package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

public class StringWrapper
{
	public interface IPropertyNames
	{
		String STRINGS = "string";
	}

	private String _string;

	public StringWrapper()
	{
		this(null);
	}

	public StringWrapper(String string)
	{
		super();
		setString(string);
	}

	public String getString()
	{
		return _string;
	}

	public void setString(String value)
	{
		_string = value;
	}
}
