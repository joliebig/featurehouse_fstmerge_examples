

package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;


public interface XYSeriesLabelGenerator {

    
    public String generateLabel(XYDataset dataset, int series);

}
