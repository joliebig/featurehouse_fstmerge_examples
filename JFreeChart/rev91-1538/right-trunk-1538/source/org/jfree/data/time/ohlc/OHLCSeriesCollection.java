

package org.jfree.data.time.ohlc;

import java.io.Serializable;
import java.util.List;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;


public class OHLCSeriesCollection extends AbstractXYDataset
                                implements OHLCDataset, Serializable {

    
    private List data;

    private TimePeriodAnchor xPosition = TimePeriodAnchor.MIDDLE;

    
    public OHLCSeriesCollection() {
        this.data = new java.util.ArrayList();
    }

    
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    
    public void setXPosition(TimePeriodAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    
    public void addSeries(OHLCSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    
    public int getSeriesCount() {
        return this.data.size();
    }

    
    public OHLCSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (OHLCSeries) this.data.get(series);
    }

    
    public Comparable getSeriesKey(int series) {
        
        return getSeries(series).getKey();
    }

    
    public int getItemCount(int series) {
        
        return getSeries(series).getItemCount();
    }

    
    protected synchronized long getX(RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond();
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond();
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond();
        }
        return result;
    }

    
    public double getXValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        RegularTimePeriod period = di.getPeriod();
        return getX(period);
    }

    
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    
    public Number getY(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return new Double(di.getYValue());
    }

    
    public double getOpenValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getOpenValue();
    }

    
    public Number getOpen(int series, int item) {
        return new Double(getOpenValue(series, item));
    }

    
    public double getCloseValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getCloseValue();
    }

    
    public Number getClose(int series, int item) {
        return new Double(getCloseValue(series, item));
    }

    
    public double getHighValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getHighValue();
    }

    
    public Number getHigh(int series, int item) {
        return new Double(getHighValue(series, item));
    }

    
    public double getLowValue(int series, int item) {
        OHLCSeries s = (OHLCSeries) this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getLowValue();
    }

    
    public Number getLow(int series, int item) {
        return new Double(getLowValue(series, item));
    }

    
    public Number getVolume(int series, int item) {
        return null;
    }

    
    public double getVolumeValue(int series, int item) {
        return Double.NaN;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OHLCSeriesCollection)) {
            return false;
        }
        OHLCSeriesCollection that = (OHLCSeriesCollection) obj;
        if (!this.xPosition.equals(that.xPosition)) {
            return false;
        }
        return ObjectUtilities.equal(this.data, that.data);
    }

    
    public Object clone() throws CloneNotSupportedException {
        OHLCSeriesCollection clone
                = (OHLCSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }

}
