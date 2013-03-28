

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;


public class IntervalXYItemLabelGenerator extends AbstractXYItemLabelGenerator
        implements XYItemLabelGenerator, Cloneable, PublicCloneable,
                   Serializable {

    
    public static final String DEFAULT_ITEM_LABEL_FORMAT = "{5} - {6}";

    
    public IntervalXYItemLabelGenerator() {
        this(DEFAULT_ITEM_LABEL_FORMAT, NumberFormat.getNumberInstance(),
            NumberFormat.getNumberInstance());
    }

    
    public IntervalXYItemLabelGenerator(String formatString,
        NumberFormat xFormat, NumberFormat yFormat) {

        super(formatString, xFormat, yFormat);
    }

    
    public IntervalXYItemLabelGenerator(String formatString,
        DateFormat xFormat, NumberFormat yFormat) {

        super(formatString, xFormat, yFormat);
    }

    
    public IntervalXYItemLabelGenerator(String formatString,
            NumberFormat xFormat, DateFormat yFormat) {

        super(formatString, xFormat, yFormat);
    }

    
    public IntervalXYItemLabelGenerator(String formatString,
            DateFormat xFormat, DateFormat yFormat) {

        super(formatString, xFormat, yFormat);
    }

    
    protected Object[] createItemArray(XYDataset dataset, int series,
                                       int item) {

        IntervalXYDataset intervalDataset = null;
        if (dataset instanceof IntervalXYDataset) {
            intervalDataset = (IntervalXYDataset) dataset;
        }
        Object[] result = new Object[7];
        result[0] = dataset.getSeriesKey(series).toString();

        double x = dataset.getXValue(series, item);
        double xs = x;
        double xe = x;
        double y = dataset.getYValue(series, item);
        double ys = y;
        double ye = y;
        if (intervalDataset != null) {
            xs = intervalDataset.getStartXValue(series, item);
            xe = intervalDataset.getEndXValue(series, item);
            ys = intervalDataset.getStartYValue(series, item);
            ye = intervalDataset.getEndYValue(series, item);
        }

        DateFormat xdf = getXDateFormat();
        if (xdf != null) {
            result[1] = xdf.format(new Date((long) x));
            result[2] = xdf.format(new Date((long) xs));
            result[3] = xdf.format(new Date((long) xe));
        }
        else {
            NumberFormat xnf = getXFormat();
            result[1] = xnf.format(x);
            result[2] = xnf.format(xs);
            result[3] = xnf.format(xe);
        }

        NumberFormat ynf = getYFormat();
        DateFormat ydf = getYDateFormat();
        if (Double.isNaN(y) && dataset.getY(series, item) == null) {
            result[4] = getNullYString();
        }
        else {
            if (ydf != null) {
                result[4] = ydf.format(new Date((long) y));
            }
            else {
                result[4] = ynf.format(y);
            }
        }
        if (Double.isNaN(ys)
                && intervalDataset.getStartY(series, item) == null) {
            result[5] = getNullYString();
        }
        else {
            if (ydf != null) {
                result[5] = ydf.format(new Date((long) ys));
            }
            else {
                result[5] = ynf.format(ys);
            }
        }
        if (Double.isNaN(ye)
                && intervalDataset.getEndY(series, item) == null) {
            result[6] = getNullYString();
        }
        else {
            if (ydf != null) {
                result[6] = ydf.format(new Date((long) ye));
            }
            else {
                result[6] = ynf.format(ye);
            }
        }
        return result;
    }

    
    public String generateLabel(XYDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalXYItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

}
