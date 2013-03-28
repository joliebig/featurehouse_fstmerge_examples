

package org.jfree.experimental.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.util.PublicCloneable;


public abstract class DialPointer extends AbstractDialLayer 
        implements DialLayer, Cloneable, Serializable {
    
    
    double radius;
    
    
    int datasetIndex;
    
    
    public DialPointer() {
        this(0);
    }
    
    
    public DialPointer(int datasetIndex) {
        this.radius = 0.675;
        this.datasetIndex = datasetIndex;
    }
    
    
    public int getDatasetIndex() {
        return this.datasetIndex;
    }
    
    
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getRadius() {
        return this.radius;
    }
    
    
    public void setRadius(double radius) {
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public boolean isClippedToWindow() {
        return true;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    public static class Pin extends DialPointer implements PublicCloneable {
    
        
        private transient Paint paint;
    
        
        private transient Stroke stroke;
        
        
        public Pin() {
            this(0);
        }
        
        
        public Pin(int datasetIndex) {
            super(datasetIndex);
            this.paint = Color.red;
            this.stroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, 
                    BasicStroke.JOIN_BEVEL);
        }
        
        
        public Paint getPaint() {
            return this.paint;
        }
        
        
        public void setPaint(Paint paint) {
            this.paint = paint;
            notifyListeners(new DialLayerChangeEvent(this));
        }
        
        
        public Stroke getStroke() {
            return this.stroke;
        }
        
        
        public void setStroke(Stroke stroke) {
            this.stroke = stroke;
            notifyListeners(new DialLayerChangeEvent(this));
        }
        
        
        public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view) {
        
            g2.setPaint(this.paint);
            g2.setStroke(this.stroke);
            Rectangle2D arcRect = DialPlot.rectangleByRadius(frame, 
                    this.radius, this.radius);

            double value = plot.getValue(this.datasetIndex);
            DialScale scale = plot.getScaleForDataset(this.datasetIndex);
            double angle = scale.valueToAngle(value);
        
            Arc2D arc = new Arc2D.Double(arcRect, angle, 0, Arc2D.OPEN);
            Point2D pt = arc.getEndPoint();
        
            Line2D line = new Line2D.Double(frame.getCenterX(), 
                    frame.getCenterY(), pt.getX(), pt.getY());
            g2.draw(line);
        }
        
    }
    
    
    public static class Pointer extends DialPointer implements PublicCloneable {
        
        
        private double widthRadius;
    
        
        public Pointer() {
            this(0);
        }
        
        
        public Pointer(int datasetIndex) {
            super(datasetIndex);
            this.radius = 0.9;
            this.widthRadius = 0.05;
        }
        
        
        public double getWidthRadius() {
            return this.widthRadius;
        }
        
        
        public void setWidthRadius(double radius) {
            this.widthRadius = radius;
            notifyListeners(new DialLayerChangeEvent(this));
        }
        
        
        public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
                Rectangle2D view) {
        
            g2.setPaint(Color.blue);
            g2.setStroke(new BasicStroke(1.0f));
            Rectangle2D lengthRect = DialPlot.rectangleByRadius(frame, 
                    this.radius, this.radius);
            Rectangle2D widthRect = DialPlot.rectangleByRadius(frame, 
                    this.widthRadius, this.widthRadius);
            double value = plot.getValue(this.datasetIndex);
            DialScale scale = plot.getScaleForDataset(this.datasetIndex);
            double angle = scale.valueToAngle(value);
        
            Arc2D arc1 = new Arc2D.Double(lengthRect, angle, 0, Arc2D.OPEN);
            Point2D pt1 = arc1.getEndPoint();
            Arc2D arc2 = new Arc2D.Double(widthRect, angle - 90.0, 180.0, 
                    Arc2D.OPEN);
            Point2D pt2 = arc2.getStartPoint();
            Point2D pt3 = arc2.getEndPoint();
            Arc2D arc3 = new Arc2D.Double(widthRect, angle - 180.0, 0.0, 
                    Arc2D.OPEN);
            Point2D pt4 = arc3.getStartPoint();
        
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) pt1.getX(), (float) pt1.getY());
            gp.lineTo((float) pt2.getX(), (float) pt2.getY());
            gp.lineTo((float) pt4.getX(), (float) pt4.getY());
            gp.lineTo((float) pt3.getX(), (float) pt3.getY());
            gp.closePath();
            g2.setPaint(Color.gray);
            g2.fill(gp);
        
            g2.setPaint(Color.black);
            Line2D line = new Line2D.Double(frame.getCenterX(), 
                    frame.getCenterY(), pt1.getX(), pt1.getY());
            g2.draw(line);
        
            line.setLine(pt2, pt3);
            g2.draw(line);
        
            line.setLine(pt3, pt1);
            g2.draw(line);
        
            line.setLine(pt2, pt1);
            g2.draw(line);
        
            line.setLine(pt2, pt4);
            g2.draw(line);

            line.setLine(pt3, pt4);
            g2.draw(line);
        }
        
    }

}
