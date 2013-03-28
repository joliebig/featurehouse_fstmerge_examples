package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;


public class ForeignKeyColumnInfo implements Serializable
{
	static final long serialVersionUID = -2645123423172494012L;
    
    private final String _fkColumnName;
	private final String _pkColumnName;
	private final int _keySeq;

	public ForeignKeyColumnInfo(String fkColumnName, String pkColumnName,
									int keySeq)
	{
		super();
		_fkColumnName = fkColumnName;
		_pkColumnName = pkColumnName;
		_keySeq = keySeq;
	}

	public String getPrimaryKeyColumnName()
	{
		return _pkColumnName;
	}

	public String getForeignKeyColumnName()
	{
		return _fkColumnName;
	}

	public int getKeySequence()
	{
		return _keySeq;
	}
}
