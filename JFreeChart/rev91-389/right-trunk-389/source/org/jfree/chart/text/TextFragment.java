
 
package org.jfree.chart.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.Size2D;


public class TextFragment implements Serializable {

    
    private static final long serialVersionUID = 4465945952903143262L;
    
    
    public static final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 12);
    
    
    public static final Paint DEFAULT_PAINT = Color.black;
    
    
    private String text;
    
    
    private Font font;
    
    
    private transient Paint paint;
    
    
    private float baselineOffset;
    
    
    public TextFragment(String text) {
        this(text, DEFAULT_FONT, DEFAULT_PAINT);
    }
    
    
    public TextFragment(String text, Font font) {
        this(text, font, DEFAULT_PAINT);
    }

    
    public TextFragment(String text, Font font, Paint paint) {
        this(text, font, paint, 0.0f);
    }

    
    public TextFragment(String text, Font font, Paint paint,
                        float baselineOffset) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");  
        }
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.text = text;
        this.font = font;
        this.paint = paint;
        this.baselineOffset = baselineOffset;
    }

    
    public String getText() {
        return this.text;
    }
    
    
    public Font getFont() {
        return this.font;
    }
    
    
    public Paint getPaint() {
        return this.paint;
    }
    
    
    public float getBaselineOffset() {
        return this.baselineOffset;   
    }
    
    
    public void draw(Graphics2D g2, float anchorX, 
                     float anchorY, TextAnchor anchor,
                     float rotateX, float rotateY, 
                     double angle) {
    
        g2.setFont(this.font);
        g2.setPaint(this.paint);
        TextUtilities.drawRotatedString(this.text, g2, anchorX, anchorY 
                + this.baselineOffset, anchor, angle, rotateX, rotateY);
    
    }
    
    
    public Size2D calculateDimensions(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics(this.font);
        Rectangle2D bounds = TextUtilities.getTextBounds(this.text, g2, fm);
        Size2D result = new Size2D(bounds.getWidth(), bounds.getHeight());
        return result;
    }
    
    
    public float calculateBaselineOffset(Graphics2D g2, TextAnchor anchor) {
        float result = 0.0f;
        FontMetrics fm = g2.getFontMetrics(this.font);
        LineMetrics lm = fm.getLineMetrics("ABCxyz", g2);
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER
                                          || anchor == TextAnchor.TOP_RIGHT) {
            result = lm.getAscent();
        }
        else if (anchor == TextAnchor.BOTTOM_LEFT 
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {
            result = -lm.getDescent() - lm.getLeading();
        }
        return result;                                             
    }
    
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;   
        }
        if (obj == this) {
            return true;   
        }
        if (obj instanceof TextFragment) {
            TextFragment tf = (TextFragment) obj;
            if (!this.text.equals(tf.text)) {
                return false;   
            }
            if (!this.font.equals(tf.font)) {
                return false;   
            }
            if (!this.paint.equals(tf.paint)) {
                return false;   
            }
            return true;
        }
        return false;
    }

    
    public int hashCode() {
        int result;
        result = (this.text != null ? this.text.hashCode() : 0);
        result = 29 * result + (this.font != null ? this.font.hashCode() : 0);
        result = 29 * result + (this.paint != null ? this.paint.hashCode() : 0);
        return result;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    
    private void readObject(ObjectInputStream stream) throws IOException, 
            ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }
   
}
