

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;

import org.jfree.chart.util.RectangleEdge;


public interface XYBarPainter {

    
    public void paintBar(Graphics2D g2, XYBarRenderer renderer,
            int row, int column, boolean selected, RectangularShape bar,
            RectangleEdge base);

    
    public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer,
            int row, int column, boolean selected, RectangularShape bar,
            RectangleEdge base, boolean pegShadow);

}
