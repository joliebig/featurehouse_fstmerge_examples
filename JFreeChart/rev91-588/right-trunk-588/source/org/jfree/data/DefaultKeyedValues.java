

package org.jfree.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SortOrder;


public class DefaultKeyedValues implements KeyedValues, 
                                           Cloneable, PublicCloneable, 
                                           Serializable {

    
    private static final long serialVersionUID = 8468154364608194797L;
    
    
    private ArrayList keys;
    private ArrayList values;
    
    
    private HashMap indexMap; 

  
    public DefaultKeyedValues() {
        this.keys = new ArrayList();
        this.values = new ArrayList();
        this.indexMap = new HashMap();
    }

    
    public int getItemCount() {
        return this.indexMap.size();
    }

    
    public Number getValue(int item) {
        return (Number) this.values.get(item);
    }

    
    public Comparable getKey(int index) {
        return (Comparable) this.keys.get(index);
    }

    
    public int getIndex(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        final Integer i = (Integer) this.indexMap.get(key);
        if (i == null) {
            return -1;  
        }
        return i.intValue();
    }

    
    public List getKeys() {
        return (List) this.keys.clone();
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
            this.keys.set(keyIndex, key);
            this.values.set(keyIndex, value);
        }
        else {
            this.keys.add(key);
            this.values.add(value);
            this.indexMap.put(key, new Integer(this.keys.size() - 1));
        }
    }
    
    
    public void insertValue(int position, Comparable key, double value) {
        insertValue(position, key, new Double(value));
    }

    
    public void insertValue(int position, Comparable key, Number value) {
        if (position < 0 || position > getItemCount()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int pos = getIndex(key);
        if (pos == position) {
            this.keys.set(pos, key);
            this.values.set(pos, value);
        }
        else {
            if (pos >= 0) {
                this.keys.remove(pos);
                this.values.remove(pos);
            }
          
            this.keys.add(position, key);
            this.values.add(position, value);
            rebuildIndex();
        }
    }

    
    private void rebuildIndex () {
        this.indexMap.clear();
        for (int i = 0; i < this.keys.size(); i++) {
            final Object key = this.keys.get(i);
            this.indexMap.put(key, new Integer(i));
        }
    }

    
    public void removeValue(int index) {
        this.keys.remove(index);
        this.values.remove(index);

        
        if (index < this.keys.size()) {
            rebuildIndex();
        }
    }

    
    public void removeValue(Comparable key) {
        int index = getIndex(key);
        if (index >= 0) {
            removeValue(index);
        }
    }
    
    
    public void clear() {
        this.keys.clear();
        this.values.clear();
        this.indexMap.clear();
    }

    
    public void sortByKeys(SortOrder order) {
      final int size = this.keys.size();
      final DefaultKeyedValue[] data = new DefaultKeyedValue[size];

      for (int i = 0; i < size; i++) {
          data[i] = new DefaultKeyedValue((Comparable) this.keys.get(i), 
                  (Number) this.values.get(i));
      }

      Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_KEY, order);
      Arrays.sort(data, comparator);
      clear();

      for (int i = 0; i < data.length; i++) {
          final DefaultKeyedValue value = data[i];
          addValue(value.getKey(), value.getValue());
      }
    }

    
    public void sortByValues(SortOrder order) {
        final int size = this.keys.size();
        final DefaultKeyedValue[] data = new DefaultKeyedValue[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DefaultKeyedValue((Comparable) this.keys.get(i), 
                    (Number) this.values.get(i));
        }

        Comparator comparator = new KeyedValueComparator(
                KeyedValueComparatorType.BY_VALUE, order);
        Arrays.sort(data, comparator);

        clear();
        for (int i = 0; i < data.length; i++) {
            final DefaultKeyedValue value = data[i];
            addValue(value.getKey(), value.getValue());
        }
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
        return (this.keys != null ? this.keys.hashCode() : 0);
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValues clone = (DefaultKeyedValues) super.clone();
        clone.keys = (ArrayList) this.keys.clone();
        clone.values = (ArrayList) this.values.clone();
        clone.indexMap = (HashMap) this.indexMap.clone();
        return clone;
    }
    
}