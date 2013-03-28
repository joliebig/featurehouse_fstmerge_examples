

package org.jmol.appletwrapper;

import org.jmol.util.Logger;

class WrappedAppletLoader extends Thread {
    
  AppletWrapper appletWrapper;
  String wrappedAppletClassName;

  private final static int minimumLoadSeconds = 0;

  WrappedAppletLoader(AppletWrapper appletWrapper,
                      String wrappedAppletClassName) {
    this.appletWrapper = appletWrapper;
    this.wrappedAppletClassName = wrappedAppletClassName;
  }
    
  public void run() {
    long startTime = System.currentTimeMillis();
    if (Logger.debugging) {
      Logger.debug("WrappedAppletLoader.run(" + wrappedAppletClassName + ")");
    }
    TickerThread tickerThread = new TickerThread(appletWrapper);
    tickerThread.start();
    WrappedApplet wrappedApplet = null;
    try {
      Class wrappedAppletClass = Class.forName(wrappedAppletClassName);
      wrappedApplet = (WrappedApplet)wrappedAppletClass.newInstance();
      wrappedApplet.setAppletWrapper(appletWrapper);
      wrappedApplet.init();
    } catch (Exception e) {
      Logger.error(
          "Could not instantiate wrappedApplet class" + wrappedAppletClassName,
          e);
    }
    long loadTimeSeconds =
      (System.currentTimeMillis() - startTime + 500) / 1000;
    if (Logger.debugging) {
      Logger.debug(
          wrappedAppletClassName + " load time = " + loadTimeSeconds + " seconds");
    }
    if (minimumLoadSeconds != 0) { 
      long minimumEndTime = startTime + 1000 * minimumLoadSeconds;
      int sleepTime = (int)(minimumEndTime - System.currentTimeMillis());
      if (sleepTime > 0) {
        Logger.warn("artificial minimum load time engaged");
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
        }
      }
    }
    tickerThread.keepRunning = false;
    tickerThread.interrupt();
    appletWrapper.wrappedApplet = wrappedApplet;
    appletWrapper.repaint();
  }
}

class TickerThread extends Thread {
  AppletWrapper appletWrapper;
  boolean keepRunning = true;

  TickerThread(AppletWrapper appletWrapper) {
    this.appletWrapper = appletWrapper;
    this.setName("AppletLoaderTickerThread");
  }

  public void run() {
    do {
      try {
        Thread.sleep(999);
      } catch (InterruptedException ie) {
        break;
      }
      appletWrapper.repaintClock();
    } while (keepRunning);
  }
}

