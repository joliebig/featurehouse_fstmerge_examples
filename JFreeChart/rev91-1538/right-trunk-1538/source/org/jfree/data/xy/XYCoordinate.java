

package org.jfree.data.xy;

import java.io.Serializable;


public class XYCoordinate implements Comparable, Serializable {

    
    private double x;

    
    private double y;

    
    public XYCoordinate() {
        this(0.0, 0.0);
    }

    
    public XYCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    
    public double getX() {
        return this.x;
    }

    
    public double getY() {
        return this.y;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYCoordinate)) {
            return false;
        }
        XYCoordinate that = (XYCoordinate) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.x);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    
    public int compareTo(Object obj) {
        if (!(obj instanceof XYCoordinate)) {
            throw new IllegalArgumentException("Incomparable object.");
        }
        XYCoordinate that = (XYCoordinate) obj;
        if (this.x > that.x) {
            return 1;
        }
        else if (this.x < that.x) {
            return -1;
        }
        else {
            if (this.y > that.y) {
                return 1;
            }
            else if (this.y < that.y) {
                return -1;
            }
        }
        return 0;
    }

}
