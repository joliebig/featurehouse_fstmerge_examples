

package org.jfree.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SortOrder;


public class DefaultKeyedValues implements KeyedValues, 
                                           Cloneable, PublicCloneable, 
                                           Serializable {

    
    private static final long serialVersionUID = 8468154364608194797L;
    
    
    private List data;

    
    public DefaultKeyedValues() {
        this.data = new java.util.ArrayList();
    }

    
    public int getItemCount() {
        return this.data.size();
    }

    
    public Number getValue(int item) {
        Number result = null;
        KeyedValue kval = (KeyedValue) this.data.get(item);
        if (kval != null) {
            result = kval.getValue();
        }
        return result;
    }

    
    public Comparable getKey(int index) {
        Comparable result = null;
        KeyedValue item = (KeyedValue) this.data.get(index);
        if (item != null) {
            result = item.getKey();
        }
        return result;
    }

    
    public int getIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int i = 0;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedValue kv = (KeyedValue) iterator.next();
            if (kv.getKey().equals(key)) {
                return i;
            }
            i++;
        }
        return -1;  
    }

    
    public List getKeys() {
        List result = new java.util.ArrayList();
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedValue kv = (KeyedValue) iterator.next();
            result.add(kv.getKey());
        }
        return result;
    }

    
    public Number getValue(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("Key not found: " + key);
        }
        return getValue(index);
    }

    
    public void addValue(Comparable key, double value) {
        addValue(key, new Double(value)); 
    }
    
    
    public void addValue(Comparable key, Number value) {
        setValue(key, value);
    }

    
    public void setValue(Comparable key, double value) {
        setValue(key, new Double(value));   
    }
    
    
    public void setValue(Comparable key, Number value) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            DefaultKeyedValue kv = (DefaultKeyedValue) this.data.get(keyIndex);
            kv.setValue(value);
        }
        else {
            KeyedValue kv = new DefaultKeyedValue(key, value);
            this.data.add(kv);
        }
    }
    
    
    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    
    public void insertValue(int position, Comparable key, Number value) {
        if (position < 0 || position > this.data.size()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int pos = this.getIndex(key);
        if (pos >= 0) {
            this.data.remove(pos);
        }
        KeyedValue kv = new DefaultKeyedValue(key, value);
        if (position <= this.data.size()) {
            this.data.add(position, kv);
        }
        else {
            this.data.add(kv);
        }
    }

    
    public void removeValue(int index) {
        this.data.remove(index);
    }

    
    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index >= 0) {
            removeValue(index);
        }
    }
    
    
    public void clear() {
        this.data.clear();
    }

    
    public void sortByKeys(SortOrder order) {
        Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_KEY, order);
        Collections.sort(this.data, comparator);
    }

    
    public void sortByValues(SortOrder order) {
        Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_VALUE, order);
        Collections.sort(this.data, comparator);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof KeyedValues)) {
            return false;
        }

        KeyedValues that = (KeyedValues) obj;
        int count = getItemCount();
        if (count != that.getItemCount()) {
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
        return (this.data != null ? this.data.hashCode() : 0);
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValues clone = (DefaultKeyedValues) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;    
    }
    
}
