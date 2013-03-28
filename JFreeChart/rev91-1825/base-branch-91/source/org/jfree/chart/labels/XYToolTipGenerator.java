

package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;


public interface XYToolTipGenerator {

    
    public String generateToolTip(XYDataset dataset, int series, int item);

}
