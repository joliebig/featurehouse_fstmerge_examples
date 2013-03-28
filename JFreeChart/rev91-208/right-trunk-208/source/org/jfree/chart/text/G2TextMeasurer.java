
 
package org.jfree.chart.text;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class G2TextMeasurer implements TextMeasurer {

    
    private Graphics2D g2;
    
    
    public G2TextMeasurer(Graphics2D g2) {
        this.g2 = g2;
    }

    
    public float getStringWidth(String text, int start, int end) {
        FontMetrics fm = this.g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text.substring(start, 
                end), this.g2, fm);
        float result = (float) bounds.getWidth();
        return result;
    }
    
}

