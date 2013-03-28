

package org.jfree.chart;

import java.awt.Graphics2D;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetSelectionState;


public interface RenderingSource {

    
    public Graphics2D createGraphics2D();
    
    
    public DatasetSelectionState getSelectionState(Dataset dataset);

    
    public void putSelectionState(Dataset dataset, DatasetSelectionState state);

}
