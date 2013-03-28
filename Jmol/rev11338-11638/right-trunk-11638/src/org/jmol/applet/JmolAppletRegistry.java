

package org.jmol.applet;

import org.jmol.appletwrapper.AppletWrapper;

import java.applet.Applet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import netscape.javascript.JSObject;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

final class JmolAppletRegistry {

  static Hashtable htRegistry = new Hashtable();

  synchronized static void checkIn(String name, Applet applet) {
    cleanRegistry();
    if (name != null) {
      Logger.info("AppletRegistry.checkIn(" + name + ")");
      htRegistry.put(name, applet);
    }
    if (Logger.debugging) {
      Enumeration keys = htRegistry.keys();
      while (keys.hasMoreElements()) {
        String theApplet = (String) keys.nextElement();
        Logger.debug(theApplet + " " + htRegistry.get(theApplet));
      }
    }
  }

  synchronized static void checkOut(String name) {
    htRegistry.remove(name);
  }

  synchronized private static void cleanRegistry() {
    Enumeration keys = htRegistry.keys();
    AppletWrapper app = null;
    boolean closed = true;
    while (keys.hasMoreElements()) {
      String theApplet = (String) keys.nextElement();
      try {
        app = (AppletWrapper) (htRegistry.get(theApplet));
        JSObject theWindow = JSObject.getWindow(app);
        
        closed = ((Boolean) theWindow.getMember("closed")).booleanValue();
        
        if (closed || theWindow.hashCode() == 0) {
          
        }
        if (Logger.debugging)
          Logger.debug("Preserving registered applet " + theApplet
              + " window: " + theWindow.hashCode());
      } catch (Exception e) {
        closed = true;
      }
      if (closed) {
        if (Logger.debugging)
          Logger.debug("Dereferencing closed window applet " + theApplet);
        htRegistry.remove(theApplet);
        app.destroy();
      }
    }
  }

  synchronized public static void findApplets(String appletName,
                                              String mySyncId,
                                              String excludeName, Vector apps) {
    if (appletName != null && appletName.indexOf(",") >= 0) {
      String[] names = TextFormat.split(appletName, ",");
      for (int i = 0; i < names.length; i++)
        findApplets(names[i], mySyncId, excludeName, apps);
      return;
    }
    String ext = "__" + mySyncId + "__";
    if (appletName == null || appletName.equals("*") || appletName.equals(">")) {
      Enumeration keys = htRegistry.keys();
      while (keys.hasMoreElements()) {
        appletName = (String) keys.nextElement();
        if (!appletName.equals(excludeName) && appletName.indexOf(ext) > 0) {
          apps.addElement(appletName);
        }
      }
      return;
    }
    if (appletName.indexOf("__") < 0)
      appletName += ext;
    if (!htRegistry.containsKey(appletName))
      appletName = "jmolApplet" + appletName;
    if (!appletName.equals(excludeName) && htRegistry.containsKey(appletName)) {
      apps.addElement(appletName);
    }
  }
}
