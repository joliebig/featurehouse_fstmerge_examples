package net.sourceforge.squirrel_sql.client.session.properties;



import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;

public class EditWhereCols {

	
	
	private String[] dataArray = new String[0];
	
	
	private static HashMap<String, HashMap<String, String>> _tables = 
        new HashMap<String, HashMap<String, String>>();
	
    
    private IApplication _app = null;
    
	
	public EditWhereCols() {}
	

	
	public String[] getDataArray() {
		
		dataArray = new String[_tables.size()];
		Iterator<String> keys = _tables.keySet().iterator();
		int index = 0;
		
		
		while (keys.hasNext()) {
			String tableName = keys.next();
			HashMap<String, String> h = _tables.get(tableName);
			Iterator<String> columnNames = h.keySet().iterator();
			String outData = tableName + " ";
			while (columnNames.hasNext()) {
				String colName = columnNames.next();
				outData += colName;
				if (columnNames.hasNext())
					outData +=  ",";
			}
			
			
			dataArray[index++] = outData;
		}

		return dataArray;
	}
	
	
	public void setDataArray(String[] inData) {
	    
		_tables = new HashMap<String, HashMap<String, String>>();	
		
		
		for (int i=0; i< inData.length; i++) {
			int endIndex = inData[i].indexOf(" ");
			String tableName = inData[i].substring(0, endIndex);
			
			int startIndex;
			ArrayList<String> colList = new ArrayList<String>();
			while (true) {
				startIndex = endIndex+1;
				endIndex = inData[i].indexOf(',', startIndex);
				if (endIndex == -1) {
					
					colList.add(inData[i].substring(startIndex));
					break;
				}
				colList.add(inData[i].substring(startIndex, endIndex));
			}
			
			
			
			HashMap<String, String> h = new HashMap<String, String>(colList.size());
			for (int j=0; j<colList.size(); j++)
				h.put(colList.get(j), colList.get(j));
				
			
			_tables.put(tableName, h);
		}
	}

	
	public void put(String tableName, HashMap<String, String> colNames) {
        if (_app == null) {
            throw new IllegalStateException("application has not been set");
        }        
		if (colNames == null) {
			_tables.remove(tableName);
        } else { 
			_tables.put(tableName, colNames);
        }
        _app.savePreferences(PreferenceType.EDITWHERECOL_PREFERENCES);
		return;
	}
	
	
	public static HashMap<String,String> get(String tableName) {
		return _tables.get(tableName);
	}


    
    public void setApplication(IApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("application cannot be null");
        }
        this._app = application;
    }

}
