

package org.jfree.chart.axis;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.RectangleEdge;


public class MarkerAxisBand implements Serializable {

    
    private static final long serialVersionUID = -1729482413886398919L;

    
    private NumberAxis axis;

    
    private double topOuterGap;

    
    private double topInnerGap;

    
    private double bottomOuterGap;

    
    private double bottomInnerGap;

    
    private Font font;

    
    private List markers;

    
    public MarkerAxisBand(NumberAxis axis,
                          double topOuterGap, double topInnerGap,
                          double bottomOuterGap, double bottomInnerGap,
                          Font font) {
        this.axis = axis;
        this.topOuterGap = topOuterGap;
        this.topInnerGap = topInnerGap;
        this.bottomOuterGap = bottomOuterGap;
        this.bottomInnerGap = bottomInnerGap;
        this.font = font;
        this.markers = new java.util.ArrayList();
    }

    
    public void addMarker(IntervalMarker marker) {
        this.markers.add(marker);
    }

    
    public double getHeight(Graphics2D g2) {

        double result = 0.0;
        if (this.markers.size() > 0) {
            LineMetrics metrics = this.font.getLineMetrics(
                "123g", g2.getFontRenderContext()
            );
            result = this.topOuterGap + this.topInnerGap + metrics.getHeight()
                     + this.bottomInnerGap + this.bottomOuterGap;
        }
        return result;

    }

    
    private void drawStringInRect(Graphics2D g2, Rectangle2D bounds, Font font,
                                  String text) {

        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        Rectangle2D r = TextUtilities.getTextBounds(text, g2, fm);
        double x = bounds.getX();
        if (r.getWidth() < bounds.getWidth()) {
            x = x + (bounds.getWidth() - r.getWidth()) / 2;
        }
        LineMetrics metrics = font.getLineMetrics(
            text, g2.getFontRenderContext()
        );
        g2.drawString(
            text, (float) x, (float) (bounds.getMaxY()
                - this.bottomInnerGap - metrics.getDescent())
        );
    }

    
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                     double x, double y) {

        double h = getHeight(g2);
        Iterator iterator = this.markers.iterator();
        while (iterator.hasNext()) {
            IntervalMarker marker = (IntervalMarker) iterator.next();
            double start =  Math.max(
                marker.getStartValue(), this.axis.getRange().getLowerBound()
            );
            double end = Math.min(
                marker.getEndValue(), this.axis.getRange().getUpperBound()
            );
            double s = this.axis.valueToJava2D(
                start, dataArea, RectangleEdge.BOTTOM
            );
            double e = this.axis.valueToJava2D(
                end, dataArea, RectangleEdge.BOTTOM
            );
            Rectangle2D r = new Rectangle2D.Double(
                s, y + this.topOuterGap, e - s,
                h - this.topOuterGap - this.bottomOuterGap
            );

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, marker.getAlpha())
            );
            g2.setPaint(marker.getPaint());
            g2.fill(r);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(r);
            g2.setComposite(originalComposite);

            g2.setPaint(Color.black);
            drawStringInRect(g2, r, this.font, marker.getLabel());
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MarkerAxisBand)) {
            return false;
        }
        MarkerAxisBand that = (MarkerAxisBand) obj;
        if (this.topOuterGap != that.topOuterGap) {
            return false;
        }
        if (this.topInnerGap != that.topInnerGap) {
            return false;
        }
        if (this.bottomInnerGap != that.bottomInnerGap) {
            return false;
        }
        if (this.bottomOuterGap != that.bottomOuterGap) {
            return false;
        }
        if (!ObjectUtilities.equal(this.font, that.font)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.markers, that.markers)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 37;
        result = 19 * result + this.font.hashCode();
        result = 19 * result + this.markers.hashCode();
        return result;
    }

}
