
package edu.rice.cs.drjava.model;
import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.Reader;
import java.io.StringReader;



public class ClassAndInterfaceFinderTest extends DrJavaTestCase {
  
  
  
  public void testStringInterfaceRecognition() {
    try {
      Reader r = new StringReader("//\n /**/public Class Interface interface Aa.12_34 {}");
      ClassAndInterfaceFinder finder = new ClassAndInterfaceFinder(r);
      String s = finder.getClassOrInterfaceName();
      assertEquals("stringInterfaceRecognition","Aa.12_34", s);
    }
    catch (Exception e) {
      fail("stringInterfaceRecognition threw " + e);
    }
  }
  
  
  
  public void testStringInterfaceRejection() {
    try {
      Reader r = new StringReader("//\n /**/public Class Interface interface Aa.12_34 {}");
      ClassAndInterfaceFinder finder = new ClassAndInterfaceFinder(r);
      String s = finder.getClassName();
      assertEquals("stringInterfaceRejection","", s);
    }
    catch (Exception e) {
      fail("stringInterfaceRejection threw " +  e);
    }
  }
  
  
  
  public void testStringClassRecognition() {
    try {
      Reader r = new StringReader("//\n /**/public Class Interface class Aa.12_34 {}");
      ClassAndInterfaceFinder finder = new ClassAndInterfaceFinder(r);
      String s = finder.getClassOrInterfaceName();
      assertEquals("stringNameRecognition","Aa.12_34", s);
    }
    catch (Exception e) {
      fail("stringClassRecognition threw " +e);
    }
  }
  
  
  public void testStringPackageRecognition() {
    try {
      Reader r = new StringReader("//\n /**/package x public interface Aa.12_34 {}");
      ClassAndInterfaceFinder finder = new ClassAndInterfaceFinder(r);
      String s = finder.getClassOrInterfaceName();
      assertEquals("stringNameRecognition","x.Aa.12_34", s);
    }
    catch (Exception e) {
      fail("stringPackageRecognition threw " + e);
    }
  }
  
  
}
