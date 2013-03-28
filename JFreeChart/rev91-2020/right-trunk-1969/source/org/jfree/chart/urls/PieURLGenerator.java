

package org.jfree.chart.urls;

import org.jfree.data.general.PieDataset;


public interface PieURLGenerator {

    
    public String generateURL(PieDataset dataset, Comparable key, int pieIndex);

}
