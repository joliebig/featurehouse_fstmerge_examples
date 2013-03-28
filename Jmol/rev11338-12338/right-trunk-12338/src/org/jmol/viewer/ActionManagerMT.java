
package org.jmol.viewer;

import java.util.List;
import java.util.Vector;

import javax.vecmath.Point3f;

import org.jmol.api.Interface;
import org.jmol.api.JmolMultiTouchAdapter;
import org.jmol.api.JmolMultiTouchClient;
import org.jmol.api.JmolTouchSimulatorInterface;
import org.jmol.util.Logger;
import org.jmol.viewer.binding.Binding;

public class ActionManagerMT extends ActionManager implements JmolMultiTouchClient {

  

  private JmolMultiTouchAdapter adapter;
  private JmolTouchSimulatorInterface simulator;
  private int groupID;
  private int simulationPhase;
  private boolean resetNeeded = true;
  private long lastLogTime = 0;
  
  
  ActionManagerMT(Viewer viewer, String commandOptions) {
    super(viewer);
    groupID = ((int) (Math.random() * 0xFFFFFF)) << 4;
    
    boolean isSparsh = commandOptions.contains("-multitouch-sparshui");
    boolean isSimulated = commandOptions.contains("-multitouch-sparshui-simulated");
    boolean isJNI = commandOptions.contains("-multitouch-jni");
    String className = (isSparsh ? "multitouch.sparshui.JmolSparshClientAdapter" : "multitouch.jni.JmolJniClientAdapter");
      adapter = (JmolMultiTouchAdapter) Interface
    .getOptionInterface(className);
    Logger.info("ActionManagerMT SparshUI groupID=" + groupID);
    if (isSparsh) {
      startSparshUIService(isSimulated);
    } else if (isJNI) {
      adapter.setMultiTouchClient(viewer, this, false);
    }
    setBinding(binding);
    xyRange = 10; 
  }

  private void startSparshUIService(boolean isSimulated) {
    haveMultiTouchInput = false;
    if (adapter == null)
      return;
    if (simulator != null) { 
      simulator.dispose();
      simulator = null;
    }
    if (isSimulated)
      Logger.error("ActionManagerMT -- for now just using touch simulation.\nPress CTRL-LEFT and then draw two traces on the window.");    

    adapter.setMultiTouchClient(viewer, this, isSimulated);
    if (isSimulated) {
      simulator = (JmolTouchSimulatorInterface) Interface
      .getInterface("com.sparshui.inputdevice.JmolTouchSimulator");
      if (simulator != null) {
        Logger.info("ActionManagerMT simulating SparshUI");
        simulator.startSimulator(viewer.getDisplay());
      }
    }
  }

  protected void setBinding(Binding newBinding) {
    super.setBinding(newBinding);
    binding.unbindMouseAction(Binding.RIGHT);
    if (simulator != null && binding != null) {
      binding.unbindJmolAction(ACTION_center);
      binding.unbind(Binding.CTRL + Binding.LEFT + Binding.SINGLE_CLICK, null);
      binding.bind(Binding.CTRL + Binding.LEFT + Binding.SINGLE_CLICK, ACTION_multiTouchSimulation);
    }
  }

  void clear() {
    
    simulationPhase = 0;
    resetNeeded = true;
    super.clear();
  }
  
  boolean doneHere;
  
  void dispose() {
    System.out.println("ActionManagerMT -- dispose");
    
    doneHere = true;
    adapter.dispose();
    if (simulator != null)
      simulator.dispose();
    super.dispose();
  }

  
  
  
  public final static int DRAG_GESTURE = 0;
  public final static int MULTI_POINT_DRAG_GESTURE = 1;
  public final static int ROTATE_GESTURE = 2;
  public final static int SPIN_GESTURE = 3;
  public final static int TOUCH_GESTURE = 4;
  public final static int ZOOM_GESTURE = 5;
  public final static int DBLCLK_GESTURE = 6;
  public final static int FLICK_GESTURE = 7;
  public final static int RELATIVE_DRAG_GESTURE = 8;
  public final static int INVALID_GESTURE = 9;
  
  
  
  private final static String TWO_POINT_GESTURE = "org.jmol.multitouch.sparshui.TwoPointGesture";
  private final static String SINGLE_POINT_GESTURE = "org.jmol.multitouch.sparshui.SinglePointGesture";

  
  
  
  public static final int DRIVER_NONE = -2;
  public static final int SERVICE_LOST = -1;
  public static final int DRAG_EVENT = 0;
  public static final int ROTATE_EVENT = 1;
  public static final int SPIN_EVENT = 2;
  public final static int TOUCH_EVENT = 3;
  public final static int ZOOM_EVENT = 4;
  public final static int DBLCLK_EVENT = 5;
  public final static int FLICK_EVENT = 6;
  public final static int RELATIVE_DRAG_EVENT = 7;
  public static final int CLICK_EVENT = 8;

  private final static String[] eventNames = new String[] {
    "drag", "rotate", "spin", "touch", "zoom",
    "double-click", "flick", "relative-drag", "click"
  };

  
  
  public final static int BIRTH = 0;
  public final static int DEATH = 1;
  public final static int MOVE = 2;
  public final static int CLICK = 3;

  
  private static String getEventName(int i) {
    try {
      return eventNames[i];
    } catch (Exception e) {
      return "?";
    }
  }
  
  public List getAllowedGestures(int groupID) {
    if (groupID != this.groupID || !viewer.allowMultiTouch())
      return null;
    Vector list = new Vector();
    
    
    
    
    list.add(TWO_POINT_GESTURE);
    if (simulator == null)
      list.add(SINGLE_POINT_GESTURE);
    
    
    
    return list;
  }

  public int getGroupID(int x, int y) {

    int gid = (!viewer.getDisplay().hasFocus()  
        || x < 0 || y < 0 || x >= viewer.getScreenWidth()
        || y >= viewer.getScreenHeight() ? 0 : groupID);
    if (resetNeeded) {
      gid |= 0x10000000;
      resetNeeded = false;
    }
    return gid;
  }

  boolean mouseDown;
  
  public void processEvent(int groupID, int eventType, int touchID, int iData,
                           Point3f pt, long time) {
    if (true || Logger.debugging)
      Logger.info(this + " time=" + time + " groupID=" + groupID + " "
          + Integer.toHexString(groupID) + " eventType=" + eventType + "("
          + getEventName(eventType) + ") iData=" + iData + " pt=" + pt);
    switch (eventType) {
    case DRIVER_NONE:
      haveMultiTouchInput = false;
      Logger.error("SparshUI reports no driver present");
      viewer.log("SparshUI reports no driver present -- setting haveMultiTouchInput FALSE");
      break;
    case SERVICE_LOST:
      viewer.log("Jmol SparshUI client reports service lost -- " + (doneHere ? "not " : "") + " restarting");
      if (!doneHere)
        startSparshUIService(simulator != null);  
      break;
    case TOUCH_EVENT:
      haveMultiTouchInput = true;
      if (touchID == Integer.MAX_VALUE) {
        mouseDown = false;
        clearMouseInfo();
        break;
      }
      switch(iData) {
      case BIRTH:
        mouseDown = true;
        super.mousePressed(time, (int) pt.x, (int) pt.y, Binding.LEFT);
        break;
      case MOVE:
        if (mouseDown)
          super.mouseDragged(time, (int) pt.x, (int) pt.y, Binding.LEFT);
        else
          super.mouseMoved(time, (int) pt.x, (int) pt.y, Binding.LEFT);
        break;
      case DEATH:
        mouseDown = false;
        super.mouseReleased(time, (int) pt.x, (int) pt.y, Binding.LEFT);
        break;
      case CLICK:
        
        super.mouseClicked(time, (int) pt.x, (int) pt.y, Binding.LEFT, 1);
        break;
      }
      break;
    case ZOOM_EVENT:
      float scale = pt.z;
      if (scale == -1 || scale == 1) {
        zoomByFactor((int)scale, Integer.MAX_VALUE, Integer.MAX_VALUE);
        logEvent("Zoom", pt);
      }
      break;
    case ROTATE_EVENT:
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.rotateZBy((int) pt.z, Integer.MAX_VALUE, Integer.MAX_VALUE);
      logEvent("Rotate", pt);
      break;
    case DRAG_EVENT:
      if (iData == 2) {
        
        checkMotion(Viewer.CURSOR_MOVE);
        viewer.translateXYBy((int) pt.x, (int) pt.y);
        logEvent("Drag", pt);
      }
      break;
    }
  }

  private void logEvent(String type, Point3f pt) {
    if (!viewer.getLogGestures())
      return;
    long time = System.currentTimeMillis(); 
    
    if (time - lastLogTime > 10000) {
      viewer.log("NOW multitouch " + type + " pt= " + pt);
      lastLogTime = time;
    }
  }

  void mouseEntered(long time, int x, int y) {
    super.mouseEntered(time, x, y);    
  }
  
  void mouseExited(long time, int x, int y) {
    super.mouseExited(time, x, y);    
  }
  
  void mouseClicked(long time, int x, int y, int mods, int count) {
    if (haveMultiTouchInput)
      return;
    super.mouseClicked(time, x, y, mods, count);
  }

  void mouseMoved(long time, int x, int y, int mods) {
    if (haveMultiTouchInput)
      return;
    adapter.mouseMoved(x, y);
    super.mouseMoved(time, x, y, mods);
  }

  void mouseWheel(long time, int rotation, int mods) {
    if (haveMultiTouchInput)
      return;
    super.mouseWheel(time, rotation, mods);
  }

  void mousePressed(long time, int x, int y, int mods) {
    if (simulator != null) {
      int action = Binding.getMouseAction(1, mods);
      if (binding.isBound(action, ACTION_multiTouchSimulation)) {
        setCurrent(0, x, y, mods);
        setFocus();
        if (simulationPhase++ == 0)
          simulator.startRecording();
        simulator.mousePressed(time, x, y);
        return;
      }
      simulationPhase = 0;
    }
    if (haveMultiTouchInput)
      return;
    super.mousePressed(time, x, y, mods);
  }

  void mouseDragged(long time, int x, int y, int mods) {
    if (simulator != null && simulationPhase > 0) {
      setCurrent(time, x, y, mods);
      simulator.mouseDragged(time, x, y);
      return;
    }
    if (haveMultiTouchInput)
      return;
    super.mouseDragged(time, x, y, mods);
  }

  void mouseReleased(long time, int x, int y, int mods) {
    if (simulator != null && simulationPhase > 0) {
      setCurrent(time, x, y, mods);
      viewer.spinXYBy(0, 0, 0);
      simulator.mouseReleased(time, x, y);
      if (simulationPhase >= 2) {
        
        resetNeeded = true;
        simulator.endRecording();
        simulationPhase = 0;
      }
      return;
    }
    if (haveMultiTouchInput)
      return;
    super.mouseReleased(time, x, y, mods);
  }

  protected float getExitRate() {
    long dt = dragGesture.getTimeDifference(2);
    return (dt > (MININUM_GESTURE_DELAY_MILLISECONDS << 3) ? 0 :
        dragGesture.getSpeedPixelsPerMillisecond(2, 1));
  }

} 
