

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class UnitType implements Serializable {

    
    private static final long serialVersionUID = 6531925392288519884L;    
    
    
    public static final UnitType ABSOLUTE = new UnitType("UnitType.ABSOLUTE");

    
    public static final UnitType RELATIVE = new UnitType("UnitType.RELATIVE");

    
    private String name;

    
    private UnitType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnitType)) {
            return false;
        }
        UnitType that = (UnitType) obj;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(UnitType.ABSOLUTE)) {
            return UnitType.ABSOLUTE;
        }
        else if (this.equals(UnitType.RELATIVE)) {
            return UnitType.RELATIVE;
        }
        return null;
    }
    
}
