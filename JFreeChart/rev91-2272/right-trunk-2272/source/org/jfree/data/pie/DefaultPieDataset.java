

package org.jfree.data.pie;

import org.jfree.data.SelectableValue;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.KeyedObjects;
import org.jfree.data.KeyedValues;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.event.DatasetChangeEvent;


public class DefaultPieDataset extends AbstractPieDataset
        implements PieDataset, PieDatasetSelectionState, Cloneable,
        PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 2904745139106540618L;

    
    private KeyedObjects data;

    
    public DefaultPieDataset() {
        this.data = new KeyedObjects();
        setSelectionState(this);
    }

    
    public DefaultPieDataset(KeyedValues data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        this.data = new KeyedObjects();
        for (int i = 0; i < data.getItemCount(); i++) {
            SelectableValue dataItem = new SelectableValue(data.getValue(i));
            this.data.addObject(data.getKey(i), dataItem);
        }
    }

    
    public int getItemCount() {
        return this.data.getItemCount();
    }

    
    public List getKeys() {
        
        return Collections.unmodifiableList(this.data.getKeys());
    }

    
    public Comparable getKey(int item) {
        return this.data.getKey(item);
    }

    
    public int getIndex(Comparable key) {
        return this.data.getIndex(key);
    }

    
    public Number getValue(int item) {
        Number result = null;
        if (getItemCount() > item) {
            SelectableValue dataItem = (SelectableValue) this.data.getObject(item);
            result = dataItem.getValue();
        }
        return result;
    }

    
    public Number getValue(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        SelectableValue dataItem = (SelectableValue) this.data.getObject(key);
        return dataItem.getValue();
    }

    
    public void setValue(Comparable key, Number value) {
        int index = this.data.getIndex(key);
        PieDatasetChangeType ct = PieDatasetChangeType.ADD;
        if (index >= 0) {
            ct = PieDatasetChangeType.UPDATE;
        }

        this.data.setObject(key, new SelectableValue(value));
        PieDatasetChangeInfo info = new PieDatasetChangeInfo(ct, index, index);
        fireDatasetChanged(info);
    }

    
    public void setValue(Comparable key, double value) {
        setValue(key, new Double(value));
    }

    
    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    
    public void insertValue(int position, Comparable key, Number value) {
        this.data.insertValue(position, key, value);
        PieDatasetChangeType ct = PieDatasetChangeType.ADD;
        
        fireDatasetChanged(new PieDatasetChangeInfo(ct, position, position));
    }

    
    public void remove(Comparable key) {
        int i = getIndex(key);
        this.data.removeValue(key);
        PieDatasetChangeType ct = PieDatasetChangeType.REMOVE;
        fireDatasetChanged(new PieDatasetChangeInfo(ct, i, i));
    }

    
    public void clear() {
        if (getItemCount() > 0) {
            this.data.clear();
            PieDatasetChangeType ct = PieDatasetChangeType.CLEAR;
            fireDatasetChanged(new PieDatasetChangeInfo(ct, -1, -1));
        }
    }

    
    public void sortByKeys(SortOrder order) {
        this.data.sortByKeys(order);
        PieDatasetChangeType ct = PieDatasetChangeType.UPDATE;
        fireDatasetChanged(new PieDatasetChangeInfo(ct, 0,
                getItemCount() - 1));
    }

    
    public void sortByValues(SortOrder order) {
        this.data.sortByObjects(order);
        PieDatasetChangeType ct = PieDatasetChangeType.UPDATE;
        fireDatasetChanged(new PieDatasetChangeInfo(ct, 0,
                getItemCount() - 1));
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PieDataset)) {
            return false;
        }
        PieDataset that = (PieDataset) obj;
        int count = getItemCount();
        if (that.getItemCount() != count) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            Comparable k1 = getKey(i);
            Comparable k2 = that.getKey(i);
            if (!k1.equals(k2)) {
                return false;
            }

            Number v1 = getValue(i);
            Number v2 = that.getValue(i);
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
        return true;

    }

    
    public int hashCode() {
        return this.data.hashCode();
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultPieDataset clone = (DefaultPieDataset) super.clone();
        clone.data = (KeyedObjects) this.data.clone();
        return clone;
    }

    public boolean isSelected(Comparable key) {
        SelectableValue item = (SelectableValue) this.data.getObject(key);
        return item.isSelected();
    }

    public void setSelected(Comparable key, boolean selected) {
        setSelected(key, selected, true);
    }

    public void setSelected(Comparable key, boolean selected, boolean notify) {
        SelectableValue item = (SelectableValue) this.data.getObject(key);
        item.setSelected(selected);
        if (notify) {
            fireSelectionEvent();
        }
    }

    public void clearSelection() {
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            SelectableValue item = (SelectableValue) this.data.getObject(i);
            item.setSelected(false);
        }
        fireSelectionEvent();
    }

    public void fireSelectionEvent() {
        this.fireDatasetChanged(new DatasetChangeInfo());
    }

}
