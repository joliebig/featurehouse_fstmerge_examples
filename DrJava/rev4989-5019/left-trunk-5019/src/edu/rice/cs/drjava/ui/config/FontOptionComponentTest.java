

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;

import java.awt.*;


public final class FontOptionComponentTest extends DrJavaTestCase {
  private static FontOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new FontOptionComponent( OptionConstants.FONT_MAIN, "Main font", new DefaultSwingFrame());
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();
  }

  public void testCancelDoesNotChangeConfig() {

    Font testFont = Font.decode("Monospaced-BOLD-10");

    _option.setValue(testFont);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig(); 
    Utilities.clearEventQueue();
    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.FONT_MAIN.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN));

  }

  public void testApplyDoesChangeConfig() {
    Font testFont = Font.decode("Monospaced-BOLD-10");

    _option.setValue(testFont);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testFont,
                 DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN));
  }

  public void testApplyThenResetDefault() {
    Font testFont = Font.decode("Monospaced-BOLD-10");

    _option.setValue(testFont);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();  
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.FONT_MAIN.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN));
  }

}