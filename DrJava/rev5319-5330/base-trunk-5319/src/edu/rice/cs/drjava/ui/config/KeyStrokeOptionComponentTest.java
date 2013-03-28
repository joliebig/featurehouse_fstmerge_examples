

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.KeyStrokeOption;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import java.awt.event.KeyEvent;


public final class KeyStrokeOptionComponentTest extends DrJavaTestCase {

  private static KeyStrokeOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new KeyStrokeOptionComponent(OptionConstants.KEY_FOR_UNIT_TESTS_ONLY, "Normal KeyStroke", new DefaultSwingFrame());
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();
  }

  public void testCancelDoesNotChangeConfig() {
    KeyStroke testKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig();   
    Utilities.clearEventQueue();
    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.KEY_FOR_UNIT_TESTS_ONLY.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_FOR_UNIT_TESTS_ONLY));
  }

  public void testApplyDoesChangeConfig() {
    KeyStroke testKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testKeyStroke,
                 DrJava.getConfig().getSetting(OptionConstants.KEY_FOR_UNIT_TESTS_ONLY));
  }

  public void testApplyThenResetDefault() {
    KeyStroke testKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;

    _option.setValue(testKeyStroke);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    
    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.KEY_FOR_UNIT_TESTS_ONLY.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_FOR_UNIT_TESTS_ONLY));
  }

}
