package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;


import java.io.Serializable;


public class RefactoringPreferenceBean implements Cloneable, Serializable
{
	private static final long serialVersionUID = 6377157814922907413L;

	static final String UNSUPPORTED = "Unsupported";

	
	private String _clientName;

	
	private String _clientVersion;

	
	private boolean _qualifyTableNames = true;

	
	private boolean _quoteIdentifiers = true;
	
	public RefactoringPreferenceBean()
	{
		super();
	}

	
	public Object clone()
	{
		try
		{
			return super.clone();
		} catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public String getClientName()
	{
		return _clientName;
	}

	
	public void setClientName(String value)
	{
		_clientName = value;
	}

	
	public String getClientVersion()
	{
		return _clientVersion;
	}

	
	public void setClientVersion(String value)
	{
		_clientVersion = value;
	}

	
	public void setQualifyTableNames(boolean qualifyTableNames)
	{
		this._qualifyTableNames = qualifyTableNames;
	}

	
	public boolean isQualifyTableNames()
	{
		return _qualifyTableNames;
	}

	
	public boolean isQuoteIdentifiers()
	{
		return _quoteIdentifiers;
	}

	
	public void setQuoteIdentifiers(boolean quoteIdentifiers)
	{
		this._quoteIdentifiers = quoteIdentifiers;
	}

}
