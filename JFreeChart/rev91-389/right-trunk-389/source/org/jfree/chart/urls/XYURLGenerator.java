

package org.jfree.chart.urls;

import org.jfree.data.xy.XYDataset;


public interface XYURLGenerator {

    
    public String generateURL(XYDataset dataset, int series, int item);

}
