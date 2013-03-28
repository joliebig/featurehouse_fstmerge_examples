

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.NumberFormat;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.util.PublicCloneable;


public class BoxAndWhiskerToolTipGenerator
        extends StandardCategoryToolTipGenerator
        implements CategoryToolTipGenerator, Cloneable, PublicCloneable,
                   Serializable {

    
    private static final long serialVersionUID = -6076837753823076334L;

    
    public static final String DEFAULT_TOOL_TIP_FORMAT
            = "X: {1} Mean: {2} Median: {3} Min: {4} Max: {5} Q1: {6} Q3: {7} ";

    
    public BoxAndWhiskerToolTipGenerator() {
        super(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getInstance());
    }

    
    public BoxAndWhiskerToolTipGenerator(String format,
                                         NumberFormat formatter) {
        super(format, formatter);
    }

    
    protected Object[] createItemArray(CategoryDataset dataset, int series,
                                       int item) {
        Object[] result = new Object[8];
        result[0] = dataset.getRowKey(series);
        Number y = dataset.getValue(series, item);
        NumberFormat formatter = getNumberFormat();
        result[1] = formatter.format(y);
        if (dataset instanceof BoxAndWhiskerCategoryDataset) {
            BoxAndWhiskerCategoryDataset d
                = (BoxAndWhiskerCategoryDataset) dataset;
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
        if (obj instanceof BoxAndWhiskerToolTipGenerator) {
            return super.equals(obj);
        }
        return false;
    }

}
