

package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.util.ObjectUtilities;


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

        
        this.unit = unitTypeToInt(unitType);
        this.rollUnit = unitTypeToInt(rollUnitType);
    }

    
    public DateTickUnitType getUnitType() {
        return this.unitType;
    }

    
    public int getMultiple() {
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
            throw new IllegalArgumentException("The 'unit' argument has a " 
                    + "value that is not recognised.");
        }

    }

    
    private static DateTickUnitType intToUnitType(int unit) {
        switch (unit) {
            case YEAR: return DateTickUnitType.YEAR;
            case MONTH: return DateTickUnitType.MONTH;
            case DAY: return DateTickUnitType.DAY;
            case HOUR: return DateTickUnitType.HOUR;
            case MINUTE: return DateTickUnitType.MINUTE;
            case SECOND: return DateTickUnitType.SECOND;
            case MILLISECOND: return DateTickUnitType.MILLISECOND;
            default: throw new IllegalArgumentException(
                    "Unrecognised 'unit' value " + unit + ".");
        }
    }

    
    private static int unitTypeToInt(DateTickUnitType unitType) {
        if (unitType == null) {
            throw new IllegalArgumentException("Null 'unitType' argument.");
        }
        if (unitType.equals(DateTickUnitType.YEAR)) {
            return YEAR;
        }
        else if (unitType.equals(DateTickUnitType.MONTH)) {
            return MONTH;
        }
        else if (unitType.equals(DateTickUnitType.DAY)) {
            return DAY;
        }
        else if (unitType.equals(DateTickUnitType.HOUR)) {
            return HOUR;
        }
        else if (unitType.equals(DateTickUnitType.MINUTE)) {
            return MINUTE;
        }
        else if (unitType.equals(DateTickUnitType.SECOND)) {
            return SECOND;
        }
        else if (unitType.equals(DateTickUnitType.MILLISECOND)) {
            return MILLISECOND;
        }
        else {
            throw new IllegalArgumentException(
                    "The 'unitType' is not recognised");
        }
    }

    
    private static DateFormat notNull(DateFormat formatter) {
        if (formatter == null) {
            return DateFormat.getDateInstance(DateFormat.SHORT);
        }
        else {
            return formatter;
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

    
    public static final int YEAR = 0;

    
    public static final int MONTH = 1;

    
    public static final int DAY = 2;

    
    public static final int HOUR = 3;

    
    public static final int MINUTE = 4;

    
    public static final int SECOND = 5;

    
    public static final int MILLISECOND = 6;

    
    private int unit;

    
    private int rollUnit;

    
    public DateTickUnit(int unit, int count, DateFormat formatter) {
        this(unit, count, unit, count, formatter);
    }

    
    public DateTickUnit(int unit, int count) {
        this(unit, count, null);
    }

    
    public DateTickUnit(int unit, int count, int rollUnit, int rollCount,
                        DateFormat formatter) {
        this(intToUnitType(unit), count, intToUnitType(rollUnit), rollCount,
                notNull(formatter));
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

    
    public Date addToDate(Date base) {
        return addToDate(base, TimeZone.getDefault());
    }

}
