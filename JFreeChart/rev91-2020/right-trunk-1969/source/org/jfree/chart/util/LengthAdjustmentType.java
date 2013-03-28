

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class LengthAdjustmentType implements Serializable {

    
    private static final long serialVersionUID = -6097408511380545010L;

    
    public static final LengthAdjustmentType NO_CHANGE
            = new LengthAdjustmentType("NO_CHANGE");

    
    public static final LengthAdjustmentType EXPAND
            = new LengthAdjustmentType("EXPAND");

    
    public static final LengthAdjustmentType CONTRACT
            = new LengthAdjustmentType("CONTRACT");

    
    private String name;

    
    private LengthAdjustmentType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LengthAdjustmentType)) {
            return false;
        }
        final LengthAdjustmentType that = (LengthAdjustmentType) obj;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(LengthAdjustmentType.NO_CHANGE)) {
            return LengthAdjustmentType.NO_CHANGE;
        }
        else if (this.equals(LengthAdjustmentType.EXPAND)) {
            return LengthAdjustmentType.EXPAND;
        }
        else if (this.equals(LengthAdjustmentType.CONTRACT)) {
            return LengthAdjustmentType.CONTRACT;
        }
        return null;
    }

}
