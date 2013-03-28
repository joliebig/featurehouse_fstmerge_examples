
package org.jfree.chart.panel;

import java.awt.Graphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.OverlayChangeListener;


public interface Overlay {

    
    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel);

    
    public void addChangeListener(OverlayChangeListener listener);

    
    public void removeChangeListener(OverlayChangeListener listener);

}
