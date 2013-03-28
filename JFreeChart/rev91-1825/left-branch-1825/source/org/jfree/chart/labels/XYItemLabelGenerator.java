

package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;


public interface XYItemLabelGenerator {

    
    public String generateLabel(XYDataset dataset, int series, int item);

}
