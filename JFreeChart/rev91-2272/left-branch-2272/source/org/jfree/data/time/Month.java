

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.date.MonthConstants;
import org.jfree.date.SerialDate;


public class Month extends RegularTimePeriod implements Serializable {

    
    private static final long serialVersionUID = -5090216912548722570L;

    
    private int month;

    
    private int year;

    
    private long firstMillisecond;

    
    private long lastMillisecond;

    
    public Month() {
        this(new Date());
    }

    
    public Month(int month, int year) {
        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year;
        peg(Calendar.getInstance());
    }

    
    public Month(int month, Year year) {
        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year.getYear();
        peg(Calendar.getInstance());
    }

    
    public Month(Date time) {
        this(time, TimeZone.getDefault());
    }

    
    public Month(Date time, TimeZone zone) {
        this(time, zone, Locale.getDefault());
    }

    
    public Month(Date time, TimeZone zone, Locale locale) {
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.year = calendar.get(Calendar.YEAR);
        peg(calendar);
    }

    
    public Year getYear() {
        return new Year(this.year);
    }

    
    public int getYearValue() {
        return this.year;
    }

    
    public int getMonth() {
        return this.month;
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
        Month result;
        if (this.month != MonthConstants.JANUARY) {
            result = new Month(this.month - 1, this.year);
        }
        else {
            if (this.year > 1900) {
                result = new Month(MonthConstants.DECEMBER, this.year - 1);
            }
            else {
                result = null;
            }
        }
        return result;
    }

    
    public RegularTimePeriod next() {
        Month result;
        if (this.month != MonthConstants.DECEMBER) {
            result = new Month(this.month + 1, this.year);
        }
        else {
            if (this.year < 9999) {
                result = new Month(MonthConstants.JANUARY, this.year + 1);
            }
            else {
                result = null;
            }
        }
        return result;
    }

    
    public long getSerialIndex() {
        return this.year * 12L + this.month;
    }

    
    public String toString() {
        return SerialDate.monthCodeToString(this.month) + " " + this.year;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Month)) {
            return false;
        }
        Month that = (Month) obj;
        if (this.month != that.month) {
            return false;
        }
        if (this.year != that.year) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.month;
        result = 37 * result + this.year;
        return result;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof Month) {
            Month m = (Month) o1;
            result = this.year - m.getYearValue();
            if (result == 0) {
                result = this.month - m.getMonth();
            }
        }

        
        
        else if (o1 instanceof RegularTimePeriod) {
            
            result = 0;
        }

        
        
        else {
            
            result = 1;
        }

        return result;

    }

    
    public long getFirstMillisecond(Calendar calendar) {
        calendar.set(this.year, this.month - 1, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        
        return calendar.getTime().getTime();
    }

    
    public long getLastMillisecond(Calendar calendar) {
        int eom = SerialDate.lastDayOfMonth(this.month, this.year);
        calendar.set(this.year, this.month - 1, eom, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        
        return calendar.getTime().getTime();
    }

    
    public static Month parseMonth(String s) {
        Month result = null;
        if (s == null) {
            return result;
        }
        
        s = s.trim();
        int i = Month.findSeparator(s);
        String s1, s2;
        boolean yearIsFirst;
        
        
        if (i == -1) {
            yearIsFirst = true;
            s1 = s.substring(0, 5);
            s2 = s.substring(5);
        }
        else {
            s1 = s.substring(0, i).trim();
            s2 = s.substring(i + 1, s.length()).trim();
            
            Year y1 = Month.evaluateAsYear(s1);
            if (y1 == null) {
                yearIsFirst = false;
            }
            else {
                Year y2 = Month.evaluateAsYear(s2);
                if (y2 == null) {
                    yearIsFirst = true;
                }
                else {
                    yearIsFirst = (s1.length() > s2.length());
                }
            }
        }
        Year year;
        int month;
        if (yearIsFirst) {
            year = Month.evaluateAsYear(s1);
            month = SerialDate.stringToMonthCode(s2);
        }
        else {
            year = Month.evaluateAsYear(s2);
            month = SerialDate.stringToMonthCode(s1);
        }
        if (month == -1) {
            throw new TimePeriodFormatException("Can't evaluate the month.");
        }
        if (year == null) {
            throw new TimePeriodFormatException("Can't evaluate the year.");
        }
        result = new Month(month, year);
        return result;
    }

    
    private static int findSeparator(String s) {
        int result = s.indexOf('-');
        if (result == -1) {
            result = s.indexOf(',');
        }
        if (result == -1) {
            result = s.indexOf(' ');
        }
        if (result == -1) {
            result = s.indexOf('.');
        }
        return result;
    }

    
    private static Year evaluateAsYear(String s) {
        Year result = null;
        try {
            result = Year.parseYear(s);
        }
        catch (TimePeriodFormatException e) {
            
        }
        return result;
    }

}
