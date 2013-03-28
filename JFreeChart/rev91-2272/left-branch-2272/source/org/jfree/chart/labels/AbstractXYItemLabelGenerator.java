

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.HashUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ObjectUtilities;


public class AbstractXYItemLabelGenerator implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 5869744396278660636L;

    
    private String formatString;

    
    private NumberFormat xFormat;

    
    private DateFormat xDateFormat;

    
    private NumberFormat yFormat;

    
    private DateFormat yDateFormat;

    
    private String nullYString = "null";

    
    protected AbstractXYItemLabelGenerator() {
        this("{2}", NumberFormat.getNumberInstance(),
                NumberFormat.getNumberInstance());
    }

    
    protected AbstractXYItemLabelGenerator(String formatString,
                                           NumberFormat xFormat,
                                           NumberFormat yFormat) {

        if (formatString == null) {
            throw new IllegalArgumentException("Null 'formatString' argument.");
        }
        if (xFormat == null) {
            throw new IllegalArgumentException("Null 'xFormat' argument.");
        }
        if (yFormat == null) {
            throw new IllegalArgumentException("Null 'yFormat' argument.");
        }
        this.formatString = formatString;
        this.xFormat = xFormat;
        this.yFormat = yFormat;

    }

    
    protected AbstractXYItemLabelGenerator(String formatString,
                                           DateFormat xFormat,
                                           NumberFormat yFormat) {

        this(formatString, NumberFormat.getInstance(), yFormat);
        this.xDateFormat = xFormat;

    }

    
    protected AbstractXYItemLabelGenerator(String formatString,
            NumberFormat xFormat, DateFormat yFormat) {

        this(formatString, xFormat, NumberFormat.getInstance());
        this.yDateFormat = yFormat;
    }

    
    protected AbstractXYItemLabelGenerator(String formatString,
                                           DateFormat xFormat,
                                           DateFormat yFormat) {

        this(formatString, NumberFormat.getInstance(),
                NumberFormat.getInstance());
        this.xDateFormat = xFormat;
        this.yDateFormat = yFormat;

    }

    
    public String getFormatString() {
        return this.formatString;
    }

    
    public NumberFormat getXFormat() {
        return this.xFormat;
    }

    
    public DateFormat getXDateFormat() {
        return this.xDateFormat;
    }

    
    public NumberFormat getYFormat() {
        return this.yFormat;
    }

    
    public DateFormat getYDateFormat() {
        return this.yDateFormat;
    }

    
    public String generateLabelString(XYDataset dataset, int series, int item) {
        String result = null;
        Object[] items = createItemArray(dataset, series, item);
        result = MessageFormat.format(this.formatString, items);
        return result;
    }

    
    public String getNullYString() {
        return this.nullYString;
    }

    
    protected Object[] createItemArray(XYDataset dataset, int series,
                                       int item) {
        Object[] result = new Object[3];
        result[0] = dataset.getSeriesKey(series).toString();

        double x = dataset.getXValue(series, item);
        if (this.xDateFormat != null) {
            result[1] = this.xDateFormat.format(new Date((long) x));
        }
        else {
            result[1] = this.xFormat.format(x);
        }

        double y = dataset.getYValue(series, item);
        if (Double.isNaN(y) && dataset.getY(series, item) == null) {
            result[2] = this.nullYString;
        }
        else {
            if (this.yDateFormat != null) {
                result[2] = this.yDateFormat.format(new Date((long) y));
            }
            else {
                result[2] = this.yFormat.format(y);
            }
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYItemLabelGenerator)) {
            return false;
        }
        AbstractXYItemLabelGenerator that = (AbstractXYItemLabelGenerator) obj;
        if (!this.formatString.equals(that.formatString)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.xFormat, that.xFormat)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.xDateFormat, that.xDateFormat)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.yFormat, that.yFormat)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.yDateFormat, that.yDateFormat)) {
            return false;
        }
        if (!this.nullYString.equals(that.nullYString)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 127;
        result = HashUtilities.hashCode(result, this.formatString);
        result = HashUtilities.hashCode(result, this.xFormat);
        result = HashUtilities.hashCode(result, this.xDateFormat);
        result = HashUtilities.hashCode(result, this.yFormat);
        result = HashUtilities.hashCode(result, this.yDateFormat);
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        AbstractXYItemLabelGenerator clone
                = (AbstractXYItemLabelGenerator) super.clone();
        if (this.xFormat != null) {
            clone.xFormat = (NumberFormat) this.xFormat.clone();
        }
        if (this.yFormat != null) {
            clone.yFormat = (NumberFormat) this.yFormat.clone();
        }
        if (this.xDateFormat != null) {
            clone.xDateFormat = (DateFormat) this.xDateFormat.clone();
        }
        if (this.yDateFormat != null) {
            clone.yDateFormat = (DateFormat) this.yDateFormat.clone();
        }
        return clone;
    }

}
