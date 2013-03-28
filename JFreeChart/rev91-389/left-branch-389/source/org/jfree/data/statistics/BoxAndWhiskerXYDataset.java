

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.xy.XYDataset;


public interface BoxAndWhiskerXYDataset extends XYDataset {

    
    public Number getMeanValue(int series, int item);

    
    public Number getMedianValue(int series, int item);

    
    public Number getQ1Value(int series, int item);

    
    public Number getQ3Value(int series, int item);

    
    public Number getMinRegularValue(int series, int item);

    
    public Number getMaxRegularValue(int series, int item);

    
    public Number getMinOutlier(int series, int item);

    
    public Number getMaxOutlier(int series, int item);
    
    
    public List getOutliers(int series, int item);

    
    public double getOutlierCoefficient();

    
    public double getFaroutCoefficient();

}
