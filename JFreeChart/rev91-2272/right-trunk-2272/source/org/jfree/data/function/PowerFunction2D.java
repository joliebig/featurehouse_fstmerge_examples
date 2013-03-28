

package org.jfree.data.function;

import java.io.Serializable;
import org.jfree.chart.util.HashUtilities;


public class PowerFunction2D implements Function2D, Serializable {

    
    private double a;

    
    private double b;

    
    public PowerFunction2D(double a, double b) {
        this.a = a;
        this.b = b;
    }

    
    public double getA() {
        return this.a;
    }

    
    public double getB() {
        return this.b;
    }

    
    public double getValue(double x) {
        return this.a * Math.pow(x, this.b);
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof PowerFunction2D)) {
            return false;
        }
        PowerFunction2D that = (PowerFunction2D) obj;
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
