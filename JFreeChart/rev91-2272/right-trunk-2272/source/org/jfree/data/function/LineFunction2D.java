

package org.jfree.data.function;

import java.io.Serializable;
import org.jfree.chart.util.HashUtilities;


public class LineFunction2D implements Function2D, Serializable {

    
    private double a;

    
    private double b;

    
    public LineFunction2D(double a, double b) {
        this.a = a;
        this.b = b;
    }

    
    public double getIntercept() {
        return this.a;
    }

    
    public double getSlope() {
        return this.b;
    }

    
    public double getValue(double x) {
        return this.a + this.b * x;
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof LineFunction2D)) {
            return false;
        }
        LineFunction2D that = (LineFunction2D) obj;
        if (this.a != that.a) {
            return false;
        }
        if (this.b != that.b) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 29;
        result = HashUtilities.hashCode(result, this.a);
        result = HashUtilities.hashCode(result, this.b);
        return result;
    }
}
