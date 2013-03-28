

package org.jfree.chart.labels;

import org.jfree.data.general.PieDataset;


public interface PieToolTipGenerator {
    
    
    public String generateToolTip(PieDataset dataset, Comparable key);
        
}
