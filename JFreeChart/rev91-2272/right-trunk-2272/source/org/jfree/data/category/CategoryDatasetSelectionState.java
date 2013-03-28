

package org.jfree.data.category;

import java.io.Serializable;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.DatasetSelectionState;


public interface CategoryDatasetSelectionState extends DatasetSelectionState {

    
    public int getRowCount();

    
    public int getColumnCount();

    
    public boolean isSelected(int row, int column);

    
    public void setSelected(int row, int column, boolean selected);

    
    public void setSelected(int row, int column, boolean selected,
            boolean notify);

    
    public void clearSelection();

    
    public void fireSelectionEvent();

}
