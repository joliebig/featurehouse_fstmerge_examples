

package org.jfree.chart.renderer.xy;

import java.awt.geom.Line2D;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYCrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.RendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYDatasetSelectionState;


public class XYItemRendererState extends RendererState {

    
    private int firstItemIndex;

    
    private int lastItemIndex;

    
    public Line2D workingLine;

    
    private boolean processVisibleItemsOnly;

    
    private XYCrosshairState crosshairState;

    
    private XYDatasetSelectionState selectionState;

    
    public XYItemRendererState(PlotRenderingInfo info) {
        super(info);
        this.workingLine = new Line2D.Double();
        this.processVisibleItemsOnly = true;
        this.crosshairState = null;
    }

    
    public boolean getProcessVisibleItemsOnly() {
        return this.processVisibleItemsOnly;
    }

    
    public void setProcessVisibleItemsOnly(boolean flag) {
        this.processVisibleItemsOnly = flag;
    }

    
    public int getFirstItemIndex() {
        return this.firstItemIndex;
    }

    
    public int getLastItemIndex() {
        return this.lastItemIndex;
    }

    
    public XYCrosshairState getCrosshairState() {
        return this.crosshairState;
    }

    
    public void setCrosshairState(XYCrosshairState state) {
        this.crosshairState = state;
    }

    
    public XYDatasetSelectionState getSelectionState() {
        return this.selectionState;
    }

    
    public void setSelectionState(XYDatasetSelectionState state) {
        this.selectionState = state;
    }

    
    public void startSeriesPass(XYDataset dataset, int series, int firstItem,
            int lastItem, int pass, int passCount) {
        this.firstItemIndex = firstItem;
        this.lastItemIndex = lastItem;
    }

    
    public void endSeriesPass(XYDataset dataset, int series, int firstItem,
            int lastItem, int pass, int passCount) {
        
    }

}
