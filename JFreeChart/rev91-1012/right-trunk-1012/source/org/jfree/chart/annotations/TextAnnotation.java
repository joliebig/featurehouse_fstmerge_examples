

package org.jfree.chart.annotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.SerialUtilities;


public class TextAnnotation implements Serializable {

    
    private static final long serialVersionUID = 7008912287533127432L;
    
    
    public static final Font DEFAULT_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    
    public static final Paint DEFAULT_PAINT = Color.black;
    
    
    public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;

        
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    
    
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;

    
    private String text;

    
    private Font font;

    
    private transient Paint paint;
    
    
    private TextAnchor textAnchor;
    
    
    private TextAnchor rotationAnchor;
    
    
    private double rotationAngle;

    
    protected TextAnnotation(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.textAnchor = DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = DEFAULT_ROTATION_ANGLE;
    }

    
    public String getText() {
        return this.text;
    }

    
    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
    }
    
    
    public Font getFont() {
        return this.font;
    }

    
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
    }
    
    
    public Paint getPaint() {
        return this.paint;
    }
    
    
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
    }

    
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    
    public void setTextAnchor(TextAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.textAnchor = anchor;
    }
    
    
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    
    public void setRotationAnchor(TextAnchor anchor) {
        this.rotationAnchor = anchor;    
    }
    
    
    public double getRotationAngle() {
        return this.rotationAngle; 
    }
    
    
    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;    
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!(obj instanceof TextAnnotation)) {
            return false;
        }
        TextAnnotation that = (TextAnnotation) obj;
        if (!ObjectUtilities.equal(this.text, that.getText())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.font, that.getFont())) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.getPaint())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.textAnchor, that.getTextAnchor())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rotationAnchor, 
                that.getRotationAnchor())) {
            return false;
        }
        if (this.rotationAngle != that.getRotationAngle()) {
            return false;
        }
        
        
        return true;
            
    }
    
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + this.font.hashCode();
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        result = 37 * result + this.rotationAnchor.hashCode();
        long temp = Double.doubleToLongBits(this.rotationAngle);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        result = 37 * result + this.text.hashCode();
        result = 37 * result + this.textAnchor.hashCode();
        return result;
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
