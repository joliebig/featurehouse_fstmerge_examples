

package org.jfree.chart.block;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;


public class LineBorder implements BlockFrame, Serializable {

    
    private transient Paint paint;
    
    
    private transient Stroke stroke;
    
    
    private RectangleInsets insets;
    
    
    public LineBorder() {
        this(Color.black, new BasicStroke(1.0f), new RectangleInsets(1.0, 1.0, 
                1.0, 1.0));
    }
    
        
    public LineBorder(Paint paint, Stroke stroke, RectangleInsets insets) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        if (insets == null) {
            throw new IllegalArgumentException("Null 'insets' argument.");
        }
        this.paint = paint;
        this.stroke = stroke;
        this.insets = insets;
    }  
    
    
    public Paint getPaint() {
        return this.paint;
    }
    
    
    public RectangleInsets getInsets() {
        return this.insets;
    }

    
    public Stroke getStroke() {
        return this.stroke;
    }
    
    
    public void draw(Graphics2D g2, Rectangle2D area) {
        double w = area.getWidth();
        double h = area.getHeight();
        
        if (w <= 0.0 || h <= 0.0) {
            return;
        }
        double t = this.insets.calculateTopInset(h);
        double b = this.insets.calculateBottomInset(h);
        double l = this.insets.calculateLeftInset(w);
        double r = this.insets.calculateRightInset(w);
        double x = area.getX();
        double y = area.getY();
        double x0 = x + l / 2.0;
        double x1 = x + w - r / 2.0;
        double y0 = y + h - b / 2.0;
        double y1 = y + t / 2.0;
        g2.setPaint(getPaint());
        g2.setStroke(getStroke());
        Line2D line = new Line2D.Double();
        if (t > 0.0) {
            line.setLine(x0, y1, x1, y1);
            g2.draw(line);
        }
        if (b > 0.0) {
            line.setLine(x0, y0, x1, y0);
            g2.draw(line);
        }
        if (l > 0.0) {
            line.setLine(x0, y0, x0, y1);
            g2.draw(line);
        }
        if (r > 0.0) {
            line.setLine(x1, y0, x1, y1);
            g2.draw(line);
        }        
    }    

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof LineBorder)) {
            return false;   
        }
        LineBorder that = (LineBorder) obj;
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)){
            return false;
        }
        if (!this.insets.equals(that.insets)) {
            return false;
        }
        return true;
    }    

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
    }    
}

                 
