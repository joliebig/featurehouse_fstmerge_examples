package net.sourceforge.squirrel_sql.fw.datasetviewer;

 
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;



public class CellImportExportInfoSaver {
	
	
	private HashMap<String, CellImportExportInfo> map = 
        new HashMap<String, CellImportExportInfo>();
	
	
	private ArrayList<String> cmdList = new ArrayList<String>();
	
	
	private static CellImportExportInfoSaver instance = null;
	
	
	public CellImportExportInfoSaver() {};

	
	static public CellImportExportInfoSaver getInstance(){
		if (instance == null)
			instance = new CellImportExportInfoSaver();
		return instance;
	}

	
	static public void setInstance(CellImportExportInfoSaver newInstance) {
		if (newInstance == null)
			instance = new CellImportExportInfoSaver();	
		else instance = newInstance;
	}
	
	
	public synchronized void save(String tableColumnName,
		String fileName,
		String command) {
		
		
		
		map.remove(tableColumnName);
		
		CellImportExportInfo infoObject =
			new CellImportExportInfo(tableColumnName, fileName, command);
		
		map.put(tableColumnName, infoObject);
		
		if (command != null && command.length() > 0) {
			cmdList.add(command);
			Collections.sort(cmdList);
		}
			
	}
	
	
	public CellImportExportInfo get(String tableColumnName) {
		return map.get(tableColumnName);
	}
	
	
	public synchronized String[] getCmdList() {
		String[] data = new String[cmdList.size()];
		return cmdList.toArray(data);
	}
	
	
	static public void remove(String tableColumnName) {
		
		if (instance == null)
			instance = new CellImportExportInfoSaver();	
		instance.map.remove(tableColumnName);
	}
	
	
	public void add(CellImportExportInfo info) {
		map.put(info.getTableColumnName(), info);
	}
	
	
	public synchronized void setData(CellImportExportInfo[] data)
	{
		for (int i = 0; i < data.length; i++) {
			map.put(data[i].getTableColumnName(), data[i]);	
		}
	}

	
	public synchronized void setCmdList(String[] data)
	{
		cmdList = new ArrayList<String>(Arrays.asList(data));
		Collections.sort(cmdList);
	}


	
	public synchronized CellImportExportInfo[] getData()
	{
		if (instance == null)
			instance = new CellImportExportInfoSaver();	
			
		CellImportExportInfo[] array = new CellImportExportInfo[instance.map.size()];
		Iterator<CellImportExportInfo> iterator = instance.map.values().iterator();
		int index = 0;
		while (iterator.hasNext()) {
			array[index] = iterator.next();
			index++;
		}

		return array;
	}


}
