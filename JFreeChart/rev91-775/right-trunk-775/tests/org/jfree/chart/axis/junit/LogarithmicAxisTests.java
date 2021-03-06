

package org.jfree.chart.axis.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.util.RectangleEdge;


public class LogarithmicAxisTests extends TestCase {

    static class MyLogarithmicAxis extends LogarithmicAxis {

        
        public MyLogarithmicAxis(String label) {
            super(label);
        }
        
        
        protected double switchedLog10(double val) {
            return super.switchedLog10(val);
        }
        
    }
    
    
    public static double EPSILON = 0.000001;
    
    MyLogarithmicAxis axis = null;

    
    public static Test suite() {
        return new TestSuite(LogarithmicAxisTests.class);
    }

    
    public LogarithmicAxisTests(String name) {
        super(name);
    }

    
    protected void setUp() throws Exception {
        this.axis = new MyLogarithmicAxis("Value (log)");
        this.axis.setAllowNegativesFlag(false);
        this.axis.setLog10TickLabelsFlag(false);
        this.axis.setLowerMargin(0.0);
        this.axis.setUpperMargin(0.0);

        this.axis.setLowerBound(0.2);
        this.axis.setUpperBound(100.0);
    }

    
    public void testSerialization() {

        LogarithmicAxis a1 = new LogarithmicAxis("Test Axis");
        LogarithmicAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            a2 = (LogarithmicAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);

    }
        
    
     public void testAdjustedLog10() {
         checkLogPowRoundTrip(20);
         checkLogPowRoundTrip(10);
         checkLogPowRoundTrip(5);
         checkLogPowRoundTrip(2);
         checkLogPowRoundTrip(1);
         checkLogPowRoundTrip(0.5);
         checkLogPowRoundTrip(0.2);
         checkLogPowRoundTrip(0.0001);
     }

     private void checkLogPowRoundTrip(double value) {
         assertEquals("log(pow(x)) = x", value, this.axis.adjustedLog10(
                 this.axis.adjustedPow10(value)), EPSILON);
         assertEquals("pow(log(x)) = x", value, this.axis.adjustedPow10(
                 this.axis.adjustedLog10(value)), EPSILON);
     }

     
      public void testSwitchedLog10() {
          assertFalse("Axis should not allow negative values",
                  this.axis.getAllowNegativesFlag());
                
          assertEquals(Math.log(0.5) / LogarithmicAxis.LOG10_VALUE,
                  this.axis.switchedLog10(0.5), EPSILON);

          checkSwitchedLogPowRoundTrip(20);
          checkSwitchedLogPowRoundTrip(10);
          checkSwitchedLogPowRoundTrip(5);
          checkSwitchedLogPowRoundTrip(2);
          checkSwitchedLogPowRoundTrip(1);
          checkSwitchedLogPowRoundTrip(0.5);
          checkSwitchedLogPowRoundTrip(0.2);
          checkSwitchedLogPowRoundTrip(0.0001);
      }

      private void checkSwitchedLogPowRoundTrip(double value) {
          assertEquals("log(pow(x)) = x", value, this.axis.switchedLog10(
                  this.axis.switchedPow10(value)), EPSILON);
          assertEquals("pow(log(x)) = x", value, this.axis.switchedPow10(
                  this.axis.switchedLog10(value)), EPSILON);
      }

      
      public void testJava2DToValue() {
          Rectangle2D plotArea = new Rectangle2D.Double(22, 33, 500, 500);
          RectangleEdge edge = RectangleEdge.BOTTOM;

          
          this.axis.setRange(10, 20);
          checkPointsToValue(edge, plotArea);

          
          this.axis.setRange(0.5, 10);
          checkPointsToValue(edge, plotArea);

          
          this.axis.setRange(0.2, 20);
          checkPointsToValue(edge, plotArea);

          
          this.axis.setRange(0.2, 0.7);
          checkPointsToValue(edge, plotArea);
      }

      
      public void testValueToJava2D() {
          Rectangle2D plotArea = new Rectangle2D.Double(22, 33, 500, 500);
          RectangleEdge edge = RectangleEdge.BOTTOM;

          
          this.axis.setRange(10, 20);
          checkPointsToJava2D(edge, plotArea);

          
          this.axis.setRange(0.5, 10);
          checkPointsToJava2D(edge, plotArea);

          
          this.axis.setRange(0.2, 20);
          checkPointsToJava2D(edge, plotArea);

          
          this.axis.setRange(0.2, 0.7);
          checkPointsToJava2D(edge, plotArea);
      }

      private void checkPointsToJava2D(RectangleEdge edge, 
              Rectangle2D plotArea) {
          assertEquals("Left most point on the axis should be beginning of "
                  + "range.", plotArea.getX(), this.axis.valueToJava2D(
                  this.axis.getLowerBound(), plotArea, edge), EPSILON);
          assertEquals("Right most point on the axis should be end of range.", 
                  plotArea.getX() + plotArea.getWidth(), 
                  this.axis.valueToJava2D(this.axis.getUpperBound(), 
                  plotArea, edge), EPSILON);
          assertEquals("Center point on the axis should geometric mean of the bounds.", 
                  plotArea.getX() + (plotArea.getWidth() / 2), 
                  this.axis.valueToJava2D(Math.sqrt(this.axis.getLowerBound() 
                  * this.axis.getUpperBound()), plotArea, edge), EPSILON);
        }

    
     private void checkPointsToValue(RectangleEdge edge, Rectangle2D plotArea) {
         assertEquals("Right most point on the axis should be end of range.",
                 this.axis.getUpperBound(), this.axis.java2DToValue(
                 plotArea.getX() + plotArea.getWidth(), plotArea, edge), 
                 EPSILON);

         assertEquals("Left most point on the axis should be beginning of "
                 + "range.", this.axis.getLowerBound(), 
                 this.axis.java2DToValue(plotArea.getX(), plotArea, edge), 
                 EPSILON);

         assertEquals("Center point on the axis should geometric mean of the "
                 + "bounds.", Math.sqrt(this.axis.getUpperBound() 
                 * this.axis.getLowerBound()), this.axis.java2DToValue(
                 plotArea.getX() + (plotArea.getWidth() / 2), plotArea, edge), 
                 EPSILON);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LogarithmicAxisTests.class);
    }

}
