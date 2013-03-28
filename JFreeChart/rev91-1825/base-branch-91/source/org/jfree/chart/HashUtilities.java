

package org.jfree.chart;

import java.awt.GradientPaint;
import java.awt.Paint;


public class HashUtilities {
    
    
    public static int hashCodeForPaint(Paint p) {
        if (p == null) 
            return 0;
        int result = 0;
        
        if (p instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) p;
            result = 193;
            result = 37 * result + gp.getColor1().hashCode();
            result = 37 * result + gp.getPoint1().hashCode();
            result = 37 * result + gp.getColor2().hashCode();
            result = 37 * result + gp.getPoint2().hashCode();
        }
        else {
            
            
            
            result = p.hashCode();
        }
        return result;
    }
    
    
    public static int hashCodeForDoubleArray(double[] a) {
        if (a == null) { 
            return 0;
        }
        int result = 193;
        long temp;
        for (int i = 0; i < a.length; i++) {
            temp = Double.doubleToLongBits(a[i]);
            result = 29 * result + (int) (temp ^ (temp >>> 32));
        }
        return result;
    }

}
