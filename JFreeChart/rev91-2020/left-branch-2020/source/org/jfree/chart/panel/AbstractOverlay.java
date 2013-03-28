

package org.jfree.chart.panel;

import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;


public class AbstractOverlay {

    
    private transient EventListenerList changeListeners;

    
    public AbstractOverlay() {
        this.changeListeners = new EventListenerList();
    }

    
    public void addChangeListener(OverlayChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.add(OverlayChangeListener.class, listener);
    }

    
    public void removeChangeListener(OverlayChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.remove(OverlayChangeListener.class, listener);
    }

    
    public void fireOverlayChanged() {
        OverlayChangeEvent event = new OverlayChangeEvent(this);
        notifyListeners(event);
    }

    
    protected void notifyListeners(OverlayChangeEvent event) {
       Object[] listeners = this.changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == OverlayChangeListener.class) {
                ((OverlayChangeListener) listeners[i + 1]).overlayChanged(
                        event);
            }
        }
    }

}

