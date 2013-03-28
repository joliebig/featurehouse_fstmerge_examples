
package org.jfree.data.xy;



public interface XYZDataset extends XYDataset {

    
    public Number getZ(int series, int item);
    
    
    public double getZValue(int series, int item);

}
