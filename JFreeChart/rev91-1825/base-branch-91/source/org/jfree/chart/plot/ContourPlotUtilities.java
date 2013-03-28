

package org.jfree.chart.plot;

import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.Range;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.contour.DefaultContourDataset;


public abstract class ContourPlotUtilities {

    
    public static Range visibleRange(ContourDataset data, Range x, Range y) {
        Range range = null;
        range = ((DefaultContourDataset) data).getZValueRange(x, y);
        return range;
    }

}
