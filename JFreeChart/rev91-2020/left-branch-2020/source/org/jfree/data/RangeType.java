

package org.jfree.data;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class RangeType implements Serializable {

    
    private static final long serialVersionUID = -9073319010650549239L;

    
    public static final RangeType FULL = new RangeType("RangeType.FULL");

    
    public static final RangeType POSITIVE
        = new RangeType("RangeType.POSITIVE");

    
    public static final RangeType NEGATIVE
        = new RangeType("RangeType.NEGATIVE");

    
    private String name;

    
    private RangeType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RangeType)) {
            return false;
        }
        RangeType that = (RangeType) obj;
        if (!this.name.equals(that.toString())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(RangeType.FULL)) {
            return RangeType.FULL;
        }
        else if (this.equals(RangeType.POSITIVE)) {
            return RangeType.POSITIVE;
        }
        else if (this.equals(RangeType.NEGATIVE)) {
            return RangeType.NEGATIVE;
        }
        return null;
    }

}
