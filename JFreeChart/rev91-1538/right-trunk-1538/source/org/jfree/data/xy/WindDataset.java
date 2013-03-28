

package org.jfree.data.xy;


public interface WindDataset extends XYDataset {

    
    public Number getWindDirection(int series, int item);

    
    public Number getWindForce(int series, int item);

}
