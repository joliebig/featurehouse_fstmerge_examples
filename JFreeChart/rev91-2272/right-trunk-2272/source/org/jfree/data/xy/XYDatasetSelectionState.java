

package org.jfree.data.xy;

import java.io.Serializable;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.DatasetSelectionState;


public interface XYDatasetSelectionState extends DatasetSelectionState {

    
    public int getSeriesCount();

    
    public int getItemCount(int series);

    
    public boolean isSelected(int series, int item);

    
    public void setSelected(int series, int item, boolean selected);

    
    public void setSelected(int series, int item, boolean selected,
            boolean notify);

    
    public void fireSelectionEvent();
    
    
    public void clearSelection();

}
