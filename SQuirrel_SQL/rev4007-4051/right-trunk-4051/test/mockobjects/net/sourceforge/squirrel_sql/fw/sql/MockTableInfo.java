
package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public class MockTableInfo extends MockDatabaseObjectInfo implements ITableInfo {

    private static final long serialVersionUID = 1L;
    private String type = null;
    private String remarks = null;
    private ITableInfo[] childTables = null;
    
    public MockTableInfo(String aSimpleName, String aSchemaName, String aCatalog) {
        super(aSimpleName, aSchemaName, aCatalog);
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getType() {
        return type;
    }

    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    
    public String getRemarks() {
        return remarks;
    }

    
    public void setChildTables(ITableInfo[] childTables) {
        this.childTables = childTables;
    }

    
    public ITableInfo[] getChildTables() {
        return childTables;
    }

    
    public ForeignKeyInfo[] getExportedKeys() {
        
        return null;
    }

    
    public ForeignKeyInfo[] getImportedKeys() {
        
        return null;
    }

    
    public void setExportedKeys(ForeignKeyInfo[] foreignKeys) {
        
        
    }

    
    public void setImportedKeys(ForeignKeyInfo[] foreignKeys) {
        
        
    }    
    

}
