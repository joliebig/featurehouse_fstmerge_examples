

package org.jfree.chart.plot;

import java.util.HashMap;
import java.util.Map;


public class PlotState {

    
    private Map sharedAxisStates;

    
    public PlotState() {
        this.sharedAxisStates = new HashMap();
    }

    
    public Map getSharedAxisStates() {
        return this.sharedAxisStates;
    }

}
