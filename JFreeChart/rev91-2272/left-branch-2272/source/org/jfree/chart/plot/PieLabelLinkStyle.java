

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class PieLabelLinkStyle implements Serializable {

    
    public static final PieLabelLinkStyle STANDARD
            = new PieLabelLinkStyle("PieLabelLinkStyle.STANDARD");

    
    public static final PieLabelLinkStyle QUAD_CURVE
            = new PieLabelLinkStyle("PieLabelLinkStyle.QUAD_CURVE");

    
    public static final PieLabelLinkStyle CUBIC_CURVE
            = new PieLabelLinkStyle("PieLabelLinkStyle.CUBIC_CURVE");

    
    private String name;

    
    private PieLabelLinkStyle(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PieLabelLinkStyle)) {
            return false;
        }
        PieLabelLinkStyle style = (PieLabelLinkStyle) obj;
        if (!this.name.equals(style.toString())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        if (this.equals(PieLabelLinkStyle.STANDARD)) {
            result = PieLabelLinkStyle.STANDARD;
        }
        else if (this.equals(PieLabelLinkStyle.QUAD_CURVE)) {
            result = PieLabelLinkStyle.QUAD_CURVE;
        }
        else if (this.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
            result = PieLabelLinkStyle.CUBIC_CURVE;
        }
        return result;
    }

}
