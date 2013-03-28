
package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;
import org.jmol.export.dialog.Dialog;
import org.jmol.util.*;
import org.jmol.viewer.JmolConstants;
import org.openscience.jmol.app.webexport.WebExport;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

class StatusListener implements JmolStatusListener {

  

  JmolPanel jmol;
  DisplayPanel display;

  JmolViewer viewer;
  void setViewer(JmolViewer viewer) {
    this.viewer = viewer;
  }
  
  StatusListener(JmolPanel jmol, DisplayPanel display) {
    
    this.jmol = jmol;
    this.display = display;  
  }
  
  
  public boolean notifyEnabled(int type) {
    switch (type) {
    case JmolConstants.CALLBACK_ANIMFRAME:
    case JmolConstants.CALLBACK_ECHO:
    case JmolConstants.CALLBACK_LOADSTRUCT:
    case JmolConstants.CALLBACK_MEASURE:
    case JmolConstants.CALLBACK_MESSAGE:
    case JmolConstants.CALLBACK_CLICK:
    case JmolConstants.CALLBACK_PICK:
    case JmolConstants.CALLBACK_SCRIPT:
      return true;
    case JmolConstants.CALLBACK_ERROR:
    case JmolConstants.CALLBACK_HOVER:
    case JmolConstants.CALLBACK_MINIMIZATION:
    case JmolConstants.CALLBACK_RESIZE:
    case JmolConstants.CALLBACK_SYNC:
      
    }
    return false;
  }

  public void notifyCallback(int type, Object[] data) {
    String strInfo = (data == null || data[1] == null ? null : data[1]
        .toString());
    switch (type) {
    case JmolConstants.CALLBACK_LOADSTRUCT:
      notifyFileLoaded(strInfo, (String) data[2], (String) data[3],
          (String) data[4]);
      break;
    case JmolConstants.CALLBACK_ANIMFRAME:
      int[] iData = (int[]) data[1];
      int file = iData[1];
      int model = iData[2];
      if (display.haveDisplay)
        display.status.setStatus(1, file + "." + model);
      break;
    case JmolConstants.CALLBACK_SCRIPT:
      int msWalltime = ((Integer) data[3]).intValue();
      if (msWalltime == 0) {
        if (data[2] != null && display.haveDisplay)
          display.status.setStatus(1, (String) data[2]);
      }
      break;
    case JmolConstants.CALLBACK_ECHO:
      sendConsoleEcho(strInfo);
      break;
    case JmolConstants.CALLBACK_MEASURE:
      String mystatus = (String) data[3];
      if (mystatus.indexOf("Picked") >= 0) 
        notifyAtomPicked(strInfo);
      else if (mystatus.indexOf("Completed") >= 0)
        sendConsoleEcho(strInfo.substring(strInfo.lastIndexOf(",") + 2, strInfo
            .length() - 1));
      if (mystatus.indexOf("Pending") < 0) {
        
        if (display.haveDisplay)
          display.measurementTable.updateTables();
      }
      break;
    case JmolConstants.CALLBACK_MESSAGE:
      sendConsoleMessage(data == null ? null : strInfo);
      break;
    case JmolConstants.CALLBACK_CLICK:
      
      
      if (display.haveDisplay)
        display.status
          .setStatus(1, "(" + data[1] + "," + data[2] + ") [" + data[3] + "]");
      break;
    case JmolConstants.CALLBACK_PICK:
      notifyAtomPicked(strInfo);
      break;
    case JmolConstants.CALLBACK_ERROR:
    case JmolConstants.CALLBACK_HOVER:
    case JmolConstants.CALLBACK_MINIMIZATION:
    case JmolConstants.CALLBACK_RESIZE:
    case JmolConstants.CALLBACK_SYNC:
      
      break;
    }
  }

  public void setCallbackFunction(String callbackType, String callbackFunction) {
    if (callbackType.equalsIgnoreCase("menu")) {
      jmol.setupNewFrame(viewer.getStateInfo());
      return;
    }
    if (callbackType.equalsIgnoreCase("language")) {
      Dialog.setupUIManager();
      if (jmol.webExport != null) {
        WebExport.saveHistory();
        WebExport.dispose();
        jmol.createWebExport();
      }
      jmol.setupNewFrame(viewer.getStateInfo());
      return;
    }
  }

  

  public String eval(String strEval) {
    sendConsoleMessage("javascript: " + strEval);
    return "# 'eval' is implemented only for the applet.";
  }

  
  public String createImage(String fileName, String type, Object text_or_bytes,
                            int quality) {
    return null;
  }

  private void notifyAtomPicked(String info) {
    JmolAppConsoleInterface appConsole = (JmolAppConsoleInterface) viewer
        .getProperty("DATA_API", "getAppConsole", null);
    if (appConsole != null) {
      appConsole.sendConsoleMessage(info);
      appConsole.sendConsoleMessage("\n");
    }
    if (display.haveDisplay)
      display.status.setStatus(1, info);
  }

  private void notifyFileLoaded(String fullPathName, String fileName,
                                String modelName, String errorMsg) {
    if (errorMsg != null) {
      return;
    }
    if (!display.haveDisplay)
      return;

    
    String title = "Jmol";
    if (modelName != null && fileName != null)
      title = fileName + " - " + modelName;
    else if (fileName != null)
      title = fileName;
    else if (modelName != null)
      title = modelName;
    jmol.notifyFileOpen(fullPathName, title);
  }

  private void sendConsoleEcho(String strEcho) {
    JmolAppConsoleInterface appConsole = (JmolAppConsoleInterface) viewer
        .getProperty("DATA_API", "getAppConsole", null);
    if (appConsole != null)
      appConsole.sendConsoleEcho(strEcho);
  }

  private void sendConsoleMessage(String strStatus) {
    JmolAppConsoleInterface appConsole = (JmolAppConsoleInterface) viewer
        .getProperty("DATA_API", "getAppConsole", null);
    if (appConsole != null)
      appConsole.sendConsoleMessage(strStatus);
  }

  public void showUrl(String url) {
    try {
      Class c = Class.forName("java.awt.Desktop");
      Method getDesktop = c.getMethod("getDesktop", new Class[] {});
      Object deskTop = getDesktop.invoke(null, new Class[] {});
      Method browse = c.getMethod("browse", new Class[] { URI.class });
      Object arguments[] = { new URI(url) };
      browse.invoke(deskTop, arguments);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      JmolAppConsoleInterface appConsole = (JmolAppConsoleInterface) viewer
          .getProperty("DATA_API", "getAppConsole", null);
      if (appConsole != null) {
        appConsole
            .sendConsoleMessage("Java 6 Desktop.browse() capability unavailable. Could not open "
                + url);
      } else {
        Logger
            .error("Java 6 Desktop.browse() capability unavailable. Could not open "
                + url);
      }
    }
  }

  
  public float[][] functionXY(String functionName, int nX, int nY) {
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    float[][] f = new float[nX][nY];
    
    for (int i = nX; --i >= 0;)
      for (int j = nY; --j >= 0;) {
        float x = i / 5f; 
        float y = j / 5f; 
        f[i][j] = (float) (x * x + y);
        if (Float.isNaN(f[i][j]))
          f[i][j] = -(float) Math.sqrt(-x * x - y);
        
        
        
        System.out.println(" functionXY " + i + " " + j + " " + f[i][j]);
      }

    return f; 
              
  }

  public float[][][] functionXYZ(String functionName, int nX, int nY, int nZ) {
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    nZ = Math.abs(nZ);
    float[][][] f = new float[nX][nY][nZ];
    for (int i = nX; --i >= 0;)
      for (int j = nY; --j >= 0;)
        for (int k = nZ; --k >= 0;) {
          float x = i / ((nX - 1) / 2f) - 1;
          float y = j / ((nY - 1) / 2f) - 1;
          float z = k / ((nZ - 1) / 2f) - 1;
          f[i][j][k] = x * x + y * y - z * z;
          
          
          
        }
    return f; 
              
  }

  public Hashtable getRegistryInfo() {
    return null;
  }

}
