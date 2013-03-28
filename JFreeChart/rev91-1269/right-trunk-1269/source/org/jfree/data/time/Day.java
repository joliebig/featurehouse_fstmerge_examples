

package org.jfree.data.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Day extends RegularTimePeriod implements Serializable {

    
    private static final long serialVersionUID = -7082667380758962755L;

    
    protected static final DateFormat DATE_FORMAT
        = new SimpleDateFormat("yyyy-MM-dd");

    
    protected static final DateFormat
        DATE_FORMAT_SHORT = DateFormat.getDateInstance(DateFormat.SHORT);

    
    protected static final DateFormat
        DATE_FORMAT_MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM);

    
    protected static final DateFormat
        DATE_FORMAT_LONG = DateFormat.getDateInstance(DateFormat.LONG);

    
    private SerialDate serialDate;

    
    private long firstMillisecond;

    
    private long lastMillisecond;

    
    public Day() {
        this(new Date());
    }

    
    public Day(int day, int month, int year) {
        this.serialDate = SerialDate.createInstance(day, month, year);
        peg(Calendar.getInstance());
    }

    
    public Day(SerialDate serialDate) {
        if (serialDate == null) {
            throw new IllegalArgumentException("Null 'serialDate' argument.");
        }
        this.serialDate = serialDate;
        peg(Calendar.getInstance());
    }

    
    public Day(Date time) {
        
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    
    public Day(Date time, TimeZone zone) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);
        this.serialDate = SerialDate.createInstance(d, m, y);
        peg(calendar);
    }

    
    public SerialDate getSerialDate() {
        return this.serialDate;
    }

    
    public int getYear() {
        return this.serialDate.getYYYY();
    }

    
    public int getMonth() {
        return this.serialDate.getMonth();
    }

    
    public int getDayOfMonth() {
        return this.serialDate.getDayOfMonth();
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

        Day result;
        int serial = this.serialDate.toSerial();
        if (serial > SerialDate.SERIAL_LOWER_BOUND) {
            SerialDate yesterday = SerialDate.createInstance(serial - 1);
            return new Day(yesterday);
        }
        else {
            result = null;
        }
        return result;

    }

    
    public RegularTimePeriod next() {

        Day result;
        int serial = this.serialDate.toSerial();
        if (serial < SerialDate.SERIAL_UPPER_BOUND) {
            SerialDate tomorrow = SerialDate.createInstance(serial + 1);
            return new Day(tomorrow);
        }
        else {
            result = null;
        }
        return result;

    }

    
    public long getSerialIndex() {
        return this.serialDate.toSerial();
    }

    
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime().getTime();
    }

    
    public long getLastMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        return calendar.getTime().getTime();
    }

    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Day)) {
            return false;
        }
        Day that = (Day) obj;
        if (!this.serialDate.equals(that.getSerialDate())) {
            return false;
        }
        return true;

    }

    
    public int hashCode() {
        return this.serialDate.hashCode();
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof Day) {
            Day d = (Day) o1;
            result = -d.getSerialDate().compare(this.serialDate);
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
        return this.serialDate.toString();
    }

    
    public static Day parseDay(String s) {

        try {
            return new Day (Day.DATE_FORMAT.parse(s));
        }
        catch (ParseException e1) {
            try {
                return new Day (Day.DATE_FORMAT_SHORT.parse(s));
            }
            catch (ParseException e2) {
              
            }
        }
        return null;

    }

}
