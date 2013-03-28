

package org.jfree.chart.urls;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.xy.XYDataset;


public class TimeSeriesURLGenerator implements XYURLGenerator, Serializable {

    
    private static final long serialVersionUID = -9122773175671182445L;    
    
    
    private DateFormat dateFormat = DateFormat.getInstance();
    
    
    private String prefix = "index.html";

    
    private String seriesParameterName = "series";

    
    private String itemParameterName = "item";

    
    public TimeSeriesURLGenerator() {
        super();
    }

    
    public TimeSeriesURLGenerator(DateFormat dateFormat, String prefix,
            String seriesParameterName, String itemParameterName) {

        if (dateFormat == null) {
            throw new IllegalArgumentException("Null 'dateFormat' argument.");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");
        }
        if (seriesParameterName == null) {
            throw new IllegalArgumentException(
                    "Null 'seriesParameterName' argument.");
        }
        if (itemParameterName == null) {
            throw new IllegalArgumentException(
                    "Null 'itemParameterName' argument.");
        }
        
        this.dateFormat = (DateFormat) dateFormat.clone();
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.itemParameterName = itemParameterName;

    }

    
    public DateFormat getDateFormat() {
        return (DateFormat) this.dateFormat.clone();
    }
    
    
    public String getPrefix() {
        return this.prefix;
    }
    
    
    public String getSeriesParameterName() {
        return this.seriesParameterName;
    }
    
    
    public String getItemParameterName() {
        return this.itemParameterName;
    }
    
    
    public String generateURL(XYDataset dataset, int series, int item) {
        String result = this.prefix;
        boolean firstParameter = result.indexOf("?") == -1;
        Comparable seriesKey = dataset.getSeriesKey(series);
        if (seriesKey != null) {
            result += firstParameter ? "?" : "&amp;";
            String s = null;
            try {
                s = URLEncoder.encode(seriesKey.toString(), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                s = seriesKey.toString();
            }
            result += this.seriesParameterName + "=" + s;
            firstParameter = false;
        }

        long x = (long) dataset.getXValue(series, item);
        String xValue = this.dateFormat.format(new Date(x));
        result += firstParameter ? "?" : "&amp;";
        String s = null;
        try {
            s = URLEncoder.encode(xValue, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            s = xValue;
        }
        result += this.itemParameterName + "=" + s;
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeSeriesURLGenerator)) {
            return false;
        }
        TimeSeriesURLGenerator that = (TimeSeriesURLGenerator) obj;
        if (!this.dateFormat.equals(that.dateFormat)) {
            return false;
        }
        if (!this.itemParameterName.equals(that.itemParameterName)) {
            return false;
        }
        if (!this.prefix.equals(that.prefix)) {
            return false;
        }
        if (!this.seriesParameterName.equals(that.seriesParameterName)) {
            return false;
        }
        return true;
    }

}
