

package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Calendar;


public class DateTickUnitType implements Serializable {

    
    public static final DateTickUnitType YEAR
            = new DateTickUnitType("DateTickUnitType.YEAR", Calendar.YEAR);

    
    public static final DateTickUnitType MONTH
            = new DateTickUnitType("DateTickUnitType.MONTH", Calendar.MONTH);

    
    public static final DateTickUnitType DAY
            = new DateTickUnitType("DateTickUnitType.DAY", Calendar.DATE);


    
    public static final DateTickUnitType HOUR
            = new DateTickUnitType("DateTickUnitType.HOUR",
                    Calendar.HOUR_OF_DAY);

    
    public static final DateTickUnitType MINUTE
            = new DateTickUnitType("DateTickUnitType.MINUTE", Calendar.MINUTE);

    
    public static final DateTickUnitType SECOND
            = new DateTickUnitType("DateTickUnitType.SECOND", Calendar.SECOND);

    
    public static final DateTickUnitType MILLISECOND
            = new DateTickUnitType("DateTickUnitType.MILLISECOND",
                    Calendar.MILLISECOND);

    
    private String name;

    
    private int calendarField;

    
    private DateTickUnitType(String name, int calendarField) {
        this.name = name;
        this.calendarField = calendarField;
    }

    
    public int getCalendarField() {
        return this.calendarField;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateTickUnitType)) {
            return false;
        }
        DateTickUnitType t = (DateTickUnitType) obj;
        if (!this.name.equals(t.toString())) {
            return false;
        }
        return true;
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(DateTickUnitType.YEAR)) {
            return DateTickUnitType.YEAR;
        }
        else if (this.equals(DateTickUnitType.MONTH)) {
            return DateTickUnitType.MONTH;
        }
        else if (this.equals(DateTickUnitType.DAY)) {
            return DateTickUnitType.DAY;
        }
        else if (this.equals(DateTickUnitType.HOUR)) {
            return DateTickUnitType.HOUR;
        }
        else if (this.equals(DateTickUnitType.MINUTE)) {
            return DateTickUnitType.MINUTE;
        }
        else if (this.equals(DateTickUnitType.SECOND)) {
            return DateTickUnitType.SECOND;
        }
        else if (this.equals(DateTickUnitType.MILLISECOND)) {
            return DateTickUnitType.MILLISECOND;
        }
        return null;
    }

}
