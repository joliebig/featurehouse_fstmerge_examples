


package org.jfree.data.pie;

import org.jfree.data.general.*;
import org.jfree.chart.event.DatasetChangeInfo;


public class AbstractPieDataset extends AbstractDataset 
        implements SelectablePieDataset{

    
    private PieDatasetSelectionState selectionState;

    
    public AbstractPieDataset() {
        super();
    }

    
    public PieDatasetSelectionState getSelectionState() {
        return this.selectionState;
    }

    
    public void setSelectionState(PieDatasetSelectionState state) {
        this.selectionState = state;
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    

}