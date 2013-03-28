

package org.jfree.data.general;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.IntervalXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;


public interface SeriesDataset extends Dataset {

    
    public int getSeriesCount();

    
    public Comparable getSeriesKey(int series);

    
    public int indexOf(Comparable seriesKey);

}
