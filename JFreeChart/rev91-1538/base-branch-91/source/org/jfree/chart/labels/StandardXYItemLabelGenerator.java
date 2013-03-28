

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;


public class StandardXYItemLabelGenerator extends AbstractXYItemLabelGenerator  
                                          implements XYItemLabelGenerator, 
                                                     Cloneable, 
                                                     PublicCloneable,
                                                     Serializable {

    
    private static final long serialVersionUID = 7807668053171837925L;
    
    
    public static final String DEFAULT_ITEM_LABEL_FORMAT = "{2}";

    
    public StandardXYItemLabelGenerator() {
        this(DEFAULT_ITEM_LABEL_FORMAT, NumberFormat.getNumberInstance(), 
                NumberFormat.getNumberInstance());
    }


    
    public StandardXYItemLabelGenerator(String formatString,
            NumberFormat xFormat, NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    }

    
    public StandardXYItemLabelGenerator(String formatString,
            DateFormat xFormat, NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    }

    
    public StandardXYItemLabelGenerator(String formatString, 
            NumberFormat xFormat, DateFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    }

    
    public StandardXYItemLabelGenerator(String formatString,
            DateFormat xFormat, DateFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
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
        if (!(obj instanceof StandardXYItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

}
