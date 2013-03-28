

package org.jfree.data.xy;

import java.util.Arrays;
import java.util.Date;

import org.jfree.chart.util.PublicCloneable;


public class DefaultHighLowDataset extends AbstractXYDataset
        implements OHLCDataset, PublicCloneable {

    
    private Comparable seriesKey;

    
    private Date[] date;

    
    private Number[] high;

    
    private Number[] low;

    
    private Number[] open;

    
    private Number[] close;

    
    private Number[] volume;

    
    public DefaultHighLowDataset(Comparable seriesKey, Date[] date,
            double[] high, double[] low, double[] open, double[] close,
            double[] volume) {

        if (seriesKey == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Null 'date' argument.");
        }
        this.seriesKey = seriesKey;
        this.date = date;
        this.high = createNumberArray(high);
        this.low = createNumberArray(low);
        this.open = createNumberArray(open);
        this.close = createNumberArray(close);
        this.volume = createNumberArray(volume);

    }

    
    public Comparable getSeriesKey(int series) {
        return this.seriesKey;
    }

    
    public Number getX(int series, int item) {
        return new Long(this.date[item].getTime());
    }

    
    public Date getXDate(int series, int item) {
        return this.date[item];
    }

    
    public Number getY(int series, int item) {
        return getClose(series, item);
    }

    
    public Number getHigh(int series, int item) {
        return this.high[item];
    }

    
    public double getHighValue(int series, int item) {
        double result = Double.NaN;
        Number high = getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();
        }
        return result;
    }

    
    public Number getLow(int series, int item) {
        return this.low[item];
    }

    
    public double getLowValue(int series, int item) {
        double result = Double.NaN;
        Number low = getLow(series, item);
        if (low != null) {
            result = low.doubleValue();
        }
        return result;
    }

    
    public Number getOpen(int series, int item) {
        return this.open[item];
    }

    
    public double getOpenValue(int series, int item) {
        double result = Double.NaN;
        Number open = getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();
        }
        return result;
    }

    
    public Number getClose(int series, int item) {
        return this.close[item];
    }

    
    public double getCloseValue(int series, int item) {
        double result = Double.NaN;
        Number close = getClose(series, item);
        if (close != null) {
            result = close.doubleValue();
        }
        return result;
    }

    
    public Number getVolume(int series, int item) {
        return this.volume[item];
    }

    
    public double getVolumeValue(int series, int item) {
        double result = Double.NaN;
        Number volume = getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();
        }
        return result;
    }

    
    public int getSeriesCount() {
        return 1;
    }

    
    public int getItemCount(int series) {
        return this.date.length;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultHighLowDataset)) {
            return false;
        }
        DefaultHighLowDataset that = (DefaultHighLowDataset) obj;
        if (!this.seriesKey.equals(that.seriesKey)) {
            return false;
        }
        if (!Arrays.equals(this.date, that.date)) {
            return false;
        }
        if (!Arrays.equals(this.open, that.open)) {
            return false;
        }
        if (!Arrays.equals(this.high, that.high)) {
            return false;
        }
        if (!Arrays.equals(this.low, that.low)) {
            return false;
        }
        if (!Arrays.equals(this.close, that.close)) {
            return false;
        }
        if (!Arrays.equals(this.volume, that.volume)) {
            return false;
        }
        return true;
    }

    
    public static Number[] createNumberArray(double[] data) {
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

}
