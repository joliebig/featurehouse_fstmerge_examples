

package org.jfree.data.function;


public class NormalDistributionFunction2D implements Function2D {

    
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

}
