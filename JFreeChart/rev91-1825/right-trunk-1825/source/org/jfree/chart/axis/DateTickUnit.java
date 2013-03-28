

package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.chart.util.ObjectUtilities;


public class DateTickUnit extends TickUnit implements Serializable {

    
    private static final long serialVersionUID = -7289292157229621901L;

    
    private DateTickUnitType unitType;

    
    private int count;

    
    private DateTickUnitType rollUnitType;

    
    private int rollCount;

    
    private DateFormat formatter;

    
    public DateTickUnit(DateTickUnitType unitType, int multiple) {
        this(unitType, multiple, DateFormat.getDateInstance(DateFormat.SHORT));
    }

    
    public DateTickUnit(DateTickUnitType unitType, int multiple,
            DateFormat formatter) {
        this(unitType, multiple, unitType, multiple, formatter);
    }

    
    public DateTickUnit(DateTickUnitType unitType, int multiple,
            DateTickUnitType rollUnitType, int rollMultiple,
            DateFormat formatter) {
        super(DateTickUnit.getMillisecondCount(unitType, multiple));
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        if (multiple <= 0) {
            throw new IllegalArgumentException("Requires 'multiple' > 0.");
        }
        if (rollMultiple <= 0) {
            throw new IllegalArgumentException("Requires 'rollMultiple' > 0.");
        }
        this.unitType = unitType;
        this.count = multiple;
        this.rollUnitType = rollUnitType;
        this.rollCount = rollMultiple;
        this.formatter = formatter;
    }

    
    public DateTickUnitType getUnitType() {
        return this.unitType;
    }

    
    public int getCount() {
        return this.count;
    }

    
    public DateTickUnitType getRollUnitType() {
        return this.rollUnitType;
    }

    
    public int getRollMultiple() {
        return this.rollCount;
    }

    
    public String valueToString(double milliseconds) {
        return this.formatter.format(new Date((long) milliseconds));
    }

    
    public String dateToString(Date date) {
        return this.formatter.format(date);
    }

    
    public Date addToDate(Date base) {
        return addToDate(base, TimeZone.getDefault());
    }

    
    public Date addToDate(Date base, TimeZone zone) {
        
        
        
        
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(this.unitType.getCalendarField(), this.count);
        return calendar.getTime();
    }

    
    public Date rollDate(Date base) {
        return rollDate(base, TimeZone.getDefault());
    }

    
    public Date rollDate(Date base, TimeZone zone) {
        
        
        
        
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(base);
        calendar.add(this.rollUnitType.getCalendarField(), this.rollCount);
        return calendar.getTime();
    }

    
    public int getCalendarField() {
        return this.unitType.getCalendarField();
    }


    
    private static long getMillisecondCount(DateTickUnitType unit, int count) {

        if (unit.equals(DateTickUnitType.YEAR)) {
            return (365L * 24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.MONTH)) {
            return (31L * 24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.DAY)) {
            return (24L * 60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.HOUR)) {
            return (60L * 60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.MINUTE)) {
            return (60L * 1000L) * count;
        }
        else if (unit.equals(DateTickUnitType.SECOND)) {
            return 1000L * count;
        }
        else if (unit.equals(DateTickUnitType.MILLISECOND)) {
            return count;
        }
        else {
            throw new IllegalArgumentException("The 'unit' argument has a " +
                    "value that is not recognised.");
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
        if (!(this.unitType.equals(that.unitType))) {
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
        result = 37 * result + this.unitType.hashCode();
        result = 37 * result + this.count;
        result = 37 * result + this.formatter.hashCode();
        return result;
    }

    
    public String toString() {
        return "DateTickUnit[" + this.unitType.toString() + ", "
                + this.count + "]";
    }

}
