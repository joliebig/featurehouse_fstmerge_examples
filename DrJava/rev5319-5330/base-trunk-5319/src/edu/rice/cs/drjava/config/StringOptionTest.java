

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class StringOptionTest extends DrJavaTestCase {
  
  public StringOptionTest(String name) { super(name); }
  
  public void testGetName() {
    StringOption so = new StringOption("classpath",null);
    assertEquals("classpath", so.getName());
  }
  
  public void testParse() {
    StringOption so = new StringOption("classpath",null);
    assertEquals("3", so.parse("3"));
    assertEquals(".:..", so.parse(".:.."));
  }
  
  public void testFormat() {
    StringOption so = new StringOption("classpath",null);
    assertEquals("3", so.format("3"));
    assertEquals(".:..", so.format(".:.."));
  }
}
