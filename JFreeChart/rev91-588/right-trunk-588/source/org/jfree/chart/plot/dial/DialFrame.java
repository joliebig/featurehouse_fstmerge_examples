

package org.jfree.chart.plot.dial;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public interface DialFrame extends DialLayer {
    
    
    public Shape getWindow(Rectangle2D frame);

}
