

package org.jfree.data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.util.PublicCloneable;


public class KeyedObjects  implements Cloneable, PublicCloneable, Serializable {

    
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
        if (item >= 0 && item < this.data.size()) {
            KeyedObject kobj = (KeyedObject) this.data.get(item);
            if (kobj != null) {
                result = kobj.getObject();
            }
        }
        return result;
    }

    
    public Comparable getKey(int index) {
        Comparable result = null;
        if (index >= 0 && index < this.data.size()) {
            KeyedObject item = (KeyedObject) this.data.get(index);
            if (item != null) {
                result = item.getKey();
            }
        }
        return result;
    }

    
    public int getIndex(Comparable key) {
        int result = -1;
        int i = 0;
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            KeyedObject ko = (KeyedObject) iterator.next();
            if (ko.getKey().equals(key)) {
                result = i;
            }
            i++;
        }
        return result;
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
        return getObject(getIndex(key));
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

    
    public void removeValue(int index) {
        this.data.remove(index);
    }

    
    public void removeValue(Comparable key) {
        removeValue(getIndex(key));
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
    
    
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (!(o instanceof KeyedObjects)) {
            return false;
        }

        KeyedObjects kos = (KeyedObjects) o;
        int count = getItemCount();
        if (count != kos.getItemCount()) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            Comparable k1 = getKey(i);
            Comparable k2 = kos.getKey(i);
            if (!k1.equals(k2)) {
                return false;
            }
            Object o1 = getObject(i);
            Object o2 = kos.getObject(i);
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
    
}
