

package org.jfree.chart.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class LogFormat extends NumberFormat {
    
    
    private double base;
    
    
    private double baseLog;
    
    
    private String baseLabel;
    
    
    private boolean showBase;
    
    
    private NumberFormat formatter = new DecimalFormat("0.0");
    
    
    public LogFormat(double base, String baseLabel, boolean showBase) {
        this.base = base;
        this.baseLog = Math.log(this.base);
        this.baseLabel = baseLabel;
        this.showBase = showBase;
    }
    
    
    private double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }
    
    
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        StringBuffer result = new StringBuffer();
        if (this.showBase) {
            result.append(this.baseLabel);
            result.append("^");
        }
        result.append(this.formatter.format(calculateLog(number)));
        return result;
    }

    
    public StringBuffer format(long number, StringBuffer toAppendTo, 
            FieldPosition pos) {
        StringBuffer result = new StringBuffer();
        if (this.showBase) {
            result.append(this.baseLabel);
            result.append("^");
        }
        result.append(this.formatter.format(calculateLog(number)));
        return result;
    }

    
    public Number parse (String source, ParsePosition parsePosition) {
        return null; 
    }

}
