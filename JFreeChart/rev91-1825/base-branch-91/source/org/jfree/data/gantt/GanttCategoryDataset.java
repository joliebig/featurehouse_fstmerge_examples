

 package org.jfree.data.gantt;

import org.jfree.data.category.IntervalCategoryDataset;


public interface GanttCategoryDataset extends IntervalCategoryDataset {

    
    public Number getPercentComplete(int row, int column);

    
    public Number getPercentComplete(Comparable rowKey, Comparable columnKey);

    
    public int getSubIntervalCount(int row, int column);

    
    public int getSubIntervalCount(Comparable rowKey, Comparable columnKey);

    
    public Number getStartValue(int row, int column, int subinterval);

    
    public Number getStartValue(Comparable rowKey, Comparable columnKey, 
                                int subinterval);

    
    public Number getEndValue(int row, int column, int subinterval);

    
    public Number getEndValue(Comparable rowKey, Comparable columnKey, 
                              int subinterval);

    
    public Number getPercentComplete(int row, int column, int subinterval);

    
    public Number getPercentComplete(Comparable rowKey, Comparable columnKey, 
                                     int subinterval);

}
