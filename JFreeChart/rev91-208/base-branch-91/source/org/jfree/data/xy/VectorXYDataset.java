

package org.jfree.data.xy;




public interface VectorXYDataset extends XYDataset {

    
    public double getVectorXValue(int series, int item);

    
    public double getVectorYValue(int series, int item);
    
    
    public Vector getVector(int series, int item);

}
