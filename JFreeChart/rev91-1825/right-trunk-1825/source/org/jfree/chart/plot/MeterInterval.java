

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.data.Range;


public class MeterInterval implements Serializable {

    
    private static final long serialVersionUID = 1530982090622488257L;

    
    private String label;

    
    private Range range;

    
    private transient Paint outlinePaint;

    
    private transient Stroke outlineStroke;

    
    private transient Paint backgroundPaint;

    
    public MeterInterval(String label, Range range) {
        this(label, range, Color.yellow, new BasicStroke(2.0f), null);
    }

    
    public MeterInterval(String label, Range range, Paint outlinePaint,
                         Stroke outlineStroke, Paint backgroundPaint) {
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        this.label = label;
        this.range = range;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.backgroundPaint = backgroundPaint;
    }

    
    public String getLabel() {
        return this.label;
    }

    
    public Range getRange() {
        return this.range;
    }

    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeterInterval)) {
            return false;
        }
        MeterInterval that = (MeterInterval) obj;
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!this.range.equals(that.range)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        return true;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
    }

}
