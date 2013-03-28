

package org.jfree.chart.renderer;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;


public class RendererState {

    
    private PlotRenderingInfo info;

    
    public RendererState(PlotRenderingInfo info) {
        this.info = info;
    }

    
    public PlotRenderingInfo getInfo() {
        return this.info;
    }

    
    public EntityCollection getEntityCollection() {
        EntityCollection result = null;
        if (this.info != null) {
            ChartRenderingInfo owner = this.info.getOwner();
            if (owner != null) {
                result = owner.getEntityCollection();
            }
        }
        return result;
    }

}
