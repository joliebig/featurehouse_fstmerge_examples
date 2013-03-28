

package org.jfree.chart.urls;

import org.jfree.data.category.CategoryDataset;


public interface CategoryURLGenerator {

    
    public String generateURL(CategoryDataset data, int series, int category);

}
