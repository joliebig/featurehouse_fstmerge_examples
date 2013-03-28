

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class Rotation implements Serializable {

    
    private static final long serialVersionUID = -4662815260201591676L;
    
    
    public static final Rotation CLOCKWISE 
        = new Rotation("Rotation.CLOCKWISE", -1.0);

    
    public static final Rotation ANTICLOCKWISE 
        = new Rotation("Rotation.ANTICLOCKWISE", 1.0);

    
    private String name;
    
    
    private double factor;

    
    private Rotation(final String name, final double factor) {
        this.name = name;
        this.factor = factor;
    }

    
    public String toString() {
        return this.name;
    }

    
    public double getFactor() {
        return this.factor;
    }

    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rotation)) {
            return false;
        }

        final Rotation rotation = (Rotation) o;

        if (this.factor != rotation.factor) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        final long temp = Double.doubleToLongBits(this.factor);
        return (int) (temp ^ (temp >>> 32));
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(Rotation.CLOCKWISE)) {
            return Rotation.CLOCKWISE;
        }
        else if (this.equals(Rotation.ANTICLOCKWISE)) {
            return Rotation.ANTICLOCKWISE;
        }      
        return null;
    }

}