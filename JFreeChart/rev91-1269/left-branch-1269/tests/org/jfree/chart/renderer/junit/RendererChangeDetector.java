

package org.jfree.chart.renderer.junit;

import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;


public class RendererChangeDetector implements RendererChangeListener {
   
     
    private boolean notified;
    
    
    public RendererChangeDetector() {
        this.notified = false;
    }
    
    
    public boolean getNotified() {
        return this.notified;
    }
    
    
    public void setNotified(boolean notified) {
        this.notified = notified;
    }
    
    
    public void rendererChanged(RendererChangeEvent event) {
        this.notified = true;
    }
    
}
