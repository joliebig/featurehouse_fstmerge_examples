

package org.jfree.chart.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OutlierListCollection {

    
    private List outlierLists;
    
    
    private boolean highFarOut = false;

    
    private boolean lowFarOut = false;
    
    
    public OutlierListCollection() {
        this.outlierLists = new ArrayList();
    }
    
    
    public boolean isHighFarOut() {
        return this.highFarOut;
    }

    
    public void setHighFarOut(boolean farOut) {
        this.highFarOut = farOut;
    }

    
    public boolean isLowFarOut() {
        return this.lowFarOut;
    }

    
    public void setLowFarOut(boolean farOut) {
        this.lowFarOut = farOut;
    }
    
    public boolean add(Outlier outlier) {

        if (this.outlierLists.isEmpty()) {
            return this.outlierLists.add(new OutlierList(outlier));
        } 
        else {
            boolean updated = false;
            for (Iterator iterator = this.outlierLists.iterator(); 
                 iterator.hasNext();) {
                OutlierList list = (OutlierList) iterator.next();
                if (list.isOverlapped(outlier)) {
                    updated = updateOutlierList(list, outlier);
                }
            }
            if (!updated) {
                
                updated = this.outlierLists.add(new OutlierList(outlier));
            }
            return updated;
        }

    }

    
    public Iterator iterator() {
        return this.outlierLists.iterator();    
    }
    
    
    
    private boolean updateOutlierList(OutlierList list, Outlier outlier) {
        boolean result = false;
        result = list.add(outlier);
        list.updateAveragedOutlier();
        list.setMultiple(true);
        return result;
    }

}
