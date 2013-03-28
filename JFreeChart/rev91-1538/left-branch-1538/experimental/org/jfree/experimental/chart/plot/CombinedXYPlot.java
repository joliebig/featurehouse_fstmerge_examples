

package org.jfree.experimental.chart.plot;

import java.util.Iterator;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;


public class CombinedXYPlot extends CombinedDomainXYPlot {

    
    public CombinedXYPlot(ValueAxis domainAxis, ValueAxis rangeAxis) {
        super(domainAxis);
        super.setGap(10.0);
        super.setRangeAxis(rangeAxis);
    }

    
    public void add(XYPlot subplot) {
        this.add(subplot, 1);
    }

    
    public void add(XYPlot subplot, int weight) {
        super.add(subplot, weight);

        ValueAxis l_range = super.getRangeAxis();
        subplot.setRangeAxis(0, l_range, false);

        super.setRangeAxis(l_range);
        if (null == l_range) {
            return;
        }

        l_range.configure();
    }

    
    public Range getDataRange(ValueAxis axis) {
        Range l_result = null;
        Iterator l_itr = getSubplots().iterator();
        while (l_itr.hasNext()) {
            XYPlot l_subplot = (XYPlot) l_itr.next();

            l_result = Range.combine(l_result, l_subplot.getDataRange(axis));
        }
        return l_result;
    }

    
    public void setRangeAxis(ValueAxis axis) {
        Iterator l_itr = getSubplots().iterator();
        while (l_itr.hasNext()) {
            XYPlot l_subplot = (XYPlot) l_itr.next();
            l_subplot.setRangeAxis(0, axis, false);
        }

        super.setRangeAxis(axis);
        if (null == axis) {
            return;
        }

        axis.configure();
    }

}

