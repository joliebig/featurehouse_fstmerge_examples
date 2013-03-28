

package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;


public class LineRenderer3D extends LineAndShapeRenderer 
                            implements Effect3D, Serializable {
   
    
    private static final long serialVersionUID = 5467931468380928736L;
    
    
    public static final double DEFAULT_X_OFFSET = 12.0;

    
    public static final double DEFAULT_Y_OFFSET = 8.0;
   
    
    public static final Paint DEFAULT_WALL_PAINT = new Color(0xDD, 0xDD, 0xDD);
   
    
    private double xOffset;

    
    private double yOffset;
   
    
    private transient Paint wallPaint;
   
    
    public LineRenderer3D() {
        super(true, false);  
        this.xOffset = DEFAULT_X_OFFSET;
        this.yOffset = DEFAULT_Y_OFFSET;
        this.wallPaint = DEFAULT_WALL_PAINT;
    }
   
    
    public double getXOffset() {
        return this.xOffset;
    }

    
    public double getYOffset() {
        return this.yOffset;
    }
   
    
    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public Paint getWallPaint() {
        return this.wallPaint;
    }

    
    public void setWallPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.wallPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }
   
    
    public void drawBackground(Graphics2D g2, CategoryPlot plot, 
                               Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                plot.getBackgroundAlpha()));

        
        Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }

        GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y2);
        leftWall.lineTo(x1, y3);
        leftWall.lineTo(x1, y1);
        leftWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(leftWall);

        GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x1, y1);
        bottomWall.lineTo(x3, y1);
        bottomWall.lineTo(x2, y0);
        bottomWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(bottomWall);

        
        g2.setPaint(Color.lightGray);
        Line2D corner = new Line2D.Double(x0, y0, x1, y1);
        g2.draw(corner);
        corner.setLine(x1, y1, x1, y3);
        g2.draw(corner);
        corner.setLine(x1, y1, x3, y1);
        g2.draw(corner);

        
        Image backgroundImage = plot.getBackgroundImage();
        if (backgroundImage != null) {
            Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX() 
                    + getXOffset(), dataArea.getY(), 
                    dataArea.getWidth() - getXOffset(), 
                    dataArea.getHeight() - getYOffset());
            plot.drawBackgroundImage(g2, adjusted);
        }
        
        g2.setComposite(originalComposite);

    }

    
    public void drawOutline(Graphics2D g2, CategoryPlot plot, 
                            Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        
        Stroke outlineStroke = plot.getOutlineStroke();
        Paint outlinePaint = plot.getOutlinePaint();
        if ((outlineStroke != null) && (outlinePaint != null)) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }

    }

    
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line1 = null;
        Line2D line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double y0 = value;
            double y1 = value - getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            double x0 = value;
            double x1 = value + getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        }
        g2.setPaint(plot.getDomainGridlinePaint());
        g2.setStroke(plot.getDomainGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);

    }

    
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                dataArea.getY() + getYOffset(),
                dataArea.getWidth() - getXOffset(),
                dataArea.getHeight() - getYOffset());

        Line2D line1 = null;
        Line2D line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double x0 = axis.valueToJava2D(value, adjusted, 
                    plot.getRangeAxisEdge());
            double x1 = x0 + getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            double y0 = axis.valueToJava2D(value, adjusted,
                    plot.getRangeAxisEdge());
            double y1 = y0 - getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        g2.setPaint(plot.getRangeGridlinePaint());
        g2.setStroke(plot.getRangeGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);

    }

    
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }

            Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), 
                    dataArea.getY() + getYOffset(), 
                    dataArea.getWidth() - getXOffset(), 
                    dataArea.getHeight() - getYOffset());

            GeneralPath path = null;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                float x = (float) axis.valueToJava2D(value, adjusted, 
                        plot.getRangeAxisEdge());
                float y = (float) adjusted.getMaxY();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo((float) (x + getXOffset()), 
                        y - (float) getYOffset());
                path.lineTo((float) (x + getXOffset()), 
                        (float) (adjusted.getMinY() - getYOffset()));
                path.lineTo(x, (float) adjusted.getMinY());
                path.closePath();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                float y = (float) axis.valueToJava2D(value, adjusted, 
                        plot.getRangeAxisEdge());
                float x = (float) dataArea.getX();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo(x + (float) this.xOffset, y - (float) this.yOffset);
                path.lineTo((float) (adjusted.getMaxX() + this.xOffset), 
                        y - (float) this.yOffset);
                path.lineTo((float) (adjusted.getMaxX()), y);
                path.closePath();
            }
            g2.setPaint(marker.getPaint());
            g2.fill(path);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(path);
        }
    }
   
   
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        if (!getItemVisible(row, column)) {
            return;   
        }
        
        
        Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }
       
        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                dataArea.getY() + getYOffset(), 
                dataArea.getWidth() - getXOffset(),
                dataArea.getHeight() - getYOffset());
       
        PlotOrientation orientation = plot.getOrientation();

        
        double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                adjusted, plot.getDomainAxisEdge());
        double value = v.doubleValue();
        double y1 = rangeAxis.valueToJava2D(value, adjusted, 
                plot.getRangeAxisEdge());

        Shape shape = getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
       
        if (getItemLineVisible(row, column)) {
            if (column != 0) {

                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {

                    
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(column - 1, 
                            getColumnCount(), adjusted, 
                            plot.getDomainAxisEdge());
                    double y0 = rangeAxis.valueToJava2D(previous, adjusted, 
                            plot.getRangeAxisEdge());

                    double x2 = x0 + getXOffset();
                    double y2 = y0 - getYOffset();
                    double x3 = x1 + getXOffset();
                    double y3 = y1 - getYOffset();
                   
                    GeneralPath clip = new GeneralPath();
                   
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        clip.moveTo((float) y0, (float) x0);
                        clip.lineTo((float) y1, (float) x1);
                        clip.lineTo((float) y3, (float) x3);
                        clip.lineTo((float) y2, (float) x2);
                        clip.lineTo((float) y0, (float) x0);
                        clip.closePath();
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        clip.moveTo((float) x0, (float) y0);
                        clip.lineTo((float) x1, (float) y1);
                        clip.lineTo((float) x3, (float) y3);
                        clip.lineTo((float) x2, (float) y2);
                        clip.lineTo((float) x0, (float) y0);
                        clip.closePath();
                    }
                   
                    g2.setPaint(getItemPaint(row, column));
                    g2.fill(clip);
                    g2.setStroke(getItemOutlineStroke(row, column));
                    g2.setPaint(getItemOutlinePaint(row, column));
                    g2.draw(clip);
                }
            }
        }

        
        if (isItemLabelVisible(row, column)) {
            drawItemLabel(g2, orientation, dataset, row, column, x1, y1, 
                    (value < 0.0));
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, shape);
        }

    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LineRenderer3D)) {
            return false;
        }
        LineRenderer3D that = (LineRenderer3D) obj;
        if (this.xOffset != that.xOffset) {
            return false;
        }
        if (this.yOffset != that.yOffset) {
            return false;
        }
        if (!PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.wallPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.wallPaint = SerialUtilities.readPaint(stream);
    }

}
