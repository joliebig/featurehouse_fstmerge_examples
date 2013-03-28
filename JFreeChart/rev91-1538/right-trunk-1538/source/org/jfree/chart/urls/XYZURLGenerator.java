

package org.jfree.chart.urls;

import org.jfree.data.xy.XYZDataset;


public interface XYZURLGenerator extends XYURLGenerator {

    
    public String generateURL(XYZDataset dataset, int series, int item);

}
