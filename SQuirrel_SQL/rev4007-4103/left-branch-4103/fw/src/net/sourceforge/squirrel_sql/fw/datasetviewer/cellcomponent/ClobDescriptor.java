package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.sql.Clob;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ClobDescriptor {

	
	Clob _clob;

	
	String _data = null;

	
	private boolean _clobRead = false;

	
	private boolean _wholeClobRead = false;

	
	private int _userSetClobLimit;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ClobDescriptor.class);

    public static interface i18n {
        String CLOB_LABEL = s_stringMgr.getString("ClobDescriptor.clob");
    }
    
	
	public ClobDescriptor (
		Clob clob, String data,
		boolean clobRead, boolean wholeClobRead, int userSetClobLimit) {
		_clob = clob;
		_data = data;
		_clobRead = clobRead;
		_wholeClobRead = wholeClobRead;
		_userSetClobLimit = userSetClobLimit;
	}

	
	public boolean equals(ClobDescriptor c) {
		if (c == null) {
			
			
			
			
			if (_clobRead == true && _data == null)
				return true;
			else
				return false;
		}

		if (c.getClobRead() == false) {
			
			if (_clobRead == true)
				return false;	
			else return true;	
		}

		
		if (_clobRead == false)
			return false;	

		
		
		
		return c.getData().equals(_data);
	}

	
	public String toString()
	{
		if (_clobRead)
		{
			if (_data == null)
			{
				return s_stringMgr.getString("ClobDescriptor.null");
			}
			if (_wholeClobRead || _userSetClobLimit > _data.length())
			{
				return _data;	
			}
			return _data + "...";	
		}
		return i18n.CLOB_LABEL;
	}

	

	public Clob getClob(){return _clob;}
	public void setClob(Clob clob){_clob = clob;}

	public String getData(){return _data;}
	public void setData(String data){_data = data;}

	public boolean getClobRead(){return _clobRead;}
	public void setClobRead(boolean clobRead){_clobRead = clobRead;}

	public boolean getWholeClobRead(){return _wholeClobRead;}
	public void setWholeClobRead(boolean wholeClobRead){_wholeClobRead = wholeClobRead;}

	public int getUserSetClobLimit(){return _userSetClobLimit;}
	public void setUserSetClobLimit(int userSetClobLimit)
		{_userSetClobLimit = userSetClobLimit;}

}
