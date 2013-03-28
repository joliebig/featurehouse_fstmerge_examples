

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class GradientPaintTransformType implements Serializable {

    
    private static final long serialVersionUID = 8331561784933982450L;
    
    
    public static final GradientPaintTransformType VERTICAL 
        = new GradientPaintTransformType("GradientPaintTransformType.VERTICAL");

    
    public static final GradientPaintTransformType HORIZONTAL 
        = new GradientPaintTransformType(
                "GradientPaintTransformType.HORIZONTAL");

    
    public static final GradientPaintTransformType CENTER_VERTICAL 
        = new GradientPaintTransformType(
                "GradientPaintTransformType.CENTER_VERTICAL");

    
    public static final GradientPaintTransformType CENTER_HORIZONTAL 
        = new GradientPaintTransformType(
                "GradientPaintTransformType.CENTER_HORIZONTAL");
        
    
    private String name;

    
    private GradientPaintTransformType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GradientPaintTransformType)) {
            return false;
        }

        GradientPaintTransformType t = (GradientPaintTransformType) obj;
        if (!this.name.equals(t.name)) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        GradientPaintTransformType result = null;
        if (this.equals(GradientPaintTransformType.HORIZONTAL)) {
            result = GradientPaintTransformType.HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.VERTICAL)) {
            result = GradientPaintTransformType.VERTICAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_HORIZONTAL)) {
            result = GradientPaintTransformType.CENTER_HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_VERTICAL)) {
            result = GradientPaintTransformType.CENTER_VERTICAL;
        }
        return result;
    }
    
}

