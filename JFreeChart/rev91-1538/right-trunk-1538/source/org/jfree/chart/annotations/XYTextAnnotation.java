

package org.jfree.chart.annotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;


public class XYTextAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -2946063342782506328L;

    
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN,
            10);

    
    public static final Paint DEFAULT_PAINT = Color.black;

    
    public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;

    
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;

    
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;

    
    private String text;

    
    private Font font;

    
    private transient Paint paint;

    
    private double x;

    
    private double y;

    
    private TextAnchor textAnchor;

    
    private TextAnchor rotationAnchor;

    
    private double rotationAngle;

    
    public XYTextAnnotation(String text, double x, double y) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.x = x;
        this.y = y;
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
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.rotationAnchor = anchor;
    }

    
    public double getRotationAngle() {
        return this.rotationAngle;
    }

    
    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;
    }

    
    public double getX() {
        return this.x;
    }

    
    public void setX(double x) {
        this.x = x;
    }

    
    public double getY() {
        return this.y;
    }

    
    public void setY(double y) {
        this.y = y;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        float anchorX = (float) domainAxis.valueToJava2D(
                this.x, dataArea, domainEdge);
        float anchorY = (float) rangeAxis.valueToJava2D(
                this.y, dataArea, rangeEdge);

        if (orientation == PlotOrientation.HORIZONTAL) {
            float tempAnchor = anchorX;
            anchorX = anchorY;
            anchorY = tempAnchor;
        }

        g2.setFont(getFont());
        g2.setPaint(getPaint());
        TextUtilities.drawRotatedString(getText(), g2, anchorX, anchorY,
                getTextAnchor(), getRotationAngle(), getRotationAnchor());
        Shape hotspot = TextUtilities.calculateRotatedStringBounds(
                getText(), g2, anchorX, anchorY, getTextAnchor(),
                getRotationAngle(), getRotationAnchor());

        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTextAnnotation)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYTextAnnotation that = (XYTextAnnotation) obj;
        if (!this.text.equals(that.text)) {
            return false;
        }
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!this.rotationAnchor.equals(that.rotationAnchor)) {
            return false;
        }
        if (this.rotationAngle != that.rotationAngle) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 193;
        result = 37 * this.text.hashCode();
        result = 37 * this.font.hashCode();
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        long temp = Double.doubleToLongBits(this.x);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        result = 37 * result + this.textAnchor.hashCode();
        result = 37 * result + this.rotationAnchor.hashCode();
        temp = Double.doubleToLongBits(this.rotationAngle);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
