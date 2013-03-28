

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.category.CategoryDataset;


public interface MultiValueCategoryDataset extends CategoryDataset {
    
    
    public List getValues(int row, int column);

    
    public List getValues(Comparable rowKey, Comparable columnKey);

}