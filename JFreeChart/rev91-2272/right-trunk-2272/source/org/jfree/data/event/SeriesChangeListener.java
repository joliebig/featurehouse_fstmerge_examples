

package org.jfree.data.event;

import org.jfree.data.event.SeriesChangeEvent;
import java.util.EventListener;


public interface SeriesChangeListener extends EventListener {

    
    public void seriesChanged(SeriesChangeEvent event);

}
