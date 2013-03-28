

package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class PlotOrientation implements Serializable {

    
    private static final long serialVersionUID = -2508771828190337782L;
    
    
    public static final PlotOrientation HORIZONTAL
        = new PlotOrientation("PlotOrientation.HORIZONTAL");

    
    public static final PlotOrientation VERTICAL
        = new PlotOrientation("PlotOrientation.VERTICAL");

    
    private String name;

    
    private PlotOrientation(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof PlotOrientation)) {
            return false;
        }

        PlotOrientation orientation = (PlotOrientation) o;
        if (!this.name.equals(orientation.toString())) {
            return false;
        }

        return true;

    }
    
    
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        if (this.equals(PlotOrientation.HORIZONTAL)) {
            result = PlotOrientation.HORIZONTAL;
        }
        else if (this.equals(PlotOrientation.VERTICAL)) {
            result = PlotOrientation.VERTICAL;
        }
        return result;
    }

}
