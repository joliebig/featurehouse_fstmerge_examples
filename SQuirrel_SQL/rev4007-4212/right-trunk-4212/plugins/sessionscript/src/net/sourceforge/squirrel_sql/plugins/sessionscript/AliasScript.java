package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

public class AliasScript implements Serializable, IHasIdentifier
{
	
	private IIdentifier _id;

	
	private String _sql;

	
	public AliasScript()
	{
		super();
	}

	
	public AliasScript(ISQLAlias alias)
	{
		super();

		if (alias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}

		_id = alias.getIdentifier();
	}

	
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((AliasScript) rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	
	public int hashCode()
	{
		return getIdentifier().hashCode();
	}

	
	public String toString()
	{
		return _sql != null ? _sql : "";
	}

	
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	
	public String getSQL()
	{
		return _sql;
	}

	
	public void setIdentifier(IIdentifier id)
	{
		_id = id;
	}

	
	public void setSQL(String value)
	{
		_sql = value;
	}
}
