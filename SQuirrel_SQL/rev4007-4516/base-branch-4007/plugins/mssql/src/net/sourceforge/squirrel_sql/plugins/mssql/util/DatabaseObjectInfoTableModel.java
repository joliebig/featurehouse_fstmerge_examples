package net.sourceforge.squirrel_sql.plugins.mssql.util;



import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class DatabaseObjectInfoTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = -1879428906496726350L;

    private ArrayList<IDatabaseObjectInfo> _objectInfo;
    
    public DatabaseObjectInfoTableModel() {
        _objectInfo = new ArrayList<IDatabaseObjectInfo>();
    }
    
    public void addElement(IDatabaseObjectInfo oi) {
        _objectInfo.add(oi);
        int size = _objectInfo.size();
        fireTableRowsInserted(size,size);
    }
    
    public boolean removeElement(IDatabaseObjectInfo oi) {
        int index = _objectInfo.indexOf(oi);
        if (index != -1) {
            _objectInfo.remove(oi);
            fireTableRowsDeleted(index,index);
        }
        return (index != -1);
    }
    
    public int getColumnCount() {
        
        return 2;
    }
    
    public int getRowCount() {
        return _objectInfo.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return _objectInfo.get(rowIndex);
            case 1:
                return _objectInfo.get(rowIndex).getSchemaName();
            default:
                return null;
        }
    }
    
    
    
    public ArrayList<IDatabaseObjectInfo> getContents() {
        return _objectInfo;
    }
    
}
