

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetAndSelection;
import org.jfree.data.general.DatasetSelectionState;


public class BufferedImageRenderingSource implements RenderingSource {

    
    private BufferedImage image;

    
    private List selectionStates = new java.util.ArrayList();

    
    public BufferedImageRenderingSource(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        this.image = image;
    }

    
    public Graphics2D createGraphics2D() {
        return this.image.createGraphics();
    }

    
    public DatasetSelectionState getSelectionState(Dataset dataset) {
        Iterator iterator = this.selectionStates.iterator();
        while (iterator.hasNext()) {
            DatasetAndSelection das = (DatasetAndSelection) iterator.next();
            if (das.getDataset() == dataset) {
                return das.getSelection();
            }
        }
        
        return null;
    }

    
    public void putSelectionState(Dataset dataset,
            DatasetSelectionState state) {
        this.selectionStates.add(new DatasetAndSelection(dataset, state));
    }

}
