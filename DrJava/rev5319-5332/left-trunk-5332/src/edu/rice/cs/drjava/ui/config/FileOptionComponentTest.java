

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import java.io.File;


public final class FileOptionComponentTest extends DrJavaTestCase {

  private static FileOptionComponent _option;

  public FileOptionComponentTest(String name) { super(name); }

  protected void setUp() throws Exception {
    super.setUp();
    _option = 
      new FileOptionComponent(OptionConstants.JAVAC_LOCATION, "Javac Location", new DefaultSwingFrame(), new JFileChooser());
    Utilities.clearEventQueue();
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();

  }

  public void testCancelDoesNotChangeConfig() {

    File testFile = FileOps.NULL_FILE;

    _option.setValue(testFile);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig(); 
    Utilities.clearEventQueue();
    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.JAVAC_LOCATION.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));

  }

  public void testApplyDoesChangeConfig() {
    File testFile = FileOps.NULL_FILE;

    _option.setValue(testFile);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testFile,
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));
  }
  
  public void testApplyThenResetDefault() {
    File testFile = FileOps.NULL_FILE;

    _option.setValue(testFile);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();  
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.JAVAC_LOCATION.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));

  }
  
  public void tearDown() throws Exception {
    
    try {
      _option = null;
      super.tearDown();

    }
    catch(Exception e) {  }
  }
}