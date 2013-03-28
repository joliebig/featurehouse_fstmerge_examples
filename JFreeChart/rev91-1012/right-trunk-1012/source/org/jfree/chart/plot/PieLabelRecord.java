

package org.jfree.chart.plot;

import java.io.Serializable;

import org.jfree.chart.text.TextBox;


public class PieLabelRecord implements Comparable, Serializable {
    
    
    private Comparable key;
    
    
    private double angle;
    
    
    private double baseY;
    
    
    private double allocatedY;

    
    private TextBox label;
    
    
    private double labelHeight;
    
    
    private double gap;
    
    
    private double linkPercent;
    
    
    public PieLabelRecord(Comparable key, double angle, double baseY, 
                          TextBox label, double labelHeight, double gap, 
                          double linkPercent) {
        this.key = key;
        this.angle = angle;
        this.baseY = baseY;
        this.allocatedY = baseY;
        this.label = label;
        this.labelHeight = labelHeight;
        this.gap = gap;
        this.linkPercent = linkPercent;
    }
    
    
    public double getBaseY() {
        return this.baseY;   
    }
    
    
    public void setBaseY(double base) {
        this.baseY = base;   
    }
    
    
    public double getLowerY() {
        return this.allocatedY - this.labelHeight / 2.0;   
    }
    
    
    public double getUpperY() {
        return this.allocatedY + this.labelHeight / 2.0;   
    }
    
    
    public double getAngle() {
        return this.angle;   
    }
    
    
    public Comparable getKey() {
        return this.key;   
    }
    
    
    public TextBox getLabel() {
        return this.label;   
    }
    
    
    public double getLabelHeight() {
        return this.labelHeight;   
    }
    
    
    public double getAllocatedY() {
        return this.allocatedY;   
    }
    
    
    public void setAllocatedY(double y) {
        this.allocatedY = y;   
    }
    
    
    public double getGap() {
        return this.gap;   
    }
    
    
    public double getLinkPercent() {
        return this.linkPercent;   
    }
    
    
    public int compareTo(Object obj) {
        int result = 0;
        if (obj instanceof PieLabelRecord) {
            PieLabelRecord plr = (PieLabelRecord) obj;
            if (this.baseY < plr.baseY) {
                result = -1;   
            }
            else if (this.baseY > plr.baseY) {
                result = 1;   
            }
        }
        return result;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieLabelRecord)) {
            return false;
        }
        PieLabelRecord that = (PieLabelRecord) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        if (this.gap != that.gap) {
            return false;
        }
        if (this.allocatedY != that.allocatedY) {
            return false;
        }
        if (this.baseY != that.baseY) {
            return false;
        }
        if (this.labelHeight != that.labelHeight) {
            return false;
        }
        if (this.linkPercent != that.linkPercent) {
            return false;
        }
        if (!this.label.equals(that.label)) {
            return false;
        }
        return true;
    }
    
    
    public String toString() {
        return this.baseY + ", " + this.key.toString();   
    }
}
