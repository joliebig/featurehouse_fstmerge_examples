

package org.jfree.data.event;

import org.jfree.data.general.*;
import java.io.Serializable;
import java.util.EventObject;


public class SeriesChangeEvent extends EventObject implements Serializable {

    
    private static final long serialVersionUID = 1593866085210089052L;

    
    private SeriesChangeInfo summary;

    
    public SeriesChangeEvent(Object source) {
        this(source, null);
    }

    
    public SeriesChangeEvent(Object source, SeriesChangeInfo summary) {
        super(source);
        this.summary = summary;
    }

    
    public SeriesChangeInfo getSummary() {
        return this.summary;
}

    
    public void setSummary(SeriesChangeInfo summary) {
        this.summary = summary;
    }

}
