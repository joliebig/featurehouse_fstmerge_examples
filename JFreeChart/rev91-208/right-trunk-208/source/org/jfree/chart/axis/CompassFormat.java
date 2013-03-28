

package org.jfree.chart.axis;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class CompassFormat extends NumberFormat {
    
    
    private static final String N = "N";
    
    
    private static final String E = "E";
    
    
    private static final String S = "S";
    
    
    private static final String W = "W";
    
    
    public static final String[] DIRECTIONS = {
        N, N + N + E, N + E, E + N + E, E, E + S + E, S + E, S + S + E, S,
        S + S + W, S + W, W + S + W, W, W + N + W, N + W, N + N + W, N
    };
    
    
    public CompassFormat() {
        super();
    }
    
    
    public String getDirectionCode(double direction) {
        
        direction = direction % 360;
        if (direction < 0.0) {
            direction = direction + 360.0;
        }
        int index = ((int) Math.floor(direction / 11.25) + 1) / 2; 
        return DIRECTIONS[index];
        
    }

    
    public StringBuffer format(double number, StringBuffer toAppendTo, 
                               FieldPosition pos) {
        return toAppendTo.append(getDirectionCode(number));
    }

    
    public StringBuffer format(long number, StringBuffer toAppendTo, 
                               FieldPosition pos) {
        return toAppendTo.append(getDirectionCode(number));
    }

    
    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
    
}
