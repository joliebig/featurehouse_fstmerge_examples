

package org.jfree.chart.util;

import java.awt.image.BufferedImage;


public interface ShadowGenerator {

    
    public BufferedImage createDropShadow(BufferedImage source);

    
    public int calculateOffsetX();

    
    public int calculateOffsetY();

}
