

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.KeyStrokeOption;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;


public final class KeyStrokeOptionComponentTest extends DrJavaTestCase {

  private static KeyStrokeOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new KeyStrokeOptionComponent( OptionConstants.KEY_NEW_FILE, "Normal KeyStroke", new Frame());
    DrJava.getConfig().resetToDefaults();
  }

  public void testCancelDoesNotChangeConfig() {
    KeyStroke testKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;

    _option.setValue(testKeyStroke);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    Utilities.clearEventQueue();
    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.KEY_NEW_FILE.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

  public void testApplyDoesChangeConfig() {
    KeyStroke testKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);

    _option.setValue(testKeyStroke);
    _option.updateConfig();

    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testKeyStroke,
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

  public void testApplyThenResetDefault() {
    KeyStroke testKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;

    _option.setValue(testKeyStroke);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.KEY_NEW_FILE.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.KEY_NEW_FILE));
  }

}