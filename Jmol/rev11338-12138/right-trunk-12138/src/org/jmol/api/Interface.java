

package org.jmol.api;

import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public class Interface {

  public static Object getOptionInterface(String name) {
    return getInterface(JmolConstants.CLASSBASE_OPTIONS + name);
  }

  public static Object getApplicationInterface(String name) {
    return getInterface("org.openscience.jmol.app." + name);
  }

  public static Object getInterface(String name) {
    try {
      return Class.forName(name).newInstance();
    } catch (Exception e) {
      Logger.error("Interface.java Error creating instance for " + name + ": \n" + e.getMessage());
      return null;
    }
  }

}
