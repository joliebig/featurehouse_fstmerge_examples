

package org.jfree.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class KeyedObjects2D implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -1015873563138522374L;

    
    private List rowKeys;

    
    private List columnKeys;

    
    private List rows;

    
    public KeyedObjects2D() {
        this.rowKeys = new java.util.ArrayList();
        this.columnKeys = new java.util.ArrayList();
        this.rows = new java.util.ArrayList();
    }

    
    public int getRowCount() {
        return this.rowKeys.size();
    }

    
    public int getColumnCount() {
        return this.columnKeys.size();
    }

    
    public Object getObject(int row, int column) {
        Object result = null;
        KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
        if (rowData != null) {
            Comparable columnKey = (Comparable) this.columnKeys.get(column);
            if (columnKey != null) {
                int index = rowData.getIndex(columnKey);
                if (index >= 0) {
                    result = rowData.getObject(columnKey);
                }
            }
        }
        return result;
    }

    
    public Comparable getRowKey(int row) {
        return (Comparable) this.rowKeys.get(row);
    }

    
    public int getRowIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.rowKeys.indexOf(key);
    }

    
    public List getRowKeys() {
        return Collections.unmodifiableList(this.rowKeys);
    }

    
    public Comparable getColumnKey(int column) {
        return (Comparable) this.columnKeys.get(column);
    }

    
    public int getColumnIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return this.columnKeys.indexOf(key);
    }

    
    public List getColumnKeys() {
        return Collections.unmodifiableList(this.columnKeys);
    }

    
    public Object getObject(Comparable rowKey, Comparable columnKey) {
        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        int row = this.rowKeys.indexOf(rowKey);
        if (row < 0) {
            throw new UnknownKeyException("Row key (" + rowKey
                    + ") not recognised.");
        }
        int column = this.columnKeys.indexOf(columnKey);
        if (column < 0) {
            throw new UnknownKeyException("Column key (" + columnKey
                    + ") not recognised.");
        }
        KeyedObjects rowData = (KeyedObjects) this.rows.get(row);
        int index = rowData.getIndex(columnKey);
        if (index >= 0) {
            return rowData.getObject(index);
        }
        else {
            return null;
        }
    }

    
    public void addObject(Object object, Comparable rowKey,
            Comparable columnKey) {
        setObject(object, rowKey, columnKey);
    }

    
    public void setObject(Object object, Comparable rowKey,
            Comparable columnKey) {

        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (columnKey == null) {
            throw new IllegalArgumentException("Null 'columnKey' argument.");
        }
        KeyedObjects row;
        int rowIndex = this.rowKeys.indexOf(rowKey);
        if (rowIndex >= 0) {
            row = (KeyedObjects) this.rows.get(rowIndex);
        }
        else {
            this.rowKeys.add(rowKey);
            row = new KeyedObjects();
            this.rows.add(row);
        }
        row.setObject(columnKey, object);
        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }

    }

    
    public void removeObject(Comparable rowKey, Comparable columnKey) {
        int rowIndex = getRowIndex(rowKey);
        if (rowIndex < 0) {
            throw new UnknownKeyException("Row key (" + rowKey
                    + ") not recognised.");
        }
        int columnIndex = getColumnIndex(columnKey);
        if (columnIndex < 0) {
            throw new UnknownKeyException("Column key (" + columnKey
                    + ") not recognised.");
        }
        setObject(null, rowKey, columnKey);

        
        boolean allNull = true;
        KeyedObjects row = (KeyedObjects) this.rows.get(rowIndex);

        for (int item = 0, itemCount = row.getItemCount(); item < itemCount;
             item++) {
            if (row.getObject(item) != null) {
                allNull = false;
                break;
            }
        }

        if (allNull) {
            this.rowKeys.remove(rowIndex);
            this.rows.remove(rowIndex);
        }

        
        allNull = true;

        for (int item = 0, itemCount = this.rows.size(); item < itemCount;
             item++) {
            row = (KeyedObjects) this.rows.get(item);
            int colIndex = row.getIndex(columnKey);
            if (colIndex >= 0 && row.getObject(colIndex) != null) {
                allNull = false;
                break;
            }
        }

        if (allNull) {
            for (int item = 0, itemCount = this.rows.size(); item < itemCount;
                 item++) {
                row = (KeyedObjects) this.rows.get(item);
                int colIndex = row.getIndex(columnKey);
                if (colIndex >= 0) {
                    row.removeValue(colIndex);
                }
            }
            this.columnKeys.remove(columnKey);
        }
    }

    
    public void removeRow(int rowIndex) {
        this.rowKeys.remove(rowIndex);
        this.rows.remove(rowIndex);
    }

    
    public void removeRow(Comparable rowKey) {
        int index = getRowIndex(rowKey);
        if (index < 0) {
            throw new UnknownKeyException("Row key (" + rowKey
                    + ") not recognised.");
        }
        removeRow(index);
    }

    
    public void removeColumn(int columnIndex) {
        Comparable columnKey = getColumnKey(columnIndex);
        removeColumn(columnKey);
    }

    
    public void removeColumn(Comparable columnKey) {
        int index = getColumnIndex(columnKey);
        if (index < 0) {
            throw new UnknownKeyException("Column key (" + columnKey
                    + ") not recognised.");
        }
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            KeyedObjects rowData = (KeyedObjects) iterator.next();
            int i = rowData.getIndex(columnKey);
            if (i >= 0) {
                rowData.removeValue(i);
            }
        }
        this.columnKeys.remove(columnKey);
    }

    
    public void clear() {
        this.rowKeys.clear();
        this.columnKeys.clear();
        this.rows.clear();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedObjects2D)) {
            return false;
        }

        KeyedObjects2D that = (KeyedObjects2D) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        if (rowCount != that.getRowCount()) {
            return false;
        }
        int colCount = getColumnCount();
        if (colCount != that.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Object v1 = getObject(r, c);
                Object v2 = that.getObject(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                }
                else {
                    if (!v1.equals(v2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    
    public int hashCode() {
        int result;
        result = this.rowKeys.hashCode();
        result = 29 * result + this.columnKeys.hashCode();
        result = 29 * result + this.rows.hashCode();
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        KeyedObjects2D clone = (KeyedObjects2D) super.clone();
        clone.columnKeys = new java.util.ArrayList(this.columnKeys);
        clone.rowKeys = new java.util.ArrayList(this.rowKeys);
        clone.rows = new java.util.ArrayList(this.rows.size());
        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            KeyedObjects row = (KeyedObjects) iterator.next();
            clone.rows.add(row.clone());
        }
        return clone;
    }

}
