

package org.jfree.data.general;

import java.util.EventListener;


public interface DatasetChangeListener extends EventListener {

    
    public void datasetChanged(DatasetChangeEvent event);

}
