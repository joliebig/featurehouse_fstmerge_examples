

package org.jfree.chart.renderer;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class AreaRendererEndType implements Serializable {

    
    private static final long serialVersionUID = -1774146392916359839L;
    
    
    public static final AreaRendererEndType TAPER = new AreaRendererEndType(
        "AreaRendererEndType.TAPER"
    );

    
    public static final AreaRendererEndType TRUNCATE = new AreaRendererEndType(
        "AreaRendererEndType.TRUNCATE"
    );
    
    
    public static final AreaRendererEndType LEVEL = new AreaRendererEndType(
        "AreaRendererEndType.LEVEL"
    );

    
    private String name;

    
    private AreaRendererEndType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof AreaRendererEndType)) {
            return false;
        }

        AreaRendererEndType t = (AreaRendererEndType) o;
        if (!this.name.equals(t.toString())) {
            return false;
        }

        return true;

    }
    
    
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        if (this.equals(AreaRendererEndType.LEVEL)) {
            result = AreaRendererEndType.LEVEL;
        }
        else if (this.equals(AreaRendererEndType.TAPER)) {
            result = AreaRendererEndType.TAPER;
        }
        else if (this.equals(AreaRendererEndType.TRUNCATE)) {
            result = AreaRendererEndType.TRUNCATE;
        }
        return result;
    }

}
