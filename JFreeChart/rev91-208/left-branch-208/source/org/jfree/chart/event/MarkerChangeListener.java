

package org.jfree.chart.event;

import java.util.EventListener;

import org.jfree.chart.plot.Marker;


public interface MarkerChangeListener extends EventListener {

    
    public void markerChanged(MarkerChangeEvent event);

}
