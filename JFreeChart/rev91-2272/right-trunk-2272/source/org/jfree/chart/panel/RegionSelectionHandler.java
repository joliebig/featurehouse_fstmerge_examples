

package org.jfree.chart.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.Selectable;
import org.jfree.chart.util.ShapeUtilities;


public class RegionSelectionHandler extends AbstractMouseHandler {

    
    private GeneralPath selection;

    
    private Point2D lastPoint;

    
    private Stroke outlineStroke;

    
    private Paint outlinePaint;

    
    private Paint fillPaint;

    
    public RegionSelectionHandler() {
        this(new BasicStroke(1.0f), Color.darkGray, new Color(255, 0, 255, 50));
    }

    
    public RegionSelectionHandler(Stroke outlineStroke, Paint outlinePaint,
            Paint fillPaint) {
        super();
        this.selection = new GeneralPath();
        this.lastPoint = null;
        this.outlineStroke = new BasicStroke(1.0f);
        this.outlinePaint = Color.darkGray;
        this.fillPaint = new Color(255, 0, 255, 50);
    }

    
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    
    public void setFillPaint(Paint paint) {
        this.fillPaint = paint;
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
    }

    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    
    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
    }

    
    public void mousePressed(MouseEvent e) {
        ChartPanel panel = (ChartPanel) e.getSource();
        JFreeChart chart = panel.getChart();
        if (chart == null) {
            return;
        }
        if (!(chart.getPlot() instanceof Selectable)) {
            return;
        }
        Selectable s = (Selectable) chart.getPlot();
        if (!s.canSelectByRegion()) {
            return;
        }
        Rectangle2D dataArea = panel.getScreenDataArea();
        if (dataArea.contains(e.getPoint())) {
            if (!e.isShiftDown()) {
                s.clearSelection();
                chart.setNotify(true);
            }
            Point pt = e.getPoint();
            this.selection.moveTo((float) pt.getX(), (float) pt.getY());
            this.lastPoint = new Point(pt);
        }
    }

    
    public void mouseDragged(MouseEvent e) {
        if (this.lastPoint == null) {
            return;  
        }
        ChartPanel panel = (ChartPanel) e.getSource();
        Point pt = e.getPoint();
        Point2D pt2 = ShapeUtilities.getPointInRectangle(pt.x, pt.y,
                panel.getScreenDataArea());
        if (pt2.distance(this.lastPoint) > 5) {
            this.selection.lineTo((float) pt2.getX(), (float) pt2.getY());
            this.lastPoint = pt2;
        }
        panel.setSelectionShape(selection);
        panel.setSelectionFillPaint(this.fillPaint);
        panel.setSelectionOutlinePaint(this.outlinePaint);
        panel.repaint();
    }

    
    public void mouseReleased(MouseEvent e) {
        if (this.lastPoint == null) {
            return;  
        }
        ChartPanel panel = (ChartPanel) e.getSource();
        this.selection.closePath();

        
        JFreeChart chart = panel.getChart();
        Plot plot = chart.getPlot();
        if (!(plot instanceof Selectable)) {
            return;
        }
        Selectable p = (Selectable) plot;
        if (p.canSelectByRegion()) {
            p.select(this.selection, panel.getScreenDataArea(), panel);
        }
        panel.setSelectionShape(null);
        this.selection.reset();
        this.lastPoint = null;
        panel.repaint();
        
    }

    
    public void mouseClicked(MouseEvent e) {
        System.out.println("mouseClicked(): " + e);
        ChartPanel panel = (ChartPanel) e.getSource();
        Rectangle2D dataArea = panel.getScreenDataArea();
        if (dataArea.contains(e.getPoint())) {
            JFreeChart chart = panel.getChart();
            if (chart.getPlot() instanceof Selectable) {
                Selectable s = (Selectable) chart.getPlot();
                if (s.canSelectByPoint()) {
                    Point pt = e.getPoint();
                    s.select(pt.getX(), pt.getY(), dataArea, panel);
                }
            }
        }
    }

}
