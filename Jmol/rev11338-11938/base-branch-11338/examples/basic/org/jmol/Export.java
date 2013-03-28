package org.jmol;


import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.jmol.api.JmolStatusListener;
import org.jmol.api.JmolViewer;
import org.jmol.viewer.JmolConstants;
import org.openscience.jmol.app.Jmol;

public class Export {
  
  public static void main(String args[]) {
    
    JFrame monitorFrame = new JFrame();
    JTextField monitor = new JTextField("Please load a molecule and click on atoms");
    monitorFrame.getContentPane().add(monitor);
    monitorFrame.pack();

    
    JFrame baseframe = new JFrame();
    Jmol jmol = Jmol.getJmol(baseframe,300, 300, "");
    JmolViewer viewer = jmol.viewer;

    
    
    MyStatusListener myStatusListener = new MyStatusListener();
    myStatusListener.monitor = monitor;
    viewer.setJmolStatusListener(myStatusListener);

    
    monitorFrame.setVisible(true);
  } 
} 

class MyStatusListener implements JmolStatusListener {
  
  public JTextField monitor;
  
  public boolean notifyEnabled(int type) {
    
    
    switch (type) {
    case JmolConstants.CALLBACK_ANIMFRAME:
    case JmolConstants.CALLBACK_ECHO:
    case JmolConstants.CALLBACK_ERROR:
    case JmolConstants.CALLBACK_LOADSTRUCT:
    case JmolConstants.CALLBACK_MEASURE:
    case JmolConstants.CALLBACK_MESSAGE:
    case JmolConstants.CALLBACK_PICK:
    case JmolConstants.CALLBACK_SYNC:
    case JmolConstants.CALLBACK_SCRIPT:
    case JmolConstants.CALLBACK_HOVER:
    case JmolConstants.CALLBACK_MINIMIZATION:
    case JmolConstants.CALLBACK_RESIZE:
    }
    return false;
  }
  
  public void notifyCallback(int type, Object[] data) {
    
    
    
    
    
    
    
    
    
    
    
    
    
    switch (type) {
    case JmolConstants.CALLBACK_ANIMFRAME:
      break;
    case JmolConstants.CALLBACK_ECHO:
      sendConsoleEcho((String) data[1]);
      break;
    case JmolConstants.CALLBACK_ERROR:
      break;
    case JmolConstants.CALLBACK_HOVER:
      break;
    case JmolConstants.CALLBACK_LOADSTRUCT:
      String strInfo = (String) data[1];
      System.out.println(strInfo);
      monitor.setText(strInfo);
      break;
    case JmolConstants.CALLBACK_MEASURE:
      break;
    case JmolConstants.CALLBACK_MESSAGE:
      sendConsoleMessage(data == null ? null : (String) data[1]);
      break;
    case JmolConstants.CALLBACK_MINIMIZATION:
      break;
    case JmolConstants.CALLBACK_PICK:
      
      notifyAtomPicked(((Integer) data[2]).intValue(), (String) data[1]);
      break;
    case JmolConstants.CALLBACK_RESIZE:
      break;
    case JmolConstants.CALLBACK_SCRIPT:
      break;
    case JmolConstants.CALLBACK_SYNC:
      break;
    }
  }  

  private void notifyAtomPicked(int atomIndex, String strInfo) {
    System.out.println(strInfo);
    monitor.setText(strInfo);
  }


  
  public void showUrl(String url) {
    System.out.println(url);
  }

  
  public void createImage(String file, String type, int quality) {
    
  }

  
  public float[][] functionXY(String functionName, int nx, int ny) {
    return null;
  }

  
  public float[][][] functionXYZ(String functionName, int nx, int ny, int nz) {
    return null;
  }

  
  private void sendConsoleEcho(String strEcho) {
    
  }

  
  private void sendConsoleMessage(String strStatus) {
    
  }

  
  public void setCallbackFunction(String callbackType, String callbackFunction) {
    
  }

  
  public String eval(String strEval) {
    return null;
  }


  public Hashtable getRegistryInfo() {
    return null;
  }

  public String createImage(String file, String type, Object text_or_bytes, int quality) {
    return null;
  }

  public String dialogAsk(String type, String data) {
    return null;
  }
}
