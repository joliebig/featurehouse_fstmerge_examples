

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.KeyStrokeOption;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;

import java.util.Vector;
import javax.swing.*;
import java.awt.event.KeyEvent;


public final class VectorKeyStrokeOptionComponentTest extends DrJavaTestCase {

  private static VectorKeyStrokeOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new VectorKeyStrokeOptionComponent(OptionConstants.KEY_NEW_FILE, "Normal KeyStroke", new DefaultSwingFrame());
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();
  }

  public void testCancelDoesNotChangeConfig() {
    Vector<KeyStroke> testKeyStroke = new Vector<KeyStroke>();

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig();   
    Utilities.clearEventQueue();
    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.KEY_NEW_FILE.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

  public void testApplyDoesChangeConfig() {
    Vector<KeyStroke> testKeyStroke = new Vector<KeyStroke>();

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testKeyStroke,
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

  public void testApplyThenResetDefault() {
    Vector<KeyStroke> testKeyStroke = new Vector<KeyStroke>();

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    
    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.KEY_NEW_FILE.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

}