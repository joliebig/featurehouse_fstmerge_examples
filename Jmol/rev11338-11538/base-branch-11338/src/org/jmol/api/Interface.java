

package org.jmol.api;

import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public class Interface {

  public static Object getOptionInterface(String name) {
    try {
      name = JmolConstants.CLASSBASE_OPTIONS + name;
      return Class.forName(name).newInstance();
    } catch (Exception e) {
      Logger.error("Interface.java Error creating instance for " + name + ": \n" + e.getMessage());
      return null;
    }
  }

  public static Object getApplicationInterface(String name) {
    try {
      name = "org.openscience.jmol.app." + name;
      return Class.forName(name).newInstance();
    } catch (Exception e) {
      Logger.error("Interface.java Error creating instance for " + name + ": \n" + e.getMessage());
      return null;
    }
  }

}
