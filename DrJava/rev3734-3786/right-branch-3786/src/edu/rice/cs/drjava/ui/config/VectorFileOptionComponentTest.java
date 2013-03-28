

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;

import java.awt.*;
import java.io.File;
import java.util.Vector;


public final class VectorFileOptionComponentTest extends DrJavaTestCase {
  private static VectorFileOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new VectorFileOptionComponent(OptionConstants.EXTRA_CLASSPATH, "Extra Classpath", new Frame());
    DrJava.getConfig().resetToDefaults();
  }

  public void testCancelDoesNotChangeConfig() {
    Vector<File> testVector = new Vector<File>();
    testVector.addElement(new File("test"));

    _option.setValue(testVector);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertTrue("Cancel (resetToCurrent) should not change the config",
               vectorEquals(OptionConstants.EXTRA_CLASSPATH.getDefault(),
                            DrJava.getConfig().getSetting(OptionConstants.EXTRA_CLASSPATH)));
  }

  public void testApplyDoesChangeConfig() {
    Vector<File> testVector = new Vector<File>();
    testVector.addElement(new File("blah"));

    _option.setValue(testVector);
    _option.updateConfig();

    assertTrue("Apply (updateConfig) should write change to file",
               vectorEquals(testVector,
                            DrJava.getConfig().getSetting(OptionConstants.EXTRA_CLASSPATH)));
  }

  public void testApplyThenResetDefault() {
    Vector<File> testVector = new Vector<File>();
    testVector.addElement(new File("blah"));

    _option.setValue(testVector);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    assertTrue("Apply (updateConfig) should write change to file",
               vectorEquals(OptionConstants.EXTRA_CLASSPATH.getDefault(),
                            DrJava.getConfig().getSetting(OptionConstants.EXTRA_CLASSPATH)));
  }

  
  public boolean vectorEquals(Vector<File> v1, Vector<File> v2) {
    if (v1.size() == v2.size()) {
      for (int i = 0; i < v1.size(); i++) {
        if (!v1.elementAt(i).equals(v2.elementAt(i))) {
          return false;
        }
      }
      return true;
    }
    else { 
      return false;
    }
  }
}
