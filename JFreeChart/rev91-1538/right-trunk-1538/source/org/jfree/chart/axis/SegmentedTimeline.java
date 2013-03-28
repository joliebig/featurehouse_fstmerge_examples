

package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class SegmentedTimeline implements Timeline, Cloneable, Serializable {

    
    private static final long serialVersionUID = 1093779862539903110L;

    
    
    

    
    public static final long DAY_SEGMENT_SIZE = 24 * 60 * 60 * 1000;

    
    public static final long HOUR_SEGMENT_SIZE = 60 * 60 * 1000;

    
    public static final long FIFTEEN_MINUTE_SEGMENT_SIZE = 15 * 60 * 1000;

    
    public static final long MINUTE_SEGMENT_SIZE = 60 * 1000;

    
    
    

    
    public static long FIRST_MONDAY_AFTER_1900;

    
    public static TimeZone NO_DST_TIME_ZONE;

    
    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    
    private Calendar workingCalendarNoDST;

    
    private Calendar workingCalendar = Calendar.getInstance();

    
    
    

    
    private long segmentSize;

    
    private int segmentsIncluded;

    
    private int segmentsExcluded;

    
    private int groupSegmentCount;

    
    private long startTime;

    
    private long segmentsIncludedSize;

    
    private long segmentsExcludedSize;

    
    private long segmentsGroupSize;

    
    private List exceptionSegments = new ArrayList();

    
    private SegmentedTimeline baseTimeline;

    
    private boolean adjustForDaylightSaving = false;

    
    
    

    static {
        
        int offset = TimeZone.getDefault().getRawOffset();
        NO_DST_TIME_ZONE = new SimpleTimeZone(offset, "UTC-" + offset);

        
        
        Calendar cal = new GregorianCalendar(NO_DST_TIME_ZONE);
        cal.set(1900, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, 1);
        }
        
        
        FIRST_MONDAY_AFTER_1900 = cal.getTime().getTime();
    }

    
    
    

    
    public SegmentedTimeline(long segmentSize,
                             int segmentsIncluded,
                             int segmentsExcluded) {

        this.segmentSize = segmentSize;
        this.segmentsIncluded = segmentsIncluded;
        this.segmentsExcluded = segmentsExcluded;

        this.groupSegmentCount = this.segmentsIncluded + this.segmentsExcluded;
        this.segmentsIncludedSize = this.segmentsIncluded * this.segmentSize;
        this.segmentsExcludedSize = this.segmentsExcluded * this.segmentSize;
        this.segmentsGroupSize = this.segmentsIncludedSize
                                 + this.segmentsExcludedSize;
        int offset = TimeZone.getDefault().getRawOffset();
        TimeZone z = new SimpleTimeZone(offset, "UTC-" + offset);
        this.workingCalendarNoDST = new GregorianCalendar(z,
                Locale.getDefault());
    }

    
    public static long firstMondayAfter1900() {
        int offset = TimeZone.getDefault().getRawOffset();
        TimeZone z = new SimpleTimeZone(offset, "UTC-" + offset);

        
        
        Calendar cal = new GregorianCalendar(z);
        cal.set(1900, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, 1);
        }
        
        
        return cal.getTime().getTime();
    }

    
    public static SegmentedTimeline newMondayThroughFridayTimeline() {
        SegmentedTimeline timeline
            = new SegmentedTimeline(DAY_SEGMENT_SIZE, 5, 2);
        timeline.setStartTime(firstMondayAfter1900());
        return timeline;
    }

    
    public static SegmentedTimeline newFifteenMinuteTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(
                FIFTEEN_MINUTE_SEGMENT_SIZE, 28, 68);
        timeline.setStartTime(firstMondayAfter1900() + 36
                * timeline.getSegmentSize());
        timeline.setBaseTimeline(newMondayThroughFridayTimeline());
        return timeline;
    }

    
    public boolean getAdjustForDaylightSaving() {
        return this.adjustForDaylightSaving;
    }

    
    public void setAdjustForDaylightSaving(boolean adjust) {
        this.adjustForDaylightSaving = adjust;
    }

    
    
    

    
    public void setStartTime(long millisecond) {
        this.startTime = millisecond;
    }

    
    public long getStartTime() {
        return this.startTime;
    }

    
    public int getSegmentsExcluded() {
        return this.segmentsExcluded;
    }

    
    public long getSegmentsExcludedSize() {
        return this.segmentsExcludedSize;
    }

    
    public int getGroupSegmentCount() {
        return this.groupSegmentCount;
    }

    
    public long getSegmentsGroupSize() {
        return this.segmentsGroupSize;
    }

    
    public int getSegmentsIncluded() {
        return this.segmentsIncluded;
    }

    
    public long getSegmentsIncludedSize() {
        return this.segmentsIncludedSize;
    }

    
    public long getSegmentSize() {
        return this.segmentSize;
    }

    
    public List getExceptionSegments() {
        return Collections.unmodifiableList(this.exceptionSegments);
    }

    
    public void setExceptionSegments(List exceptionSegments) {
        this.exceptionSegments = exceptionSegments;
    }

    
    public SegmentedTimeline getBaseTimeline() {
        return this.baseTimeline;
    }

    
    public void setBaseTimeline(SegmentedTimeline baseTimeline) {

        
        if (baseTimeline != null) {
            if (baseTimeline.getSegmentSize() < this.segmentSize) {
                throw new IllegalArgumentException(
                    "baseTimeline.getSegmentSize() is smaller than segmentSize"
                );
            }
            else if (baseTimeline.getStartTime() > this.startTime) {
                throw new IllegalArgumentException(
                    "baseTimeline.getStartTime() is after startTime"
                );
            }
            else if ((baseTimeline.getSegmentSize() % this.segmentSize) != 0) {
                throw new IllegalArgumentException(
                    "baseTimeline.getSegmentSize() is not multiple of "
                    + "segmentSize"
                );
            }
            else if (((this.startTime
                    - baseTimeline.getStartTime()) % this.segmentSize) != 0) {
                throw new IllegalArgumentException(
                    "baseTimeline is not aligned"
                );
            }
        }

        this.baseTimeline = baseTimeline;
    }

    
    public long toTimelineValue(long millisecond) {

        long result;
        long rawMilliseconds = millisecond - this.startTime;
        long groupMilliseconds = rawMilliseconds % this.segmentsGroupSize;
        long groupIndex = rawMilliseconds / this.segmentsGroupSize;

        if (groupMilliseconds >= this.segmentsIncludedSize) {
            result = toTimelineValue(
                this.startTime + this.segmentsGroupSize * (groupIndex + 1)
            );
        }
        else {
            Segment segment = getSegment(millisecond);
            if (segment.inExceptionSegments()) {
                do {
                    segment = getSegment(millisecond = segment.getSegmentEnd()
                            + 1);
                } while (segment.inExceptionSegments());
                result = toTimelineValue(millisecond);
            }
            else {
                long shiftedSegmentedValue = millisecond - this.startTime;
                long x = shiftedSegmentedValue % this.segmentsGroupSize;
                long y = shiftedSegmentedValue / this.segmentsGroupSize;

                long wholeExceptionsBeforeDomainValue =
                    getExceptionSegmentCount(this.startTime, millisecond - 1);





                


                if (x < this.segmentsIncludedSize) {
                    result = this.segmentsIncludedSize * y
                             + x - wholeExceptionsBeforeDomainValue
                             * this.segmentSize;
                             
                }
                else {
                    result = this.segmentsIncludedSize * (y + 1)
                             - wholeExceptionsBeforeDomainValue
                             * this.segmentSize;
                             
                }
            }
        }

        return result;
    }

    
    public long toTimelineValue(Date date) {
        return toTimelineValue(getTime(date));
        
    }

    
    public long toMillisecond(long timelineValue) {

        
        Segment result = new Segment(this.startTime + timelineValue
            + (timelineValue / this.segmentsIncludedSize)
            * this.segmentsExcludedSize);

        long lastIndex = this.startTime;

        
        while (lastIndex <= result.segmentStart) {

            
            long exceptionSegmentCount;
            while ((exceptionSegmentCount = getExceptionSegmentCount(
                 lastIndex, (result.millisecond / this.segmentSize)
                 * this.segmentSize - 1)) > 0
            ) {
                lastIndex = result.segmentStart;
                
                
                for (int i = 0; i < exceptionSegmentCount; i++) {
                    do {
                        result.inc();
                    }
                    while (result.inExcludeSegments());
                }
            }
            lastIndex = result.segmentStart;

            
            while (result.inExceptionSegments() || result.inExcludeSegments()) {
                result.inc();
                lastIndex += this.segmentSize;
            }

            lastIndex++;
        }

        return getTimeFromLong(result.millisecond);
    }

    
    public long getTimeFromLong(long date) {
        long result = date;
        if (this.adjustForDaylightSaving) {
            this.workingCalendarNoDST.setTime(new Date(date));
            this.workingCalendar.set(
                this.workingCalendarNoDST.get(Calendar.YEAR),
                this.workingCalendarNoDST.get(Calendar.MONTH),
                this.workingCalendarNoDST.get(Calendar.DATE),
                this.workingCalendarNoDST.get(Calendar.HOUR_OF_DAY),
                this.workingCalendarNoDST.get(Calendar.MINUTE),
                this.workingCalendarNoDST.get(Calendar.SECOND)
            );
            this.workingCalendar.set(
                Calendar.MILLISECOND,
                this.workingCalendarNoDST.get(Calendar.MILLISECOND)
            );
            
            
            result = this.workingCalendar.getTime().getTime();
        }
        return result;
    }

    
    public boolean containsDomainValue(long millisecond) {
        Segment segment = getSegment(millisecond);
        return segment.inIncludeSegments();
    }

    
    public boolean containsDomainValue(Date date) {
        return containsDomainValue(getTime(date));
    }

    
    public boolean containsDomainRange(long domainValueStart,
                                       long domainValueEnd) {
        if (domainValueEnd < domainValueStart) {
            throw new IllegalArgumentException(
                "domainValueEnd (" + domainValueEnd
                + ") < domainValueStart (" + domainValueStart + ")"
            );
        }
        Segment segment = getSegment(domainValueStart);
        boolean contains = true;
        do {
            contains = (segment.inIncludeSegments());
            if (segment.contains(domainValueEnd)) {
                break;
            }
            else {
                segment.inc();
            }
        }
        while (contains);
        return (contains);
    }

    
    public boolean containsDomainRange(Date dateDomainValueStart,
                                       Date dateDomainValueEnd) {
        return containsDomainRange(
            getTime(dateDomainValueStart), getTime(dateDomainValueEnd)
        );
    }

    
    public void addException(long millisecond) {
        addException(new Segment(millisecond));
    }

    
    public void addException(long fromDomainValue, long toDomainValue) {
        addException(new SegmentRange(fromDomainValue, toDomainValue));
    }

    
    public void addException(Date exceptionDate) {
        addException(getTime(exceptionDate));
        
    }

    
    public void addExceptions(List exceptionList) {
        for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
            addException((Date) iter.next());
        }
    }

    
    private void addException(Segment segment) {
         if (segment.inIncludeSegments()) {
             int p = binarySearchExceptionSegments(segment);
             this.exceptionSegments.add(-(p + 1), segment);
         }
    }

    
    public void addBaseTimelineException(long domainValue) {

        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {

            
            
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {

                    
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    
                    addException(fromDomainValue, toDomainValue);

                }
                else {
                    
                    segment.inc();
                }
            }
        }
    }

    
    public void addBaseTimelineException(Date date) {
        addBaseTimelineException(getTime(date));
    }

    
    public void addBaseTimelineExclusions(long fromBaseDomainValue,
                                          long toBaseDomainValue) {

        
        Segment baseSegment = this.baseTimeline.getSegment(fromBaseDomainValue);
        while (baseSegment.getSegmentStart() <= toBaseDomainValue
               && !baseSegment.inExcludeSegments()) {

            baseSegment.inc();

        }

        
        while (baseSegment.getSegmentStart() <= toBaseDomainValue) {

            long baseExclusionRangeEnd = baseSegment.getSegmentStart()
                 + this.baseTimeline.getSegmentsExcluded()
                 * this.baseTimeline.getSegmentSize() - 1;

            
            
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseExclusionRangeEnd) {
                if (segment.inIncludeSegments()) {

                    
                    long fromDomainValue = segment.getSegmentStart();
                    long toDomainValue;
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    }
                    while (segment.inIncludeSegments());

                    
                    addException(new BaseTimelineSegmentRange(
                        fromDomainValue, toDomainValue
                    ));
                }
                else {
                    
                    segment.inc();
                }
            }

            
            baseSegment.inc(this.baseTimeline.getGroupSegmentCount());
        }
    }

    
    public long getExceptionSegmentCount(long fromMillisecond,
                                         long toMillisecond) {
        if (toMillisecond < fromMillisecond) {
            return (0);
        }

        int n = 0;
        for (Iterator iter = this.exceptionSegments.iterator();
             iter.hasNext();) {
            Segment segment = (Segment) iter.next();
            Segment intersection
                = segment.intersect(fromMillisecond, toMillisecond);
            if (intersection != null) {
                n += intersection.getSegmentCount();
            }
        }

        return (n);
    }

    
    public Segment getSegment(long millisecond) {
        return new Segment(millisecond);
    }

    
    public Segment getSegment(Date date) {
        return (getSegment(getTime(date)));
    }

    
    private boolean equals(Object o, Object p) {
        return (o == p || ((o != null) && o.equals(p)));
    }

    
    public boolean equals(Object o) {
        if (o instanceof SegmentedTimeline) {
            SegmentedTimeline other = (SegmentedTimeline) o;

            boolean b0 = (this.segmentSize == other.getSegmentSize());
            boolean b1 = (this.segmentsIncluded == other.getSegmentsIncluded());
            boolean b2 = (this.segmentsExcluded == other.getSegmentsExcluded());
            boolean b3 = (this.startTime == other.getStartTime());
            boolean b4 = equals(
                this.exceptionSegments, other.getExceptionSegments()
            );
            return b0 && b1 && b2 && b3 && b4;
        }
        else {
            return (false);
        }
    }

    
    public int hashCode() {
        int result = 19;
        result = 37 * result
                 + (int) (this.segmentSize ^ (this.segmentSize >>> 32));
        result = 37 * result + (int) (this.startTime ^ (this.startTime >>> 32));
        return result;
    }

    
    private int binarySearchExceptionSegments(Segment segment) {
        int low = 0;
        int high = this.exceptionSegments.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Segment midSegment = (Segment) this.exceptionSegments.get(mid);

            
            if (segment.contains(midSegment) || midSegment.contains(segment)) {
                return mid;
            }

            if (midSegment.before(segment)) {
                low = mid + 1;
            }
            else if (midSegment.after(segment)) {
                high = mid - 1;
            }
            else {
                throw new IllegalStateException("Invalid condition.");
            }
        }
        return -(low + 1);  
    }

    
    public long getTime(Date date) {
        long result = date.getTime();
        if (this.adjustForDaylightSaving) {
            this.workingCalendar.setTime(date);
            this.workingCalendarNoDST.set(
                this.workingCalendar.get(Calendar.YEAR),
                this.workingCalendar.get(Calendar.MONTH),
                this.workingCalendar.get(Calendar.DATE),
                this.workingCalendar.get(Calendar.HOUR_OF_DAY),
                this.workingCalendar.get(Calendar.MINUTE),
                this.workingCalendar.get(Calendar.SECOND)
            );
            this.workingCalendarNoDST.set(
                Calendar.MILLISECOND,
                this.workingCalendar.get(Calendar.MILLISECOND)
            );
            Date revisedDate = this.workingCalendarNoDST.getTime();
            result = revisedDate.getTime();
        }

        return result;
    }

    
    public Date getDate(long value) {
        this.workingCalendarNoDST.setTime(new Date(value));
        return (this.workingCalendarNoDST.getTime());
    }

    
    public Object clone() throws CloneNotSupportedException {
        SegmentedTimeline clone = (SegmentedTimeline) super.clone();
        return clone;
    }

    
    public class Segment implements Comparable, Cloneable, Serializable {

        
        protected long segmentNumber;

        
        protected long segmentStart;

        
        protected long segmentEnd;

        
        protected long millisecond;

        
        protected Segment() {
            
        }

        
        protected Segment(long millisecond) {
            this.segmentNumber = calculateSegmentNumber(millisecond);
            this.segmentStart = SegmentedTimeline.this.startTime
                + this.segmentNumber * SegmentedTimeline.this.segmentSize;
            this.segmentEnd
                = this.segmentStart + SegmentedTimeline.this.segmentSize - 1;
            this.millisecond = millisecond;
        }

        
        public long calculateSegmentNumber(long millis) {
            if (millis >= SegmentedTimeline.this.startTime) {
                return (millis - SegmentedTimeline.this.startTime)
                    / SegmentedTimeline.this.segmentSize;
            }
            else {
                return ((millis - SegmentedTimeline.this.startTime)
                    / SegmentedTimeline.this.segmentSize) - 1;
            }
        }

        
        public long getSegmentNumber() {
            return this.segmentNumber;
        }

        
        public long getSegmentCount() {
            return 1;
        }

        
        public long getSegmentStart() {
            return this.segmentStart;
        }

        
        public long getSegmentEnd() {
            return this.segmentEnd;
        }

        
        public long getMillisecond() {
            return this.millisecond;
        }

        
        public Date getDate() {
            return SegmentedTimeline.this.getDate(this.millisecond);
        }

        
        public boolean contains(long millis) {
            return (this.segmentStart <= millis && millis <= this.segmentEnd);
        }

        
        public boolean contains(long from, long to) {
            return (this.segmentStart <= from && to <= this.segmentEnd);
        }

        
        public boolean contains(Segment segment) {
            return contains(segment.getSegmentStart(), segment.getSegmentEnd());
        }

        
        public boolean contained(long from, long to) {
            return (from <= this.segmentStart && this.segmentEnd <= to);
        }

        
        public Segment intersect(long from, long to) {
            if (from <= this.segmentStart && this.segmentEnd <= to) {
                return this;
            }
            else {
                return null;
            }
        }

        
        public boolean before(Segment other) {
            return (this.segmentEnd < other.getSegmentStart());
        }

        
        public boolean after(Segment other) {
            return (this.segmentStart > other.getSegmentEnd());
        }

        
        public boolean equals(Object object) {
            if (object instanceof Segment) {
                Segment other = (Segment) object;
                return (this.segmentNumber == other.getSegmentNumber()
                        && this.segmentStart == other.getSegmentStart()
                        && this.segmentEnd == other.getSegmentEnd()
                        && this.millisecond == other.getMillisecond());
            }
            else {
                return false;
            }
        }

        
        public Segment copy() {
            try {
                return (Segment) this.clone();
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }

        
        public int compareTo(Object object) {
            Segment other = (Segment) object;
            if (this.before(other)) {
                return -1;
            }
            else if (this.after(other)) {
                return +1;
            }
            else {
                return 0;
            }
        }

        
        public boolean inIncludeSegments() {
            if (getSegmentNumberRelativeToGroup()
                    < SegmentedTimeline.this.segmentsIncluded) {
                return !inExceptionSegments();
            }
            else {
                return false;
            }
        }

        
        public boolean inExcludeSegments() {
            return getSegmentNumberRelativeToGroup()
                >= SegmentedTimeline.this.segmentsIncluded;
        }

        
        private long getSegmentNumberRelativeToGroup() {
            long p = (this.segmentNumber
                    % SegmentedTimeline.this.groupSegmentCount);
            if (p < 0) {
                p += SegmentedTimeline.this.groupSegmentCount;
            }
            return p;
        }

        
        public boolean inExceptionSegments() {
            return binarySearchExceptionSegments(this) >= 0;
        }

        
        public void inc(long n) {
            this.segmentNumber += n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart += m;
            this.segmentEnd += m;
            this.millisecond += m;
        }

        
        public void inc() {
            inc(1);
        }

        
        public void dec(long n) {
            this.segmentNumber -= n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart -= m;
            this.segmentEnd -= m;
            this.millisecond -= m;
        }

        
        public void dec() {
            dec(1);
        }

        
        public void moveIndexToStart() {
            this.millisecond = this.segmentStart;
        }

        
        public void moveIndexToEnd() {
            this.millisecond = this.segmentEnd;
        }

    }

    
    protected class SegmentRange extends Segment {

        
        private long segmentCount;

        
        public SegmentRange(long fromMillisecond, long toMillisecond) {

            Segment start = getSegment(fromMillisecond);
            Segment end = getSegment(toMillisecond);






            this.millisecond = fromMillisecond;
            this.segmentNumber = calculateSegmentNumber(fromMillisecond);
            this.segmentStart = start.segmentStart;
            this.segmentEnd = end.segmentEnd;
            this.segmentCount
                = (end.getSegmentNumber() - start.getSegmentNumber() + 1);
        }

        
        public long getSegmentCount() {
            return this.segmentCount;
        }

        
        public Segment intersect(long from, long to) {

            
            
            
            
            long start = Math.max(from, this.segmentStart);
            long end = Math.min(to, this.segmentEnd);
            
            
            
            
            if (start <= end) {
                return new SegmentRange(start, end);
            }
            else {
                return null;
            }
        }

        
        public boolean inIncludeSegments() {
            for (Segment segment = getSegment(this.segmentStart);
                segment.getSegmentStart() < this.segmentEnd;
                segment.inc()) {
                if (!segment.inIncludeSegments()) {
                    return (false);
                }
            }
            return true;
        }

        
        public boolean inExcludeSegments() {
            for (Segment segment = getSegment(this.segmentStart);
                segment.getSegmentStart() < this.segmentEnd;
                segment.inc()) {
                if (!segment.inExceptionSegments()) {
                    return (false);
                }
            }
            return true;
        }

        
        public void inc(long n) {
            throw new IllegalArgumentException(
                "Not implemented in SegmentRange"
            );
        }

    }

    
    protected class BaseTimelineSegmentRange extends SegmentRange {

        
        public BaseTimelineSegmentRange(long fromDomainValue,
                                        long toDomainValue) {
            super(fromDomainValue, toDomainValue);
        }

    }

}
