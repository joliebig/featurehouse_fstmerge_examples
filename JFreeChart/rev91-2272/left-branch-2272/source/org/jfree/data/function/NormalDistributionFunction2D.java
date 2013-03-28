

package org.jfree.data.function;

import java.io.Serializable;

import org.jfree.chart.HashUtilities;


public class NormalDistributionFunction2D implements Function2D, Serializable {

    
    private double mean;

    
    private double std;

    
    private double factor;

    
    private double denominator;

    
    public NormalDistributionFunction2D(double mean, double std) {
        if (std <= 0) {
            throw new IllegalArgumentException("Requires 'std' > 0.");
        }
        this.mean = mean;
        this.std = std;
        
        this.factor = 1 / (std * Math.sqrt(2.0 * Math.PI));
        this.denominator = 2 * std * std;
    }

    
    public double getMean() {
        return this.mean;
    }
    
    
    public double getStandardDeviation() {
        return this.std;
    }

    
    public double getValue(double x) {
        double z = x - this.mean;
        return this.factor * Math.exp(-z * z / this.denominator);
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof NormalDistributionFunction2D)) {
            return false;
        }
        NormalDistributionFunction2D that = (NormalDistributionFunction2D) obj;
        if (this.mean != that.mean) {
            return false;
        }
        if (this.std != that.std) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 29;
        result = HashUtilities.hashCode(result, this.mean);
        result = HashUtilities.hashCode(result, this.std);
        return result;
    }

}
