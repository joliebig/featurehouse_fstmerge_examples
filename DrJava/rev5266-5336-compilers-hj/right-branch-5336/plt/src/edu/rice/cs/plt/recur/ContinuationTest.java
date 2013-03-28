

package edu.rice.cs.plt.recur;

import junit.framework.TestCase;

public class ContinuationTest extends TestCase {
  
  
  private static void checkStack() {
    
    RuntimeException e = new RuntimeException();
    if (e.getStackTrace().length > 100) { throw e; }
  }
    
  
  
  public static boolean isEven(int x) {
    checkStack();
    if (x == 0) { return true; }
    if (x == 1) { return false; }
    else { return isEven(x - 2); }
  }
  
  
  public static Continuation<Boolean> safeIsEven(final int x) {
    checkStack();
    if (x == 0) { return ValueContinuation.make(true); }
    if (x == 1) { return ValueContinuation.make(false); }
    else {
      return new PendingContinuation<Boolean>() {
        public Continuation<Boolean> step() { return safeIsEven(x - 2); }
      };
    }
  }
  
  public void testIsEven() {
    
    assertTrue(isEven(0));
    assertFalse(isEven(1));
    assertTrue(isEven(6));
    assertFalse(isEven(7));
    
    
    assertTrue(safeIsEven(0).value());
    assertFalse(safeIsEven(1).value());
    assertTrue(safeIsEven(6).value());
    assertFalse(safeIsEven(7).value());

    
    try { isEven(500); fail("isEven(500) did not overflow the stack"); }
    catch (RuntimeException e) {  }
    
    
    assertTrue(safeIsEven(500).value());
  }
  
  
  public static long sum(int n) {
    checkStack();
    if (n == 0) return 0l;
    else return n + sum(n-1);
  }
  
  
  public static Continuation<Long> safeSum(final int n) {
    checkStack();
    if (n == 0) { return ValueContinuation.make(0l); }
    else {
      return new ArgContinuation<Long, Long>() {
        public Continuation<Long> arg() { return safeSum(n-1); }
        public Continuation<Long> apply(Long arg) { return ValueContinuation.make(arg + n); }
      };
    }
  }
          
  public void testSum() {
    
    assertEquals(0l, sum(0));
    assertEquals(1l, sum(1));
    assertEquals(3l, sum(2));
    assertEquals(6l, sum(3));
    assertEquals(10l, sum(4));
    assertEquals(15l, sum(5));
    assertEquals(21l, sum(6));

    
    assertEquals(0l, (long) safeSum(0).value());
    assertEquals(1l, (long) safeSum(1).value());
    assertEquals(3l, (long) safeSum(2).value());
    assertEquals(6l, (long) safeSum(3).value());
    assertEquals(10l, (long) safeSum(4).value());
    assertEquals(15l, (long) safeSum(5).value());
    assertEquals(21l, (long) safeSum(6).value());

    
    try { sum(500); fail("sum(500) did not overflow the stack"); }
    catch (RuntimeException e) {  }
    
    
    long bigResult = safeSum(500).value();
    assertTrue(bigResult > 500l);
    assertTrue(bigResult < (500l * 500l));
  }
  
  
  public static double fib(int n) {
    return fibHelp(n, new double[n+1]);
  }
  
  private static double fibHelp(int n, double[] results) {
    checkStack();
    if (results[n] != 0) { return results[n]; }
    else {
      double result;
      if (n == 0) { result = 0.0; }
      else if (n == 1) { result = 1.0; }
      else { result = fibHelp(n-2, results) + fibHelp(n-1, results); }
      results[n] = result;
      return result;
    }
  }
  
  public static double safeFib(int n) {
    return safeFibHelp(n, new double[n+1]).value();
  }
  
  private static Continuation<Double> safeFibHelp(final int n, final double[] results) {
    checkStack();
    if (results[n] != 0.0) { return ValueContinuation.make(results[n]); }
    else {
      if (n == 0) { results[0] = 0.0; return ValueContinuation.make(0.0); }
      else if (n == 1) { results[1] = 1.0; return ValueContinuation.make(1.0); }
      else {
        return new BinaryArgContinuation<Double, Double, Double>() {
          public Continuation<Double> arg1() { return safeFibHelp(n-2, results); }
          public Continuation<Double> arg2() { return safeFibHelp(n-1, results); }
          public Continuation<Double> apply(Double arg1, Double arg2) {
            results[n] = arg1 + arg2;
            return ValueContinuation.make(results[n]);
          }
        };
      }
    }
  }
  
  public void testFib() {
    
    assertEquals(0.0, fib(0));
    assertEquals(1.0, fib(1));
    assertEquals(1.0, fib(2));
    assertEquals(2.0, fib(3));
    assertEquals(3.0, fib(4));
    assertEquals(5.0, fib(5));
    assertEquals(8.0, fib(6));

    
    assertEquals(0.0, safeFib(0));
    assertEquals(1.0, safeFib(1));
    assertEquals(1.0, safeFib(2));
    assertEquals(2.0, safeFib(3));
    assertEquals(3.0, safeFib(4));
    assertEquals(5.0, safeFib(5));
    assertEquals(8.0, safeFib(6));

  
    
    try { fib(500); fail("fib(500) did not overflow the stack"); }
    catch (RuntimeException e) {  }
    
    
    assertTrue(safeFib(500) > 500.0);
  }
  
}
