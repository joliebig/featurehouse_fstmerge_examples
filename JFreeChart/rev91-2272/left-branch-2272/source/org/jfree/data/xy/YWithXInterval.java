

package org.jfree.data.xy;

import java.io.Serializable;


public class YWithXInterval implements Serializable {

    
    private double y;

    
    private double xLow;

    
    private double xHigh;

    
    public YWithXInterval(double y, double xLow, double xHigh) {
        this.y = y;
        this.xLow = xLow;
        this.xHigh = xHigh;
    }

    
    public double getY() {
        return this.y;
    }

    
    public double getXLow() {
        return this.xLow;
    }

    
    public double getXHigh() {
        return this.xHigh;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YWithXInterval)) {
            return false;
        }
        YWithXInterval that = (YWithXInterval) obj;
        if (this.y != that.y) {
            return false;
        }
        if (this.xLow != that.xLow) {
            return false;
        }
        if (this.xHigh != that.xHigh) {
            return false;
        }
        return true;
    }

}
