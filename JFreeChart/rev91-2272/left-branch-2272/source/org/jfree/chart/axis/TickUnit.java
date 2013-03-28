

package org.jfree.chart.axis;

import java.io.Serializable;


public abstract class TickUnit implements Comparable, Serializable {

    
    private static final long serialVersionUID = 510179855057013974L;

    
    private double size;

    
    private int minorTickCount;

    
    public TickUnit(double size) {
        this.size = size;
    }

    
    public TickUnit(double size, int minorTickCount) {
        this.size = size;
        this.minorTickCount = minorTickCount;
    }

    
    public double getSize() {
        return this.size;
    }

    
    public int getMinorTickCount() {
        return this.minorTickCount;
    }

    
    public String valueToString(double value) {
        return String.valueOf(value);
    }

    
    public int compareTo(Object object) {

        if (object instanceof TickUnit) {
            TickUnit other = (TickUnit) object;
            if (this.size > other.getSize()) {
                return 1;
            }
            else if (this.size < other.getSize()) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            return -1;
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TickUnit)) {
            return false;
        }
        TickUnit that = (TickUnit) obj;
        if (this.size != that.size) {
            return false;
        }
        if (this.minorTickCount != that.minorTickCount) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        long temp = this.size != +0.0d ? Double.doubleToLongBits(this.size)
                : 0L;
        return (int) (temp ^ (temp >>> 32));
    }

}
