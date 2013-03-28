package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;



import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseFileInfo {
    
    private String _databaseName;
    private String _databaseSize;
    private String _owner;
    private short _compatibilityLevel;
    private String _createdDate;
    private HashMap<String, String> _options;
    private ArrayList<DatabaseFile> _dataFiles;
    private ArrayList<Object> _logFiles;
    
    
    public DatabaseFileInfo() {
        _options = new HashMap<String, String>();
        _dataFiles = new ArrayList<DatabaseFile>();
        _logFiles = new ArrayList<Object>();
    }
    
    
    public String getDatabaseSize() {
        return this._databaseSize;
    }
    
    
    public void setDatabaseSize(String databaseSize) {
        this._databaseSize = databaseSize;
    }
    
    
    public String getOwner() {
        return this._owner;
    }
    
    
    public void setOwner(String owner) {
        this._owner = owner;
    }
    
    
    public short getCompatibilityLevel() {
        return this._compatibilityLevel;
    }
    
    
    public void setCompatibilityLevel(short compatibilityLevel) {
        this._compatibilityLevel = compatibilityLevel;
    }
    
    
    public String getCreatedDate() {
        return this._createdDate;
    }
    
    
    public void setCreatedDate(String createdDate) {
        this._createdDate = createdDate;
    }
    
    public void setOption(String option, String value) {
        _options.put(option,value);
    }
    
    public String getOption(String option) {
        if (_options.containsKey(option))
            return _options.get(option);
        else
            return "";
    }
    
    
    public String getDatabaseName() {
        return this._databaseName;
    }
    
    
    public void setDatabaseName(String databaseName) {
        this._databaseName = databaseName;
    }
    
    public Object[] getLogFiles() {
        return this._logFiles.toArray();
    }
    
    public Object[] getDataFiles() {
        return this._dataFiles.toArray();
    }
    
    public void addLogFile(DatabaseFile file) {
        _logFiles.add(file);
    }
    
    public void addDataFile(DatabaseFile file) {
        
        for (int i = 0; i < _dataFiles.size(); i++) {
            DatabaseFile f = _dataFiles.get(i);
            if (f.getFilegroup().equals(file.getFilegroup())) {
                
                for (int j = i + 1; j < _dataFiles.size(); j++) {
                    f = _dataFiles.get(i);
                    if (!f.getFilegroup().equals(file.getFilegroup())) {
                        
                        _dataFiles.add(j,file);
                        return;
                    }
                }
            }
        }
        
        _dataFiles.add(file);
    }
}
