
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.DatabaseMetaData;


public class CreateScriptPreferences {
    
    
    
    private int deleteAction = DatabaseMetaData.importedKeyNoAction;
    
    private int updateAction = DatabaseMetaData.importedKeyNoAction;
    
    
    private boolean deleteRefAction = false;
    
    
    private boolean updateRefAction = false;
    
    private boolean constraintsAtEnd;
    
    private boolean includeExternalReferences;
    
    private boolean qualifyTableNames;
    
    public void setDeleteRefAction(boolean deleteRefAction) {
        this.deleteRefAction = deleteRefAction;
    }

    public boolean isDeleteRefAction() {
        return deleteRefAction;
    }

    public void setDeleteAction(int action) {
        this.deleteAction = action;
    }

    public int getDeleteAction() {
        return deleteAction;
    }

    public void setUpdateAction(int updateAction) {
        this.updateAction = updateAction;
    }

    public int getUpdateAction() {
        return updateAction;
    }

    public void setUpdateRefAction(boolean updateRefAction) {
        this.updateRefAction = updateRefAction;
    }

    public boolean isUpdateRefAction() {
        return updateRefAction;
    }

    public String getRefActionByType(int type) {
        switch (type) {
            case DatabaseMetaData.importedKeyNoAction:
                return "NO ACTION";
            case DatabaseMetaData.importedKeyCascade:
                return "CASCADE";
            case DatabaseMetaData.importedKeySetDefault:
                return "SET DEFAULT";
            case DatabaseMetaData.importedKeySetNull:
                return "SET NULL";
            default:
                return "NO ACTION";
        }
    }

    
    public void setConstraintsAtEnd(boolean constraintsAtEnd) {
        this.constraintsAtEnd = constraintsAtEnd;
    }

    
    public boolean isConstraintsAtEnd() {
        return constraintsAtEnd;
    }

    
    public void setIncludeExternalReferences(boolean includeExternalReferences) {
        this.includeExternalReferences = includeExternalReferences;
    }

    
    public boolean isIncludeExternalReferences() {
        return includeExternalReferences;
    }

    
    public void setQualifyTableNames(boolean qualifyTableNames) {
        this.qualifyTableNames = qualifyTableNames;
    }

    
    public boolean isQualifyTableNames() {
        return qualifyTableNames;
    }
    
}
