
package org.jmol.viewer;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;

import org.jmol.i18n.GT;
import org.jmol.modelset.MeasurementPending;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Point3fi;
import org.jmol.util.TextFormat;
import org.jmol.viewer.binding.DragBinding;
import org.jmol.viewer.binding.Binding;
import org.jmol.viewer.binding.PfaatBinding;
import org.jmol.viewer.binding.RasmolBinding;
import org.jmol.viewer.binding.JmolBinding;

public class ActionManager {

  private final static String[] actionInfo = new String[] {
    
    GT._("rotate"),
    GT._("zoom"),
    GT._("rotate Z"),
    GT._("rotate Z (horizontal motion of mouse) or zoom (vertical motion of mouse)"),
    GT._("translate"),
    
    GT._("center"),
    GT._("zoom (along right edge of window)"),
    GT._("move selected atoms (requires {0})", "SET DRAGSELECTED"), 
    GT._("rotate selected atoms (requires {0})", "SET DRAGSELECTED"),
    GT._("move label (requires {0})", "SET PICKING LABEL"),
    
    GT._("move specific DRAW point (requires {0})", "SET PICKING DRAW"),
    GT._("move whole DRAW object (requires {0})", "SET PICKING DRAW"),
    GT._("spin model (swipe and release button and stop motion simultaneously)"),
    GT._("click on two points to spin around axis clockwise (requires SET PICKING SPIN)"),
    GT._("click on two points to spin around axis counterclockwise (requires SET PICKING SPIN)"),
    
    GT._("adjust slab (front plane; requires {0})", "SLAB ON"),
    GT._("adjust depth (back plane; requires {0})", "SLAB ON"),
    GT._("move slab/depth window (both planes; requires {0})", "SLAB ON"),
    GT._("pop up the full context menu"),
    GT._("pop up recent context menu (click on Jmol frank)"),
    
    GT._("translate navigation point (requires {0} and {1})", new String[] {"SET NAVIGATIONMODE", "SET PICKING NAVIGATE"}),
    GT._("pick an atom"),
    GT._("pick a DRAW point (for measurements)"),
    GT._("pick a label to toggle it hidden/displayed (requires {0})", "SET PICKING LABEL"),
    GT._("pick an atom to include it in a measurement (after starting a measurement or after {0})", "SET PICKING DISTANCE/ANGLE/TORSION"),
    
    GT._("pick an atom to initiate or conclude a measurement"),
    GT._("pick an ISOSURFACE point"),
    GT._("pick a point or atom to navigate to (requires SET NAVIGATIONMODE; undocumented)"),
    GT._("select an atom (requires {0})", "SET PICKINGSTYLE EXTENDEDSELECT"),
    GT._("select NONE (requires {0})", "SET PICKINGSTYLE EXTENDEDSELECT"),
    
    GT._("toggle selection (requires {0} or {1})", new String[] {
        "SET PICKINGSTYLE DRAG/EXTENDEDSELECT/RASMOL"}),
    GT._("click and drag to unselect this group of atoms (requires {0})", "DRAG/EXTENDEDSELECT"),
    GT._("click and drag to add this group of atoms to the set of selected atoms (requires {0})", "SET PICKINGSTYLE DRAG/EXTENDEDSELECT"),
    GT._("if all are selected, unselect all, otherwise add this group of atoms to the set of selected atoms (requires {0})", "SET PICKINGSTYLE DRAG"),    

    GT._("reset (when clicked off the model)"),
    
    GT._("simulate multi-touch using the mouse)"),
  };

  public String getBindingInfo(String qualifiers) {
    return binding.getBindingInfo(actionInfo, qualifiers);  
  }

  public Hashtable getMouseInfo() {
    Hashtable info = new Hashtable();
    Vector vb = new Vector();
    Enumeration e = binding.getBindings().elements();
    while (e.hasMoreElements()) {
      Object obj = e.nextElement();
      if (obj instanceof Boolean)
        continue;
      if (obj instanceof int[]) {
        int[] binding = (int[]) obj;
        obj = new String[] { Binding.getMouseActionName(binding[0], false),
            getActionName(binding[1]) };
      }
      vb.add(obj);
    }
    info.put("bindings", vb);
    info.put("bindingName", binding.getName());
    info.put("actionNames", actionNames);
    info.put("actionInfo", actionInfo);
    info.put("bindingInfo", TextFormat.split(getBindingInfo(null), '\n'));
    return info;
  }

  private final static String[] actionNames = new String[] {
    
    "_rotate",
    "_wheelZoom",
    "_rotateZ",
    "_rotateZorZoom",
    "_translate",
    
    "_center",
    "_slideZoom",
    "_dragSelected",
    "_rotateSelected",
    "_dragLabel",
    
    "_dragDrawPoint",
    "_dragDrawObject",
    "_swipe",
    "_spinDrawObjectCW",
    "_spinDrawObjectCCW",
    
    "_slab",
    "_depth",
    "_slabAndDepth",
    "_popupMenu",
    "_clickFrank",
    
    "_navTranslate",
    "_pickAtom",
    "_pickPoint",
    "_pickLabel",
    "_pickMeasure",
    
    "_setMeasure",
    "_pickIsosurface",
    "_pickNavigate",
    "_select",
    "_selectNone",
    
    "_selectToggle",
    "_selectAndNot",
    "_selectOr",
    "_selectToggleOr",
    "_reset",
    
    "_multiTouchSimulation",
  };
  public static String getActionName(int i) {
    return (i < actionNames.length ? actionNames[i] : null);
  }
  
  public static int getActionFromName(String name) {
    for (int i = 0; i < actionNames.length; i++)
      if (actionNames[i].equalsIgnoreCase(name))
        return i;
    return -1;
  }
  
  public final static int ACTION_rotate = 0;
  public final static int ACTION_wheelZoom = 1;
  public final static int ACTION_rotateZ = 2;
  public final static int ACTION_rotateZorZoom = 3;
  public final static int ACTION_translate = 4;
  
  public final static int ACTION_center = 5;
  public final static int ACTION_slideZoom = 6;
  public final static int ACTION_dragSelected = 7;
  public final static int ACTION_rotateSelected = 8;
  public final static int ACTION_dragLabel = 9;
  public final static int ACTION_dragDrawPoint = 10;
  public final static int ACTION_dragDrawObject = 11;
  public final static int ACTION_swipe = 12;

  public final static int ACTION_spinDrawObjectCW = 13;
  public final static int ACTION_spinDrawObjectCCW = 14;

  public final static int ACTION_slab = 15;
  public final static int ACTION_depth = 16;
  public final static int ACTION_slabAndDepth = 17;

  public final static int ACTION_popupMenu = 18;
  public final static int ACTION_clickFrank = 19;
  public final static int ACTION_navTranslate = 20;

  public final static int ACTION_pickAtom = 21;
  public final static int ACTION_pickPoint = 22;
  public final static int ACTION_pickLabel = 23;
  public final static int ACTION_pickMeasure = 24;
  public final static int ACTION_setMeasure = 25;
  public static final int ACTION_pickIsosurface = 26;
  public static final int ACTION_pickNavigate = 27;
  
  public static final int ACTION_select = 28;
  public static final int ACTION_selectNone = 29;
  public static final int ACTION_selectToggle = 30;  
  public static final int ACTION_selectAndNot = 31;
  public static final int ACTION_selectOr = 32;
  public static final int ACTION_selectToggleExtended = 33;
  
  public final static int ACTION_reset = 34;
  public final static int ACTION_multiTouchSimulation = 35;
  public final static int ACTION_count = 36;
  
  static {
    if (actionNames.length != ACTION_count)
      System.out.println("ERROR IN ActionManager: actionNames length?");
    if (actionInfo.length != ACTION_count)
      System.out.println("ERROR IN ActionManager: actionInfo length?");
  }

  final static float ZOOM_FACTOR = 1.02f;
  
  private final static long MAX_DOUBLE_CLICK_MILLIS = 700;
  private static final long MININUM_GESTURE_DELAY_MILLISECONDS = 5;
  private static final int SLIDE_ZOOM_X_PERCENT = 98;
 
  protected Viewer viewer;
  
  Binding binding;
  Binding jmolBinding;
  Binding pfaatBinding;
  Binding dragBinding;
  Binding rasmolBinding;

  ActionManager aman;
  ActionManager(Viewer viewer) {
    this.viewer = viewer;
    aman = this;
    setBinding(jmolBinding = new JmolBinding());
  }

  boolean isBound(int gesture, int action) {
    return binding.isBound(gesture, action);
  }

  void bindAction(String desc, String name, Point3f range1,
                         Point3f range2) {
    int jmolAction = getActionFromName(name);
    int mouseAction = Binding.getMouseAction(desc);
    if (jmolAction >= 0) {
      binding.bind(mouseAction, jmolAction);
    } else {
      binding.bind(mouseAction, name);
    }
  }

  protected void clearBindings() {
    setBinding(jmolBinding = new JmolBinding());
    pfaatBinding = null;
    dragBinding = null;
    rasmolBinding = null; 
  }
  
  void unbindAction(String desc, String name) {
    if (desc == null && name == null) {
      clearBindings();
      return;
    }
    int jmolAction = getActionFromName(name);
    int mouseAction = Binding.getMouseAction(desc);
    if (jmolAction >= 0)
      binding.unbind(mouseAction, jmolAction);
    else
      binding.unbind(mouseAction, name);
    if (name == null)
      binding.unbindUserAction(desc);    
  }

  protected Thread hoverWatcherThread;

  protected class Mouse {
    protected int x = -1000;
    protected int y = -1000;
    protected int modifiers = 0;
    protected long time = -1;
    
    protected void set(long time, int x, int y, int modifiers) {
      this.time = time;
      this.x = x;
      this.y = y;
      this.modifiers = modifiers;
    }

    protected void setCurrent() {
      time = current.time;
      x = current.x;
      y = current.y;
      modifiers = current.modifiers;
    }

    public boolean check(int x, int y, int modifiers, long time, long delayMax) {
      return (this.x == x && this.y == y && this.modifiers == modifiers
        && (time - this.time) < delayMax);
    }
  }
  
  protected final Mouse current = new Mouse();
  protected final Mouse moved = new Mouse();
  private final Mouse clicked = new Mouse();
  private final Mouse pressed = new Mouse();
  private final Mouse dragged = new Mouse();

  protected void setCurrent(long time, int x, int y, int mods) {
    hoverOff();
    current.set(time, x, y, mods);
  }
  
  int getCurrentX() {
    return current.x;
  }
  int getCurrentY() {
    return current.y;
  }

  protected int pressedCount;
  private int pressedAtomIndex;
  
  private int clickedCount;

  private boolean drawMode = false;
  private boolean labelMode = false;
  private boolean dragSelectedMode = false;
  private boolean measuresEnabled = true;
  private MeasurementPending measurementPending;

  private boolean hoverActive = false;

  private boolean rubberbandSelectionMode = false;
  private final Rectangle rectRubber = new Rectangle();

  boolean isAltKeyReleased = true;  
  private boolean keyProcessing;

  void dispose() {
    clear();
  }

  void clear() {
    startHoverWatcher(false);
    clearTimeouts();
    pickingMode = JmolConstants.PICKING_IDENT;
    drawHover = false;
  }

  synchronized void startHoverWatcher(boolean isStart) {
    if (viewer.isPreviewOnly())
      return;
    try {
      if (isStart) {
        if (hoverWatcherThread != null)
          return;
        current.time = -1;
        hoverWatcherThread = new Thread(new HoverWatcher());
        hoverWatcherThread.setName("HoverWatcher");
        hoverWatcherThread.start();
      } else {
        if (hoverWatcherThread == null)
          return;
        current.time = -1;
        hoverWatcherThread.interrupt();
        hoverWatcherThread = null;
      }
    } catch (Exception e) {
      
    }
  }

  void setModeMouse(int modeMouse) {
    if (modeMouse == JmolConstants.MOUSE_NONE) {
      startHoverWatcher(false);
    }
  }

  
  void keyPressed(KeyEvent ke) {
    if (keyProcessing)
      return;
    keyProcessing = true;
    int i = ke.getKeyCode();
    switch(i) {
    case KeyEvent.VK_ALT:
      if (dragSelectedMode && isAltKeyReleased)
        viewer.moveSelected(Integer.MIN_VALUE, 0, 0, 0, false);
      isAltKeyReleased = false;
      moved.modifiers |= Binding.ALT;
      break;
    case KeyEvent.VK_SHIFT:
      moved.modifiers |= Binding.SHIFT;
      break;
    case KeyEvent.VK_CONTROL:
      moved.modifiers |= Binding.CTRL;
    }
    int action = Binding.LEFT+Binding.SINGLE_CLICK+moved.modifiers;
    if(!binding.isUserAction(action))
      checkMotionRotateZoom(action, current.x, 0, 0, false);
    if (viewer.getNavigationMode()) {
      int m = ke.getModifiers();
      
      
      
      switch (i) {
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_SPACE:
      case KeyEvent.VK_PERIOD:
        viewer.navigate(i, m);
        break;
      }
    }
    keyProcessing = false;
  }

  public void keyReleased(KeyEvent ke) {
    int i = ke.getKeyCode();
    switch(i) {
    case KeyEvent.VK_ALT:
      if (dragSelectedMode)
        viewer.moveSelected(Integer.MAX_VALUE, 0, 0, 0, false);
      isAltKeyReleased = true;
      moved.modifiers &= ~Binding.ALT;
      break;
    case KeyEvent.VK_SHIFT:
      moved.modifiers &= ~Binding.SHIFT;
      break;
    case KeyEvent.VK_CONTROL:
      moved.modifiers &= ~Binding.CTRL;
    }
    if (moved.modifiers == 0)
      viewer.setCursor(Viewer.CURSOR_DEFAULT);
    if (!viewer.getNavigationMode())
      return;
    
      
    switch (i) {
    case KeyEvent.VK_UP:
    case KeyEvent.VK_DOWN:
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_RIGHT:
      viewer.navigate(0, 0);
      break;
    }
  }

  void mouseEntered(long time, int x, int y) {
    setCurrent(time, x, y, 0);
  }

  void mouseExited(long time, int x, int y) {
    setCurrent(time, x, y, 0);
    exitMeasurementMode();
  }

  void setMouseMode() {
    drawMode = labelMode = false;
    dragSelectedMode = viewer.getBooleanProperty("dragSelected");
    measuresEnabled = !dragSelectedMode;
    if (!dragSelectedMode)
      switch (pickingMode) {
      default:
        return;
      case JmolConstants.PICKING_DRAW:
        drawMode = true;
        
        measuresEnabled = false;
        break;
      
      case JmolConstants.PICKING_LABEL:
        labelMode = true;
        measuresEnabled = false;
        break;
      case JmolConstants.PICKING_SELECT_ATOM:
      case JmolConstants.PICKING_MEASURE_DISTANCE:
      case JmolConstants.PICKING_MEASURE_ANGLE:
      case JmolConstants.PICKING_MEASURE_TORSION:
        measuresEnabled = false;
        break;
      }
    exitMeasurementMode();
  }
  
  void mouseClicked(long time, int x, int y, int modifiers) {
    setMouseMode();
    setCurrent(time, x, y, modifiers);
    clickedCount = (clicked.check(x, y, modifiers, time, MAX_DOUBLE_CLICK_MILLIS)
        ? clickedCount + 1 : 1);
    clicked.setCurrent();
    setFocus();
    checkPointOrAtomClicked(x, y, modifiers, clickedCount);
  }

  void mouseMoved(long time, int x, int y, int modifiers) {
    setCurrent(time, x, y, modifiers);
    moved.setCurrent();
    if (measurementPending != null || hoverActive)
      checkPointOrAtomClicked(x, y, 0, 0);
    else if (isZoomArea(x))
      checkMotionRotateZoom(Binding.getMouseAction(1, Binding.LEFT), 0, 0, 0, false);
    else
      viewer.setCursor(Viewer.CURSOR_DEFAULT);
  }

  void mouseWheel(long time, int rotation, int mods) {
    if (viewer.isApplet() && !viewer.getDisplay().hasFocus())
      return;
    
    
    setCurrent(time, current.x, current.y, mods);
    checkAction(Binding.getMouseAction(0, mods), current.x, current.y, 0, rotation, time, 3);
  }

  void mousePressed(long time, int x, int y, int mods) {
    setCurrent(time, x, y, mods);
    pressedCount = (pressed.check(x, y, mods, time, MAX_DOUBLE_CLICK_MILLIS)
        ? pressedCount + 1 : 1);
    pressed.setCurrent();
    dragged.setCurrent();
    setFocus();
    int action = Binding.getMouseAction(pressedCount, mods);
    dragGesture.setAction(action, time);
    if (Binding.getModifiers(action) != 0) {
      action = viewer.notifyMouseClicked(x, y, action);
      if (action == 0)
        return;
    }    
    pressedAtomIndex = Integer.MAX_VALUE;
    if (checkUserAction(action, x, y, 0, 0, time, 0))
      return;
    if (isBound(action, ACTION_popupMenu)) {
      viewer.popupMenu(x, y);
      return;
    }
    if (drawMode && (
        isBound(action, ACTION_dragDrawObject)
        || isBound(action, ACTION_dragDrawPoint))
      || labelMode && isBound(action, ACTION_dragLabel)) {
      viewer.checkObjectDragged(Integer.MIN_VALUE, 0, x, y, action);
      return;
    }
    if (dragSelectedMode) {
      viewer.moveSelected(Integer.MIN_VALUE, 0, 0, 0, false);
      return;
    }
    checkMotionRotateZoom(action, x, 0, 0, true);
  }

  protected void setFocus() {
    if (!viewer.getDisplay().hasFocus())
      viewer.getDisplay().requestFocusInWindow();
  }

  void mouseDragged(long time, int x, int y, int mods) {
    setMouseMode();
    int deltaX = x - dragged.x;
    int deltaY = y - dragged.y;
    setCurrent(time, x, y, mods);
    dragged.setCurrent();
    int action = Binding.getMouseAction(pressedCount, mods);
    dragGesture.add(action, x, y, time);
    checkAction(action, x, y, deltaX, deltaY, time, 1);
  }

  void mouseReleased(long time, int x, int y, int mods) {
    setCurrent(time, x, y, mods);
    viewer.spinXYBy(0, 0, 0);
    boolean dragRelease = !pressed.check(x, y, mods, time, Long.MAX_VALUE);
    viewer.setInMotion(false);
    viewer.setCursor(Viewer.CURSOR_DEFAULT);
    int action = Binding.getMouseAction(pressedCount, mods);
    dragGesture.add(action, x, y, time);
    boolean isRbAction = isRubberBandSelect(action);
    if (isRbAction) {
      BitSet bs = viewer.findAtomsInRectangle(rectRubber);
      if (BitSetUtil.firstSetBit(bs) >= 0) {
        String s = Escape.escape(bs);
        if (isBound(action, ACTION_selectOr))
          viewer.script("select selected or " + s);
        else if (isBound(action, ACTION_selectAndNot))
          viewer.script("select selected and not " + s);
        else
          
          viewer.script("select selected tog " + s);
      }
      viewer.refresh(3, "mouseReleased");
    }
    rubberbandSelectionMode = false;
    rectRubber.x = Integer.MAX_VALUE;
    if (dragRelease)
      viewer.notifyMouseClicked(x, y, Binding.getMouseAction(pressedCount, 0));
    
    if (drawMode
        && (isBound(action, ACTION_dragDrawObject) || isBound(action,
            ACTION_dragDrawPoint)) || labelMode
        && isBound(action, ACTION_dragLabel)) {
      viewer.checkObjectDragged(Integer.MAX_VALUE, 0, x, y, action);
      return;
    }
    if (dragSelectedMode)
      viewer.moveSelected(Integer.MAX_VALUE, 0, 0, 0, false);
    
    if (dragRelease && checkUserAction(action, x, y, 0, 0, time, 2))
      return;
    
    if (viewer.getBooleanProperty("allowGestures")) {
      if (isBound(action, ACTION_swipe)) {
        if (dragGesture.getTimeDifference(2) <= MININUM_GESTURE_DELAY_MILLISECONDS
            && dragGesture.getPointCount(10, 5) == 10) {
          float speed = dragGesture.getSpeedPixelsPerMillisecond(10, 5);
          viewer.spinXYBy(dragGesture.getDX(10, 5), dragGesture.getDY(10, 5),
              speed * 30);
          return;
        }
      }
    }
  }

  private boolean isRubberBandSelect(int action) {
    return rubberbandSelectionMode && 
        (  isBound(action, ACTION_selectToggle)
        || isBound(action, ACTION_selectOr)
        || isBound(action, ACTION_selectAndNot)
        );
  }

  Rectangle getRubberBand() {
    if (!rubberbandSelectionMode || rectRubber.x == Integer.MAX_VALUE)
      return null;
    return rectRubber;
  }

  private void calcRectRubberBand() {
    if (current.x < pressed.x) {
      rectRubber.x = current.x;
      rectRubber.width = pressed.x - current.x;
    } else {
      rectRubber.x = pressed.x;
      rectRubber.width = current.x - pressed.x;
    }
    if (current.y < pressed.y) {
      rectRubber.y = current.y;
      rectRubber.height = pressed.y - current.y;
    } else {
      rectRubber.y = pressed.y;
      rectRubber.height = current.y - pressed.y;
    }
  }

  private void checkAction(int action, int x, int y, int deltaX, int deltaY,
                           long time, int mode) {
    int mods = Binding.getModifiers(action);
    if (Binding.getModifiers(action) != 0) {
      int newAction = viewer.notifyMouseClicked(x, y,  Binding.getMouseAction(-pressedCount, mods));
      if (newAction == 0)
        return;
      if (newAction > 0)
        action = newAction;
    }
    
    if (isRubberBandSelect(action)) {
      calcRectRubberBand();
      viewer.refresh(3, "rubberBand selection");
      return;
    }

    if (checkUserAction(action, x, y, deltaX, deltaY, time, mode))
      return;
    
    if (isBound(action, ACTION_translate)) {
      viewer.translateXYBy(deltaX, deltaY);
      return;
    }

    if (isBound(action, ACTION_center)) { 
      if (pressedAtomIndex == Integer.MAX_VALUE)
        pressedAtomIndex = viewer.findNearestAtomIndex(pressed.x, pressed.y);
      Point3f pt = (pressedAtomIndex < 0 ? null : viewer.getAtomPoint3f(pressedAtomIndex));
      if (pt == null)
        viewer.translateXYBy(deltaX, deltaY);
      else
        viewer.centerAt(x, y, pt);
      return;
    }

    if (checkMotionRotateZoom(action, x, deltaX, deltaY, true)) {
      viewer.zoomBy(deltaY);
      return;
    }
    
    if (isBound(action, ACTION_rotate)) {
      viewer.rotateXYBy(deltaX, deltaY);
      return;      
    }

    if (dragSelectedMode && isBound(action, ACTION_dragSelected)) {
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.moveSelected(deltaX, deltaY, x, y, true);
      return;
    }

    if (viewer.allowRotateSelected() && isBound(action, ACTION_rotateSelected)) {
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.rotateMolecule(deltaX, deltaY);
      return;
    }
    if (drawMode && (
          isBound(action, ACTION_dragDrawObject)
          || isBound(action, ACTION_dragDrawPoint))
          || labelMode && isBound(action, ACTION_dragLabel)) {
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.checkObjectDragged(dragged.x, dragged.y, x, y,
          action);
      return;
    }
    if (dragSelectedMode && isBound(action, ACTION_dragSelected)) {
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.moveSelected(deltaX, deltaY, x, y, true);
      return;
    } 
    if (isBound(action, ACTION_rotateZorZoom)) {
      if (Math.abs(deltaY) > 5 * Math.abs(deltaX)) {
        
        checkMotion(Viewer.CURSOR_ZOOM);
        viewer.zoomBy(deltaY);
      } else if (Math.abs(deltaX) > 5 * Math.abs(deltaY)) {
        
        checkMotion(Viewer.CURSOR_MOVE);
        viewer.rotateZBy(-deltaX, Integer.MAX_VALUE, Integer.MAX_VALUE);
      }
      return;
    } else if (isBound(action, ACTION_wheelZoom)) {
      zoomByFactor(deltaY, Integer.MAX_VALUE, Integer.MAX_VALUE);
      return;
    } else if (isBound(action, ACTION_rotateZ)) {
      checkMotion(Viewer.CURSOR_MOVE);
      viewer.rotateZBy(-deltaX, Integer.MAX_VALUE, Integer.MAX_VALUE);
      return;
    }
    if (viewer.getSlabEnabled()) {
      if (isBound(action, ACTION_depth)) {
        viewer.depthByPixels(deltaY);
        return;
      }
      if (isBound(action, ACTION_slab)) {
        viewer.slabByPixels(deltaY);
        return;
      }
      if (isBound(action, ACTION_slabAndDepth)) {
        viewer.slabDepthByPixels(deltaY);
        return;
      }
    }
  }

  protected void zoomByFactor(int dz, int x, int y) {
    checkMotion(Viewer.CURSOR_ZOOM);
    if (dz == 0)
      return;
    viewer.zoomByFactor((float) Math.pow(ZOOM_FACTOR, dz), x, y);
  }

  private boolean checkUserAction(int action, int x, int y, 
                                  int deltaX, int deltaY, long time, int mode) {
    if (!binding.isUserAction(action))
      return false;
    Hashtable ht = binding.getBindings();
    Enumeration e = ht.keys();
    boolean ret = false;
    Object obj;
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (key.indexOf(action + "\t") != 0 
          || !((obj = ht.get(key)) instanceof String[]))
        continue;
      String script = ((String[]) obj)[1];
      script = TextFormat.simpleReplace(script,"_ACTION", "" + action);
      script = TextFormat.simpleReplace(script,"_X", "" + x);
      script = TextFormat.simpleReplace(script,"_Y", "" + (viewer.getScreenHeight() - y));
      script = TextFormat.simpleReplace(script,"_DELTAX", "" + deltaX);
      script = TextFormat.simpleReplace(script,"_DELTAY", "" + deltaY);
      script = TextFormat.simpleReplace(script,"_TIME", "" + time);
      script = TextFormat.simpleReplace(script,"_MODE", "" + mode);
      viewer.evalStringQuiet(script);
      ret = true;
    }
    return ret;
  }

  private boolean checkMotionRotateZoom(int action, int x, 
                                        int deltaX, int deltaY,
                                        boolean inMotion) {
    boolean isSlideZoom = isBound(action, ACTION_slideZoom);
    boolean isRotateXY = isBound(action, ACTION_rotate);
    boolean isRotateZorZoom = isBound(action, ACTION_rotateZorZoom);
    if (!isSlideZoom && !isRotateXY && !isRotateZorZoom) 
      return false;
    boolean isZoom = false;
    if (isRotateZorZoom)
      isZoom = (deltaX == 0 || Math.abs(deltaY) > 5 * Math.abs(deltaX));
    if (isSlideZoom)
      isZoom = isZoomArea(moved.x);
    int cursor = (isZoom || isBound(action, ACTION_wheelZoom) ? Viewer.CURSOR_ZOOM 
        : isRotateXY || isRotateZorZoom ? Viewer.CURSOR_MOVE : Viewer.CURSOR_DEFAULT);
    viewer.setCursor(cursor);
    if (inMotion)
      viewer.setInMotion(true);
    return isZoom;
  }

  private boolean isZoomArea(int x) {
    return x > viewer.getScreenWidth()* SLIDE_ZOOM_X_PERCENT / 100f;
  }

  private void checkPointOrAtomClicked(int x, int y, int mods,
                                       int clickedCount) {
    if (!viewer.haveModelSet())
      return;
    
    
    int action = Binding.getMouseAction(clickedCount, mods);
    if (action != 0) {
      action = viewer.notifyMouseClicked(x, y, action);
      if (action == 0)
        return;
    }
    Point3fi nearestPoint = (drawMode ? null : viewer.checkObjectClicked(x, y,
        action));
    if (nearestPoint != null && Float.isNaN(nearestPoint.x))
      return;
    int nearestAtomIndex = (drawMode || nearestPoint != null ? -1 : viewer
        .findNearestAtomIndex(x, y));
    if (nearestAtomIndex >= 0 && (clickedCount > 0 || measurementPending == null)
        && !viewer.isInSelectionSubset(nearestAtomIndex))
      nearestAtomIndex = -1;
    
    if (clickedCount == 0) {
      
      if (measurementPending == null)
        return;
      if (nearestPoint != null
          || measurementPending.getIndexOf(nearestAtomIndex) == 0)
        measurementPending.addPoint(nearestAtomIndex, nearestPoint, false);
      if (measurementPending.haveModified())
        viewer.setPendingMeasurement(measurementPending);
      viewer.refresh(3, "measurementPending");
      return;
    }
    setMouseMode();
    if (isBound(action, ACTION_clickFrank) && viewer.frankClicked(x, y)) {
      viewer.popupMenu(-x, y);
      return;
    }
    if (viewer.getNavigationMode() 
        && pickingMode == JmolConstants.PICKING_NAVIGATE
        && isBound(action, ACTION_pickNavigate)) {
      viewer.navTranslatePercent(0f, x * 100f / viewer.getScreenWidth()
          - 50f, y * 100f / viewer.getScreenHeight() - 50f);
      return;
    }
    
    if (measurementPending != null && isBound(action, ACTION_pickMeasure)) {
      atomPicked(nearestAtomIndex, nearestPoint, action);
      if (addToMeasurement(nearestAtomIndex, nearestPoint, false) == 4) {
        clickedCount = 0;
        toggleMeasurement();
      }
      return;
    }      
    if (isBound(action, ACTION_setMeasure)) {
      if (measurementPending != null) {
        addToMeasurement(nearestAtomIndex, nearestPoint, true);
        toggleMeasurement();
      } else if (!drawMode && !labelMode && !dragSelectedMode && measuresEnabled) {
        enterMeasurementMode();
        addToMeasurement(nearestAtomIndex, nearestPoint, true);
      }
      atomPicked(nearestAtomIndex, nearestPoint, action);
      return;
    }      
    if (isBound(action, ACTION_pickAtom) || isBound(action, ACTION_pickPoint)) {
      
      atomPicked(nearestAtomIndex, nearestPoint, action);
      return;
    }
    if (isBound(action, ACTION_reset)) {
      if (nearestAtomIndex < 0)
        viewer.script("!reset");
      return;
    }
  }

  protected void checkMotion(int cursor) {
    viewer.setCursor(cursor);
    viewer.setInMotion(true);
  }

  private int addToMeasurement(int atomIndex, Point3fi nearestPoint,
                               boolean dblClick) {
    if (atomIndex == -1 && nearestPoint == null) {
      exitMeasurementMode();
      return 0;
    }
    int measurementCount = measurementPending.getCount();
    return (measurementCount == 4 && !dblClick ? measurementCount
        : measurementPending.addPoint(atomIndex, nearestPoint, true));
  }

  private void enterMeasurementMode() {
    viewer.setCursor(Viewer.CURSOR_CROSSHAIR);
    viewer.setPendingMeasurement(measurementPending = new MeasurementPending(
        viewer.getModelSet()));
  }

  private void exitMeasurementMode() {
    if (measurementPending == null)
      return;
    viewer.setPendingMeasurement(measurementPending = null);
    viewer.setCursor(Viewer.CURSOR_DEFAULT);
  }

  private void toggleMeasurement() {
    if (measurementPending == null)
      return;
    int measurementCount = measurementPending.getCount();
    if (measurementCount >= 2 && measurementCount <= 4) {
      viewer.script("!measure " + measurementPending.getMeasurementScript(" ", true));
    }
    exitMeasurementMode();
  }

  Hashtable timeouts;
  
  String showTimeout(String name) {
    StringBuffer sb = new StringBuffer();
    if (timeouts != null) {
      Enumeration e = timeouts.elements();
      while (e.hasMoreElements()) {
        TimeoutThread t = (TimeoutThread) e.nextElement();
        if (name == null || t.name.equalsIgnoreCase(name))
          sb.append(t.toString()).append("\n");
      }
    }
    return (sb.length() > 0 ? sb.toString() : "<no timeouts set>");
  }

  void clearTimeouts() {
    if (timeouts == null)
      return;
    Enumeration e = timeouts.elements();
    while (e.hasMoreElements())
      ((Thread) e.nextElement()).interrupt();
    timeouts.clear();    
  }
  
  void setTimeout(String name, int mSec, String script) {
    if (name == null) {
      clearTimeouts();
      return;
    }
    if (timeouts == null)
      timeouts = new Hashtable();
    if (mSec == 0) {
      Thread t = (Thread) timeouts.get(name);
      if (t != null) {
        t.interrupt();
        timeouts.remove(name);
      }
      return;
    }
    TimeoutThread t = (TimeoutThread) timeouts.get(name);
    if (t != null) {
      t.set(mSec, script);
      return;
    }
    t = new TimeoutThread(name, mSec, script);
    timeouts.put(name, t);
    t.start();
  }

  class TimeoutThread extends Thread {
    String name;
    private int ms;
    private long targetTime;
    private int status;
    private String script;
    
    TimeoutThread(String name, int ms, String script) {
      this.name = name;
      this.ms = ms;
      this.script = script;
      Thread.currentThread().setName("timeout " + name);
      targetTime = System.currentTimeMillis() + Math.abs(ms);
      if (Logger.debugging) 
        Logger.debug(toString());
    }
    
    void set(int ms, String script) {
      this.ms = ms;
      if (script != null && script.length() != 0)
        this.script = script; 
    }

    public String toString() {
      return "timeout name=" + name + " executions=" + status + " mSec=" + ms 
      + " secRemaining=" + (targetTime - System.currentTimeMillis())/1000f + " script=" + script;      
    }
    
    public void run() {
      if (script == null || script.length() == 0 || ms == 0)
        return;
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      try {
        while (true) {
          Thread.sleep(10);
          if (targetTime > System.currentTimeMillis())
            continue;
          status++;
          targetTime += Math.abs(ms);
          if (ms > 0)
            timeouts.remove(name);
          if (Logger.debugging)
            viewer.script(script);
          else 
            viewer.evalStringQuiet(script);
          if (ms > 0)
            break;
        }
      } catch (InterruptedException ie) {
        Logger.debug("Timeout " + name + " interrupted");
      } catch (Exception ie) {
        Logger.debug("Timeout " + name + " Exception: " + ie);
      }
      timeouts.remove(name);
    }
  }
  
  void hoverOn(int atomIndex) {
    viewer.hoverOn(atomIndex, Binding.getMouseAction(clickedCount, moved.modifiers));
  }

  void hoverOff() {
    try {
      viewer.hoverOff();
    } catch (Exception e) {
      
    }
  }

  class HoverWatcher implements Runnable {
    public void run() {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      int hoverDelay;
      try {
        while (Thread.currentThread().equals(hoverWatcherThread) && (hoverDelay = viewer.getHoverDelay()) > 0) {
          Thread.sleep(hoverDelay);
          if (current.x == moved.x && current.y == moved.y
              && current.time == moved.time) { 
                                                  
            long currentTime = System.currentTimeMillis();
            int howLong = (int) (currentTime - moved.time);
            if (howLong > hoverDelay) {
              if (Thread.currentThread().equals(hoverWatcherThread) && !viewer.getInMotion()
                  && !viewer.getSpinOn() && !viewer.getNavOn()
                  && !viewer.checkObjectHovered(current.x, current.y)) {
                int atomIndex = viewer.findNearestAtomIndex(current.x, current.y);
                if (atomIndex >= 0) {
                  hoverOn(atomIndex);
                }
              }
            }
          }
        }
      } catch (InterruptedException ie) {
        Logger.debug("Hover interrupted");
      } catch (Exception ie) {
        Logger.debug("Hover Exception: " + ie);
      }
    }
  }
  
  
  
  private int pickingMode = JmolConstants.PICKING_IDENT;
  private int pickingStyleSelect = JmolConstants.PICKINGSTYLE_SELECT_JMOL;
  private int pickingStyleMeasure = JmolConstants.PICKINGSTYLE_MEASURE_OFF;

  private boolean drawHover;
  private int pickingStyle;
    
  private MeasurementPending measurementQueued;
  
  void setPickingMode(int pickingMode) {
    this.pickingMode = pickingMode;
    resetMeasurement();
  }

  int getPickingMode() {
    return pickingMode;
  }
    
  private void resetMeasurement() {
    measurementQueued = new MeasurementPending(viewer.getModelSet());    
  }

  void setPickingStyle(int pickingStyle) {
    this.pickingStyle = pickingStyle;
    if (pickingStyle >= JmolConstants.PICKINGSTYLE_MEASURE_ON) {
      pickingStyleMeasure = pickingStyle;
      resetMeasurement();
    } else {
      pickingStyleSelect = pickingStyle;
    }
    rubberbandSelectionMode = false;
    switch (pickingStyleSelect) {
    case JmolConstants.PICKINGSTYLE_SELECT_PFAAT:
      if (binding.getName() != "Pfaat") 
        setBinding(pfaatBinding = (pfaatBinding == null ? new PfaatBinding() : pfaatBinding));
      break;
    case JmolConstants.PICKINGSTYLE_SELECT_DRAG:
      if (binding.getName() != "Drag")
        setBinding(dragBinding = (dragBinding == null ? new DragBinding() : dragBinding));
      rubberbandSelectionMode = true;
      break;
    case JmolConstants.PICKINGSTYLE_SELECT_RASMOL:
      if (binding.getName() != "Rasmol")
        setBinding(rasmolBinding = (rasmolBinding == null ? new RasmolBinding() : rasmolBinding));
      break;
    default:
      if (binding != jmolBinding)
        setBinding(jmolBinding);
    }
  }

  protected void setBinding(Binding newBinding) {
    binding = newBinding;
  }
  
  int getPickingStyle() {
    return pickingStyle;
  }

  void setDrawHover(boolean TF) {
    drawHover = TF;
  }
  
  boolean getDrawHover() {
    return drawHover;
  }

  private void atomPicked(int atomIndex, Point3fi ptClicked, int action) {
    
    if (atomIndex < 0) {
      resetMeasurement();
      if (isBound(action, ACTION_selectNone)) {
        viewer.script("select none");
        return;
      }
      if (pickingMode != JmolConstants.PICKING_SPIN)
        return;
    }
    int n = 2;
    switch (pickingMode) {
    case JmolConstants.PICKING_OFF:
      return;
    case JmolConstants.PICKING_MEASURE_TORSION:
      n++;
      
    case JmolConstants.PICKING_MEASURE_ANGLE:
      n++;
      
    case JmolConstants.PICKING_MEASURE:
    case JmolConstants.PICKING_MEASURE_DISTANCE:
      if (!isBound(action, ACTION_pickMeasure))
        return;
      if (measurementQueued == null || measurementQueued.getCount() >= n)
        resetMeasurement();
      if (queueAtom(atomIndex, ptClicked) < n)
        return;
      viewer.setStatusMeasuring("measurePicked", n, measurementQueued.getStringDetail());
      if (pickingMode == JmolConstants.PICKING_MEASURE
          || pickingStyleMeasure == JmolConstants.PICKINGSTYLE_MEASURE_ON) {
        viewer.script("measure " + measurementQueued.getMeasurementScript(" ", true));
      }
      return;
    case JmolConstants.PICKING_CENTER:
      if (!isBound(action, ACTION_pickAtom))
        return;
      if (ptClicked == null)
        viewer.script("zoomTo (atomindex=" + atomIndex+")");
      else
        viewer.script("zoomTo " + Escape.escape(ptClicked));
      return;
    case JmolConstants.PICKING_SPIN:
      if (!isBound(action, ACTION_pickAtom))
        return;
      if (viewer.getSpinOn() || viewer.getNavOn() || viewer.getPendingMeasurement() != null) {
        resetMeasurement();
        viewer.script("spin off");
        return;
      }
      if (measurementQueued.getCount() >= 2)
        resetMeasurement();
      int queuedAtomCount = measurementQueued.getCount(); 
      if (queuedAtomCount == 1) {
        if (ptClicked == null) {
          if (measurementQueued.getAtomIndex(1) == atomIndex)
            return;
        } else {
          if (measurementQueued.getAtom(1).distance(ptClicked) == 0)
            return;
        }
      }
      if (atomIndex >= 0 || ptClicked != null)
        queuedAtomCount = queueAtom(atomIndex, ptClicked);
      if (queuedAtomCount < 2) {
        viewer.scriptStatus(queuedAtomCount == 1 ?
            GT._("pick one more atom in order to spin the model around an axis") :
            GT._("pick two atoms in order to spin the model around an axis"));
        return;
      }
      viewer.script("spin" + measurementQueued.getMeasurementScript(" ", false) + " " + viewer.getPickingSpinRate());
    }
    if (ptClicked != null)
      return;
    switch (pickingMode) {
    case JmolConstants.PICKING_IDENT:
      if (isBound(action, ACTION_pickAtom))
        viewer.setStatusAtomPicked(atomIndex, null);
      return;
    case JmolConstants.PICKING_LABEL:
      if (isBound(action, ACTION_pickLabel))
        viewer.script("set labeltoggle {atomindex="+atomIndex+"}");
      return;
    }
    String spec = "atomindex=" + atomIndex;
    switch (pickingMode) {
    case JmolConstants.PICKING_LABEL:
      viewer.script("set labeltoggle " + spec);
      return;
    default:
      return;
    case JmolConstants.PICKING_IDENT:
    case JmolConstants.PICKING_SELECT_ATOM:
      applySelectStyle(spec, action);
      break;
    case JmolConstants.PICKING_SELECT_CHAIN:
      applySelectStyle("within(chain, " + spec +")", action);
      break;
    case JmolConstants.PICKING_SELECT_ELEMENT:
      applySelectStyle("visible and within(element, " + spec +")", action);
      break;
    case JmolConstants.PICKING_SELECT_GROUP:
      applySelectStyle("within(group, " + spec +")", action);
      break;
    case JmolConstants.PICKING_SELECT_MODEL:
      applySelectStyle("within(model, " + spec +")", action);
      break;
    case JmolConstants.PICKING_SELECT_MOLECULE:
      applySelectStyle("visible and within(molecule, " + spec +")", action);
      break;
    case JmolConstants.PICKING_SELECT_SITE:
      applySelectStyle("visible and within(site, " + spec +")", action);
      break;
    }
    viewer.clearClickCount();
  }

  private int queueAtom(int atomIndex, Point3fi ptClicked) {
    int n = measurementQueued.addPoint(atomIndex, ptClicked, true);
    if (atomIndex >= 0)
      viewer.setStatusAtomPicked(atomIndex, "Atom #" + n + ":"
          + viewer.getAtomInfo(atomIndex));
    return n;
  }

  private void applySelectStyle(String item, int action) {
    if (measurementPending != null)
      return;
    String s = (isBound(action, ACTION_selectAndNot) ? "selected and not " 
         : isBound(action, ACTION_selectOr) ? "selected or " 
         : isBound(action, ACTION_selectToggle) ? 
             "selected and not (" + item + ") or (not selected) and "
         : isBound(action, ACTION_selectToggleExtended) ?
             "selected tog " 
         : isBound(action, ACTION_select) ? "" : null);
    if (s == null)
      return;
    viewer.script("select " + s + "(" + item + ")");
  }

  protected class MotionPoint {
    int index;
    int x;
    int y;
    long time;

    void set(int index, int x, int y, long time) {
      this.index = index;
      this.x = x;
      this.y = y;
      this.time = time;
    }
  }
  
  private Gesture dragGesture = new Gesture(20);
  
  protected class Gesture {
    private int action;
    MotionPoint[] nodes;
    private int ptNext;
    private long time0;

    Gesture(int nPoints) {
      nodes = new MotionPoint[nPoints];
      for (int i = 0; i < nPoints; i++)
        nodes[i] = new MotionPoint();
    }
    
    void setAction(int action, long time) {
      this.action = action;
      ptNext = 0;
      time0 = time;
      for (int i = 0; i < nodes.length; i++)
        nodes[i].index = -1;
    }
    
    int getAction() {
      return action;
    }
    
    int add(int action, int x, int y, long time) {
      this.action = action;
      getNode(ptNext).set(ptNext, x, y, time - time0);
      ptNext++;
      return ptNext;
    }
    
    long getTimeDifference(int nPoints) {
      nPoints = getPointCount(nPoints, 0);
      if (nPoints < 2)
        return 0;
      MotionPoint mp1 = getNode(ptNext - 1);
      MotionPoint mp0 = getNode(ptNext - nPoints);
      return mp1.time - mp0.time;
    }

    float getSpeedPixelsPerMillisecond(int nPoints, int nPointsPrevious) {
      nPoints = getPointCount(nPoints, nPointsPrevious);
      if (nPoints < 2)
        return 0;
      MotionPoint mp1 = getNode(ptNext - 1 - nPointsPrevious);
      MotionPoint mp0 = getNode(ptNext - nPoints - nPointsPrevious);
      float dx = mp1.x - mp0.x;
      float dy = mp1.y - mp0.y;
      float speed = (float) Math.sqrt(dx * dx + dy * dy) / (mp1.time - mp0.time);
      return speed;
    }

    int getDX(int nPoints, int nPointsPrevious) {
      nPoints = getPointCount(nPoints, nPointsPrevious);
      if (nPoints < 2)
        return 0;
      MotionPoint mp1 = getNode(ptNext - 1 - nPointsPrevious);
      MotionPoint mp0 = getNode(ptNext - nPoints - nPointsPrevious);
      return mp1.x - mp0.x;
    }

    int getDY(int nPoints, int nPointsPrevious) {
      nPoints = getPointCount(nPoints, nPointsPrevious);
      if (nPoints < 2)
        return 0;
      MotionPoint mp1 = getNode(ptNext - 1 - nPointsPrevious);
      MotionPoint mp0 = getNode(ptNext - nPoints - nPointsPrevious);
      return mp1.y - mp0.y;
    }

    int getPointCount(int nPoints, int nPointsPrevious) {
      if (nPoints > nodes.length - nPointsPrevious)
        nPoints = nodes.length - nPointsPrevious;
      int n = nPoints + 1;
      for (; --n >= 0; )
        if (getNode(ptNext - n - nPointsPrevious).index >= 0)
          break;
      return n;
    }

    MotionPoint getNode(int i) {
      return nodes[(i + nodes.length + nodes.length) % nodes.length];
    }
  }

} 
