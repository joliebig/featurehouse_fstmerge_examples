

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.XYDataset;


public class StandardXYToolTipGenerator extends AbstractXYItemLabelGenerator
        implements XYToolTipGenerator, Cloneable, PublicCloneable,
                   Serializable {

    
    private static final long serialVersionUID = -3564164459039540784L;

    
    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2})";

    
    public static StandardXYToolTipGenerator getTimeSeriesInstance() {
        return new StandardXYToolTipGenerator(DEFAULT_TOOL_TIP_FORMAT,
                DateFormat.getInstance(), NumberFormat.getInstance());
    }

    
    public StandardXYToolTipGenerator() {
        this(DEFAULT_TOOL_TIP_FORMAT, NumberFormat.getNumberInstance(),
                NumberFormat.getNumberInstance());
    }

    
    public StandardXYToolTipGenerator(String formatString,
            NumberFormat xFormat, NumberFormat yFormat) {

        super(formatString, xFormat, yFormat);

    }

    
    public StandardXYToolTipGenerator(String formatString, DateFormat xFormat,
            NumberFormat yFormat) {

        super(formatString, xFormat, yFormat);

    }

    
    public StandardXYToolTipGenerator(String formatString,
            NumberFormat xFormat, DateFormat yFormat) {

        super(formatString, xFormat, yFormat);
    }
    
    public StandardXYToolTipGenerator(String formatString,
            DateFormat xFormat, DateFormat yFormat) {

        super(formatString, xFormat, yFormat);

    }

    
    public String generateToolTip(XYDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYToolTipGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
