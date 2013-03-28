

package org.jfree.chart.event;

import java.util.EventListener;
import org.jfree.chart.panel.Overlay;


public interface OverlayChangeListener extends EventListener {

    
    public void overlayChanged(OverlayChangeEvent event);

}

