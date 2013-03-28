

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


public class FixedMillisecond extends RegularTimePeriod
        implements Serializable {

    
    private static final long serialVersionUID = 7867521484545646931L;

    
    private long time;

    
    public FixedMillisecond() {
        this(new Date());
    }

    
    public FixedMillisecond(long millisecond) {
        this(new Date(millisecond));
    }

    
    public FixedMillisecond(Date time) {
        this.time = time.getTime();
    }

    
    public Date getTime() {
        return new Date(this.time);
    }

    
    public void peg(Calendar calendar) {
        
    }

    
    public RegularTimePeriod previous() {
        RegularTimePeriod result = null;
        long t = this.time;
        if (t != Long.MIN_VALUE) {
            result = new FixedMillisecond(t - 1);
        }
        return result;
    }

    
    public RegularTimePeriod next() {
        RegularTimePeriod result = null;
        long t = this.time;
        if (t != Long.MAX_VALUE) {
            result = new FixedMillisecond(t + 1);
        }
        return result;
    }

    
    public boolean equals(Object object) {
        if (object instanceof FixedMillisecond) {
            FixedMillisecond m = (FixedMillisecond) object;
            return this.time == m.getFirstMillisecond();
        }
        else {
            return false;
        }

    }

    
    public int hashCode() {
        return (int) this.time;
    }

    
    public int compareTo(Object o1) {

        int result;
        long difference;

        
        
        if (o1 instanceof FixedMillisecond) {
            FixedMillisecond t1 = (FixedMillisecond) o1;
            difference = this.time - t1.time;
            if (difference > 0) {
                result = 1;
            }
            else {
                if (difference < 0) {
                   result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        
        
        else if (o1 instanceof RegularTimePeriod) {
            
            result = 0;
        }

        
        
        else {
            
            result = 1;
        }

        return result;

    }

    
    public long getFirstMillisecond() {
        return this.time;
    }


    
    public long getFirstMillisecond(Calendar calendar) {
        return this.time;
    }

    
    public long getLastMillisecond() {
        return this.time;
    }

    
    public long getLastMillisecond(Calendar calendar) {
        return this.time;
    }

    
    public long getMiddleMillisecond() {
        return this.time;
    }

    
    public long getMiddleMillisecond(Calendar calendar) {
        return this.time;
    }

    
    public long getSerialIndex() {
        return this.time;
    }

}
