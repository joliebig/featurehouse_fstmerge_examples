

package org.jfree.data.event;

import org.jfree.data.event.DatasetChangeEvent;
import java.util.EventListener;


public interface DatasetChangeListener extends EventListener {

    
    public void datasetChanged(DatasetChangeEvent event);

}
