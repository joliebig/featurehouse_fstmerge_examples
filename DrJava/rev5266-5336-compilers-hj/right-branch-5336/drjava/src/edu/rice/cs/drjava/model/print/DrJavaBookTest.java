

package edu.rice.cs.drjava.model.print;

import edu.rice.cs.drjava.DrJavaTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;


public final class DrJavaBookTest extends DrJavaTestCase {
  
  private DrJavaBook book = null;
  
  
  public DrJavaBookTest(String name) { super(name); }
  
  
  public static Test suite() { return  new TestSuite(DrJavaBookTest.class); }
  
  public void setUp() throws Exception {
    super.setUp();
    book = new DrJavaBook("import java.io.*;", "simple_file.java", new PageFormat());
  }
  
  public void tearDown() throws Exception {
    book = null;
    super.tearDown();
  }
  
  public void testGetNumberOfPages() {
    assertEquals("testGetNumberOfPages:", Integer.valueOf(1), Integer.valueOf(book.getNumberOfPages()));
  }
  
  public void testGetPageFormat() {
    assertEquals("testGetPageFormat:", PageFormat.PORTRAIT, book.getPageFormat(0).getOrientation());
  }
  
  public void testGetPrintable() { 
    Graphics g = (new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)).getGraphics();
    Printable p = book.getPrintable(0);
    try { 
      assertEquals("testGetPrintable:", Integer.valueOf(Printable.PAGE_EXISTS), 
                   Integer.valueOf(p.print(g, new PageFormat(), 0)));
    }
    catch(Exception e) { fail("testGetPrintable: Unexpected exception!\n" + e); }
    
    try {
      p = book.getPrintable(99);
      fail("previous operation should throw an IndexOutOfBoundsException");
    }
    catch(IndexOutOfBoundsException e) {
      
    }
  }
}