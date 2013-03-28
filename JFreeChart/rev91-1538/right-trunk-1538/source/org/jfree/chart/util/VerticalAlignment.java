

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class VerticalAlignment implements Serializable {

    
    private static final long serialVersionUID = 7272397034325429853L;
    
    
    public static final VerticalAlignment TOP 
            = new VerticalAlignment("VerticalAlignment.TOP");

    
    public static final VerticalAlignment BOTTOM 
            = new VerticalAlignment("VerticalAlignment.BOTTOM");

    
    public static final VerticalAlignment CENTER 
            = new VerticalAlignment("VerticalAlignment.CENTER");

    
    private String name;

    
    private VerticalAlignment(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VerticalAlignment)) {
            return false;
        }

        VerticalAlignment alignment = (VerticalAlignment) obj;
        if (!this.name.equals(alignment.name)) {
            return false;
        }
        return true;
    }
    
    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(VerticalAlignment.TOP)) {
            return VerticalAlignment.TOP;
        }
        else if (this.equals(VerticalAlignment.BOTTOM)) {
            return VerticalAlignment.BOTTOM;
        }
        else if (this.equals(VerticalAlignment.CENTER)) {
            return VerticalAlignment.CENTER;
        }
        else {
            return null;  
        }
    }
    
}
