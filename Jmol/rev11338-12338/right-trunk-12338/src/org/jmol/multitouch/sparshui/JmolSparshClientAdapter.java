
package org.jmol.multitouch.sparshui;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.jmol.api.Interface;
import org.jmol.api.JmolGestureServerInterface;
import org.jmol.api.JmolMultiTouchClient;
import org.jmol.multitouch.JmolMultiTouchClientAdapter;
import org.jmol.util.Logger;
import org.jmol.viewer.ActionManagerMT;
import org.jmol.viewer.Viewer;

import com.sparshui.client.SparshClient;
import com.sparshui.client.ClientServerConnection;
import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.NetworkConfiguration;
import com.sparshui.common.messages.events.DragEvent;
import com.sparshui.common.messages.events.RotateEvent;
import com.sparshui.common.messages.events.TouchEvent;
import com.sparshui.common.messages.events.ZoomEvent;

public class JmolSparshClientAdapter extends JmolMultiTouchClientAdapter implements SparshClient {

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

  private ClientServerConnection serverConnection;
  
  public JmolSparshClientAdapter() {
  }

  

  boolean doneHere;
  
  public void dispose() {
    
    System.out.println("JmolSparshClientAdapter -- dispose");
    doneHere = true;
    try {
      if (serverConnection != null) {
        serverConnection.close();
        serverConnection.interrupt();
      }
    } catch (Exception e) {
      
    }
    try {
      if (gestureServer != null) {
        gestureServer.dispose();
      }
    } catch (Exception e) {
      
    }
  }
  
  private JmolGestureServerInterface gestureServer;
  public void setMultiTouchClient(Viewer viewer, JmolMultiTouchClient client,
                              boolean isSimulation) {
    super.setMultiTouchClient(viewer, client, isSimulation);
    String err;
    gestureServer = (JmolGestureServerInterface) Interface
        .getInterface("com.sparshui.server.GestureServer");
    gestureServer.startGestureServer();
    if (true || isSimulation) {
      Logger.info("JmolSparshClientAdapter skipping driver startup");
    } else {
      try {
        String driver = (new File("JmolMultiTouchDriver.exe")).getAbsolutePath();
        Logger.info("JmolSparshClientAdapter starting " + driver);
        Process p = Runtime.getRuntime().exec(driver);
        Logger.info("JmolSparshClientAdapter process " + p);
        
          
        Thread.sleep(2000);
        
        
        
        Logger.info("JmolSparshClientAdapter successful starting driver process");
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    int port = NetworkConfiguration.CLIENT_PORT;
    try {
      serverConnection = new ClientServerConnection("127.0.0.1", this);
      Logger.info("SparshUI connection established at 127.0.0.1 port " + port);
      return;
    } catch (UnknownHostException e) {
      err = e.getMessage();
    } catch (IOException e) {
      err = e.getMessage();
    }
    actionManager = null;
    Logger.error("Cannot create SparshUI connection at 127.0.0.1 port " + port
        + ": " + err);
  }
  
  
  
  public List getAllowedGestures(int groupID) {
    return (actionManager == null ? null : actionManager.getAllowedGestures(groupID));
  }

  public int getGroupID(Location location) {
    if (actionManager == null)
      return 0;
    fixXY(location.getX(), location.getY(), true);
    return (actionManager == null ? 0 : actionManager.getGroupID(xyTemp.x, xyTemp.y));
  }

  
  
  public void processEvent(int groupID, Event event) {
    if (actionManager == null)
      return;
    if (event == null) {
      
      int errorType = groupID;
      switch (errorType) {
      case ActionManagerMT.SERVICE_LOST:
        System.out.println("JmolSparshAdapter service lost event...disposing ");
        dispose();
        break;
      case ActionManagerMT.DRIVER_NONE:
        break;
      }
      actionManager.processEvent(-1, errorType, -1, -1, null, -1);
      return;
    }
    int id = 0;
    int iData = 0;
    int type = event.getEventType();
    long time = 0;
    switch (type) {
    case ActionManagerMT.TOUCH_EVENT:
      id = ((TouchEvent) event).getTouchID();
      fixXY(((TouchEvent) event).getX(), ((TouchEvent) event).getY(), true);
      iData = ((TouchEvent) event).getState();
      time = ((TouchEvent) event).getTime();
      break;
    case ActionManagerMT.DRAG_EVENT:
      fixXY((((DragEvent) event).getDx()), ((DragEvent) event).getDy(), false);
      iData = ((DragEvent) event).getNPoints();
      time = ((DragEvent) event).getTime();
      break;
    case ActionManagerMT.ZOOM_EVENT:
      fixXY(((ZoomEvent) event).getX(), ((ZoomEvent) event).getY(), true);
      ptTemp.z = ((ZoomEvent) event).getScale();
      time = ((ZoomEvent) event).getTime();
      break;
    case ActionManagerMT.ROTATE_EVENT:
      fixXY((((RotateEvent) event).getX()), ((RotateEvent) event).getY(), true);
      ptTemp.z = ((RotateEvent) event).getRotation();
      time = ((RotateEvent) event).getTime();
      break;
    }
    actionManager.processEvent(groupID, type, id, iData, ptTemp, time);
  }
} 
