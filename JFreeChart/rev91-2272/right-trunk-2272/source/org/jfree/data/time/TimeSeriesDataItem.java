

package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;


public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {

    
    private static final long serialVersionUID = -2235346966016401302L;

    
    private RegularTimePeriod period;

    
    private Number value;

    
    private boolean selected;

    
    public TimeSeriesDataItem(RegularTimePeriod period, Number value) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");
        }
        this.period = period;
        this.value = value;
        this.selected = false;
    }

    
    public TimeSeriesDataItem(RegularTimePeriod period, double value) {
        this(period, new Double(value));
    }

    
    public RegularTimePeriod getPeriod() {
        return this.period;
    }

    
    public Number getValue() {
        return this.value;
    }

    
    public void setValue(Number value) {
        this.value = value;
    }

    
    public boolean isSelected() {
        return this.selected;
    }

    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimeSeriesDataItem)) {
            return false;
        }
        TimeSeriesDataItem that = (TimeSeriesDataItem) obj;
        if (!this.period.equals(that.period)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.value, that.value)) {
            return false;
        }
        if (this.selected != that.selected) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result;
        result = (this.period != null ? this.period.hashCode() : 0);
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
        result = HashUtilities.hashCode(result, this.selected);
        return result;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof TimeSeriesDataItem) {
            TimeSeriesDataItem datapair = (TimeSeriesDataItem) o1;
            result = getPeriod().compareTo(datapair.getPeriod());
        }

        
        
        else {
            
            result = 1;
        }

        return result;

    }

    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { 
            e.printStackTrace();
        }
        return clone;
    }

}
