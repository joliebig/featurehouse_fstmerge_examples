

package org.jfree.data.general.junit;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.PublicCloneable;

public class TestIntervalCategoryDataset extends AbstractDataset
        implements IntervalCategoryDataset, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -8168173757291644622L;

    
    private KeyedObjects2D data;

    
    public TestIntervalCategoryDataset() {
        this.data = new KeyedObjects2D();
    }

    
    public int getRowCount() {
        return this.data.getRowCount();
    }

    
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    
    public Number getValue(int row, int column) {
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(row,
                column);
        if (item == null) {
            return null;
        }
        return item.getValue();
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
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(rowKey,
                columnKey);
        if (item == null) {
            return null;
        }
        return item.getValue();
    }

    
    public void addItem(Number value, Number lower, Number upper,
            Comparable rowKey, Comparable columnKey) {
        IntervalDataItem item = new IntervalDataItem(value, lower, upper);
        this.data.addObject(item, rowKey, columnKey);
        fireDatasetChanged();
    }

    
    public void addItem(double value, double lower, double upper,
            Comparable rowKey, Comparable columnKey) {
        addItem(new Double(value), new Double(lower), new Double(upper),
                rowKey, columnKey);
    }

    
    public void setItem(Number value, Number lower, Number upper,
            Comparable rowKey, Comparable columnKey) {
        IntervalDataItem item = new IntervalDataItem(value, lower, upper);
        this.data.addObject(item, rowKey, columnKey);
        fireDatasetChanged();
    }

    
    public void setItem(double value, double lower, double upper,
            Comparable rowKey, Comparable columnKey) {
        setItem(new Double(value), new Double(lower), new Double(upper),
                rowKey, columnKey);
    }

    
    public void removeItem(Comparable rowKey, Comparable columnKey) {
        this.data.removeObject(rowKey, columnKey);
        fireDatasetChanged();
    }

    
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        fireDatasetChanged();
    }

    
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        fireDatasetChanged();
    }

    
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        fireDatasetChanged();
    }

    
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        fireDatasetChanged();
    }

    
    public void clear() {
        this.data.clear();
        fireDatasetChanged();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TestIntervalCategoryDataset)) {
            return false;
        }
        TestIntervalCategoryDataset that = (TestIntervalCategoryDataset) obj;
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
        TestIntervalCategoryDataset clone = (TestIntervalCategoryDataset)
                super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }

    public Number getStartValue(int series, int category) {
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(series,
                category);
        if (item == null) {
            return null;
        }
        return item.getLowerBound();
    }

    public Number getStartValue(Comparable series, Comparable category) {
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(series,
                category);
        if (item == null) {
            return null;
        }
        return item.getLowerBound();
    }

    public Number getEndValue(int series, int category) {
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(series,
                category);
        if (item == null) {
            return null;
        }
        return item.getUpperBound();
    }

    public Number getEndValue(Comparable series, Comparable category) {
        IntervalDataItem item = (IntervalDataItem) this.data.getObject(series,
                category);
        if (item == null) {
            return null;
        }
        return item.getUpperBound();
    }

}

