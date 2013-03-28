

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Second extends RegularTimePeriod implements Serializable {

    
    private static final long serialVersionUID = -6536564190712383466L;
    
    
    public static final int FIRST_SECOND_IN_MINUTE = 0;

    
    public static final int LAST_SECOND_IN_MINUTE = 59;

    
    private Day day;
    
    
    private byte hour;
    
    
    private byte minute;

    
    private byte second;

    
    private long firstMillisecond;
  
    
    public Second() {
        this(new Date());
    }

    
    public Second(int second, Minute minute) {
        if (minute == null) {
            throw new IllegalArgumentException("Null 'minute' argument.");   
        }
        this.day = minute.getDay();
        this.hour = (byte) minute.getHourValue();
        this.minute = (byte) minute.getMinute();
        this.second = (byte) second;
        peg(Calendar.getInstance());
    }

    
    public Second(int second, int minute, int hour, 
                  int day, int month, int year) {
        this(second, new Minute(minute, hour, day, month, year));    
    }
    
    
    public Second(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    
    public Second(Date time, final TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.second = (byte) calendar.get(Calendar.SECOND);
        this.minute = (byte) calendar.get(Calendar.MINUTE);
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.day = new Day(time, zone);
        peg(calendar);
    }

    
    public int getSecond() {
        return this.second;
    }

    
    public Minute getMinute() {
        return new Minute(this.minute, new Hour(this.hour, this.day));
    }

    
    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    
    public long getLastMillisecond() {
        return this.firstMillisecond + 999L;
    }
    
    
    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
    }

    
    public RegularTimePeriod previous() {
        
        Second result = null;
        if (this.second != FIRST_SECOND_IN_MINUTE) {
            result = new Second(this.second - 1, getMinute());
        }
        else {
            Minute previous = (Minute) getMinute().previous();
            if (previous != null) {
                result = new Second(LAST_SECOND_IN_MINUTE, previous);
            }
        }
        return result;
        
    }

    
    public RegularTimePeriod next() {
        
        Second result = null;
        if (this.second != LAST_SECOND_IN_MINUTE) {
            result = new Second(this.second + 1, getMinute());
        }
        else {
            Minute next = (Minute) getMinute().next();
            if (next != null) {
                result = new Second(FIRST_SECOND_IN_MINUTE, next);
            }
        }
        return result;

    }

    
    public long getSerialIndex() {
        long hourIndex = this.day.getSerialIndex() * 24L + this.hour;
        long minuteIndex = hourIndex * 60L + this.minute;
        return minuteIndex * 60L + this.second;
    }

    
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int day = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, day, this.hour, this.minute, this.second);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime().getTime();
    }

    
    public long getLastMillisecond(Calendar calendar) {
        return getFirstMillisecond(calendar) + 999L;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Second)) {
            return false;
        }
        Second that = (Second) obj;
        if (this.second != that.second) {
            return false;
        }
        if (this.minute != that.minute) {
            return false;
        }
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
        result = 37 * result + this.second;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof Second) {
            Second s = (Second) o1;
            if (this.firstMillisecond < s.firstMillisecond) {
                return -1;
            }
            else if (this.firstMillisecond > s.firstMillisecond) {
                return 1;
            }
            else {
                return 0;
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

    
    public static Second parseSecond(String s) {

        Second result = null;
        s = s.trim();

        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmsstr = s.substring(
                Math.min(daystr.length() + 1, s.length()), s.length()
            );
            hmsstr = hmsstr.trim();

            int l = hmsstr.length();
            String hourstr = hmsstr.substring(0, Math.min(2, l));
            String minstr = hmsstr.substring(Math.min(3, l), Math.min(5, l));
            String secstr = hmsstr.substring(Math.min(6, l), Math.min(8, l));
            int hour = Integer.parseInt(hourstr);

            if ((hour >= 0) && (hour <= 23)) {

                int minute = Integer.parseInt(minstr);
                if ((minute >= 0) && (minute <= 59)) {

                    Minute m = new Minute(minute, new Hour(hour, day));
                    int second = Integer.parseInt(secstr);
                    if ((second >= 0) && (second <= 59)) {
                        result = new Second(second, m);
                    }
                }
            }
        }

        return result;

    }

}
