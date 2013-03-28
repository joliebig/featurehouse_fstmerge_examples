

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.config.OptionConstants;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public final class FileOptionComponentTest extends DrJavaTestCase {

  private static FileOptionComponent _option;

  public FileOptionComponentTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    _option = new FileOptionComponent(OptionConstants.JAVAC_LOCATION,
                                      "Javac Location", new Frame(),
                                      new JFileChooser());
    DrJava.getConfig().resetToDefaults();

  }

  public void testCancelDoesNotChangeConfig() {

    File testFile = FileOption.NULL_FILE;

    _option.setValue(testFile);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.JAVAC_LOCATION.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));

  }

  public void testApplyDoesChangeConfig() {
    File testFile = FileOption.NULL_FILE;

    _option.setValue(testFile);
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 testFile,
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));
  }

  public void testApplyThenResetDefault() {
    File testFile = FileOption.NULL_FILE;

    _option.setValue(testFile);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.JAVAC_LOCATION.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVAC_LOCATION));
  }
}