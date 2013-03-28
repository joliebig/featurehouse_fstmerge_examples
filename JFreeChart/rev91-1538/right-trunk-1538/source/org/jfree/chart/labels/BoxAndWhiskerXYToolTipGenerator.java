

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.xy.XYDataset;


public class BoxAndWhiskerXYToolTipGenerator extends StandardXYToolTipGenerator
                                             implements XYToolTipGenerator,
                                                        Cloneable,
                                                        Serializable {

    
    private static final long serialVersionUID = -2648775791161459710L;

    
    public static final String DEFAULT_TOOL_TIP_FORMAT
        = "X: {1} Mean: {2} Median: {3} Min: {4} Max: {5} Q1: {6} Q3: {7} ";

    
    public BoxAndWhiskerXYToolTipGenerator() {
        super(
            DEFAULT_TOOL_TIP_FORMAT,
            NumberFormat.getInstance(), NumberFormat.getInstance()
        );
    }

    
    public BoxAndWhiskerXYToolTipGenerator(String toolTipFormat,
                                           DateFormat dateFormat,
                                           NumberFormat numberFormat) {

        super(toolTipFormat, dateFormat, numberFormat);

    }

    
    protected Object[] createItemArray(XYDataset dataset, int series,
                                       int item) {
        Object[] result = new Object[8];
        result[0] = dataset.getSeriesKey(series).toString();
        Number x = dataset.getX(series, item);
        if (getXDateFormat() != null) {
            result[1] = getXDateFormat().format(new Date(x.longValue()));
        }
        else {
            result[1] = getXFormat().format(x);
        }
        NumberFormat formatter = getYFormat();

        if (dataset instanceof BoxAndWhiskerXYDataset) {
            BoxAndWhiskerXYDataset d = (BoxAndWhiskerXYDataset) dataset;
            result[2] = formatter.format(d.getMeanValue(series, item));
            result[3] = formatter.format(d.getMedianValue(series, item));
            result[4] = formatter.format(d.getMinRegularValue(series, item));
            result[5] = formatter.format(d.getMaxRegularValue(series, item));
            result[6] = formatter.format(d.getQ1Value(series, item));
            result[7] = formatter.format(d.getQ3Value(series, item));
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BoxAndWhiskerXYToolTipGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

}
