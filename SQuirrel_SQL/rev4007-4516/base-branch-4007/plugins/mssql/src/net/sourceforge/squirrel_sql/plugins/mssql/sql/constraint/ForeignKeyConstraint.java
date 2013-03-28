package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;



import java.util.ArrayList;

public class ForeignKeyConstraint extends MssqlConstraint {
    
    private ArrayList<String> _primaryColumns;
    
    
    private String _referencedTable;
    
      
    
    
    public ForeignKeyConstraint() {
        super();
        _primaryColumns = new ArrayList<String>();
    }
    
    public void addPrimaryColumn(String columnName) {
        _primaryColumns.add(columnName);
    }
    
    public Object[] getPrimaryColumns() {
        return _primaryColumns.toArray();
    }
    
    
    public String getReferencedTable() {
        return this._referencedTable;
    }
    
    
    public void setReferencedTable(String referencedTable) {
        this._referencedTable = referencedTable;
    }
    
}
