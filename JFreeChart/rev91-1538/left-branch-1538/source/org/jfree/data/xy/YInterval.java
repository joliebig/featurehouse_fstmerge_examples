

package org.jfree.data.xy;

import java.io.Serializable;


public class YInterval implements Serializable {

    
    private double y;

    
    private double yLow;

    
    private double yHigh;

    
    public YInterval(double y, double yLow, double yHigh) {
        this.y = y;
        this.yLow = yLow;
        this.yHigh = yHigh;
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
        if (!(obj instanceof YInterval)) {
            return false;
        }
        YInterval that = (YInterval) obj;
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
