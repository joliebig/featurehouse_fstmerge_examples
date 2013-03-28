



package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class StreamRedirectorTest extends DrJavaTestCase {
  
  public void testEmptyInput() throws IOException {
    InputStreamRedirector isr = new InputStreamRedirector() {
      protected String _getInput() {
        return "";
      }
    };
    try {
      isr.read();
      fail("Should have thrown IOException on empty input!");
    }
    catch (IOException ioe) {
      
    }
  }

  
  public void testStaticInput() throws IOException {
    InputStreamRedirector isr = new InputStreamRedirector() {
      protected String _getInput() {
        return "Hello World!\n";
      }
    };
    BufferedReader br = new BufferedReader(new InputStreamReader(isr));
    assertEquals("First read", "Hello World!", br.readLine());
    assertEquals("Second read", "Hello World!", br.readLine());  
  }

  
  public void testDynamicInput() throws IOException {
    InputStreamRedirector isr = new InputStreamRedirector() {
      int x = -1;
      protected String _getInput() {
        x++;
        return x + "\n";
      }
    };
    BufferedReader br = new BufferedReader(new InputStreamReader(isr));
    assertEquals("First read", "0", br.readLine());
    
    assertEquals("Second read", "1", br.readLine());
    assertEquals("Third read", "2", br.readLine());
  }

  
  public void testMultiLineInput() throws IOException {
    InputStreamRedirector isr = new InputStreamRedirector() {
      private boolean alreadyCalled = false;

      protected String _getInput() {
        if (alreadyCalled) {
          throw new RuntimeException("_getInput() has already been called!");
        }
        alreadyCalled = true;
        return "Line 1\nLine 2\n";
      }
    };
    BufferedReader br = new BufferedReader(new InputStreamReader(isr));
    assertEquals("First read calls _getInput()", "Line 1", br.readLine());
    assertEquals("First read does not call _getInput()", "Line 2", br.readLine());
    try {
      br.readLine();
      fail("_getInput() should be called again!");
    }
    catch(RuntimeException re) {
      assertEquals("Should have thrown correct exception.",
                   "_getInput() has already been called!", re.getMessage());
    }
  }
}