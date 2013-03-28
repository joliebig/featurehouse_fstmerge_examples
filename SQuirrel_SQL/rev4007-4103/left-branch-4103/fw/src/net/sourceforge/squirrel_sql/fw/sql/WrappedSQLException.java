package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class WrappedSQLException extends BaseException
{
	static final long serialVersionUID = 8923509127367847605L;

    
	public WrappedSQLException(SQLException ex)
	{
		super(checkParams(ex));
	}

	
	public SQLException getSQLExeption()
	{
		return (SQLException)getWrappedThrowable();
	}

	private static SQLException checkParams(SQLException ex)
	{
		if (ex == null)
		{
			throw new IllegalArgumentException("SQLException == null");
		}
		return ex;
	}
}
