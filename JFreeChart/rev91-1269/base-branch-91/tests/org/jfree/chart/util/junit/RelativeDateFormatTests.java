

package org.jfree.chart.util.junit;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.util.RelativeDateFormat;



public class RelativeDateFormatTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(RelativeDateFormatTests.class);
    }

    
    public RelativeDateFormatTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        RelativeDateFormat df1 = new RelativeDateFormat();
        RelativeDateFormat df2 = new RelativeDateFormat();
        assertEquals(df1, df2);
        
        df1.setBaseMillis(123L);
        assertFalse(df1.equals(df2));
        df2.setBaseMillis(123L);
        assertTrue(df1.equals(df2));
        
        df1.setDaySuffix("D");
        assertFalse(df1.equals(df2));
        df2.setDaySuffix("D");
        assertTrue(df1.equals(df2));
        
        df1.setHourSuffix("H");
        assertFalse(df1.equals(df2));
        df2.setHourSuffix("H");
        assertTrue(df1.equals(df2));
        
        df1.setMinuteSuffix("M");
        assertFalse(df1.equals(df2));
        df2.setMinuteSuffix("M");
        assertTrue(df1.equals(df2));
        
        df1.setSecondSuffix("S");
        assertFalse(df1.equals(df2));
        df2.setSecondSuffix("S");
        assertTrue(df1.equals(df2));
        
        df1.setShowZeroDays(!df1.getShowZeroDays()); 
        assertFalse(df1.equals(df2));
        df2.setShowZeroDays(!df2.getShowZeroDays()); 
        assertTrue(df1.equals(df2));
        
        df1.setSecondFormatter(new DecimalFormat("0.0"));
        assertFalse(df1.equals(df2));
        df2.setSecondFormatter(new DecimalFormat("0.0"));
        assertTrue(df1.equals(df2));
    }
    
    
    public void testHashCode() {
        RelativeDateFormat df1 = new RelativeDateFormat(123L);
        RelativeDateFormat df2 = new RelativeDateFormat(123L);
        assertTrue(df1.equals(df2));
        int h1 = df1.hashCode();
        int h2 = df2.hashCode();
        assertEquals(h1, h2);
    }    
    
    
    public void testCloning() {
        NumberFormat nf = new DecimalFormat("0");
        RelativeDateFormat df1 = new RelativeDateFormat();
        df1.setSecondFormatter(nf);
        RelativeDateFormat df2 = null;
        df2 = (RelativeDateFormat) df1.clone();
        assertTrue(df1 != df2);
        assertTrue(df1.getClass() == df2.getClass());
        assertTrue(df1.equals(df2));
    
        
        nf.setMinimumFractionDigits(2);
        assertFalse(df1.equals(df2));
    }
    
    
}

