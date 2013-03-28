

package org.jfree.data;

import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;



public class KeyedObject implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 2677930479256885863L;
    
    
    private Comparable key;

    
    private Object object;

    
    public KeyedObject(Comparable key, Object object) {
        this.key = key;
        this.object = object;
    }

    
    public Comparable getKey() {
        return this.key;
    }

    
    public Object getObject() {
        return this.object;
    }

    
    public void setObject(Object object) {
        this.object = object;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        KeyedObject clone = (KeyedObject) super.clone();
        if (this.object instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.object;
            clone.object = pc.clone();
        }
        return clone;      
    }
    
    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof KeyedObject)) {
            return false;
        }
        KeyedObject that = (KeyedObject) obj;
        if (!ObjectUtilities.equal(this.key, that.key)) {
            return false;
        }

        if (!ObjectUtilities.equal(this.object, that.object)) {
            return false;
        }

        return true;
    }
    
}
