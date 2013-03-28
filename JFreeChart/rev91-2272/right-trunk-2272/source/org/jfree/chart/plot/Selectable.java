

package org.jfree.chart.plot;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.RenderingSource;


public interface Selectable {

    
    public boolean canSelectByPoint();

    
    public boolean canSelectByRegion();

    
    public void select(double x, double y, Rectangle2D dataArea,
            RenderingSource source);

    
    public void select(GeneralPath region, Rectangle2D dataArea,
            RenderingSource source);

    
    public void clearSelection();

}

