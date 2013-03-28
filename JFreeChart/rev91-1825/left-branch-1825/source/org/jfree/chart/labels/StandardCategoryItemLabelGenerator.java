

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.category.CategoryDataset;
import org.jfree.util.PublicCloneable;


public class StandardCategoryItemLabelGenerator
    extends AbstractCategoryItemLabelGenerator
    implements CategoryItemLabelGenerator, Cloneable, PublicCloneable,
               Serializable {

    
    private static final long serialVersionUID = 3499701401211412882L;

    
    public static final String DEFAULT_LABEL_FORMAT_STRING = "{2}";

    
    public StandardCategoryItemLabelGenerator() {
        super(DEFAULT_LABEL_FORMAT_STRING, NumberFormat.getInstance());
    }

    
    public StandardCategoryItemLabelGenerator(String labelFormat,
                                              NumberFormat formatter) {
        super(labelFormat, formatter);
    }

    
    public StandardCategoryItemLabelGenerator(String labelFormat,
            NumberFormat formatter, NumberFormat percentFormatter) {
        super(labelFormat, formatter, percentFormatter);
    }

    
    public StandardCategoryItemLabelGenerator(String labelFormat,
                                              DateFormat formatter) {
        super(labelFormat, formatter);
    }

    
    public String generateLabel(CategoryDataset dataset, int row, int column) {
        return generateLabelString(dataset, row, column);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCategoryItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }
}
