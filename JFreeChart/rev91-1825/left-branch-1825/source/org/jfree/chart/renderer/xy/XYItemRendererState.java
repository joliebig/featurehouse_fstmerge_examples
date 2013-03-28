

package org.jfree.chart.renderer.xy;

import java.awt.geom.Line2D;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.RendererState;
import org.jfree.data.xy.XYDataset;


public class XYItemRendererState extends RendererState {

    
    private int firstItemIndex;

    
    private int lastItemIndex;

    
    public Line2D workingLine;

    
    private boolean processVisibleItemsOnly;

    
    public XYItemRendererState(PlotRenderingInfo info) {
        super(info);
        this.workingLine = new Line2D.Double();
        this.processVisibleItemsOnly = true;
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

    
    public void startSeriesPass(XYDataset dataset, int series, int firstItem,
            int lastItem, int pass, int passCount) {
        this.firstItemIndex = firstItem;
        this.lastItemIndex = lastItem;
    }

    
    public void endSeriesPass(XYDataset dataset, int series, int firstItem,
            int lastItem, int pass, int passCount) {
        
    }

}
