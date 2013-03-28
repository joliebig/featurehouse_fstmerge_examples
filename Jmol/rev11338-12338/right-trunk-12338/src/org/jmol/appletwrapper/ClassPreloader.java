

package org.jmol.appletwrapper;

import org.jmol.util.Logger;

class ClassPreloader extends Thread {
    
  AppletWrapper appletWrapper;

  ClassPreloader(AppletWrapper appletWrapper) {
    this.appletWrapper = appletWrapper;
  }
    
  public void run() {
    String className;
    setPriority(getPriority() - 1);
    while ((className = appletWrapper.getNextPreloadClassName()) != null) {
      try {
        int lastCharIndex = className.length() - 1;
        boolean constructOne = className.charAt(lastCharIndex) == '+';
        
        if (constructOne)
          className = className.substring(0, lastCharIndex);
        Class preloadClass = Class.forName(className);
        if (constructOne)
          preloadClass.newInstance();
      } catch (Exception e) {
        Logger.fatal("error preloading " + className, e);
      }
    }
  }
}
