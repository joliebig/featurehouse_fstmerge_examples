

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtilities;


public class LookupPaintScale
        implements PaintScale, PublicCloneable, Serializable {

    
    class PaintItem implements Comparable, Serializable {

        
        static final long serialVersionUID = 698920578512361570L;

        
        double value;

        
        transient Paint paint;

        
        public PaintItem(double value, Paint paint) {
            this.value = value;
            this.paint = paint;
        }

        
        public int compareTo(Object obj) {
            PaintItem that = (PaintItem) obj;
            double d1 = this.value;
            double d2 = that.value;
            if (d1 > d2) {
                return 1;
            }
            if (d1 < d2) {
                return -1;
            }
            return 0;
        }

        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PaintItem)) {
                return false;
            }
            PaintItem that = (PaintItem) obj;
            if (this.value != that.value) {
                return false;
            }
            if (!PaintUtilities.equal(this.paint, that.paint)) {
                return false;
            }
            return true;
        }

        
        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            SerialUtilities.writePaint(this.paint, stream);
        }

        
        private void readObject(ObjectInputStream stream)
                throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.paint = SerialUtilities.readPaint(stream);
        }

    }

    
    static final long serialVersionUID = -5239384246251042006L;

    
    private double lowerBound;

    
    private double upperBound;

    
    private transient Paint defaultPaint;

    
    private List lookupTable;

    
    public LookupPaintScale() {
        this(0.0, 1.0, Color.lightGray);
    }

    
    public LookupPaintScale(double lowerBound, double upperBound,
            Paint defaultPaint) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException(
                    "Requires lowerBound < upperBound.");
        }
        if (defaultPaint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.defaultPaint = defaultPaint;
        this.lookupTable = new java.util.ArrayList();
    }

    
    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }

    
    public double getLowerBound() {
        return this.lowerBound;
    }

    
    public double getUpperBound() {
        return this.upperBound;
    }

    
    public void add(double value, Paint paint) {
        PaintItem item = new PaintItem(value, paint);
        int index = Collections.binarySearch(this.lookupTable, item);
        if (index >= 0) {
            this.lookupTable.set(index, item);
        }
        else {
            this.lookupTable.add(-(index + 1), item);
        }
    }

    
    public Paint getPaint(double value) {

        
        if (value < this.lowerBound) {
            return this.defaultPaint;
        }
        if (value > this.upperBound) {
            return this.defaultPaint;
        }

        int count = this.lookupTable.size();
        if (count == 0) {
            return this.defaultPaint;
        }

        
        PaintItem item = (PaintItem) this.lookupTable.get(0);
        if (value < item.value) {
            return this.defaultPaint;
        }

        
        int low = 0;
        int high = this.lookupTable.size() - 1;
        while (high - low > 1) {
            int current = (low + high) / 2;
            item = (PaintItem) this.lookupTable.get(current);
            if (value >= item.value) {
                low = current;
            }
            else {
                high = current;
            }
        }
        if (high > low) {
            item = (PaintItem) this.lookupTable.get(high);
            if (value < item.value) {
                item = (PaintItem) this.lookupTable.get(low);
            }
        }
        return (item != null ? item.paint : this.defaultPaint);
    }


    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LookupPaintScale)) {
            return false;
        }
        LookupPaintScale that = (LookupPaintScale) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (!PaintUtilities.equal(this.defaultPaint, that.defaultPaint)) {
            return false;
        }
        if (!this.lookupTable.equals(that.lookupTable)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        LookupPaintScale clone = (LookupPaintScale) super.clone();
        clone.lookupTable = new java.util.ArrayList(this.lookupTable);
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.defaultPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.defaultPaint = SerialUtilities.readPaint(stream);
    }

}
