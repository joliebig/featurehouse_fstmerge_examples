
 
package org.jfree.chart.util;

import java.awt.GradientPaint;
import java.awt.Shape;


public interface GradientPaintTransformer {
    
    
    public GradientPaint transform(GradientPaint paint, Shape target);

}
