

package org.jfree.chart.event;

import org.jfree.chart.plot.Marker;


public class MarkerChangeEvent extends ChartChangeEvent {

    
    private Marker marker;

    
    public MarkerChangeEvent(Marker marker) {
        super(marker);
        this.marker = marker;
    }

    
    public Marker getMarker() {
        return this.marker;
    }

}
