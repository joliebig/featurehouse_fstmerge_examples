package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

public class TableColumnDifference extends AbstractDifference {
        
    
    private String _columnName;
    
    public TableColumnDifference() {
        
    }

    
    public void setColumnName(String _columnName) {
        this._columnName = _columnName;
    }

    
    public String getColumnName() {
        return _columnName;
    }
    
    
}
