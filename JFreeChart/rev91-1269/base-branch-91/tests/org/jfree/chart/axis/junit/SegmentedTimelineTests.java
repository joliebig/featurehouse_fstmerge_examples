

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.SegmentedTimeline;


public class SegmentedTimelineTests extends TestCase {

    
    private static final int TEST_CYCLE_START = 0;

    
    private static final int TEST_CYCLE_END   = 1000;

    
    private static final int TEST_CYCLE_INC   = 55;

    
    private static final long FIVE_YEARS 
        = 5 * 365 * SegmentedTimeline.DAY_SEGMENT_SIZE;

    
    private static final NumberFormat NUMBER_FORMAT 
        = NumberFormat.getNumberInstance();

    
    private static final SimpleDateFormat DATE_FORMAT;

    
    private static final SimpleDateFormat DATE_TIME_FORMAT;

    
    private static final String[] MS_EXCEPTIONS =
        {"0", "2", "4", "10", "15", "16", "17", "18", "19", "20", "21", "22", 
         "23", "24", "47", "58", "100", "101"};

     
     private static final String[] MS2_BASE_TIMELINE_EXCEPTIONS =
         {"0", "8", "16", "24", "32", "40", "48", "56", "64", "72", "80", "88", 
          "96", "104", "112", "120", "128", "136"};

    
    private static final String[] US_HOLIDAYS =
        {"2000-01-17", "2000-02-21", "2000-04-21", "2000-05-29", "2000-07-04",
         "2000-09-04", "2000-11-23", "2000-12-25", "2001-01-01", "2001-01-15",
         "2001-02-19", "2001-04-13", "2001-05-28", "2001-07-04", "2001-09-03",
         "2001-09-11", "2001-09-12", "2001-09-13", "2001-09-14", "2001-11-22",
         "2001-12-25", "2002-01-01", "2002-01-21", "2002-02-18", "2002-03-29",
         "2002-05-27", "2002-07-04", "2002-09-02", "2002-11-28", "2002-12-25"};

     
     private static final String[] FIFTEEN_MIN_EXCEPTIONS =
         {"2000-01-10 09:00:00", "2000-01-10 09:15:00", "2000-01-10 09:30:00",
          "2000-01-10 09:45:00", "2000-01-10 10:00:00", "2000-01-10 10:15:00",
          "2000-02-15 09:00:00", "2000-02-15 09:15:00", "2000-02-15 09:30:00",
          "2000-02-15 09:45:00", "2000-02-15 10:00:00", "2000-02-15 10:15:00",
          "2000-02-16 11:00:00", "2000-02-16 11:15:00", "2000-02-16 11:30:00",
          "2000-02-16 11:45:00", "2000-02-16 12:00:00", "2000-02-16 12:15:00",
          "2000-02-16 12:30:00", "2000-02-16 12:45:00", "2000-02-16 01:00:00",
          "2000-02-16 01:15:00", "2000-02-16 01:30:00", "2000-02-16 01:45:00",
          "2000-05-17 11:45:00", "2000-05-17 12:00:00", "2000-05-17 12:15:00",
          "2000-05-17 12:30:00", "2000-05-17 12:45:00", "2000-05-17 01:00:00",
          "2000-05-17 01:15:00", "2000-05-17 01:30:00", "2000-05-17 01:45:00",
          "2000-05-17 02:00:00", "2000-05-17 02:15:00", "2000-05-17 02:30:00",
          "2000-05-17 02:45:00", "2000-05-17 03:00:00", "2000-05-17 03:15:00",
          "2000-05-17 03:30:00", "2000-05-17 03:45:00", "2000-05-17 04:00:00"};

    
    private SegmentedTimeline msTimeline;

    
    private SegmentedTimeline ms2Timeline;

    
    private SegmentedTimeline ms2BaseTimeline;

    
    private SegmentedTimeline mondayFridayTimeline;

    
    private SegmentedTimeline fifteenMinTimeline;

    
    private Calendar monday;

    
    private Calendar monday9am;

    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        DATE_FORMAT.setTimeZone(SegmentedTimeline.NO_DST_TIME_ZONE);

        DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DATE_TIME_FORMAT.setTimeZone(SegmentedTimeline.NO_DST_TIME_ZONE);
    }

    
    public static Test suite() {
        return new TestSuite(SegmentedTimelineTests.class);
    }

    
    public SegmentedTimelineTests(String name) {
        super(name);
    }

    
    protected void setUp() throws Exception {
        
        
        
        
        
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        this.msTimeline = new SegmentedTimeline(1, 5, 2);
        this.msTimeline.setStartTime(0);

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        this.ms2BaseTimeline = new SegmentedTimeline(4, 1, 1);
        this.ms2BaseTimeline.setStartTime(0);

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        this.ms2Timeline = new SegmentedTimeline(1, 2, 2);
        this.ms2Timeline.setStartTime(1);
        this.ms2Timeline.setBaseTimeline(this.ms2BaseTimeline);

        
        this.mondayFridayTimeline 
            = SegmentedTimeline.newMondayThroughFridayTimeline();

        
        this.fifteenMinTimeline 
            = SegmentedTimeline.newFifteenMinuteTimeline();

        
        Calendar cal = new GregorianCalendar(
            SegmentedTimeline.NO_DST_TIME_ZONE
        );
        cal.set(2001, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, 1);
        }
        this.monday = (Calendar) cal.clone();

        
        cal.add(Calendar.HOUR, 9);
        this.monday9am = (Calendar) cal.clone();
    }

    
    protected void tearDown() throws Exception {
        
    }

    
    
    

    
    public void testMsSegmentedTimeline() {
        
        assertEquals(1, this.msTimeline.getSegmentSize());
        assertEquals(0, this.msTimeline.getStartTime());
        assertEquals(5, this.msTimeline.getSegmentsIncluded());
        assertEquals(2, this.msTimeline.getSegmentsExcluded());
    }

    
    public void testMs2SegmentedTimeline() {
        
        assertEquals(1, this.ms2Timeline.getSegmentSize());
        assertEquals(1, this.ms2Timeline.getStartTime());
        assertEquals(2, this.ms2Timeline.getSegmentsIncluded());
        assertEquals(2, this.ms2Timeline.getSegmentsExcluded());
        assertEquals(this.ms2BaseTimeline, this.ms2Timeline.getBaseTimeline());
    }

    
    public void testMondayThroughFridaySegmentedTimeline() {
        
        assertEquals(
            SegmentedTimeline.DAY_SEGMENT_SIZE, 
            this.mondayFridayTimeline.getSegmentSize()
        );
        assertEquals(
            SegmentedTimeline.FIRST_MONDAY_AFTER_1900, 
            this.mondayFridayTimeline.getStartTime()
        );
        assertEquals(5, this.mondayFridayTimeline.getSegmentsIncluded());
        assertEquals(2, this.mondayFridayTimeline.getSegmentsExcluded());
    }

    
    public void testFifteenMinSegmentedTimeline() {
        assertEquals(SegmentedTimeline.FIFTEEN_MINUTE_SEGMENT_SIZE,
                this.fifteenMinTimeline.getSegmentSize());
        assertEquals(SegmentedTimeline.FIRST_MONDAY_AFTER_1900 + 36 
                     * this.fifteenMinTimeline.getSegmentSize(),
                     this.fifteenMinTimeline.getStartTime());
        assertEquals(28, this.fifteenMinTimeline.getSegmentsIncluded());
        assertEquals(68, this.fifteenMinTimeline.getSegmentsExcluded());
    }

    
    
    

    
    public void testMsSegment() {
        verifyOneSegment(this.msTimeline);
    }

    
    public void testMs2Segment() {
        verifyOneSegment(this.ms2Timeline);
    }

    
    public void testMondayThroughFridaySegment() {
        verifyOneSegment(this.mondayFridayTimeline);
    }

    
    public void testFifteenMinSegment() {
        verifyOneSegment(this.fifteenMinTimeline);
    }

    
    public void verifyOneSegment(SegmentedTimeline timeline) {
        
        for (long testCycle = TEST_CYCLE_START; testCycle < TEST_CYCLE_END;
             testCycle += TEST_CYCLE_INC) {

            
            SegmentedTimeline.Segment segment1 = timeline.getSegment(
                this.monday.getTime().getTime() + testCycle
            );
            SegmentedTimeline.Segment segment2 =
                timeline.getSegment(segment1.getSegmentEnd() + 1);

            
            assertEquals(
                segment1.getSegmentNumber() + 1, segment2.getSegmentNumber()
            );
            assertEquals(
                segment1.getSegmentEnd() + 1, segment2.getSegmentStart()
            );
            assertEquals(
                segment1.getSegmentStart() + timeline.getSegmentSize() - 1,
                segment1.getSegmentEnd()
            );
            assertEquals(
                segment1.getSegmentStart() + timeline.getSegmentSize(),
                segment2.getSegmentStart()
            );
            assertEquals(
                segment1.getSegmentEnd() + timeline.getSegmentSize(),
                segment2.getSegmentEnd()
            );

            
            long delta;
            if (timeline.getSegmentSize() > 1000000) {
                delta = timeline.getSegmentSize() / 10000;
            } 
            else if (timeline.getSegmentSize() > 100000) {
                delta = timeline.getSegmentSize() / 1000;
            } 
            else if (timeline.getSegmentSize() > 10000) {
                delta = timeline.getSegmentSize() / 100;
            }
            else if (timeline.getSegmentSize() > 1000) {
                delta = timeline.getSegmentSize() / 10;
            }
            else if (timeline.getSegmentSize() > 100) {
                delta = timeline.getSegmentSize() / 5;
            }
            else {
                delta = 1;
            }

            long start = segment1.getSegmentStart() + delta;
            long end = segment1.getSegmentStart() 
                       + timeline.getSegmentSize() - 1;
            SegmentedTimeline.Segment lastSeg = timeline.getSegment(
                segment1.getSegmentStart()
            );
            SegmentedTimeline.Segment seg;
            for (long i = start; i < end; i += delta) {
                seg = timeline.getSegment(i);
                assertEquals(
                    lastSeg.getSegmentNumber(), seg.getSegmentNumber()
                );
                assertEquals(lastSeg.getSegmentStart(), seg.getSegmentStart());
                assertEquals(lastSeg.getSegmentEnd(), seg.getSegmentEnd());
                assertTrue(lastSeg.getMillisecond() < seg.getMillisecond());
                lastSeg = seg;
            }

            
            seg = timeline.getSegment(end + 1);
            assertEquals(segment2.getSegmentNumber(), seg.getSegmentNumber());
            assertEquals(segment2.getSegmentStart(), seg.getSegmentStart());
            assertEquals(segment2.getSegmentEnd(), seg.getSegmentEnd());
        }
    }

    
    
    

    
    public void testMsInc() {
        verifyInc(this.msTimeline);
    }

    
    public void testMs2Inc() {
        verifyInc(this.ms2Timeline);
    }

    
    public void testMondayThroughFridayInc() {
        verifyInc(this.mondayFridayTimeline);
    }

    
    public void testFifteenMinInc() {
        verifyInc(this.fifteenMinTimeline);
    }

    
    public void verifyInc(SegmentedTimeline timeline) {
        for (long testCycle = TEST_CYCLE_START; testCycle < TEST_CYCLE_END;
             testCycle += TEST_CYCLE_INC) {

            long m = timeline.getSegmentSize();
            SegmentedTimeline.Segment segment = timeline.getSegment(testCycle);
            SegmentedTimeline.Segment seg1 = segment.copy();
            for (int i = 0; i < 1000; i++) {

                
                SegmentedTimeline.Segment seg2 = seg1.copy();
                seg2.inc();

                if ((seg1.getSegmentEnd() + 1) != seg2.getSegmentStart()) {
                    
                    
                    assertTrue(
                        !timeline.containsDomainRange(
                            seg1.getSegmentEnd() + 1, seg2.getSegmentStart() - 1
                        )
                    );
                    assertEquals(
                        0, (seg2.getSegmentStart() - seg1.getSegmentStart()) % m
                    );
                    assertEquals(
                        0, (seg2.getSegmentEnd() - seg1.getSegmentEnd()) % m
                    );
                    assertEquals(
                        0, (seg2.getMillisecond() - seg1.getMillisecond()) % m
                    );
                } 
                else {
                    
                    assertEquals(
                        seg1.getSegmentStart() + m, seg2.getSegmentStart()
                    );
                    assertEquals(
                        seg1.getSegmentEnd() + m, seg2.getSegmentEnd()
                    );
                    assertEquals(
                        seg1.getMillisecond() + m, seg2.getMillisecond()
                    );
                }

                
                SegmentedTimeline.Segment seg3 = seg1.copy();
                SegmentedTimeline.Segment seg4 = seg1.copy();

                for (int j = 0; j < i; j++) {
                    seg3.inc();
                }
                seg4.inc(i);

                assertEquals(seg3.getSegmentStart(), seg4.getSegmentStart());
                assertEquals(seg3.getSegmentEnd(), seg4.getSegmentEnd());
                assertEquals(seg3.getMillisecond(), seg4.getMillisecond());

                
                seg1.inc();
            }
        }
    }

    
    
    

    
    public void testMsIncludedAndExcludedSegments() {
        verifyIncludedAndExcludedSegments(this.msTimeline, 0);
    }

    
    public void testMs2IncludedAndExcludedSegments() {
        verifyIncludedAndExcludedSegments(this.ms2Timeline, 1);
    }

    
    public void testMondayThroughFridayIncludedAndExcludedSegments() {
        verifyIncludedAndExcludedSegments(
            this.mondayFridayTimeline, this.monday.getTime().getTime()
        );
    }

    
    public void testFifteenMinIncludedAndExcludedSegments() {
        verifyIncludedAndExcludedSegments(
            this.fifteenMinTimeline, this.monday9am.getTime().getTime()
        );
    }

    
    public void verifyIncludedAndExcludedSegments(SegmentedTimeline timeline, 
                                                  long n) {
        
        timeline.setExceptionSegments(new java.util.ArrayList());

        
        SegmentedTimeline.Segment segment = timeline.getSegment(n);
        for (int i = 0; i < 1000; i++) {
            int d = (i % timeline.getGroupSegmentCount());
            if (d < timeline.getSegmentsIncluded()) {
                
                assertTrue(segment.inIncludeSegments());
                assertTrue(!segment.inExcludeSegments());
                assertTrue(!segment.inExceptionSegments());
            } 
            else {
                
                assertTrue(!segment.inIncludeSegments());
                assertTrue(segment.inExcludeSegments());
                assertTrue(!segment.inExceptionSegments());
            }
            segment.inc();
        }
    }

    
    
    

    
    public void testMsExceptionSegments() throws ParseException {
        verifyExceptionSegments(this.msTimeline, MS_EXCEPTIONS, NUMBER_FORMAT);
    }

    
    public void testMs2BaseTimelineExceptionSegments() throws ParseException {
        verifyExceptionSegments(
            this.ms2BaseTimeline, MS2_BASE_TIMELINE_EXCEPTIONS, NUMBER_FORMAT
        );
    }

    
    public void testMondayThoughFridayExceptionSegments() 
        throws ParseException {
        verifyExceptionSegments(
            this.mondayFridayTimeline, US_HOLIDAYS, DATE_FORMAT
        );
    }

    
    public void testFifteenMinExceptionSegments() throws ParseException {
        verifyExceptionSegments(
            this.fifteenMinTimeline, FIFTEEN_MIN_EXCEPTIONS, DATE_TIME_FORMAT
        );
    }

    
    public void verifyExceptionSegments(SegmentedTimeline timeline,
                                        String[] exceptionString,
                                        Format fmt)
        throws ParseException {

        
        long[] exception = verifyFillInExceptions(
            timeline, exceptionString, fmt
        );

        int m = exception.length;

        
        assertEquals(exception.length, timeline.getExceptionSegments().size());
        SegmentedTimeline.Segment lastSegment 
            = timeline.getSegment(exception[m - 1]);
        for (int i = 0; i < m; i++) {
            SegmentedTimeline.Segment segment 
                = timeline.getSegment(exception[i]);
            assertTrue(segment.inExceptionSegments());
            
            assertEquals(m - i, timeline.getExceptionSegmentCount(
                segment.getSegmentStart(), lastSegment.getSegmentEnd()));
            
            assertEquals(
                Math.max(0, m - i - 2), timeline.getExceptionSegmentCount(
                exception[i] + 1, exception[m - 1] - 1)
            );
        }

    }

    
    
    

    
    public void testMsTranslations() throws ParseException {
        verifyFillInExceptions(this.msTimeline, MS_EXCEPTIONS, NUMBER_FORMAT);
        verifyTranslations(this.msTimeline, 0);
    }

    
    public void testMs2BaseTimelineTranslations() throws ParseException {
        verifyFillInExceptions(
            this.ms2BaseTimeline, MS2_BASE_TIMELINE_EXCEPTIONS, NUMBER_FORMAT
        );
        verifyTranslations(this.ms2BaseTimeline, 0);
    }

    
    public void testMs2Translations() throws ParseException {
        fillInBaseTimelineExceptions(
            this.ms2Timeline, MS2_BASE_TIMELINE_EXCEPTIONS, NUMBER_FORMAT
        );
        fillInBaseTimelineExclusionsAsExceptions(this.ms2Timeline, 0, 5000);
        verifyTranslations(this.ms2Timeline, 1);
    }

    
    public void testMondayThroughFridayTranslations() throws ParseException {
        verifyFillInExceptions(
            this.mondayFridayTimeline, US_HOLIDAYS, DATE_FORMAT
        );
        verifyTranslations(
            this.mondayFridayTimeline, this.monday.getTime().getTime()
        );
    }

    
    public void testFifteenMinTranslations() throws ParseException {
        verifyFillInExceptions(
            this.fifteenMinTimeline, FIFTEEN_MIN_EXCEPTIONS, DATE_TIME_FORMAT
        );
        fillInBaseTimelineExceptions(
            this.fifteenMinTimeline, US_HOLIDAYS, DATE_FORMAT
        );
        fillInBaseTimelineExclusionsAsExceptions(
            this.fifteenMinTimeline,
            this.monday9am.getTime().getTime(),
            this.monday9am.getTime().getTime() + FIVE_YEARS
        );
        verifyTranslations(
            this.fifteenMinTimeline, this.monday9am.getTime().getTime()
        );
    }

    
    public void verifyTranslations(SegmentedTimeline timeline, long startTest) {
        for (long testCycle = TEST_CYCLE_START; testCycle < TEST_CYCLE_END;
             testCycle += TEST_CYCLE_INC) {

            long millisecond = startTest + testCycle 
                               * timeline.getSegmentSize();
            SegmentedTimeline.Segment segment 
                = timeline.getSegment(millisecond);
            
            for (int i = 0; i < 1000; i++) {
                long translatedValue 
                    = timeline.toTimelineValue(segment.getMillisecond());
                long newValue = timeline.toMillisecond(translatedValue);

                if (segment.inExcludeSegments() 
                        || segment.inExceptionSegments()) {
                    
                    
                    SegmentedTimeline.Segment tempSegment = segment.copy();
                    tempSegment.moveIndexToStart();
                    do {
                        tempSegment.inc();
                    }
                    while (!tempSegment.inIncludeSegments());
                    assertEquals(tempSegment.getMillisecond(), newValue);
                }

                else {
                    assertEquals(segment.getMillisecond(), newValue);
                }
                segment.inc();
            }
        }
    }

    
    
    

    
    public void testSerialization() {
        verifySerialization(this.msTimeline);
        verifySerialization(this.ms2Timeline);
        verifySerialization(this.ms2BaseTimeline);
        verifySerialization(SegmentedTimeline.newMondayThroughFridayTimeline());
        verifySerialization(SegmentedTimeline.newFifteenMinuteTimeline());
    }

    
    private void verifySerialization(SegmentedTimeline a1) {
        SegmentedTimeline a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (SegmentedTimeline) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);
    }

    
    private long[] verifyFillInExceptions(SegmentedTimeline timeline,
                                         String[] exceptionString,
                                         Format fmt) throws ParseException {
        
        timeline.setExceptionSegments(new java.util.ArrayList());
        assertEquals(0, timeline.getExceptionSegments().size());

        
        ArrayList exceptionList = new ArrayList();
        for (int i = 0; i < exceptionString.length; i++) {
            long e;
            if (fmt instanceof NumberFormat) {
                e = ((NumberFormat) fmt).parse(exceptionString[i]).longValue();
            }
            else {
                e = timeline.getTime(
                    ((SimpleDateFormat) fmt).parse(exceptionString[i])
                );
            }
            
            SegmentedTimeline.Segment segment = timeline.getSegment(e);
            if (segment.inIncludeSegments()) {
                timeline.addException(e);
                exceptionList.add(new Long(e));
                assertEquals(
                    exceptionList.size(), timeline.getExceptionSegments().size()
                );
                assertTrue(segment.inExceptionSegments());
            }
        }

        
        long[] exception = new long[exceptionList.size()];
        int i = 0;
        for (Iterator iter = exceptionList.iterator(); iter.hasNext();) {
            Long l = (Long) iter.next();
            exception[i++] = l.longValue();
        }

        return (exception);

    }

    
    private void fillInBaseTimelineExceptions(SegmentedTimeline timeline,
                                             String[] exceptionString,
                                             Format fmt) throws ParseException {
        SegmentedTimeline baseTimeline = timeline.getBaseTimeline();
        for (int i = 0; i < exceptionString.length; i++) {
            long e;
            if (fmt instanceof NumberFormat) {
                e = ((NumberFormat) fmt).parse(exceptionString[i]).longValue();
            }
            else {
                e = timeline.getTime(
                    ((SimpleDateFormat) fmt).parse(exceptionString[i])
                );
            }
            timeline.addBaseTimelineException(e);

            
            
            SegmentedTimeline.Segment segment1 = baseTimeline.getSegment(e);
            for (SegmentedTimeline.Segment segment2 
                = timeline.getSegment(segment1.getSegmentStart());
                 segment2.getSegmentStart() <= segment1.getSegmentEnd();
                 segment2.inc()) {
                if (!segment2.inExcludeSegments()) {
                    assertTrue(segment2.inExceptionSegments());
                }
            }

        }
    }

    
    private void fillInBaseTimelineExclusionsAsExceptions(
            SegmentedTimeline timeline, long from, long to) {

        
        timeline.addBaseTimelineExclusions(from, to);

        
        for (SegmentedTimeline.Segment segment1 
                = timeline.getBaseTimeline().getSegment(from);
             segment1.getSegmentStart() <= to;
             segment1.inc()) {

            if (segment1.inExcludeSegments()) {

                
                
                for (SegmentedTimeline.Segment segment2 
                     = timeline.getSegment(segment1.getSegmentStart());
                     segment2.getSegmentStart() <= segment1.getSegmentEnd();
                     segment2.inc()) {
                    if (!segment2.inExcludeSegments()) {
                        assertTrue(segment2.inExceptionSegments());
                    }
                }
            }
        }
    }

    
    public void testCloning() {
        SegmentedTimeline l1 = new SegmentedTimeline(1000, 5, 2);
        SegmentedTimeline l2 = null;
        try {
            l2 = (SegmentedTimeline) l1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(l1 != l2);
        assertTrue(l1.getClass() == l2.getClass());
        assertTrue(l1.equals(l2));
    }

    
    public void testEquals() {
        
        SegmentedTimeline l1 = new SegmentedTimeline(1000, 5, 2);
        SegmentedTimeline l2 = new SegmentedTimeline(1000, 5, 2);
        assertTrue(l1.equals(l2));
        
        l1 = new SegmentedTimeline(1000, 5, 2);
        l2 = new SegmentedTimeline(1001, 5, 2);
        assertFalse(l1.equals(l2));
        
        l1 = new SegmentedTimeline(1000, 5, 2);
        l2 = new SegmentedTimeline(1000, 4, 2);
        assertFalse(l1.equals(l2));
        
        l1 = new SegmentedTimeline(1000, 5, 2);
        l2 = new SegmentedTimeline(1000, 5, 1);
        assertFalse(l1.equals(l2));
        
        l1 = new SegmentedTimeline(1000, 5, 2);
        l2 = new SegmentedTimeline(1000, 5, 2);
        
        
        l1.setStartTime(1234L);
        assertFalse(l1.equals(l2));
        l2.setStartTime(1234L);
        assertTrue(l1.equals(l2));

    }
    
    
    public void testHashCode() {
        SegmentedTimeline l1 = new SegmentedTimeline(1000, 5, 2);
        SegmentedTimeline l2 = new SegmentedTimeline(1000, 5, 2);
        assertTrue(l1.equals(l2));
        int h1 = l1.hashCode();
        int h2 = l2.hashCode();
        assertEquals(h1, h2);
    }    
    
    
    public void testSerialization2() {

        SegmentedTimeline l1 = new SegmentedTimeline(1000, 5, 2);
        SegmentedTimeline l2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(l1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            l2 = (SegmentedTimeline) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = l1.equals(l2);
        assertTrue(b);

    }

    
    
    

    
    public void testBasicSegmentedTimeline() {
        SegmentedTimeline stl = new SegmentedTimeline(10, 2, 3);
        stl.setStartTime(946684800000L);  
        assertFalse(stl.containsDomainValue(946684799999L));
        assertTrue(stl.containsDomainValue(946684800000L));
        assertTrue(stl.containsDomainValue(946684800019L));
        assertFalse(stl.containsDomainValue(946684800020L));
        assertFalse(stl.containsDomainValue(946684800049L));
        assertTrue(stl.containsDomainValue(946684800050L));
        assertTrue(stl.containsDomainValue(946684800069L));
        assertFalse(stl.containsDomainValue(946684800070L));
        assertFalse(stl.containsDomainValue(946684800099L));
        assertTrue(stl.containsDomainValue(946684800100L));
        
        assertEquals(0, stl.toTimelineValue(946684800000L));
        assertEquals(19, stl.toTimelineValue(946684800019L));
        assertEquals(20, stl.toTimelineValue(946684800020L));
        assertEquals(20, stl.toTimelineValue(946684800049L));
        assertEquals(20, stl.toTimelineValue(946684800050L));
        assertEquals(39, stl.toTimelineValue(946684800069L));
        assertEquals(40, stl.toTimelineValue(946684800070L));
        assertEquals(40, stl.toTimelineValue(946684800099L));
        assertEquals(40, stl.toTimelineValue(946684800100L));
        
        assertEquals(946684800000L, stl.toMillisecond(0));
        assertEquals(946684800019L, stl.toMillisecond(19));
        assertEquals(946684800050L, stl.toMillisecond(20));
        assertEquals(946684800069L, stl.toMillisecond(39));
        assertEquals(946684800100L, stl.toMillisecond(40));
        
    }
    
    
    public void testSegmentedTimelineWithException1() {
        SegmentedTimeline stl = new SegmentedTimeline(10, 2, 3);
        stl.setStartTime(946684800000L);  
        stl.addException(946684800050L);        
        assertFalse(stl.containsDomainValue(946684799999L));
        assertTrue(stl.containsDomainValue(946684800000L));
        assertTrue(stl.containsDomainValue(946684800019L));
        assertFalse(stl.containsDomainValue(946684800020L));
        assertFalse(stl.containsDomainValue(946684800049L));
        assertFalse(stl.containsDomainValue(946684800050L));
        assertFalse(stl.containsDomainValue(946684800059L));
        assertTrue(stl.containsDomainValue(946684800060L));
        assertTrue(stl.containsDomainValue(946684800069L));
        assertFalse(stl.containsDomainValue(946684800070L));
        assertFalse(stl.containsDomainValue(946684800099L));
        assertTrue(stl.containsDomainValue(946684800100L));

        
        assertEquals(0, stl.toTimelineValue(946684800000L));
        assertEquals(19, stl.toTimelineValue(946684800019L));
        assertEquals(20, stl.toTimelineValue(946684800020L));
        assertEquals(20, stl.toTimelineValue(946684800049L));
        assertEquals(20, stl.toTimelineValue(946684800050L));
        assertEquals(29, stl.toTimelineValue(946684800069L));
        assertEquals(30, stl.toTimelineValue(946684800070L));
        assertEquals(30, stl.toTimelineValue(946684800099L));
        assertEquals(30, stl.toTimelineValue(946684800100L));

        assertEquals(946684800000L, stl.toMillisecond(0));
        assertEquals(946684800019L, stl.toMillisecond(19));
        assertEquals(946684800060L, stl.toMillisecond(20));
        assertEquals(946684800069L, stl.toMillisecond(29));
        assertEquals(946684800100L, stl.toMillisecond(30));

    }    

    
    
    

    
    public static void main(String[] args) throws Exception {
        SegmentedTimelineTests test = new SegmentedTimelineTests("Test");
        test.setUp();
        test.testMondayThoughFridayExceptionSegments();
        test.tearDown();
    }

}
