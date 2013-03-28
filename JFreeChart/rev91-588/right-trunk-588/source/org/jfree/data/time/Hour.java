

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Hour extends RegularTimePeriod implements Serializable {

    
    private static final long serialVersionUID = -835471579831937652L;
    
    
    public static final int FIRST_HOUR_IN_DAY = 0;

    
    public static final int LAST_HOUR_IN_DAY = 23;

    
    private Day day;

    
    private byte hour;

    
    private long firstMillisecond;
    
    
    private long lastMillisecond;

    
    public Hour() {
        this(new Date());
    }

    
    public Hour(int hour, Day day) {
        if (day == null) {
            throw new IllegalArgumentException("Null 'day' argument.");
        }
        this.hour = (byte) hour;
        this.day = day;
        peg(Calendar.getInstance());
    }

    
    public Hour(int hour, int day, int month, int year) {
        this(hour, new Day(day, month, year));
    }
    
    
    public Hour(Date time) {
        
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    
    public Hour(Date time, TimeZone zone) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.day = new Day(time, zone);
        peg(calendar);
    }

    
    public int getHour() {
        return this.hour;
    }

    
    public Day getDay() {
        return this.day;
    }

    
    public int getYear() {
        return this.day.getYear();
    }

    
    public int getMonth() {
        return this.day.getMonth();
    }

    
    public int getDayOfMonth() {
        return this.day.getDayOfMonth();
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

        Hour result;
        if (this.hour != FIRST_HOUR_IN_DAY) {
            result = new Hour(this.hour - 1, this.day);
        }
        else { 
            Day prevDay = (Day) this.day.previous();
            if (prevDay != null) {
                result = new Hour(LAST_HOUR_IN_DAY, prevDay);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    
    public RegularTimePeriod next() {

        Hour result;
        if (this.hour != LAST_HOUR_IN_DAY) {
            result = new Hour(this.hour + 1, this.day);
        }
        else { 
            Day nextDay = (Day) this.day.next();
            if (nextDay != null) {
                result = new Hour(FIRST_HOUR_IN_DAY, nextDay);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    
    public long getSerialIndex() {
        return this.day.getSerialIndex() * 24L + this.hour;
    }

    
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime().getTime();
    }

    
    public long getLastMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        return calendar.getTime().getTime();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Hour)) {
            return false;
        }
        Hour that = (Hour) obj;
        if (this.hour != that.hour) {
            return false;
        }
        if (!this.day.equals(that.day)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof Hour) {
            Hour h = (Hour) o1;
            result = getDay().compareTo(h.getDay());
            if (result == 0) {
                result = this.hour - h.getHour();
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

    
    public static Hour parseHour(String s) {

        Hour result = null;
        s = s.trim();

        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hourstr = s.substring(
                Math.min(daystr.length() + 1, s.length()), s.length()
            );
            hourstr = hourstr.trim();
            int hour = Integer.parseInt(hourstr);
            
            if ((hour >= FIRST_HOUR_IN_DAY) && (hour <= LAST_HOUR_IN_DAY)) {
                result = new Hour(hour, day);
            }
        }

        return result;

    }

}
