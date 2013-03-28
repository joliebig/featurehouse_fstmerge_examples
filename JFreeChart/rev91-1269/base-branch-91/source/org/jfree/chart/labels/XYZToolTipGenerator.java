

package org.jfree.chart.labels;

import org.jfree.data.xy.XYZDataset;


public interface XYZToolTipGenerator extends XYToolTipGenerator {

    
    public String generateToolTip(XYZDataset dataset, int series, int item);

}
