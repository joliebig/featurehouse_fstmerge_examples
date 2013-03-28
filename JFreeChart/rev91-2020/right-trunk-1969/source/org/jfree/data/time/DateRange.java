

package org.jfree.data.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.Range;


public class DateRange extends Range implements Serializable {

    
    private static final long serialVersionUID = -4705682568375418157L;

    
    private long lowerDate;

    
    private long upperDate;

    
    public DateRange() {
        this(new Date(0), new Date(1));
    }

    
    public DateRange(Date lower, Date upper) {
        super(lower.getTime(), upper.getTime());
        this.lowerDate = lower.getTime();
        this.upperDate = upper.getTime();
    }

    
    public DateRange(double lower, double upper) {
        super(lower, upper);
        this.lowerDate = (long) lower;
        this.upperDate = (long) upper;
    }

    
    public DateRange(Range other) {
        this(other.getLowerBound(), other.getUpperBound());
    }

    
    public Date getLowerDate() {
        return new Date(this.lowerDate);
    }

    
    public long getLowerMillis() {
        return this.lowerDate;
    }

    
    public Date getUpperDate() {
        return new Date(this.upperDate);
    }

    
    public long getUpperMillis() {
        return this.upperDate;
    }

    
    public String toString() {
        DateFormat df = DateFormat.getDateTimeInstance();
        return "[" + df.format(getLowerDate()) + " --> "
                + df.format(getUpperDate()) + "]";
    }

}
