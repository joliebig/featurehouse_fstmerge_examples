

package org.jfree.data.xy;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.SeriesDataset;


public interface XYDataset extends SeriesDataset {

    
    public DomainOrder getDomainOrder();

    
    public int getItemCount(int series);

    
    public Number getX(int series, int item);

    
    public double getXValue(int series, int item);

    
    public Number getY(int series, int item);

    
    public double getYValue(int series, int item);

}
