

package org.jfree.chart.labels;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.category.CategoryDataset;


public interface CategoryItemLabelGenerator {

    
    public String generateRowLabel(CategoryDataset dataset, int row);
    
    
    public String generateColumnLabel(CategoryDataset dataset, int column);
    
    
    public String generateLabel(CategoryDataset dataset, int row, int column);
    
}
