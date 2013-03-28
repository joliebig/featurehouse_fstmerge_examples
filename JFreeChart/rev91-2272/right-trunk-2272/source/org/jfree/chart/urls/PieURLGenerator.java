

package org.jfree.chart.urls;

import org.jfree.data.pie.PieDataset;


public interface PieURLGenerator {

    
    public String generateURL(PieDataset dataset, Comparable key, int pieIndex);

}
