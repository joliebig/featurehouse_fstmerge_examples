

package org.jfree.data.category;


public interface IntervalCategoryDataset extends CategoryDataset {

    
    public Number getStartValue(int series, int category);

    
    public Number getStartValue(Comparable series, Comparable category);

    
    public Number getEndValue(int series, int category);

    
    public Number getEndValue(Comparable series, Comparable category);

}
