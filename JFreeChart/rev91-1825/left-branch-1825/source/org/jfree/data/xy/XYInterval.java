

package org.jfree.data.xy;

import java.io.Serializable;


public class XYInterval implements Serializable {

    
    private double xLow;

    
    private double xHigh;

    
    private double y;

    
    private double yLow;

    
    private double yHigh;

    
    public XYInterval(double xLow, double xHigh, double y, double yLow,
            double yHigh) {
        this.xLow = xLow;
        this.xHigh = xHigh;
        this.y = y;
        this.yLow = yLow;
        this.yHigh = yHigh;
    }

    
    public double getXLow() {
        return this.xLow;
    }

    
    public double getXHigh() {
        return this.xHigh;
    }

    
    public double getY() {
        return this.y;
    }

    
    public double getYLow() {
        return this.yLow;
    }

    
    public double getYHigh() {
        return this.yHigh;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYInterval)) {
            return false;
        }
        XYInterval that = (XYInterval) obj;
        if (this.xLow != that.xLow) {
            return false;
        }
        if (this.xHigh != that.xHigh) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (this.yLow != that.yLow) {
            return false;
        }
        if (this.yHigh != that.yHigh) {
            return false;
        }
        return true;
    }

}
