
package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class MockDatabaseObjectInfo implements IDatabaseObjectInfo {

    private String simpleName = null;
    
    private String schemaName = null;
    
    private String catalogName = null;
    
    public MockDatabaseObjectInfo(String aSimpleName, String aSchemaName, String aCatalog) {
        simpleName = aSimpleName;
        schemaName = aSchemaName;
        catalogName = aCatalog;
    }
    
    
    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String aName) {
        catalogName = aName;
    }
    
    
    public DatabaseObjectType getDatabaseObjectType() {
        return DatabaseObjectType.TABLE;
    }

    
    public String getQualifiedName() {
        return simpleName;
    }

    
    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String aName) {
        schemaName = aName;
    }
    
    
    public String getSimpleName() {
        return simpleName;
    }

    
    public int compareTo(IDatabaseObjectInfo o) {
        
        System.err.println("MockDatabaseObjectInfo.compareTo: stub not yet implemented");
        return 0;
    }

    public String toString() {
    	StringBuffer result = new StringBuffer();
    	result.append("catalog=");
        result.append(catalogName);
        result.append(" schema=");
        result.append(schemaName);
        result.append(" simpleName=");
        result.append(simpleName);
    	return result.toString();
    }
}
