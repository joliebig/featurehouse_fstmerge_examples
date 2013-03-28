

package org.jfree.chart.labels;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.contour.ContourDataset;


public interface ContourToolTipGenerator {

    
    public String generateToolTip(ContourDataset dataset, int item);

}
