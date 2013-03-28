

package org.jfree.chart.axis.junit;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jfree.chart.axis.SegmentedTimeline;


public class SegmentedTimelineTests2 extends TestCase {

    
    public SegmentedTimelineTests2() {
        super();
    }

    
    public void test1() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Locale savedLocale = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
                  
        SegmentedTimeline timeline = getTimeline();      
        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
        assertTrue("test1", value == (900000 * 34) 
                && date.getTime() == reverted.getTime());
        TimeZone.setDefault(savedZone);
        Locale.setDefault(savedLocale);
    }

    
    public void test2() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
                  
        SegmentedTimeline timeline = getTimeline();      

        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
        assertTrue(
            "test2", value == (900000 * 34 + 900000) 
            && date.getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
     }

    
    public void test3() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();                 
        SegmentedTimeline timeline = getTimeline();      

        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
      
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
        assertTrue(
            "test2", value == (900000 * 34 + 900000 * 2) 
            && date.getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }

    
    public void test4() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date date = cal.getTime();
        SegmentedTimeline timeline = getTimeline();      

        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
        assertTrue(
            "test4", value == (900000 * 34 + 900000 * 2 + 1) 
            && date.getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }

    
    public void test5() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        SegmentedTimeline timeline = getTimeline();      

        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
        Calendar expectedReverted = Calendar.getInstance(Locale.UK);
        expectedReverted.set(Calendar.YEAR, 2004);
        expectedReverted.set(Calendar.MONTH, Calendar.MARCH);
        expectedReverted.set(Calendar.DAY_OF_MONTH, 26);
        expectedReverted.set(Calendar.HOUR_OF_DAY, 9);
        expectedReverted.set(Calendar.MINUTE, 0);
        expectedReverted.set(Calendar.SECOND, 0);
        expectedReverted.set(Calendar.MILLISECOND, 0);
      
        assertTrue(
            "test5", value == (900000 * 34) 
            && expectedReverted.getTime().getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }

    
    public void test6() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        SegmentedTimeline timeline = getTimeline();      

        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
      
        Calendar expectedReverted = Calendar.getInstance(Locale.UK);
        expectedReverted.set(Calendar.YEAR, 2004);
        expectedReverted.set(Calendar.MONTH, Calendar.MARCH);
        expectedReverted.set(Calendar.DAY_OF_MONTH, 29);
        expectedReverted.set(Calendar.HOUR_OF_DAY, 9);
        expectedReverted.set(Calendar.MINUTE, 0);
        expectedReverted.set(Calendar.SECOND, 0);
        expectedReverted.set(Calendar.MILLISECOND, 0);
      
        assertTrue(
            "test6", value == (900000 * 34 * 2) 
            && expectedReverted.getTime().getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }
             
    
    public void test7() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
                  
        SegmentedTimeline timeline = getTimeline();
        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
      
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
      
        Calendar expectedReverted = Calendar.getInstance();
        expectedReverted.set(Calendar.YEAR, 2004);
        expectedReverted.set(Calendar.MONTH, Calendar.MARCH);
        expectedReverted.set(Calendar.DAY_OF_MONTH, 29);
        expectedReverted.set(Calendar.HOUR_OF_DAY, 9);
        expectedReverted.set(Calendar.MINUTE, 0);
        expectedReverted.set(Calendar.SECOND, 0);
        expectedReverted.set(Calendar.MILLISECOND, 0);
      
        assertTrue(
            "test7", value == (900000 * 34 * 2) 
            && expectedReverted.getTime().getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }

    
    public void test8() {
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
                  
        SegmentedTimeline timeline = getTimeline();      
      
        
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        timeline.addException(cal.getTime());
            
        long value = timeline.toTimelineValue(date);   
        long ms = timeline.toMillisecond(value);
      
        Calendar cal2 = Calendar.getInstance(Locale.UK);
        cal2.setTime(new Date(ms));
        Date reverted = cal2.getTime();
      
        Calendar expectedReverted = Calendar.getInstance();
        expectedReverted.set(Calendar.YEAR, 2004);
        expectedReverted.set(Calendar.MONTH, Calendar.MARCH);
        expectedReverted.set(Calendar.DAY_OF_MONTH, 29);
        expectedReverted.set(Calendar.HOUR_OF_DAY, 10);
        expectedReverted.set(Calendar.MINUTE, 0);
        expectedReverted.set(Calendar.SECOND, 0);
        expectedReverted.set(Calendar.MILLISECOND, 0);
      
        assertTrue(
            "test8", value == (900000 * 34 * 2 + 900000 * (4 - 1)) 
            && expectedReverted.getTime().getTime() == reverted.getTime()
        );
        TimeZone.setDefault(savedZone);
    }
   
    
    private SegmentedTimeline getTimeline() {
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date from = cal.getTime();

        cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 30);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date to = cal.getTime();

        return getTimeline(from, to);
    }
   
    
    private SegmentedTimeline getTimeline(Date start, Date end) {
      
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date open = cal.getTime();

        cal = Calendar.getInstance(Locale.UK);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date close = cal.getTime();
                        
        SegmentedTimeline result = null;      
        
        long quarterHourCount = (close.getTime() - open.getTime())
            / SegmentedTimeline.FIFTEEN_MINUTE_SEGMENT_SIZE;
        long totalQuarterHourCount = SegmentedTimeline.DAY_SEGMENT_SIZE 
            / SegmentedTimeline.FIFTEEN_MINUTE_SEGMENT_SIZE;
        result = new SegmentedTimeline(
            SegmentedTimeline.FIFTEEN_MINUTE_SEGMENT_SIZE,
            (int) quarterHourCount, 
            (int) (totalQuarterHourCount - quarterHourCount)  
        );
        result.setAdjustForDaylightSaving(true);
        
        result.setStartTime(start.getTime());
        
        result.setBaseTimeline(
            SegmentedTimeline.newMondayThroughFridayTimeline()
        );
        
        if (start != null && end != null) {
            result.addBaseTimelineExclusions(start.getTime(), end.getTime());
        }
          
        return result;   
    }
      
    
}
