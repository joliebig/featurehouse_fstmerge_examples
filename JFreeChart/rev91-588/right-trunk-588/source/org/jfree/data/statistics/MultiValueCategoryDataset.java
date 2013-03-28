

package org.jfree.data.statistics;

import org.jfree.data.category.CategoryDataset;

import java.util.List;


public interface MultiValueCategoryDataset extends CategoryDataset {
    
    
    public List getValues(int row, int column);

    
    public List getValues(Comparable rowKey, Comparable columnKey);

}