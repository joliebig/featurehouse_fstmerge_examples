

package org.jfree.chart.event;


public class RendererChangeEvent extends ChartChangeEvent {

    
    private Object renderer;

    
    public RendererChangeEvent(Object renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    
    public Object getRenderer() {
        return this.renderer;
    }

}
