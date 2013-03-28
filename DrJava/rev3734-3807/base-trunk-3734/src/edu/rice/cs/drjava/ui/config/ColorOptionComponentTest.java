

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.Utilities;

import java.awt.*;


public final class ColorOptionComponentTest extends DrJavaTestCase {

  private static ColorOptionComponent _option;

  public ColorOptionComponentTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    _option = new ColorOptionComponent( OptionConstants.DEFINITIONS_NORMAL_COLOR, "Normal Color", new Frame());
    DrJava.getConfig().resetToDefaults();

  }

  public void testCancelDoesNotChangeConfig() {

    Color testColor = Color.decode("#ABCDEF");

    _option.setValue(testColor);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.DEFINITIONS_NORMAL_COLOR.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR));

  }

  public void testApplyDoesChangeConfig() {
    Color testColor = Color.decode("#ABCDEF");

    _option.setValue(testColor);
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 testColor,
                 DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR));
  }

  public void testApplyThenResetDefault() {
    Color testColor = Color.decode("#ABCDEF");

    _option.setValue(testColor);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.DEFINITIONS_NORMAL_COLOR.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR));
  }

}
