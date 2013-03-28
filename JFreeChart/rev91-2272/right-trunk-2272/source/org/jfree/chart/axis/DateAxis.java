

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;


public class DateAxis extends ValueAxis implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -1013460999649007604L;

    
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();

    
    public static final double
            DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;

    
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT
            = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat());

    
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    
    private DateTickUnit tickUnit;

    
    private DateFormat dateFormatOverride;

    
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.START;

    
    private static class DefaultTimeline implements Timeline, Serializable {

        
        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        
        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        
        public long toMillisecond(long value) {
            return value;
        }

        
        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        
        public boolean containsDomainValue(Date date) {
            return true;
        }

        
        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        
        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            if (object instanceof DefaultTimeline) {
                return true;
            }
            return false;
        }
    }

    
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();

    
    private TimeZone timeZone;

    
    private Locale locale;

    
    private Timeline timeline;

    
    public DateAxis() {
        this(null);
    }

    
    public DateAxis(String label) {
        this(label, TimeZone.getDefault());
    }

    
    public DateAxis(String label, TimeZone zone) {
        this(label, zone, Locale.getDefault());
    }

    
    public DateAxis(String label, TimeZone zone, Locale locale) {
        super(label, DateAxis.createStandardDateTickUnits(zone, locale));
        setTickUnit(DateAxis.DEFAULT_DATE_TICK_UNIT, false, false);
        setAutoRangeMinimumSize(
                DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.locale = locale;
        this.timeline = DEFAULT_TIMELINE;
    }

    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    
    public void setTimeZone(TimeZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (!this.timeZone.equals(zone)) {
            this.timeZone = zone;
            setStandardTickUnits(createStandardDateTickUnits(zone,
                    this.locale));
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    
    public Timeline getTimeline() {
        return this.timeline;
    }

    
    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    
    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    
    public void setTickUnit(DateTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    
    public void setTickUnit(DateTickUnit unit, boolean notify,
                            boolean turnOffAutoSelection) {

        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    
    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public void setRange(Range range) {
        setRange(range, true, true);
    }

    
    public void setRange(Range range, boolean turnOffAutoRange,
                         boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        
        
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    
    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    
    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    
    public Date getMinimumDate() {
        Date result = null;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getLowerDate();
        }
        else {
            result = new Date((long) range.getLowerBound());
        }
        return result;
    }

    
    public void setMinimumDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Null 'date' argument.");
        }
        
        Date maxDate = getMaximumDate();
        long maxMillis = maxDate.getTime();
        long newMinMillis = date.getTime();
        if (maxMillis <= newMinMillis) {
            Date oldMin = getMinimumDate();
            long length = maxMillis - oldMin.getTime();
            maxDate = new Date(newMinMillis + length);
        }
        setRange(new DateRange(date, maxDate), true, false);
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public Date getMaximumDate() {
        Date result = null;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getUpperDate();
        }
        else {
            result = new Date((long) range.getUpperBound());
        }
        return result;
    }

    
    public void setMaximumDate(Date maximumDate) {
        if (maximumDate == null) {
            throw new IllegalArgumentException("Null 'maximumDate' argument.");
        }
        
        Date minDate = getMinimumDate();
        long minMillis = minDate.getTime();
        long newMaxMillis = maximumDate.getTime();
        if (minMillis >= newMaxMillis) {
            Date oldMax = getMaximumDate();
            long length = oldMax.getTime() - minMillis;
            minDate = new Date(newMaxMillis - length);
        }
        setRange(new DateRange(minDate, maximumDate), true, false);
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    
    public void setTickMarkPosition(DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.tickMarkPosition = position;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    
    public boolean isHiddenValue(long millis) {
        return (!this.timeline.containsDomainValue(new Date(millis)));
    }

    
    public double valueToJava2D(double value, Rectangle2D area,
                                RectangleEdge edge) {

        value = this.timeline.toTimelineValue((long) value);

        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin))
                         * (minX - maxX);
            }
            else {
                result = minX + ((value - axisMin) / (axisMax - axisMin))
                         * (maxX - minX);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                result = minY + (((value - axisMin) / (axisMax - axisMin))
                         * (maxY - minY));
            }
            else {
                result = maxY - (((value - axisMin) / (axisMax - axisMin))
                         * (maxY - minY));
            }
        }
        return result;

    }

    
    public double dateToJava2D(Date date, Rectangle2D area,
                               RectangleEdge edge) {
        double value = date.getTime();
        return valueToJava2D(value, area, edge);
    }

    
    public double java2DToValue(double java2DValue, Rectangle2D area,
                                RectangleEdge edge) {

        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }

        double result;
        if (isInverted()) {
             result = axisMax - ((java2DValue - min) / (max - min)
                      * (axisMax - axisMin));
        }
        else {
             result = axisMin + ((java2DValue - min) / (max - min)
                      * (axisMax - axisMin));
        }

        return this.timeline.toMillisecond((long) result);
    }

    
    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        return nextStandardDate(getMinimumDate(), unit);
    }

    
    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {
        return previousStandardDate(getMaximumDate(), unit);
    }

    
    protected Date previousStandardDate(Date date, DateTickUnit unit) {

        int milliseconds;
        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;

        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        int count = unit.getMultiple();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);

        DateTickUnitType t = unit.getUnitType();
        if (t.equals(DateTickUnitType.MILLISECOND)) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            calendar.set(years, months, days, hours, minutes, seconds);
            calendar.set(Calendar.MILLISECOND, value);
            Date mm = calendar.getTime();
            if (mm.getTime() >= date.getTime()) {
                calendar.set(Calendar.MILLISECOND, value - 1);
                mm = calendar.getTime();
            }
            return mm;
        }
        else if (t.equals(DateTickUnitType.SECOND)) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                milliseconds = 0;
            }
            else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                milliseconds = 500;
            }
            else {
                milliseconds = 999;
            }
            calendar.set(Calendar.MILLISECOND, milliseconds);
            calendar.set(years, months, days, hours, minutes, value);
            Date dd = calendar.getTime();
            if (dd.getTime() >= date.getTime()) {
                calendar.set(Calendar.SECOND, value - 1);
                dd = calendar.getTime();
            }
            return dd;
        }
        else if (t.equals(DateTickUnitType.MINUTE)) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
               seconds = 0;
            }
            else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                seconds = 30;
            }
            else {
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, hours, value, seconds);
            Date d0 = calendar.getTime();
            if (d0.getTime() >= date.getTime()) {
                calendar.set(Calendar.MINUTE, value - 1);
                d0 = calendar.getTime();
            }
            return d0;
        }
        else if (t.equals(DateTickUnitType.HOUR)) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                minutes = 0;
                seconds = 0;
            }
            else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                minutes = 30;
                seconds = 0;
            }
            else {
                minutes = 59;
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, value, minutes, seconds);
            Date d1 = calendar.getTime();
            if (d1.getTime() >= date.getTime()) {
                calendar.set(Calendar.HOUR_OF_DAY, value - 1);
                d1 = calendar.getTime();
            }
            return d1;
        }
        else if (t.equals(DateTickUnitType.DAY)) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                hours = 0;
                minutes = 0;
                seconds = 0;
            }
            else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                hours = 12;
                minutes = 0;
                seconds = 0;
            }
            else {
                hours = 23;
                minutes = 59;
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, value, hours, 0, 0);
            
                
            Date d2 = calendar.getTime();
            if (d2.getTime() >= date.getTime()) {
                calendar.set(Calendar.DATE, value - 1);
                d2 = calendar.getTime();
            }
            return d2;
        }
        else if (t.equals(DateTickUnitType.MONTH)) {
            years = calendar.get(Calendar.YEAR);
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, value, 1, 0, 0, 0);
            Month month = new Month(calendar.getTime(), this.timeZone,
                    this.locale);
            Date standardDate = calculateDateForPosition(
                    month, this.tickMarkPosition);
            long millis = standardDate.getTime();
            if (millis >= date.getTime()) {
                month = (Month) month.previous();
                
                
                month.peg(Calendar.getInstance(this.timeZone));
                standardDate = calculateDateForPosition(
                        month, this.tickMarkPosition);
            }
            return standardDate;
        }
        else if (t.equals(DateTickUnitType.YEAR)) {
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                months = 0;
                days = 1;
            }
            else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                months = 6;
                days = 1;
            }
            else {
                months = 11;
                days = 31;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(value, months, days, 0, 0, 0);
            Date d3 = calendar.getTime();
            if (d3.getTime() >= date.getTime()) {
                calendar.set(Calendar.YEAR, value - 1);
                d3 = calendar.getTime();
            }
            return d3;
        }
        else {
            return null;
        }

    }

    
    private Date calculateDateForPosition(RegularTimePeriod period,
                                          DateTickMarkPosition position) {

        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        }
        else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        }
        else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;

    }

    
    protected Date nextStandardDate(Date date, DateTickUnit unit) {
        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getMultiple());
        return calendar.getTime();
    }

    
    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault(),
                Locale.getDefault());
    }

    
    public static TickUnitSource createStandardDateTickUnits(TimeZone zone) {
    	return createStandardDateTickUnits(zone, Locale.getDefault());
    }

    
    public static TickUnitSource createStandardDateTickUnits(TimeZone zone,
    		Locale locale) {

        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (locale == null) {
        	throw new IllegalArgumentException("Null 'locale' argument.");
        }
        TickUnits units = new TickUnits();

        
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS", locale);
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss", locale);
        DateFormat f3 = new SimpleDateFormat("HH:mm", locale);
        DateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm", locale);
        DateFormat f5 = new SimpleDateFormat("d-MMM", locale);
        DateFormat f6 = new SimpleDateFormat("MMM-yyyy", locale);
        DateFormat f7 = new SimpleDateFormat("yyyy", locale);

        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);

        
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 5,
                DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 10,
                DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 25,
                DateTickUnitType.MILLISECOND, 5, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 50,
                DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 100,
                DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 250,
                DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 500,
                DateTickUnitType.MILLISECOND, 50, f1));

        
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 1,
                DateTickUnitType.MILLISECOND, 50, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 5,
                DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 10,
                DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 30,
                DateTickUnitType.SECOND, 5, f2));

        
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 1,
                DateTickUnitType.SECOND, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 2,
                DateTickUnitType.SECOND, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 5,
                DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 10,
                DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 15,
                DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 20,
                DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 30,
                DateTickUnitType.MINUTE, 5, f3));

        
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 1,
                DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 2,
                DateTickUnitType.MINUTE, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 4,
                DateTickUnitType.MINUTE, 30, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 6,
                DateTickUnitType.HOUR, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 12,
                DateTickUnitType.HOUR, 1, f4));

        
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1,
                DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2,
                DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 7,
                DateTickUnitType.DAY, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 15,
                DateTickUnitType.DAY, 1, f5));

        
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 1,
                DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 2,
                DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 3,
                DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 4,
                DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 6,
                DateTickUnitType.MONTH, 1, f6));

        
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1,
                DateTickUnitType.MONTH, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2,
                DateTickUnitType.MONTH, 3, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5,
                DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 10,
                DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 25,
                DateTickUnitType.YEAR, 5, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 50,
                DateTickUnitType.YEAR, 10, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 100,
                DateTickUnitType.YEAR, 20, f7));

        return units;

    }

    
    protected void autoAdjustRange() {

        Plot plot = getPlot();

        if (plot == null) {
            return;  
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                if (this.timeline instanceof SegmentedTimeline) {
                    
                    r = new DateRange((
                            (SegmentedTimeline) this.timeline).getStartTime(),
                            ((SegmentedTimeline) this.timeline).getStartTime()
                            + 1);
                }
                else {
                    r = new DateRange();
                }
            }

            long upper = this.timeline.toTimelineValue(
                    (long) r.getUpperBound());
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                lower = this.timeline.toTimelineValue((long) r.getLowerBound());
                double range = upper - lower;
                long minRange = (long) getAutoRangeMinimumSize();
                if (range < minRange) {
                    long expand = (long) (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }
                upper = upper + (long) (range * getUpperMargin());
                lower = lower - (long) (range * getLowerMargin());
            }

            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            DateRange dr = new DateRange(new Date(lower), new Date(upper));
            setRange(dr, false, false);
        }

    }

    
    protected void selectAutoTickUnit(Graphics2D g2,
                                      Rectangle2D dataArea,
                                      RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }

    }

    
    protected void selectHorizontalAutoTickUnit(Graphics2D g2,
            Rectangle2D dataArea, RectangleEdge edge) {

        long shift = 0;
        if (this.timeline instanceof SegmentedTimeline) {
            shift = ((SegmentedTimeline) this.timeline).getStartTime();
        }
        double zero = valueToJava2D(shift + 0.0, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2,
                getTickUnit());

        
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = valueToJava2D(shift + unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);

        
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = valueToJava2D(shift + unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    
    protected void selectVerticalAutoTickUnit(Graphics2D g2,
                                              Rectangle2D dataArea,
                                              RectangleEdge edge) {

        
        TickUnitSource tickUnits = getStandardTickUnits();
        double zero = valueToJava2D(0.0, dataArea, edge);

        
        double estimate1 = getRange().getLength() / 10.0;
        DateTickUnit candidate1
            = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate1);
        double labelHeight1 = estimateMaximumTickLabelHeight(g2, candidate1);
        double y1 = valueToJava2D(candidate1.getSize(), dataArea, edge);
        double candidate1UnitHeight = Math.abs(y1 - zero);

        
        double estimate2
            = (labelHeight1 / candidate1UnitHeight) * candidate1.getSize();
        DateTickUnit candidate2
            = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate2);
        double labelHeight2 = estimateMaximumTickLabelHeight(g2, candidate2);
        double y2 = valueToJava2D(candidate2.getSize(), dataArea, edge);
        double unit2Height = Math.abs(y2 - zero);

       
       DateTickUnit finalUnit;
       if (labelHeight2 < unit2Height) {
           finalUnit = candidate2;
       }
       else {
           finalUnit = (DateTickUnit) tickUnits.getLargerTickUnit(candidate2);
       }
       setTickUnit(finalUnit, false, false);

    }

    
    private double estimateMaximumTickLabelWidth(Graphics2D g2,
                                                 DateTickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (isVerticalTickLabels()) {
            
            
            result += lm.getHeight();
        }
        else {
            
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }

        return result;

    }

    
    private double estimateMaximumTickLabelHeight(Graphics2D g2,
                                                  DateTickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!isVerticalTickLabels()) {
            
            
            result += lm.getHeight();
        }
        else {
            
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }

        return result;

    }

    
    public List refreshTicks(Graphics2D g2,
                             AxisState state,
                             Rectangle2D dataArea,
                             RectangleEdge edge) {

        List result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;

    }

    
    private Date correctTickDateForPosition(Date time, DateTickUnit unit,
            DateTickMarkPosition position) {
        Date result = time;
        DateTickUnitType t = unit.getUnitType();
        if (t.equals(DateTickUnitType.MONTH)) {
            result = calculateDateForPosition(new Month(time,
                    this.timeZone, this.locale), position);
        }
        else if (t.equals(DateTickUnitType.YEAR)) {
            result = calculateDateForPosition(new Year(time,
                    this.timeZone, this.locale), position);
        }
        return result;
    }

    
    protected List refreshTicksHorizontal(Graphics2D g2,
                Rectangle2D dataArea, RectangleEdge edge) {

        List result = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }

        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();

        while (tickDate.before(upperDate)) {
            
            tickDate = correctTickDateForPosition(tickDate, unit,
                    this.tickMarkPosition);

            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime()
                    - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance
                        * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime)
                        && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR,
                            new Date(minorTickTime), "", TextAnchor.TOP_CENTER,
                            TextAnchor.CENTER, 0.0));
                }
            }

            if (!isHiddenValue(tickDate.getTime())) {
                
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = Math.PI / 2.0;
                    }
                    else {
                        angle = -Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    }
                    else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }

                Tick tick = new DateTick(tickDate, tickLabel, anchor,
                        rotationAnchor, angle);
                result.add(tick);

                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces;
                        minorTick++){
                    long minorTickTime = currentTickTime
                            + (nextTickTime - currentTickTime)
                            * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime)
                            && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR,
                                new Date(minorTickTime), "",
                                TextAnchor.TOP_CENTER, TextAnchor.CENTER,
                                0.0));
                    }
                }

            }
            else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                continue;
            }

        }
        return result;

    }

    
    protected List refreshTicksVertical(Graphics2D g2,
            Rectangle2D dataArea, RectangleEdge edge) {

        List result = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();

        while (tickDate.before(upperDate)) {

            
            tickDate = correctTickDateForPosition(tickDate, unit,
                    this.tickMarkPosition);

            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime()
                    - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance
                        * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime)
                        && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR,
                            new Date(minorTickTime), "", TextAnchor.TOP_CENTER,
                            TextAnchor.CENTER, 0.0));
                }
            }
            if (!isHiddenValue(tickDate.getTime())) {
                
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -Math.PI / 2.0;
                    }
                    else {
                        angle = Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }

                Tick tick = new DateTick(tickDate, tickLabel, anchor,
                        rotationAnchor, angle);
                result.add(tick);
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces;
                        minorTick++){
                    long minorTickTime = currentTickTime
                            + (nextTickTime - currentTickTime)
                            * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime)
                            && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR,
                                new Date(minorTickTime), "",
                                TextAnchor.TOP_CENTER, TextAnchor.CENTER,
                                0.0));
                    }
                }
            }
            else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
            }
        }
        return result;
    }

    
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea,
            Rectangle2D dataArea, RectangleEdge edge,
            PlotRenderingInfo plotState) {

        
        if (!isVisible()) {
            AxisState state = new AxisState(cursor);
            
            
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }

        
        AxisState state = drawTickMarksAndLabels(g2, cursor, plotArea,
                dataArea, edge, plotState);

        
        
        state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state,
                plotState);
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;

    }

    
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.timeline.toTimelineValue(
            (long) getRange().getLowerBound()
        );
        double length = (this.timeline.toTimelineValue(
                (long) getRange().getUpperBound())
                - this.timeline.toTimelineValue(
                    (long) getRange().getLowerBound()));
        Range adjusted = null;
        if (isInverted()) {
            adjusted = new DateRange(this.timeline.toMillisecond((long) (start
                    + (length * (1 - upperPercent)))),
                    this.timeline.toMillisecond((long) (start + (length
                    * (1 - lowerPercent)))));
        }
        else {
            adjusted = new DateRange(this.timeline.toMillisecond(
                    (long) (start + length * lowerPercent)),
                    this.timeline.toMillisecond((long) (start + length
                    * upperPercent)));
        }
        setRange(adjusted);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis) obj;
        if (!ObjectUtilities.equal(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.dateFormatOverride,
                that.dateFormatOverride)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickMarkPosition,
                that.tickMarkPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.timeline, that.timeline)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public int hashCode() {
        if (getLabel() != null) {
            return getLabel().hashCode();
        }
        else {
            return 0;
        }
    }

    
    public Object clone() throws CloneNotSupportedException {
        DateAxis clone = (DateAxis) super.clone();
        
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride
                = (DateFormat) this.dateFormatOverride.clone();
        }
        
        return clone;
    }

}
