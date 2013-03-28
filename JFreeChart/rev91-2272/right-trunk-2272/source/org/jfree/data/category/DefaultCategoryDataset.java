

package org.jfree.data.category;

import java.io.Serializable;
import java.util.List;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.SelectableValue;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.event.DatasetChangeEvent;


public class DefaultCategoryDataset extends AbstractCategoryDataset
        implements CategoryDataset, SelectableCategoryDataset, 
        CategoryDatasetSelectionState, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -8168173757291644622L;

    
    private KeyedObjects2D data;

    
    public DefaultCategoryDataset() {
        this.data = new KeyedObjects2D();
        
        setSelectionState(this);
    }

    
    public int getRowCount() {
        return this.data.getRowCount();
    }

    
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    
    public Number getValue(int row, int column) {
        SelectableValue sv = (SelectableValue) this.data.getObject(row, 
                column);
        if (sv == null) {
            return null;
        }
        else {
            return sv.getValue();
        }
    }

    
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    
    public int getRowIndex(Comparable key) {
        
        return this.data.getRowIndex(key);
    }

    
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    
    public int getColumnIndex(Comparable key) {
        
        return this.data.getColumnIndex(key);
    }

    
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        SelectableValue sv = (SelectableValue) this.data.getObject(rowKey,
                columnKey);
        if (sv != null) {
            return sv.getValue();
        }
        else {
            return null;
        }
    }

    
    public void addValue(Number value, Comparable rowKey,
                         Comparable columnKey) {
        this.data.addObject(new SelectableValue(value), rowKey, columnKey);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void addValue(double value, Comparable rowKey,
                         Comparable columnKey) {
        addValue(new Double(value), rowKey, columnKey);
    }

    
    public void setValue(Number value, Comparable rowKey,
                         Comparable columnKey) {
        this.data.setObject(new SelectableValue(value), rowKey, columnKey);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void setValue(double value, Comparable rowKey,
                         Comparable columnKey) {
        setValue(new Double(value), rowKey, columnKey);
    }

    
    public void incrementValue(double value,
                               Comparable rowKey,
                               Comparable columnKey) {
        double existing = 0.0;
        Number n = getValue(rowKey, columnKey);
        if (n != null) {
            existing = n.doubleValue();
        }
        setValue(existing + value, rowKey, columnKey);
    }

    
    public void removeValue(Comparable rowKey, Comparable columnKey) {
        this.data.removeObject(rowKey, columnKey);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void clear() {
        this.data.clear();
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryDataset)) {
            return false;
        }
        CategoryDataset that = (CategoryDataset) obj;
        if (!getRowKeys().equals(that.getRowKeys())) {
            return false;
        }
        if (!getColumnKeys().equals(that.getColumnKeys())) {
            return false;
        }
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Number v1 = getValue(r, c);
                Number v2 = that.getValue(r, c);
                if (v1 == null) {
                    if (v2 != null) {
                        return false;
                    }
                }
                else if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    
    public int hashCode() {
        return this.data.hashCode();
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultCategoryDataset clone = (DefaultCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }

    public boolean isSelected(int row, int column) {
        SelectableValue sv = (SelectableValue) this.data.getObject(row, column);
        return sv.isSelected();
    }

    public void setSelected(int row, int column, boolean selected) {
        setSelected(row, column, selected, true);
    }

    public void setSelected(int row, int column, boolean selected,
            boolean notify) {
        SelectableValue sv = (SelectableValue) this.data.getObject(row, column);
        sv.setSelected(selected);
        if (notify) {
            fireSelectionEvent();
        }
    }

    public void clearSelection() {
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                setSelected(r, c, false, false);
            }
        }
        fireSelectionEvent();
    }

    public void fireSelectionEvent() {
        
        fireDatasetChanged(new DatasetChangeInfo());
    }

}
