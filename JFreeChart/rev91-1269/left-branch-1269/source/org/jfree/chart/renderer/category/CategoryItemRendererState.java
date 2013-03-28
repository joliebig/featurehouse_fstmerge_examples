

package org.jfree.chart.renderer.category;

import org.jfree.chart.plot.CategoryCrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.RendererState;


public class CategoryItemRendererState extends RendererState {

    
    private double barWidth;

    
    private double seriesRunningTotal;

    
    private CategoryCrosshairState crosshairState;

    
    public CategoryItemRendererState(PlotRenderingInfo info) {
        super(info);
        this.barWidth = 0.0;
        this.seriesRunningTotal = 0.0;
    }

    
    public double getBarWidth() {
        return this.barWidth;
    }

    
    public void setBarWidth(double width) {
        this.barWidth = width;
    }

    
    public double getSeriesRunningTotal() {
        return this.seriesRunningTotal;
    }

    
    void setSeriesRunningTotal(double total) {
        this.seriesRunningTotal = total;
    }

    
    public CategoryCrosshairState getCrosshairState() {
    	return this.crosshairState;
    }

    
    public void setCrosshairState(CategoryCrosshairState state) {
    	this.crosshairState = state;
    }

}
