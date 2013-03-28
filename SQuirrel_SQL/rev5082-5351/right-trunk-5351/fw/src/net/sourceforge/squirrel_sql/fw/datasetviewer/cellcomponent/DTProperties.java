package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;



import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class DTProperties {
	
	
	private String[] dataArray = new String[0];
	
	
	private static HashMap<String, HashMap<String, String>> dataTypes = 
        new HashMap<String, HashMap<String, String>>();
	
	
	public DTProperties() {}
	

	
	public String[] getDataArray() {
		
		Iterator<String> keys = dataTypes.keySet().iterator();

		ArrayList<String> propertyList = new ArrayList<String>();
		
		
		while (keys.hasNext()) {
			String tableName = keys.next();
			HashMap<String, String> h = dataTypes.get(tableName);
			
			Set<Entry<String, String>> properties =  h.entrySet();
			for (Entry<String, String> entry : properties) {
				String propertyName = entry.getKey();
				StringBuilder tmp = new StringBuilder(tableName);
				tmp.append(" ");
            tmp.append(propertyName);
            tmp.append("=");
            tmp.append(entry.getValue());
            propertyList.add(tmp.toString());
			}
		}

		dataArray = propertyList.toArray(dataArray);
		return dataArray;
	}
	
	
	public void setDataArray(String[] inData) {
		dataTypes = new HashMap<String, HashMap<String, String>>();	
		
		
		for (int i=0; i< inData.length; i++) {
			int endIndex = inData[i].indexOf(" ");
			String dataTypeName = inData[i].substring(0, endIndex);
			
			int startIndex;
			startIndex = endIndex + 1;
			endIndex = inData[i].indexOf("=", startIndex);
			String propertyName = inData[i].substring(startIndex, endIndex);
			String propertyValue = inData[i].substring(endIndex+1);
			
			
			
			HashMap<String, String> h = dataTypes.get(dataTypeName);
			if (h == null) {
				h = new HashMap<String, String>();
				dataTypes.put(dataTypeName, h);
			}
			
			
			h.put(propertyName, propertyValue);
		}
	}

	
	public static void put(String dataTypeName, String propertyName,
		String propertyValue) {
		
		
		HashMap<String, String> h = dataTypes.get(dataTypeName);
		if (h == null) {
			h = new HashMap<String, String>();
			dataTypes.put(dataTypeName, h);
		}
		h.put(propertyName, propertyValue);
	}
	
	
	public static String get(String dataTypeName, String propertyName) {
		HashMap<String, String> h = dataTypes.get(dataTypeName);
		if (h == null)
			return null;
		
		return h.get(propertyName);
	}
		
}
