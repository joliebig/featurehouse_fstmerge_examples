

package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.Serializable;

import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;


public class StandardBarPainter implements BarPainter, Serializable {

	
	public StandardBarPainter() {
	}

	
	public void paintBar(Graphics2D g2, BarRenderer renderer, int row,
			int column, RectangularShape bar, RectangleEdge base) {

        Paint itemPaint = renderer.getItemPaint(row, column);
        GradientPaintTransformer t = renderer.getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);

        
        if (renderer.isDrawBarOutline()) {
               
            Stroke stroke = renderer.getItemOutlineStroke(row, column);
            Paint paint = renderer.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

	}

	
	public void paintBarShadow(Graphics2D g2, BarRenderer renderer, int row,
			int column, RectangularShape bar, RectangleEdge base,
			boolean pegShadow) {

		
		
        Paint itemPaint = renderer.getItemPaint(row, column);
        if (itemPaint instanceof Color) {
        	Color c = (Color) itemPaint;
        	if (c.getAlpha() == 0) {
        		return;
        	}
        }

        RectangularShape shadow = createShadow(bar, renderer.getShadowXOffset(),
        		renderer.getShadowYOffset(), base, pegShadow);
        g2.setPaint(Color.gray);
        g2.fill(shadow);

	}

	
	private Rectangle2D createShadow(RectangularShape bar, double xOffset,
			double yOffset, RectangleEdge base, boolean pegShadow) {
		double x0 = bar.getMinX();
		double x1 = bar.getMaxX();
		double y0 = bar.getMinY();
		double y1 = bar.getMaxY();
		if (base == RectangleEdge.TOP) {
		    x0 += xOffset;
		    x1 += xOffset;
		    if (!pegShadow) {
		    	y0 += yOffset;
		    }
		    y1 += yOffset;
		}
		else if (base == RectangleEdge.BOTTOM) {
		    x0 += xOffset;
		    x1 += xOffset;
		    y0 += yOffset;
		    if (!pegShadow) {
		    	y1 += yOffset;
		    }
		}
		else if (base == RectangleEdge.LEFT) {
			if (!pegShadow) {
				x0 += xOffset;
			}
			x1 += xOffset;
			y0 += yOffset;
			y1 += yOffset;
		}
		else if (base == RectangleEdge.RIGHT) {
			x0 += xOffset;
			if (!pegShadow) {
				x1 += xOffset;
			}
			y0 += yOffset;
			y1 += yOffset;
		}
		return new Rectangle2D.Double(x0, y0, (x1 - x0), (y1 - y0));
	}

	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StandardBarPainter)) {
			return false;
		}
		return true;
	}

	
	public int hashCode() {
        int hash = 37;
		
		return hash;
	}

}
