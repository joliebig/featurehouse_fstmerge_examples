

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;


public final class IntegerOptionComponentTest extends DrJavaTestCase {

  private static volatile IntegerOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new IntegerOptionComponent(OptionConstants.INDENT_LEVEL, "Indent Level", new DefaultSwingFrame());
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();
  }

  public void testCancelDoesNotChangeConfig() {

    Integer testInteger = Integer.valueOf(0);

    _option.setValue(testInteger);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig(); 
    Utilities.clearEventQueue();

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.INDENT_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));

  }

  public void testApplyDoesChangeConfig() {
    Integer testInteger = Integer.valueOf(10);

    _option.setValue(testInteger);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 testInteger,
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));
  }

  public void testApplyThenResetDefault() {
    Integer testInteger = Integer.valueOf(10);

    _option.setValue(testInteger);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.INDENT_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));
  }

}