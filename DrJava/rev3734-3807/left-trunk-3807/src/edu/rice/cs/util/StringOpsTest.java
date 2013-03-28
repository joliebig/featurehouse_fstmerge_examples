

package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.PrintWriter;


public class StringOpsTest extends DrJavaTestCase {
  
  public void testReplace() {
    String test = "aabbccdd";
    assertEquals("testReplace:", "aab12cdd", StringOps.replace(test, "bc", "12"));
    test = "cabcabc";
    assertEquals("testReplace:", "cabc", StringOps.replace(test, "cabc", "c"));
  }

  
  public void testGetOffsetAndLength() {
    String test = "123456789\n123456789\n123456789\n";

    
    
    Pair<Integer,Integer> oAndL = StringOps.getOffsetAndLength(test, 1, 1, 1, 9);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(0), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(9), oAndL.getSecond());

    oAndL = StringOps.getOffsetAndLength(test, 1, 1, 2, 3);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(0), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(13), oAndL.getSecond());

    oAndL = StringOps.getOffsetAndLength(test, 1, 5, 2, 3);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(4), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(9), oAndL.getSecond());

    oAndL = StringOps.getOffsetAndLength(test, 1, 1, 1, 1);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(0), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(1), oAndL.getSecond());

    oAndL = StringOps.getOffsetAndLength(test, 3, 5, 3, 5);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(24), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(1), oAndL.getSecond());

    oAndL = StringOps.getOffsetAndLength(test, 2, 3, 3, 6);
    assertEquals("testGetOffsetAndLength- offSet:", new Integer(12), oAndL.getFirst());
    assertEquals("testGetOffsetAndLength- length:", new Integer(14), oAndL.getSecond());

    try {
      StringOps.getOffsetAndLength(test, 3, 2, 2, 3);
      fail("Should not have been able to compute offset where startRow > endRow");
    }
    catch (IllegalArgumentException ex) {
      
    }

    try {
      StringOps.getOffsetAndLength(test, 2, 4, 2, 3);
      fail("Should not have been able to compute offset where start > end");
    }
    catch (IllegalArgumentException ex) {
      
    }

    try {
      StringOps.getOffsetAndLength(test, 4, 4, 5, 5);
      fail("Should not have been able to compute offset where the\n" +
           "given coordinates are not contained within the string");
    }
    catch (IllegalArgumentException ex) {
      
    }

    try {
      StringOps.getOffsetAndLength(test, 3, 4, 3, 12);
      fail("Should not have been able to compute offset where the\n" +
           "given coordinates are not contained within the string");
    }
    catch (IllegalArgumentException ex) {
      
    }

    try {
      StringOps.getOffsetAndLength(test, 2, 15, 3, 1);
      fail("Should not have been able to compute offset where the\n" +
           "given coordinates are not contained within the string");
    }
    catch (IllegalArgumentException ex) {
      
    }
  }

  
  public void testGetStackTrace() {
    final String trace = "hello";
    Throwable t = new Throwable() {
      public void printStackTrace(PrintWriter w) {
        w.print(trace);
      }
    };
    assertEquals("Should have returned the correct stack trace!", trace, StringOps.getStackTrace(t));
  }

  
  public void testConvertToLiteral() {
    String toConvert = " a  b  c  d";
    String expResult = "\" a  b  c  d\"";
    String actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);

    toConvert = "\\ hello world \\";
    expResult = "\"\\\\ hello world \\\\\"";
    actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);

    toConvert = "\\\n\\n";
    expResult = "\"\\\\\\n\\\\n\"";
    actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);

    toConvert = "\\\"\t\\t";
    expResult = "\"\\\\\\\"\\t\\\\t\"";
    actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);

    toConvert = "\"\\\"\t\\n\n\\\n\"";
    expResult = "\"\\\"\\\\\\\"\\t\\\\n\\n\\\\\\n\\\"\"";
    actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);

    toConvert = "    ";
    expResult = "\"    \"";
    actResult = StringOps.convertToLiteral(toConvert);
    assertEquals("converting "+toConvert+" should yield "+ expResult, expResult, actResult);
  }
  
  private static class TestGetSimpleNameInner {
    public static class Nested {
      public static Class anonClass() {
        java.awt.event.ActionListener l = new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) { }
        };
        return l.getClass();
      }
    }
    public class Inner {
      public Class anonClass() {
        java.awt.event.ActionListener l = new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) { }
        };
        return l.getClass();
      }
    }
    public Inner getInner() {
      return new Inner();
    }
    public static Class anonClass() {
      java.awt.event.ActionListener l = new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) { }
      };
      return l.getClass();
    }
    public static Lambda<Class, Object> getLambda() {
      return new Lambda<Class, Object>() {
        public Class apply(Object param) {
          java.awt.event.ActionListener l = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { }
          };
          return l.getClass();
        }
      };
    }
  }

  
  public void testGetSimpleName() {
    String exp = "Integer";
    String act = StringOps.getSimpleName(java.lang.Integer.class);
    assertEquals("Wrong simple name for java.lang.Integer, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "TestGetSimpleNameInner";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.class);
    assertEquals("Wrong simple name for TestGetSimpleNameInner, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "Nested";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.Nested.class);
    assertEquals("Wrong simple name for TestGetSimpleNameInner.Nested, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "Inner";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.Inner.class);
    assertEquals("Wrong simple name for TestGetSimpleNameInner.Inner, exp="+exp+", act="+act,
                 exp,
                 act);
    
    java.awt.event.ActionListener l = new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) { }
    };
    
    exp = "";
    act = StringOps.getSimpleName(l.getClass());
    assertEquals("Wrong simple name for anonymous inner class, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.anonClass());
    assertEquals("Wrong simple name for anonymous inner class, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.Nested.anonClass());
    assertEquals("Wrong simple name for anonymous inner class, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "";
    act = StringOps.getSimpleName((new TestGetSimpleNameInner()).getInner().anonClass());
    assertEquals("Wrong simple name for anonymous inner class, exp="+exp+", act="+act,
                 exp,
                 act);
    
    exp = "";
    act = StringOps.getSimpleName(TestGetSimpleNameInner.getLambda().apply(null));
    assertEquals("Wrong simple name for anonymous inner class, exp="+exp+", act="+act,
                 exp,
                 act);
  }
  
  



























































  
  public void testToStringLong() {
    long[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new long[] {}));
    assertEquals("[1]", StringOps.toString(new long[] {1}));
    assertEquals("[1, 2]", StringOps.toString(new long[] {1, 2}));
  }
  
  public void testToStringInt() {
    int[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new int[] {}));
    assertEquals("[1]", StringOps.toString(new int[] {1}));
    assertEquals("[1, 2]", StringOps.toString(new int[] {1, 2}));
  }
  
  public void testToStringShort() {
    short[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new short[] {}));
    assertEquals("[1]", StringOps.toString(new short[] {1}));
    assertEquals("[1, 2]", StringOps.toString(new short[] {1, 2}));
  }
  
  public void testToStringChar() {
    char[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new char[] {}));
    assertEquals("[a]", StringOps.toString(new char[] {'a'}));
    assertEquals("[a, b]", StringOps.toString(new char[] {'a', 'b'}));
  }
  
  public void testToStringByte() {
    byte[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new byte[] {}));
    assertEquals("[1]", StringOps.toString(new byte[] {1}));
    assertEquals("[1, 2]", StringOps.toString(new byte[] {1, 2}));
  }
  
  public void testToStringBoolean() {
    boolean[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new boolean[] {}));
    assertEquals("[true]", StringOps.toString(new boolean[] {true}));
    assertEquals("[true, false]", StringOps.toString(new boolean[] {true, false}));
  }
  
  public void testToStringFloat() {
    float[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new float[] {}));
    assertEquals("[1.23]", StringOps.toString(new float[] {1.23f}));
    assertEquals("[1.23, 4.56]", StringOps.toString(new float[] {1.23f, 4.56f}));
  }
  
  public void testToStringDouble() {
    double[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new double[] {}));
    assertEquals("[1.23]", StringOps.toString(new double[] {1.23}));
    assertEquals("[1.23, 4.56]", StringOps.toString(new double[] {1.23, 4.56}));
  }
  
  public void testToStringObject() {
    Object[] a = null;
    assertEquals("null", StringOps.toString(a));
    assertEquals("[]", StringOps.toString(new Object[] {}));
    assertEquals("[123]", StringOps.toString(new Object[] {"123"}));
    assertEquals("[123, 123]", StringOps.toString(new Object[] {"123", new Integer(123)}));
  }
}