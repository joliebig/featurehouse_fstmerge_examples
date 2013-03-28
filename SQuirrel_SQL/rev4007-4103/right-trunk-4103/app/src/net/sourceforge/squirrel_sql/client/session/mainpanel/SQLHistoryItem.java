package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.io.Serializable;
import java.util.Date;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SQLHistoryItem implements Serializable, Cloneable
{
	
    private static final long serialVersionUID = 1L;


    
	private String _sql;


   private java.util.Date _lastUsageTime;

   
	private String _shortSql;
   private String _aliasName;

   
	public SQLHistoryItem()
	{
		this("", "");
	}

	
	public SQLHistoryItem(String sql, String aliasName)
	{
		super();
      if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

      _aliasName = aliasName;

      if(0 < sql.length())
      {
         _lastUsageTime = new Date();
      }

      setSQL(sql);
	}

	
    @Override
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (this == rhs)
		{
			rc = true;
		}
		else if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((SQLHistoryItem)rhs).getSQL().equals(getSQL());
		}
		return rc;
	}

    @Override
    public int hashCode() {
        return getSQL().hashCode();
    }
    
	
    @Override
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

	
    @Override
	public String toString()
	{
		return _shortSql;
	}

	
	public String getSQL()
	{
		return _sql;
	}

	
	public void setSQL(String sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

		_sql = sql.trim();
		_shortSql = StringUtilities.cleanString(sql);
	}

   public Date getLastUsageTime()
   {
      return _lastUsageTime;
   }

   public void setLastUsageTime(Date _creationTime)
   {
      this._lastUsageTime = _creationTime;
   }


   public String getAliasName()
   {
      return _aliasName;
   }

   public void setAliasName(String _aliasName)
   {
      this._aliasName = _aliasName;
   }
}
