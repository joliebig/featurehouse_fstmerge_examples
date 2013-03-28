

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.category.CategoryDataset;


public interface BoxAndWhiskerCategoryDataset extends CategoryDataset {

    
    public Number getMeanValue(int row, int column);

    
    public Number getMeanValue(Comparable rowKey, Comparable columnKey);

    
    public Number getMedianValue(int row, int column);

    
    public Number getMedianValue(Comparable rowKey, Comparable columnKey);

    
    public Number getQ1Value(int row, int column);

    
    public Number getQ1Value(Comparable rowKey, Comparable columnKey);

    
    public Number getQ3Value(int row, int column);

    
    public Number getQ3Value(Comparable rowKey, Comparable columnKey);

    
    public Number getMinRegularValue(int row, int column);

    
    public Number getMinRegularValue(Comparable rowKey, Comparable columnKey);

    
    public Number getMaxRegularValue(int row, int column);

    
    public Number getMaxRegularValue(Comparable rowKey, Comparable columnKey);

    
    public Number getMinOutlier(int row, int column);

    
    public Number getMinOutlier(Comparable rowKey, Comparable columnKey);

    
    public Number getMaxOutlier(int row, int column);

    
    public Number getMaxOutlier(Comparable rowKey, Comparable columnKey);

    
    public List getOutliers(int row, int column);

    
    public List getOutliers(Comparable rowKey, Comparable columnKey);

}
