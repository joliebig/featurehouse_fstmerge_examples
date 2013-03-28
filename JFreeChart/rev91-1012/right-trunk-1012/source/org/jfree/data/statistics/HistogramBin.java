

package org.jfree.data.statistics;

import java.io.Serializable;


public class HistogramBin implements Cloneable, Serializable {
    
    
    private static final long serialVersionUID = 7614685080015589931L;
    
    
    private int count;
    
    
    private double startBoundary;
    
    
    private double endBoundary;

    
    public HistogramBin(double startBoundary, double endBoundary) {
        if (startBoundary > endBoundary) {
            throw new IllegalArgumentException(
                    "HistogramBin():  startBoundary > endBoundary.");
        }
        this.count = 0;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    
    public int getCount() {
        return this.count;
    }
    
    
    public void incrementCount() {
        this.count++;
    }
    
    
    public double getStartBoundary() {
        return this.startBoundary;
    }
    
    
    public double getEndBoundary() {
        return this.endBoundary;
    }
    
    
    public double getBinWidth() {
        return this.endBoundary - this.startBoundary;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;   
        }
        if (obj == this) {
            return true;   
        }
        if (obj instanceof HistogramBin) {
            HistogramBin bin = (HistogramBin) obj;
            boolean b0 = bin.startBoundary == this.startBoundary;
            boolean b1 = bin.endBoundary == this.endBoundary;
            boolean b2 = bin.count == this.count;
            return b0 && b1 && b2;
        }
        return false;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();   
    }
    
}
