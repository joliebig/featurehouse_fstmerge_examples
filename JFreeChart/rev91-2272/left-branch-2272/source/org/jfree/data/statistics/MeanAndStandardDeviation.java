

package org.jfree.data.statistics;

import java.io.Serializable;

import org.jfree.util.ObjectUtilities;


public class MeanAndStandardDeviation implements Serializable {

    
    private static final long serialVersionUID = 7413468697315721515L;

    
    private Number mean;

    
    private Number standardDeviation;

    
    public MeanAndStandardDeviation(double mean, double standardDeviation) {
        this(new Double(mean), new Double(standardDeviation));
    }

    
    public MeanAndStandardDeviation(Number mean, Number standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    
    public Number getMean() {
        return this.mean;
    }

    
    public double getMeanValue() {
        double result = Double.NaN;
        if (this.mean != null) {
            result = this.mean.doubleValue();
        }
        return result;
    }

    
    public Number getStandardDeviation() {
        return this.standardDeviation;
    }

    
    public double getStandardDeviationValue() {
        double result = Double.NaN;
        if (this.standardDeviation != null) {
            result = this.standardDeviation.doubleValue();
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeanAndStandardDeviation)) {
            return false;
        }
        MeanAndStandardDeviation that = (MeanAndStandardDeviation) obj;
        if (!ObjectUtilities.equal(this.mean, that.mean)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.standardDeviation, that.standardDeviation)
        ) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "[" + this.mean + ", " + this.standardDeviation + "]";
    }

}