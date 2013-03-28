

package org.jfree.data.pie;

import org.jfree.data.general.*;
import java.io.Serializable;
import org.jfree.chart.util.PublicCloneable;


public interface PieDatasetSelectionState extends DatasetSelectionState {

    
    public int getItemCount();

    
    public boolean isSelected(Comparable key);

    
    public void setSelected(Comparable key, boolean selected);

    
    public void setSelected(Comparable key, boolean selected, boolean notify);

    
    public void clearSelection();

    
    public void fireSelectionEvent();

}
