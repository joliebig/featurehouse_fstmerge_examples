package net.sourceforge.squirrel_sql.fw.persist;

import net.sourceforge.squirrel_sql.fw.util.BaseException;

public class ValidationException extends BaseException
{
	private static final long serialVersionUID = -2357979339685131322L;

	public ValidationException(String msg)
	{
		super(msg);
	}
}
