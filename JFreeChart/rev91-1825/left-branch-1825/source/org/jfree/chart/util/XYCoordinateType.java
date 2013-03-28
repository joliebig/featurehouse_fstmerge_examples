

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class XYCoordinateType implements Serializable {

    
    public static final XYCoordinateType DATA
            = new XYCoordinateType("XYCoordinateType.DATA");

    
    public static final XYCoordinateType RELATIVE
            = new XYCoordinateType("XYCoordinateType.RELATIVE");

    
    public static final XYCoordinateType INDEX
            = new XYCoordinateType("XYCoordinateType.INDEX");

    
    private String name;

    
    private XYCoordinateType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XYCoordinateType)) {
            return false;
        }
        XYCoordinateType order = (XYCoordinateType) obj;
        if (!this.name.equals(order.toString())) {
            return false;
        }
        return true;
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(XYCoordinateType.DATA)) {
            return XYCoordinateType.DATA;
        }
        else if (this.equals(XYCoordinateType.RELATIVE)) {
            return XYCoordinateType.RELATIVE;
        }
        else if (this.equals(XYCoordinateType.INDEX)) {
            return XYCoordinateType.INDEX;
        }
        return null;
    }

}
