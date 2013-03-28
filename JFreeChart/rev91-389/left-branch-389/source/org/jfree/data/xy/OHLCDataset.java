

package org.jfree.data.xy;


public interface OHLCDataset extends XYDataset {

    
    public Number getHigh(int series, int item);

    
    public double getHighValue(int series, int item);
    
    
    public Number getLow(int series, int item);

    
    public double getLowValue(int series, int item);
    
    
    public Number getOpen(int series, int item);

    
    public double getOpenValue(int series, int item);
    
    
    public Number getClose(int series, int item);

    
    public double getCloseValue(int series, int item);
    
    
    public Number getVolume(int series, int item);
    
    
    public double getVolumeValue(int series, int item);
    
}
