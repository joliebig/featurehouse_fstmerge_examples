

package org.jfree.data.general;

import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;


public class DefaultValueDataset extends AbstractDataset
        implements ValueDataset, Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 8137521217249294891L;

    
    private Number value;

    
    public DefaultValueDataset() {
        this(null);
    }

    
    public DefaultValueDataset(double value) {
        this(new Double(value));
    }

    
    public DefaultValueDataset(Number value) {
        super();
        this.value = value;
    }

    
    public Number getValue() {
        return this.value;
    }

    
    public void setValue(Number value) {
        this.value = value;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ValueDataset) {
            ValueDataset vd = (ValueDataset) obj;
            return ObjectUtilities.equal(this.value, vd.getValue());
        }
        return false;
    }

    
    public int hashCode() {
        return (this.value != null ? this.value.hashCode() : 0);
    }

}
