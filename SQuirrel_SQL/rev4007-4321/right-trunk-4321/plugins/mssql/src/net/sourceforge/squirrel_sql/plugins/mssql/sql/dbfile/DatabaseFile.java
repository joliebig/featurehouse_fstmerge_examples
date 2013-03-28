package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;



public class DatabaseFile {
    
    private String _name;
    private short _id;
    private String _filename;
    private String _filegroup;
    private String _size;
    private String _maxSize;
    private String _growth;
    private String _usage;
    
    
    public DatabaseFile() {
    }
    
    
    public String getName() {
        return this._name;
    }
    
    
    public void setName(String name) {
        this._name = name;
    }
    
    
    public short getId() {
        return this._id;
    }
    
    
    public void setId(short id) {
        this._id = id;
    }
    
    
    public String getFilename() {
        return this._filename;
    }
    
    
    public void setFilename(String filename) {
        this._filename = filename;
    }
    
    
    public String getFilegroup() {
        return this._filegroup;
    }
    
    
    public void setFilegroup(String filegroup) {
        this._filegroup = filegroup;
    }
    
    
    public String getSize() {
        return this._size;
    }
    
    
    public void setSize(String size) {
        this._size = size;
    }
    
    
    public String getMaxSize() {
        return this._maxSize;
    }
    
    
    public void setMaxSize(String maxSize) {
        this._maxSize = maxSize;
    }
    
    
    public String getGrowth() {
        return this._growth;
    }
    
    
    public void setGrowth(String growth) {
        this._growth = growth;
    }
    
    
    public String getUsage() {
        return this._usage;
    }
    
    
    public void setUsage(String usage) {
        this._usage = usage;
    }
    
}
