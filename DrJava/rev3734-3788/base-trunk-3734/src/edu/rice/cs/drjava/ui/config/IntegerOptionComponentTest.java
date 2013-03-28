

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;

import java.awt.*;


public final class IntegerOptionComponentTest extends DrJavaTestCase {

  private static IntegerOptionComponent _option;

  protected void setUp() throws Exception {
    super.setUp();
    _option = new IntegerOptionComponent(OptionConstants.INDENT_LEVEL, "Indent Level", new Frame());
    DrJava.getConfig().resetToDefaults();

  }

  public void testCancelDoesNotChangeConfig() {

    Integer testInteger = new Integer(0);

    _option.setValue(testInteger);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.INDENT_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));

  }

  public void testApplyDoesChangeConfig() {
    Integer testInteger = new Integer(10);

    _option.setValue(testInteger);
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 testInteger,
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));
  }

  public void testApplyThenResetDefault() {
    Integer testInteger = new Integer(10);

    _option.setValue(testInteger);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.INDENT_LEVEL.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.INDENT_LEVEL));
  }

}