

package org.jfree.data.general;


public class DatasetAndSelection {

    
    private Dataset dataset;

    
    private DatasetSelectionState selection;

    
    public DatasetAndSelection(Dataset dataset, DatasetSelectionState selection) {
        this.dataset = dataset;
        this.selection = selection;
    }

    
    public Dataset getDataset() {
        return this.dataset;
    }

    
    public DatasetSelectionState getSelection() {
        return this.selection;
    }

}

