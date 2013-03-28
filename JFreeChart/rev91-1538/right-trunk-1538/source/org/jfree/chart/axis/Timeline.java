

package org.jfree.chart.axis;

import java.util.Date;


public interface Timeline {

    
    long toTimelineValue(long millisecond);

    
    long toTimelineValue(Date date);

    
    long toMillisecond(long timelineValue);

    
    boolean containsDomainValue(long millisecond);

    
    boolean containsDomainValue(Date date);

    
    boolean containsDomainRange(long fromMillisecond, long toMillisecond);

    
    boolean containsDomainRange(Date fromDate, Date toDate);

}
