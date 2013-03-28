

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.PieDataset;


public class StandardPieToolTipGenerator extends AbstractPieItemLabelGenerator
                                           implements PieToolTipGenerator,
                                                      Cloneable, 
                                                      PublicCloneable, 
                                                      Serializable {

    
    private static final long serialVersionUID = 2995304200445733779L;
    
    
    public static final String DEFAULT_TOOLTIP_FORMAT = "{0}: ({1}, {2})";

    
    public StandardPieToolTipGenerator() {
        this(DEFAULT_TOOLTIP_FORMAT);
    }

    
    public StandardPieToolTipGenerator(Locale locale) {
        this(DEFAULT_TOOLTIP_FORMAT, locale);
    }
    
    
    public StandardPieToolTipGenerator(String labelFormat) {
        this(labelFormat, Locale.getDefault());
    }
    
    
    public StandardPieToolTipGenerator(String labelFormat, Locale locale) {
        this(labelFormat, NumberFormat.getNumberInstance(locale), 
                NumberFormat.getPercentInstance(locale));
    }
    
    
    public StandardPieToolTipGenerator(String labelFormat, 
            NumberFormat numberFormat, NumberFormat percentFormat) {
        super(labelFormat, numberFormat, percentFormat);
    }

    
    public String generateToolTip(PieDataset dataset, Comparable key) {
        return generateSectionLabel(dataset, key);
    }

    
    public Object clone() throws CloneNotSupportedException {      
        return super.clone();
    }

}
