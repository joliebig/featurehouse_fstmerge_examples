

package org.jfree.data.time;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.date.MonthConstants;


public abstract class RegularTimePeriod implements TimePeriod, Comparable, 
                                                   MonthConstants {

    
    public static RegularTimePeriod createInstance(Class c, Date millisecond, 
                                                   TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            Constructor constructor = c.getDeclaredConstructor(
                    new Class[] {Date.class, TimeZone.class});
            result = (RegularTimePeriod) constructor.newInstance(
                    new Object[] {millisecond, zone});
        }
        catch (Exception e) {
            
        }
        return result;  
    }
    
    
    public static Class downsize(Class c) {
        if (c.equals(Year.class)) {
            return Quarter.class;
        }
        else if (c.equals(Quarter.class)) {
            return Month.class;
        }
        else if (c.equals(Month.class)) {
            return Day.class;
        }
        else if (c.equals(Day.class)) {
            return Hour.class;
        }
        else if (c.equals(Hour.class)) {
            return Minute.class;
        }
        else if (c.equals(Minute.class)) {
            return Second.class;
        }
        else if (c.equals(Second.class)) {
            return Millisecond.class;
        }
        else {
            return Millisecond.class;
        }
    }
    
    
    public abstract RegularTimePeriod previous();

    
    public abstract RegularTimePeriod next();

    
    public abstract long getSerialIndex();

    

    
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    
    public static final Calendar WORKING_CALENDAR 
        = Calendar.getInstance(DEFAULT_TIME_ZONE);

    
    public abstract void peg(Calendar calendar);
    
    
    public Date getStart() {
        return new Date(getFirstMillisecond());
    }

    
    public Date getEnd() {
        return new Date(getLastMillisecond());
    }

    
    public abstract long getFirstMillisecond();

    
    public long getFirstMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        return getFirstMillisecond(calendar);
    }

    
    public abstract long getFirstMillisecond(Calendar calendar);

    
    public abstract long getLastMillisecond();

    
    public long getLastMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        return getLastMillisecond(calendar);
    }

    
    public abstract long getLastMillisecond(Calendar calendar);

    
    public long getMiddleMillisecond() {
        long m1 = getFirstMillisecond();
        long m2 = getLastMillisecond();
        return m1 + (m2 - m1) / 2;
    }

    
    public long getMiddleMillisecond(TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        long m1 = getFirstMillisecond(calendar);
        long m2 = getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2;
    }

    
    public long getMiddleMillisecond(Calendar calendar) {
        long m1 = getFirstMillisecond(calendar);
        long m2 = getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2;
    }

    
    public String toString() {
        return String.valueOf(getStart());
    }

}
