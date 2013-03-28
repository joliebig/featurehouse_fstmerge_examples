package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

 
import java.sql.Blob;
import java.util.Arrays;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class BlobDescriptor {
	
	
	Blob _blob;
	
	
	byte[] _data = null;
	
	
	private boolean _blobRead = false;
	
	
	private boolean _wholeBlobRead = false;

	
	private int _userSetBlobLimit;
	
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(BlobDescriptor.class);
	
    public static interface i18n {
        String BLOB_LABEL = s_stringMgr.getString("BlobDescriptor.blob");
    }
    
	
	public BlobDescriptor (
		Blob blob, byte[] data, 
		boolean blobRead, boolean wholeBlobRead, int userSetBlobLimit) {
		_blob = blob;
		_data = data;
		_blobRead = blobRead;
		_wholeBlobRead = wholeBlobRead;
		_userSetBlobLimit = userSetBlobLimit;
	}
	
	
	public boolean equals(BlobDescriptor c) {
		if (c == null) {
			
			
			
			
			if (_blobRead == true && _data == null)
				return true;
			else
				return false;
		}
		
		if (c.getBlobRead() == false) {
			
			if (_blobRead == true)
				return false;	
			else return true;	
		}
		
		
		if (_blobRead == false)
			return false;	
		
		
		
		
		return Arrays.equals(c.getData(), _data);
	}
	
	
	public String toString() {
		if (_blobRead) {
			if (_data == null)
				return null;
			
			
			
			Byte[] useValue = new Byte[_data.length];
					for (int i=0; i<_data.length; i++)
						useValue[i] = Byte.valueOf(_data[i]);
			String outString = BinaryDisplayConverter.convertToString(useValue,
						                            BinaryDisplayConverter.HEX, 
                                                    false);
			if (_wholeBlobRead || _userSetBlobLimit > _data.length)
				return outString;	
			else return outString+"...";	
		}
		else return i18n.BLOB_LABEL;
	}
	
	
	 
	public Blob getBlob(){return _blob;}
	public void setBlob(Blob blob){_blob = blob;}
	 
	public byte[] getData(){return _data;}
	public void setData(byte[] data){_data = data;}
	
	public boolean getBlobRead(){return _blobRead;}
	public void setBlobRead(boolean blobRead){_blobRead = blobRead;}
	 
	public boolean getWholeBlobRead(){return _wholeBlobRead;}
	public void setWholeBlobRead(boolean wholeBlobRead){_wholeBlobRead = wholeBlobRead;}

	public int getUserSetBlobLimit(){return _userSetBlobLimit;}
	public void setUserSetBlobLimit(int userSetBlobLimit)
		{_userSetBlobLimit = userSetBlobLimit;}

}
