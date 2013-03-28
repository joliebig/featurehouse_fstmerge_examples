

package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.util.ObjectUtilities;


public class DateTickUnit extends TickUnit implements Serializable {

    
    private static final long serialVersionUID = -7289292157229621901L;
    
    
    public static final int YEAR = 0;

    
    public static final int MONTH = 1;

    
    public static final int DAY = 2;

    
    public static final int HOUR = 3;

    
    public static final int MINUTE = 4;

    
    public static final int SECOND = 5;

    
    public static final int MILLISECOND = 6;

    
    private int unit;

    
    private int count;

    
    private int rollUnit;

    
    private int rollCount;

    
    private DateFormat formatter;

    
    public DateTickUnit(int unit, int count) {
        this(unit, count, null);
    }

    
    public DateTickUnit(int unit, int count, DateFormat formatter) {

        this(unit, count, unit, count, formatter);

    }

    
    public DateTickUnit(int unit, int count, int rollUnit, int rollCount, 
                        DateFormat formatter) {
        super(DateTickUnit.getMillisecondCount(unit, count));
        this.unit = unit;
        this.count = count;
        this.rollUnit = rollUnit;
        this.rollCount = rollCount;
        this.formatter = formatter;
        if (formatter == null) {
            this.formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        }
    }

    
    public int getUnit() {
        return this.unit;
    }

    
    public int getCount() {
        return this.count;
    }

    
    public int getRollUnit() {
        return this.rollUnit;
    }

    
    public int getRollCount() {
        return this.rollCount;
    }

    
    public String valueToString(double milliseconds) {
        return this.formatter.format(new Date((long) milliseconds));
    }

    
    public String dateToString(Date date) {
        return this.formatter.format(date);
    }

    
    public Date addToDate(Date base) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(getCalendarField(this.unit), this.count);
        return calendar.getTime();
    }

    
    public Date addToDate(Date base, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(getCalendarField(this.unit), this.count);
        return calendar.getTime();
    }

    
    public Date rollDate(Date base) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(getCalendarField(this.rollUnit), this.rollCount);
        return calendar.getTime();
    }

    
    public Date rollDate(Date base, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(getCalendarField(this.rollUnit), this.rollCount);
        return calendar.getTime();
    }

    
    public int getCalendarField() {
        return getCalendarField(this.unit);
    }

    
    private int getCalendarField(int tickUnit) {

        switch (tickUnit) {
            case (YEAR):
                return Calendar.YEAR;
            case (MONTH):
                return Calendar.MONTH;
            case (DAY):
                return Calendar.DATE;
            case (HOUR):
                return Calendar.HOUR_OF_DAY;
            case (MINUTE):
                return Calendar.MINUTE;
            case (SECOND):
                return Calendar.SECOND;
            case (MILLISECOND):
                return Calendar.MILLISECOND;
            default:
                return Calendar.MILLISECOND;
        }

    }

    
    private static long getMillisecondCount(int unit, int count) {

        switch (unit) {
            case (YEAR):
                return (365L * 24L * 60L * 60L * 1000L) * count;
            case (MONTH):
                return (31L * 24L * 60L * 60L * 1000L) * count;
            case (DAY):
                return (24L * 60L * 60L * 1000L) * count;
            case (HOUR):
                return (60L * 60L * 1000L) * count;
            case (MINUTE):
                return (60L * 1000L) * count;
            case (SECOND):
                return 1000L * count;
            case (MILLISECOND):
                return count;
            default:
                throw new IllegalArgumentException(
                    "DateTickUnit.getMillisecondCount() : unit must "
                    + "be one of the constants YEAR, MONTH, DAY, HOUR, MINUTE, "
                    + "SECOND or MILLISECOND defined in the DateTickUnit "
                    + "class. Do *not* use the constants defined in "
                    + "java.util.Calendar."
                );
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        DateTickUnit that = (DateTickUnit) obj;
        if (this.unit != that.unit) {
            return false;
        }
        if (this.count != that.count) {
            return false;
        }
        if (!ObjectUtilities.equal(this.formatter, that.formatter)) {
            return false;
        }
        return true;
    }
    
    
    public int hashCode() {
        int result = 19;
        result = 37 * result + this.unit;
        result = 37 * result + this.count;
        result = 37 * result + this.formatter.hashCode();
        return result;
    }
    
    
    private static final String[] units = {"YEAR", "MONTH", "DAY", "HOUR", 
            "MINUTE", "SECOND", "MILLISECOND"};
    
    
    public String toString() {
        return "DateTickUnit[" + DateTickUnit.units[this.unit] + ", " 
                + this.count + "]";
    }

}
