

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.Date;


public class OHLCDataItem implements Comparable, Serializable {

    
    private static final long serialVersionUID = 7753817154401169901L;

    
    private Date date;

    
    private Number open;

    
    private Number high;

    
    private Number low;

    
    private Number close;

    
    private Number volume;

    
    public OHLCDataItem(Date date,
                        double open,
                        double high,
                        double low,
                        double close,
                        double volume) {
        if (date == null) {
            throw new IllegalArgumentException("Null 'date' argument.");
        }
        this.date = date;
        this.open = new Double(open);
        this.high = new Double(high);
        this.low = new Double(low);
        this.close = new Double(close);
        this.volume = new Double(volume);
    }

    
    public Date getDate() {
        return this.date;
    }

    
    public Number getOpen() {
        return this.open;
    }

    
    public Number getHigh() {
        return this.high;
    }

    
    public Number getLow() {
        return this.low;
    }

    
    public Number getClose() {
        return this.close;
    }

    
    public Number getVolume() {
        return this.volume;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OHLCDataItem)) {
            return false;
        }
        OHLCDataItem that = (OHLCDataItem) obj;
        if (!this.date.equals(that.date)) {
            return false;
        }
        if (!this.high.equals(that.high)) {
            return false;
        }
        if (!this.low.equals(that.low)) {
            return false;
        }
        if (!this.open.equals(that.open)) {
            return false;
        }
        if (!this.close.equals(that.close)) {
            return false;
        }
        return true;
    }

    
    public int compareTo(Object object) {
        if (object instanceof OHLCDataItem) {
            OHLCDataItem item = (OHLCDataItem) object;
            return this.date.compareTo(item.date);
        }
        else {
            throw new ClassCastException("OHLCDataItem.compareTo().");
        }
    }

}
