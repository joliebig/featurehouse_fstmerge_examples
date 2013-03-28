

package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;


public interface CategorySeriesLabelGenerator {

    
    public String generateLabel(CategoryDataset dataset, int series);

}
