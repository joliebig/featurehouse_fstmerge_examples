

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;

import java.awt.*;


public final class ForcedChoiceOptionComponentTest extends DrJavaTestCase {
  private static ForcedChoiceOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new ForcedChoiceOptionComponent( OptionConstants.JAVADOC_ACCESS_LEVEL, "Private", new Frame());
    DrJava.getConfig().resetToDefaults();
  }

  public void testCancelDoesNotChangeConfig() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.JAVADOC_ACCESS_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

  }

  public void testApplyDoesChangeConfig() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 testForcedChoice,
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));
  }

  public void testApplyThenResetDefault() {
    String testForcedChoice = new String(DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));

    _option.setValue(testForcedChoice);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.JAVADOC_ACCESS_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));
  }

}