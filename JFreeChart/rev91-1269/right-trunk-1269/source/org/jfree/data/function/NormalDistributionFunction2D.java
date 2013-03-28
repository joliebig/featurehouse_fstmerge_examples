

package org.jfree.data.function;


public class NormalDistributionFunction2D implements Function2D {

    
    private double mean;

    
    private double std;

    
    public NormalDistributionFunction2D(double mean, double std) {
        this.mean = mean;
        this.std = std;
    }
    
    
    public double getMean() {
        return this.mean;
    }
    
    
    public double getStandardDeviation() {
        return this.std;
    }

    
    public double getValue(double x) {
        return Math.exp(-1.0 * (x - this.mean) * (x - this.mean) 
                / (2 * this.std * this.std)) / Math.sqrt(2 * Math.PI 
                * this.std * this.std);
    }

}
