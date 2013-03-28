
package org.jmol.multitouch.sparshui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.vecmath.Point3f;

import org.jmol.api.Interface;
import org.jmol.api.JmolGestureServerInterface;
import org.jmol.api.JmolSparshAdapter;
import org.jmol.api.JmolSparshClient;
import org.jmol.util.Logger;
import org.jmol.viewer.ActionManagerMT;

import com.sparshui.client.Client;
import com.sparshui.client.ServerConnection;
import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.NetworkConfiguration;
import com.sparshui.common.messages.events.DblClkEvent;
import com.sparshui.common.messages.events.DragEvent;
import com.sparshui.common.messages.events.FlickEvent;
import com.sparshui.common.messages.events.RelativeDragEvent;
import com.sparshui.common.messages.events.RotateEvent;
import com.sparshui.common.messages.events.SpinEvent;
import com.sparshui.common.messages.events.TouchEvent;
import com.sparshui.common.messages.events.ZoomEvent;

public class JmolSparshClientAdapter implements Client, JmolSparshAdapter {

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

  private JmolSparshClient client;
  private ServerConnection serverConnection;
  private Component display;
  
  public JmolSparshClientAdapter() {
  }

  

  public void dispose() {
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
  public void setSparshClient(Component display, JmolSparshClient client) {
    String err;
    this.display = display;
    gestureServer = (JmolGestureServerInterface) Interface
    .getInterface("com.sparshui.server.GestureServer");
    gestureServer.startGestureServer();    
    int port = NetworkConfiguration.PORT;
    try {
      this.client = client; 
      serverConnection = new ServerConnection("127.0.0.1", this);
      Logger.info("SparshUI connection established at 127.0.0.1 port " + port);
      return;
    } catch (UnknownHostException e) {
      err = e.getMessage();
    } catch (IOException e) {
      err = e.getMessage();
    }  
    this.client = null;
    Logger.error("Cannot create SparshUI connection at 127.0.0.1 port " 
        + port + ": " + err);
  }
  
  
  
  public List getAllowedGestures(int groupID) {
    return (client == null ? null : client.getAllowedGestures(groupID));
  }

  public int getGroupID(Location location) {
    if (client == null)
      return 0;
    fixXY(location.getX(), location.getY());
    return (client == null ? 0 : client.getGroupID(xyTemp.x, xyTemp.y));
  }

  int x0, y0;
  static int screenWidth, screenHeight;
  static {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    screenWidth = screen.width;
    screenHeight = screen.height;
    if (Logger.debugging)
      Logger.info("screen resolution: " + screenWidth + " x " + screenHeight);
  }
  
  
  
  public void processEvent(int groupID, Event event) {
    if (client == null)
      return;
    if (event == null) {
      dispose();
      return;
    }
    int id = 0;
    int iData = 0;
    int type = event.getEventType();
    long time = 0;
    switch (type) {
    case ActionManagerMT.DRAG_EVENT:
      fixXY((((DragEvent) event).getAbsX()), ((DragEvent) event).getAbsY());
      time = ((DragEvent) event).getTime();
      iData = ((DragEvent) event).getNPoints();
      break;
    case ActionManagerMT.RELATIVE_DRAG_EVENT:
      fixXY(((RelativeDragEvent) event).getChangeInX(),
          ((RelativeDragEvent) event).getChangeInY());
      break;
    case ActionManagerMT.ROTATE_EVENT:
      fixXY((((RotateEvent) event).getCenter().getX()), ((RotateEvent) event).getCenter().getY());
      ptTemp.z = ((RotateEvent) event).getRotation();
      break;
    case ActionManagerMT.SPIN_EVENT:
      ptTemp.set(((SpinEvent) event).getRotationX(),
          ((SpinEvent) event).getRotationY(),
          ((SpinEvent) event).getRotationZ());
      break;
    case ActionManagerMT.TOUCH_EVENT:
      id = ((TouchEvent) event).getTouchID();
      fixXY(((TouchEvent) event).getX(), ((TouchEvent) event).getY());
      iData = ((TouchEvent) event).getState();
      time = ((TouchEvent) event).getTime();
      break;
    case ActionManagerMT.ZOOM_EVENT:
      fixXY(((ZoomEvent) event).getCenter().getX(), ((ZoomEvent) event).getCenter().getY());
      ptTemp.z = ((ZoomEvent) event).getScale();
      break;
    case ActionManagerMT.DBLCLK_EVENT:
      id = ((DblClkEvent) event).getTouchID();
      fixXY(((DblClkEvent) event).getX(), ((DblClkEvent) event).getY());
      iData = ((DblClkEvent) event).getState();
      time = ((DblClkEvent) event).getTime();
      break;
    case ActionManagerMT.FLICK_EVENT:
      fixXY(((FlickEvent) event).getXdirection(), ((FlickEvent) event).getYdirection());
      ptTemp.z = ((FlickEvent) event).getSpeedLevel();
      break;
    }
    client.processEvent(groupID, type, id, iData, ptTemp, time);
  }

  public void mouseMoved(int x, int y) {
    
    
  }

  Point xyTemp = new Point();
  Point3f ptTemp = new Point3f();
  private void fixXY(float x, float y) {
    xyTemp.setLocation(x * screenWidth, y * screenHeight);
    SwingUtilities.convertPointFromScreen(xyTemp, display);
    ptTemp.set(xyTemp.x, xyTemp.y, Float.NaN);
  }
} 
