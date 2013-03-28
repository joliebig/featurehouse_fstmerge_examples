

package org.jmol.util;


public class PerformanceMeasure {

  private long start;
  private String method;

  public PerformanceMeasure(String method) {
    this.method = method;
    start = System.currentTimeMillis();
  }

  public void logPerformance(String text) {
    long current = System.currentTimeMillis();
    if (current - start == 0) {
      return;
    }
    System.err.print(method);
    System.err.print(": ");
    System.err.print(current - start);
    System.err.print(" milliseconds: ");
    System.err.println(text);
  }
}
