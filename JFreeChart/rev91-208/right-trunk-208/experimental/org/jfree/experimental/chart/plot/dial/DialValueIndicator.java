

package org.jfree.experimental.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleAnchor;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.Size2D;


public class DialValueIndicator extends AbstractDialLayer implements DialLayer, 
        Cloneable, PublicCloneable, Serializable {
    
    
    private int datasetIndex;
    
    
    private double angle;
    
    
    private double radius;
    
    
    private RectangleAnchor frameAnchor;
    
    
    private Number templateValue;
    
    
    private NumberFormat formatter;

    
    private Font font;
    
    
    private transient Paint paint;
    
    
    private transient Paint backgroundPaint;
    
    
    private transient Stroke outlineStroke;
    
    
    private transient Paint outlinePaint;
    
    
    private RectangleInsets insets;
    
    
    private RectangleAnchor valueAnchor;
    
    
    private TextAnchor textAnchor;
    
    
    public DialValueIndicator(int datasetIndex, String label) {
        this.datasetIndex = datasetIndex;
        this.angle = -90.0;
        this.radius = 0.3;
        this.frameAnchor = RectangleAnchor.CENTER;
        this.templateValue = new Double(100.0);
        this.formatter = new DecimalFormat("0.0");
        this.font = new Font("Dialog", Font.BOLD, 14);
        this.paint = Color.black;
        this.backgroundPaint = Color.white;
        this.outlineStroke = new BasicStroke(1.0f);
        this.outlinePaint = Color.blue;
        this.insets = new RectangleInsets(4, 4, 4, 4);
        this.valueAnchor = RectangleAnchor.RIGHT;
        this.textAnchor = TextAnchor.CENTER_RIGHT;
    }
    
    
    public int getDatasetIndex() {
        return this.datasetIndex;
    }
    
    
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
    }
    
    
    public double getAngle() {
        return this.angle;
    }
    
    
    public void setAngle(double angle) {
        this.angle = angle;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getRadius() {
        return this.radius;
    }
    
    
    public void setRadius(double radius) {
        
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public RectangleAnchor getFrameAnchor() {
        return this.frameAnchor;
    }
    
    
    public void setFrameAnchor(RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.frameAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Number getTemplateValue() {
        return this.templateValue;
    }
    
    
    public void setTemplateValue(Number value) {
        this.templateValue = value;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public NumberFormat getNumberFormat() {
        return this.formatter;
    }
    
    
    public void setNumberFormat(NumberFormat formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Font getFont() {
        return this.font;
    }
    
    
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Paint getPaint() {
        return this.paint;
    }
    
    
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }
    
    
    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.backgroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));      
    }
    
    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }
     
    
    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.outlineStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }
    
    
    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.outlinePaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public RectangleInsets getInsets() {
        return this.insets;
    }
    
    
    public void setInsets(RectangleInsets insets) {
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        this.insets = insets;
        notifyListeners(new DialLayerChangeEvent(this));        
    }
    
    
    public RectangleAnchor getValueAnchor() {
        return this.valueAnchor;
    }
    
    
    public void setValueAnchor(RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.valueAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));                
    }
    
    
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    
    public void setTextAnchor(TextAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.textAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));                        
    }
    
    
    public boolean isClippedToWindow() {
        return true;
    }
    
    
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view) {

        
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius, 
                this.radius);
        Arc2D arc = new Arc2D.Double(f, this.angle, 0.0, Arc2D.OPEN);
        Point2D pt = arc.getStartPoint();
        
        
        FontMetrics fm = g2.getFontMetrics(this.font);
        String s = this.formatter.format(this.templateValue);
        Rectangle2D tb = TextUtilities.getTextBounds(s, g2, fm);

        
        Rectangle2D bounds = RectangleAnchor.createRectangle(new Size2D(
                tb.getWidth(), tb.getHeight()), pt.getX(), pt.getY(), 
                this.frameAnchor);
        
        
        Rectangle2D fb = this.insets.createOutsetRectangle(bounds);

        
        g2.setPaint(this.backgroundPaint);
        g2.fill(fb);

        
        g2.setStroke(this.outlineStroke);
        g2.setPaint(this.outlinePaint);
        g2.draw(fb);
        
        
        
        double value = plot.getValue(this.datasetIndex);
        String valueStr = this.formatter.format(value);
        Point2D pt2 = RectangleAnchor.coordinates(bounds, this.valueAnchor);
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        TextUtilities.drawAlignedString(valueStr, g2, (float) pt2.getX(), 
                (float) pt2.getY(), this.textAnchor);
        
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialValueIndicator)) {
            return false;
        }
        DialValueIndicator that = (DialValueIndicator) obj;
        if (this.datasetIndex != that.datasetIndex) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        if (this.radius != that.radius) {
            return false;
        }
        if (!this.frameAnchor.equals(that.frameAnchor)) {
            return false;
        }
        if (!this.templateValue.equals(that.templateValue)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.insets.equals(that.insets)) {
            return false;
        }
        if (!this.valueAnchor.equals(that.valueAnchor)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        
        return true;
    }
    
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.backgroundPaint);
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.outlinePaint);
        result = 37 * result + this.outlineStroke.hashCode();
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
    }

}
