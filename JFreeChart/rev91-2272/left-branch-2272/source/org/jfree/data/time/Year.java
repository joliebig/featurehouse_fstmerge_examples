

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Year extends RegularTimePeriod implements Serializable {

    
    public static final int MINIMUM_YEAR = -9999;

    
    public static final int MAXIMUM_YEAR = 9999;

    
    private static final long serialVersionUID = -7659990929736074836L;

    
    private short year;

    
    private long firstMillisecond;

    
    private long lastMillisecond;

    
    public Year() {
        this(new Date());
    }

    
    public Year(int year) {
        if ((year < Year.MINIMUM_YEAR) || (year > Year.MAXIMUM_YEAR)) {
            throw new IllegalArgumentException(
                "Year constructor: year (" + year + ") outside valid range.");
        }
        this.year = (short) year;
        peg(Calendar.getInstance());
    }

    
    public Year(Date time) {
        this(time, TimeZone.getDefault());
    }

    
    public Year(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    
    public Year(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.year = (short) calendar.get(Calendar.YEAR);
        peg(calendar);
    }

    
    public int getYear() {
        return this.year;
    }

    
    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    
    public long getLastMillisecond() {
        return this.lastMillisecond;
    }

    
    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
        this.lastMillisecond = getLastMillisecond(calendar);
    }

    
    public RegularTimePeriod previous() {
        if (this.year > Year.MINIMUM_YEAR) {
            return new Year(this.year - 1);
        }
        else {
            return null;
        }
    }

    
    public RegularTimePeriod next() {
        if (this.year < Year.MAXIMUM_YEAR) {
            return new Year(this.year + 1);
        }
        else {
            return null;
        }
    }

    
    public long getSerialIndex() {
        return this.year;
    }

    
    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        
        return calendar.getTime().getTime();
    }

    
    public long getLastMillisecond(Calendar calendar) {
        calendar.set(this.year, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        
        return calendar.getTime().getTime();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Year)) {
            return false;
        }
        Year that = (Year) obj;
        return (this.year == that.year);
    }

    
    public int hashCode() {
        int result = 17;
        int c = this.year;
        result = 37 * result + c;
        return result;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof Year) {
            Year y = (Year) o1;
            result = this.year - y.getYear();
        }

        
        
        else if (o1 instanceof RegularTimePeriod) {
            
            result = 0;
        }

        
        
        else {
            
            result = 1;
        }

        return result;

    }

    
    public String toString() {
        return Integer.toString(this.year);
    }

    
    public static Year parseYear(String s) {

        
        int y;
        try {
            y = Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            throw new TimePeriodFormatException("Cannot parse string.");
        }

        
        try {
            return new Year(y);
        }
        catch (IllegalArgumentException e) {
            throw new TimePeriodFormatException("Year outside valid range.");
        }
    }

}
