

package org.jfree.data.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.Range;


public class DateRange extends Range implements Serializable {

    
    private static final long serialVersionUID = -4705682568375418157L;
    
    
    private Date lowerDate;

    
    private Date upperDate;

    
    public DateRange() {
        this(new Date(0), new Date(1));
    }

    
    public DateRange(Date lower, Date upper) {

        super(lower.getTime(), upper.getTime());
        this.lowerDate = lower;
        this.upperDate = upper;

    }

    
    public DateRange(double lower, double upper) {
        super(lower, upper);
        this.lowerDate = new Date((long) lower);
        this.upperDate = new Date((long) upper);
    }

    
    public DateRange(Range other) {
        this(other.getLowerBound(), other.getUpperBound());
    }

    
    public Date getLowerDate() {
        return this.lowerDate;
    }

    
    public Date getUpperDate() {
        return this.upperDate;
    }
    
    
    public String toString() {
        DateFormat df = DateFormat.getDateTimeInstance();
        return "[" + df.format(this.lowerDate) + " --> " 
            + df.format(this.upperDate) + "]";
    }

}
