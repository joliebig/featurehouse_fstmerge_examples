

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.DefaultSwingFrame;
import edu.rice.cs.util.swing.Utilities;


public final class ForcedChoiceOptionComponentTest extends DrJavaTestCase {
  private static ForcedChoiceOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new ForcedChoiceOptionComponent( OptionConstants.JAVADOC_ACCESS_LEVEL, "Private", new DefaultSwingFrame());
    DrJava.getConfig().resetToDefaults();
    Utilities.clearEventQueue();
  }

  public void testCancelDoesNotChangeConfig() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    Utilities.clearEventQueue();
    _option.resetToCurrent(); 
    Utilities.clearEventQueue();
    _option.updateConfig(); 
    Utilities.clearEventQueue();

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.JAVADOC_ACCESS_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

  }

  public void testApplyDoesChangeConfig() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    assertEquals("Apply (updateConfig) should write change to file",
                 testForcedChoice,
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));
  }

  public void testApplyThenResetDefault() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();
    _option.resetToDefault(); 
    Utilities.clearEventQueue();
    _option.updateConfig();
    Utilities.clearEventQueue();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.JAVADOC_ACCESS_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));
  }

}