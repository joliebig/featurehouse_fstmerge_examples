

package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;


public interface CategoryToolTipGenerator {

    
    public String generateToolTip(CategoryDataset dataset, int row, int column);
    
}
