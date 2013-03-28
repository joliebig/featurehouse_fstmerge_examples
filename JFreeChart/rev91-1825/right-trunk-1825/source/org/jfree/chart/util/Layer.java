

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class Layer implements Serializable {

    
    private static final long serialVersionUID = -1470104570733183430L;

    
    public static final Layer FOREGROUND = new Layer("Layer.FOREGROUND");

    
    public static final Layer BACKGROUND = new Layer("Layer.BACKGROUND");

    
    private String name;

    
    private Layer(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Layer)) {
            return false;
        }

        Layer layer = (Layer) obj;
        if (!this.name.equals(layer.name)) {
            return false;
        }

        return true;

    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        Layer result = null;
        if (this.equals(Layer.FOREGROUND)) {
            result = Layer.FOREGROUND;
        }
        else if (this.equals(Layer.BACKGROUND)) {
            result = Layer.BACKGROUND;
        }
        return result;
    }

}

