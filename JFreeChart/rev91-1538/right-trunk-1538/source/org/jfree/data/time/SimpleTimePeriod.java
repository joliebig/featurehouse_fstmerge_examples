

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Date;


public class SimpleTimePeriod implements TimePeriod, Comparable, Serializable {

    
    private static final long serialVersionUID = 8684672361131829554L;

    
    private long start;

    
    private long end;

    
    public SimpleTimePeriod(long start, long end) {
        if (start > end) {
            throw new IllegalArgumentException("Requires start <= end.");
        }
        this.start = start;
        this.end = end;
    }

    
    public SimpleTimePeriod(Date start, Date end) {
        this(start.getTime(), end.getTime());
    }

    
    public Date getStart() {
        return new Date(this.start);
    }

    
    public long getStartMillis() {
        return this.start;
    }

    
    public Date getEnd() {
        return new Date(this.end);
    }

    
    public long getEndMillis() {
        return this.end;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimePeriod)) {
            return false;
        }
        TimePeriod that = (TimePeriod) obj;
        if (!this.getStart().equals(that.getStart())) {
            return false;
        }
        if (!this.getEnd().equals(that.getEnd())) {
            return false;
        }
        return true;
    }

    
    public int compareTo(Object obj) {
        TimePeriod that = (TimePeriod) obj;
        long t0 = getStart().getTime();
        long t1 = getEnd().getTime();
        long m0 = t0 + (t1 - t0) / 2L;
        long t2 = that.getStart().getTime();
        long t3 = that.getEnd().getTime();
        long m1 = t2 + (t3 - t2) / 2L;
        if (m0 < m1) {
            return -1;
        }
        else if (m0 > m1) {
            return 1;
        }
        else {
            if (t0 < t2) {
                return -1;
            }
            else if (t0 > t2) {
                return 1;
            }
            else {
                if (t1 < t3) {
                    return -1;
                }
                else if (t1 > t3) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        }
    }

    
    public int hashCode() {
        int result = 17;
        result = 37 * result + (int) this.start;
        result = 37 * result + (int) this.end;
        return result;
    }

}
