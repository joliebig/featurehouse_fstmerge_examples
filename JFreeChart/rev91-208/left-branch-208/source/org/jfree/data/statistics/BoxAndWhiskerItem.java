

package org.jfree.data.statistics;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.util.ObjectUtilities;


public class BoxAndWhiskerItem implements Serializable {
    
    
    private static final long serialVersionUID = 7329649623148167423L;
    
    
    private Number mean;
    
    
    private Number median;
    
    
    private Number q1;
    
    
    private Number q3;
    
    
    private Number minRegularValue;
    
    
    private Number maxRegularValue;
    
    
    private Number minOutlier;
    
    
    private Number maxOutlier;
    
    
    private List outliers;
    
    
    public BoxAndWhiskerItem(Number mean,
                             Number median,
                             Number q1,
                             Number q3,
                             Number minRegularValue,
                             Number maxRegularValue,
                             Number minOutlier,
                             Number maxOutlier,
                             List outliers) {
                                 
        this.mean = mean;
        this.median = median;    
        this.q1 = q1;
        this.q3 = q3;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
        this.minOutlier = minOutlier;
        this.maxOutlier = maxOutlier;
        this.outliers = outliers;
        
    }

    
    public Number getMean() {
        return this.mean;
    }
    
    
    public Number getMedian() {
        return this.median;
    }
    
    
    public Number getQ1() {
        return this.q1;
    }
    
    
    public Number getQ3() {
        return this.q3;
    }
    
    
    public Number getMinRegularValue() {
        return this.minRegularValue;
    }
    
    
    public Number getMaxRegularValue() {
        return this.maxRegularValue;
    }
    
    
    public Number getMinOutlier() {
        return this.minOutlier;
    }
    
    
    public Number getMaxOutlier() {
        return this.maxOutlier;
    }
    
    
    public List getOutliers() {
        if (this.outliers == null) {
            return null;
        }
        return Collections.unmodifiableList(this.outliers);
    }
    
    
    public String toString() {
        return super.toString() + "[mean=" + this.mean + ",median=" 
                + this.median + ",q1=" + this.q1 + ",q3=" + this.q3 + "]";
    }
    
    
    public boolean equals(Object obj) {
        
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof BoxAndWhiskerItem)) {
            return false;
        }
        BoxAndWhiskerItem that = (BoxAndWhiskerItem) obj;
        if (!ObjectUtilities.equal(this.mean, that.mean)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.median, that.median)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.q1, that.q1)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.q3, that.q3)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.minRegularValue, 
                that.minRegularValue)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.maxRegularValue, 
                that.maxRegularValue)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.minOutlier, that.minOutlier)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.maxOutlier, that.maxOutlier)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outliers, that.outliers)) {
            return false;
        }
        return true;
    }
    
}
