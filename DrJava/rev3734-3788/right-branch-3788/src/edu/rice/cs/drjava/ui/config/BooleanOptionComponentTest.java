

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;

import java.awt.*;


public final class BooleanOptionComponentTest extends DrJavaTestCase {

  private static BooleanOptionComponent _option;

  public BooleanOptionComponentTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    _option = new BooleanOptionComponent( OptionConstants.LINEENUM_ENABLED, "Line Enumeration", new Frame());
    DrJava.getConfig().resetToDefaults();

  }

  public void testCancelDoesNotChangeConfig() {

    Boolean testBoolean = new Boolean (!DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED).booleanValue());

    _option.setValue(testBoolean);
    _option.resetToCurrent(); 
    _option.updateConfig(); 

    assertEquals("Cancel (resetToCurrent) should not change the config",
                 OptionConstants.LINEENUM_ENABLED.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED));

  }

  public void testApplyDoesChangeConfig() {
    Boolean testBoolean = new Boolean (!DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED).booleanValue());

    _option.setValue(testBoolean);
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 testBoolean,
                 DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED));
  }

  public void testApplyThenResetDefault() {
    Boolean testBoolean = new Boolean (!DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED).booleanValue());

    _option.setValue(testBoolean);
    _option.updateConfig();
    _option.resetToDefault(); 
    _option.updateConfig();

    assertEquals("Apply (updateConfig) should write change to file",
                 OptionConstants.LINEENUM_ENABLED.getDefault(),
                 DrJava.getConfig().getSetting(OptionConstants.LINEENUM_ENABLED));
  }
}

