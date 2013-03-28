

package org.jfree.data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.util.PublicCloneable;


public class KeyedObjects implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 1321582394193530984L;
    
    
    private List data;

    
    public KeyedObjects() {
        this.data = new java.util.ArrayList();
    }

    
    public int getItemCount() {
        return this.data.size();
    }

    
    public Object getObject(int item) {
        Object result = null;
        KeyedObject kobj = (KeyedObject) this.data.get(item);
        if (kobj != null) {
            result = kobj.getObject();
        }
        return result;
    }

    
    public Comparable getKey(int index) {
        Comparable result = null;
        KeyedObject item = (KeyedObject) this.data.get(index);
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
            KeyedObject ko = (KeyedObject) iterator.next();
            if (ko.getKey().equals(key)) {
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
            KeyedObject ko = (KeyedObject) iterator.next();
            result.add(ko.getKey());
        }
        return result;
    }

    
    public Object getObject(Comparable key) {
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key 
                    + ") is not recognised.");
        }
        return getObject(index);
    }

    
    public void addObject(Comparable key, Object object) {
        setObject(key, object);
    }

    
    public void setObject(Comparable key, Object object) {
        int keyIndex = getIndex(key);
        if (keyIndex >= 0) {
            KeyedObject ko = (KeyedObject) this.data.get(keyIndex);
            ko.setObject(object);
        }
        else {
            KeyedObject ko = new KeyedObject(key, object);
            this.data.add(ko);
        }
    }

    
    public void insertValue(int position, Comparable key, Object value) {
        if (position < 0 || position > this.data.size()) {
            throw new IllegalArgumentException("'position' out of bounds.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        int pos = getIndex(key);
        if (pos >= 0) {
            this.data.remove(pos);
        }
        KeyedObject item = new KeyedObject(key, value);
        if (position <= this.data.size()) {
            this.data.add(position, item);
        }
        else {
            this.data.add(item);
        }
    }

    
    public void removeValue(int index) {
        this.data.remove(index);
    }

    
    public void removeValue(Comparable key) {
        
        int index = getIndex(key);
        if (index < 0) {
            throw new UnknownKeyException("The key (" + key.toString() 
                    + ") is not recognised.");
        }
        removeValue(index);
    }
    
    
    public void clear() {
        this.data.clear();
    }

    
    public Object clone() throws CloneNotSupportedException {
        KeyedObjects clone = (KeyedObjects) super.clone();
        clone.data = new java.util.ArrayList();
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedObject ko = (KeyedObject) iterator.next();
            clone.data.add(ko.clone());
        }
        return clone;      
    }
    
    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedObjects)) {
            return false;
        }
        KeyedObjects that = (KeyedObjects) obj;
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
            Object o1 = getObject(i);
            Object o2 = that.getObject(i);
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
            }
            else {
                if (!o1.equals(o2)) {
                    return false;
                }
            }
        }
        return true;

    }
    
    
    public int hashCode() {
        return (this.data != null ? this.data.hashCode() : 0);
    }
  
}
