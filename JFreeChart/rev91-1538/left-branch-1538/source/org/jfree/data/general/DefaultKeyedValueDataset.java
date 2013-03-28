

package org.jfree.data.general;

import java.io.Serializable;

import org.jfree.data.DefaultKeyedValue;
import org.jfree.data.KeyedValue;
import org.jfree.util.ObjectUtilities;


public class DefaultKeyedValueDataset extends AbstractDataset
        implements KeyedValueDataset, Serializable {

    
    private static final long serialVersionUID = -8149484339560406750L;

    
    private KeyedValue data;

    
    public DefaultKeyedValueDataset() {
        this(null);
    }

    
    public DefaultKeyedValueDataset(Comparable key, Number value) {
        this(new DefaultKeyedValue(key, value));
    }

    
    public DefaultKeyedValueDataset(KeyedValue data) {
        this.data = data;
    }

    
    public Comparable getKey() {
        Comparable result = null;
        if (this.data != null) {
            result = this.data.getKey();
        }
        return result;
    }

    
    public Number getValue() {
        Number result = null;
        if (this.data != null) {
            result = this.data.getValue();
        }
        return result;
    }

    
    public void updateValue(Number value) {
        if (this.data == null) {
            throw new RuntimeException("updateValue: can't update null.");
        }
        setValue(this.data.getKey(), value);
    }

    
    public void setValue(Comparable key, Number value) {
        this.data = new DefaultKeyedValue(key, value);
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyedValueDataset)) {
            return false;
        }
        KeyedValueDataset that = (KeyedValueDataset) obj;
        if (this.data == null) {
            if (that.getKey() != null || that.getValue() != null) {
                return false;
            }
            return true;
        }
        if (!ObjectUtilities.equal(this.data.getKey(), that.getKey())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.data.getValue(), that.getValue())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return (this.data != null ? this.data.hashCode() : 0);
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultKeyedValueDataset clone
            = (DefaultKeyedValueDataset) super.clone();
        return clone;
    }

}
