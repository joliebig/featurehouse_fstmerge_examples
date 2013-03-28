

package org.jfree.chart.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class HexNumberFormat extends NumberFormat {

    
    public static final int BYTE = 2;
    
    
    public static final int WORD = 4;

    
    public static final int DWORD = 8;

    
    public static final int QWORD = 16;

    
    private int m_numDigits = DWORD;

    
    public HexNumberFormat(){
        this(DWORD);
    }

    
    public HexNumberFormat(int digits) {
        super();
        this.m_numDigits = digits;
    }

    
    public final int getNumberOfDigits() {
        return this.m_numDigits;
    }

    
    public void setNumberOfDigits(int digits) {
        this.m_numDigits = digits;
    }

    
    public StringBuffer format(double number, StringBuffer toAppendTo,
            FieldPosition pos) {
        return format((long) number, toAppendTo, pos);
    }

    
    public StringBuffer format(long number, StringBuffer toAppendTo, 
            FieldPosition pos) {
        String l_hex = Long.toHexString(number).toUpperCase();

        int l_pad = this.m_numDigits - l_hex.length();
        l_pad = (0 < l_pad) ? l_pad : 0;

        StringBuffer l_extended = new StringBuffer("0x");
        for (int i = 0; i < l_pad; i++) {
            l_extended.append(0);
        }
        l_extended.append(l_hex);

        return l_extended;
    }

    
    public Number parse (String source, ParsePosition parsePosition) {
        return null; 
    }
    
}
