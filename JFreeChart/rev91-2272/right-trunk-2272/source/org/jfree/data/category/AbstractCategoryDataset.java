


package org.jfree.data.category;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.data.general.AbstractDataset;


public class AbstractCategoryDataset extends AbstractDataset {

    
    private CategoryDatasetSelectionState selectionState;

    
    public AbstractCategoryDataset() {
        super();
    }

    
    public CategoryDatasetSelectionState getSelectionState() {
        return this.selectionState;
    }

    
    public void setSelectionState(CategoryDatasetSelectionState state) {
        this.selectionState = state;
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    

}
