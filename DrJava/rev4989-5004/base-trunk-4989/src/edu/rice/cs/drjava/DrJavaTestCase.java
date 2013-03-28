

package edu.rice.cs.drjava;

import javax.swing.text.BadLocationException;

import junit.framework.TestCase;

import edu.rice.cs.drjava.config.Option;
import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.Log;


public class DrJavaTestCase extends TestCase {
  
  public DrJavaTestCase() { super(); }
  
  
  public DrJavaTestCase(String name) { super(name); }
  
  private static Log _log = new Log("DrJavaTestCase.txt", false);
  
  
  protected void setUp() throws Exception {
    super.setUp();  
    Utilities.TEST_MODE = true;
    final String newName = System.getProperty("drjava.test.config");
    assert newName != null;



    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        DrJava.setPropertiesFile(newName);  

        DrJava._initConfig();               
      }
    });
  }
  
  
  protected void tearDown() throws Exception { 
    DrJava.cleanUp();  
    super.tearDown();
  }

  protected <T> void setConfigSetting(final Option<T> op, final T value) {
    Utilities.invokeAndWait(new Runnable() { public void run() { DrJava.getConfig().setSetting(op, value); } });
  }
  
    
  protected static final void setDocText(final AbstractDJDocument doc, final String text) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try {
          doc.clear();
          doc.insertString(0, text, null);
        }
        catch(BadLocationException e) { throw new UnexpectedException(e); }
      }
    });
    Utilities.clearEventQueue();  
  }
}
