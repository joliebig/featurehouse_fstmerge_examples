

package org.jfree.data.time.ohlc;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;
import org.jfree.data.time.RegularTimePeriod;


public class OHLCSeries extends ComparableObjectSeries {

    
    public OHLCSeries(Comparable key) {
        super(key, true, false);
    }

    
    public RegularTimePeriod getPeriod(int index) {
        OHLCItem item = (OHLCItem) getDataItem(index);
        return item.getPeriod();
    }

    
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

    
    public void add(RegularTimePeriod period, double open, double high,
            double low, double close) {
        if (getItemCount() > 0) {
            OHLCItem item0 = (OHLCItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException(
                        "Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new OHLCItem(period, open, high, low, close), true);
    }

    
    public ComparableObjectItem remove(int index) {
        return super.remove(index);
    }

}
