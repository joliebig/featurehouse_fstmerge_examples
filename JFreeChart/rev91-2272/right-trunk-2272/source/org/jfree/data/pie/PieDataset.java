

package org.jfree.data.pie;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.Dataset;


public interface PieDataset extends KeyedValues, Dataset {

    
    public PieDatasetSelectionState getSelectionState();

}
