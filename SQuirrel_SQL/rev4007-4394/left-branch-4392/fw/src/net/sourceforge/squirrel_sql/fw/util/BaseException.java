package net.sourceforge.squirrel_sql.fw.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class BaseException extends Exception
{
	
	private Throwable _wrapee;

	
	public BaseException()
	{
		this("");
	}

	
	public BaseException(String msg)
	{
		super(msg != null ? msg : "");
	}

	
	public BaseException(Throwable wrapee)
	{
      super(wrapee);
		_wrapee = wrapee;
	}

	public String toString()
	{
		if (_wrapee != null)
		{
			return _wrapee.toString();
		}
		return super.toString();
	}

	public void printStackTrace()
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace();
		}
		else
		{
			super.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(s);
		}
		else
		{
			super.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter wtr)
	{
		if (_wrapee != null)
		{
			_wrapee.printStackTrace(wtr);
		}
		else
		{
			super.printStackTrace(wtr);
		}
	}

	private static String getMessageFromException(Throwable th)
	{
		String rtn = "";
		if (th != null)
		{
			String msg = th.getMessage();
			if (msg != null)
			{
				rtn = msg;
			}
		}
		return rtn;
	}

	
	public Throwable getWrappedThrowable()
	{
		return _wrapee;
	}
}
