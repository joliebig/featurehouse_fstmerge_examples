

package org.jfree.data.statistics;

import org.jfree.data.category.CategoryDataset;


public interface StatisticalCategoryDataset extends CategoryDataset {

    
    public Number getMeanValue(int row, int column);

    
    public Number getMeanValue(Comparable rowKey, Comparable columnKey);

    
    public Number getStdDevValue(int row, int column);

    
    public Number getStdDevValue(Comparable rowKey, Comparable columnKey);

}

