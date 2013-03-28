package net.sourceforge.squirrel_sql.plugins.derby.types;


public class DerbyClobDescriptor {
	
	String _data = null;
    
	
	public DerbyClobDescriptor (String data) {
		_data = data;
	}

	
	public boolean equals(DerbyClobDescriptor c) {
		if (c == null) {
			
			
			
			
			if (_data == null)
				return true;
			else
				return false;
		}

		
		
		
		return c.getData().equals(_data);
	}

	
	public String toString()
	{
		if (_data == null)
		{
			return "<null>";
		} else {
			return _data;	
		}

	}

	
	public String getData(){return _data;}
	public void setData(String data){_data = data;}

}
