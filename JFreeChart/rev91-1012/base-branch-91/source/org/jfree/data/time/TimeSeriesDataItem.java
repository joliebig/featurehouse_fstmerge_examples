

package org.jfree.data.time;

import java.io.Serializable;


public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {

    
    private static final long serialVersionUID = -2235346966016401302L;
    
    
    private RegularTimePeriod period;

    
    private Number value;

    
    public TimeSeriesDataItem(RegularTimePeriod period, Number value) {
        if (period == null) {
            throw new IllegalArgumentException("Null 'period' argument.");   
        }
        this.period = period;
        this.value = value;
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

    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeSeriesDataItem)) {
            return false;
        }
        TimeSeriesDataItem timeSeriesDataItem = (TimeSeriesDataItem) o;
        if (this.period != null) {
            if (!this.period.equals(timeSeriesDataItem.period)) {
                return false;
            }
        }
        else if (timeSeriesDataItem.period != null) {
           return false;
        }
        
        if (this.value != null) {
            if (!this.value.equals(timeSeriesDataItem.value)) {
                return false;
            }
        }
        else if (timeSeriesDataItem.value != null) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        int result;
        result = (this.period != null ? this.period.hashCode() : 0);
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
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
