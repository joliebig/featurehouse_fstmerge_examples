

package org.jfree.chart.event;


public class RendererChangeEvent extends ChartChangeEvent {

    
    private Object renderer;

    
    private boolean seriesVisibilityChanged;

    
    public RendererChangeEvent(Object renderer) {
        this(renderer, false);
    }

    
    public RendererChangeEvent(Object renderer,
            boolean seriesVisibilityChanged) {
        super(renderer);
        this.renderer = renderer;
        this.seriesVisibilityChanged = seriesVisibilityChanged;
    }

    
    public Object getRenderer() {
        return this.renderer;
    }

    
    public boolean getSeriesVisibilityChanged() {
        return this.seriesVisibilityChanged;
    }

}
