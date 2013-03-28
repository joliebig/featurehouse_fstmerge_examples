
package org.jmol.viewer;

import javax.vecmath.Point3f;
import javax.vecmath.Matrix3f;

import java.util.Hashtable;
import java.util.BitSet;
import java.util.Enumeration;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Bond;
import org.jmol.modelset.LabelToken;
import org.jmol.modelset.ModelSet;
import org.jmol.script.ScriptFunction;
import org.jmol.script.ScriptVariable;
import org.jmol.util.Escape;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;

import java.util.Arrays;

public class StateManager {

  

  public final static int OBJ_BACKGROUND = 0;
  public final static int OBJ_AXIS1 = 1;
  public final static int OBJ_AXIS2 = 2;
  public final static int OBJ_AXIS3 = 3;
  public final static int OBJ_BOUNDBOX = 4;
  public final static int OBJ_UNITCELL = 5;
  public final static int OBJ_FRANK = 6;
  public final static int OBJ_MAX = 7;
  private final static String objectNameList = "background axis1      axis2      axis3      boundbox   unitcell   frank      ";

  public static String getVariableList(Hashtable htVariables, int nMax) {
    StringBuffer sb = new StringBuffer();
    
    int n = 0;
    Enumeration e = htVariables.keys();

    String[] list = new String[htVariables.size()];
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      ScriptVariable var = (ScriptVariable) htVariables.get(key);
      list[n++] = key  + (key.charAt(0) == '@' ? " "
              + ScriptVariable.sValue(var) : " = " + varClip(key, var.escape(), nMax));
    }
    Arrays.sort(list, 0, n);
    for (int i = 0; i < n; i++)
      if (list[i] != null)
        appendCmd(sb, list[i]);
    if (n == 0)
      sb.append("# --no global user variables defined--;\n");
    return sb.toString();
  }
  
  public static int getObjectIdFromName(String name) {
    if (name == null)
      return -1;
    int objID = objectNameList.indexOf(name.toLowerCase());
    return (objID < 0 ? objID : objID / 11);
  }

  static String getObjectNameFromId(int objId) {
    if (objId < 0 || objId >= OBJ_MAX)
      return null;
    return objectNameList.substring(objId * 11, objId * 11 + 11).trim();
  }

  Viewer viewer;
  Hashtable saved = new Hashtable();
  String lastOrientation = "";
  String lastConnections = "";
  String lastSelected = "";
  String lastState = "";
  String lastShape = "";
  String lastCoordinates = "";

  StateManager(Viewer viewer) {
    this.viewer = viewer;
  }

  GlobalSettings getGlobalSettings(GlobalSettings gsOld) {
    return new GlobalSettings(gsOld);
  }

  void clear(GlobalSettings global) {
    viewer.setShowAxes(false);
    viewer.setShowBbcage(false);
    viewer.setShowUnitCell(false);
    global.clear();
  }

  void setCrystallographicDefaults() {
    
    viewer.setAxesModeUnitCell(true);
    viewer.setShowAxes(true);
    viewer.setShowUnitCell(true);
    viewer.setBooleanProperty("perspectiveDepth", false);
  }

  private void setCommonDefaults() {
    viewer.setBooleanProperty("perspectiveDepth", true);
    viewer.setFloatProperty("bondTolerance",
        JmolConstants.DEFAULT_BOND_TOLERANCE);
    viewer.setFloatProperty("minBondDistance",
        JmolConstants.DEFAULT_MIN_BOND_DISTANCE);
  }

  void setJmolDefaults() {
    setCommonDefaults();
    viewer.setStringProperty("defaultColorScheme", "Jmol");
    viewer.setBooleanProperty("axesOrientationRasmol", false);
    viewer.setBooleanProperty("zeroBasedXyzRasmol", false);
    viewer.setIntProperty("percentVdwAtom",
        JmolConstants.DEFAULT_PERCENT_VDW_ATOM);
    viewer.setIntProperty("bondRadiusMilliAngstroms",
        JmolConstants.DEFAULT_BOND_MILLIANGSTROM_RADIUS);
    viewer.setDefaultVdw("auto");
  }

  void setRasMolDefaults() {
    setCommonDefaults();
    viewer.setStringProperty("defaultColorScheme", "RasMol");
    viewer.setBooleanProperty("axesOrientationRasmol", true);
    viewer.setBooleanProperty("zeroBasedXyzRasmol", true);
    viewer.setIntProperty("percentVdwAtom", 0);
    viewer.setIntProperty("bondRadiusMilliAngstroms", 1);
    viewer.setDefaultVdw("Rasmol");
  }

  String getStandardLabelFormat() {
    
    
    
    
    
    
    
    String strLabel = LabelToken.STANDARD_LABEL;
    
    
    
    return strLabel;
  }

  String listSavedStates() {
    String names = "";
    Enumeration e = saved.keys();
    while (e.hasMoreElements())
      names += "\n" + e.nextElement();
    return names;
  }

  private void deleteSaved(String type) {
    Enumeration e = saved.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (name.startsWith(type)) {
        saved.remove(name);
        Logger.debug("deleted " + name);
      }
    }
  }

  void saveSelection(String saveName, BitSet bsSelected) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("Selected_");
      return;
    }
    saveName = lastSelected = "Selected_" + saveName;
    saved.put(saveName, BitSetUtil.copy(bsSelected));
  }

  boolean restoreSelection(String saveName) {
    String name = (saveName.length() > 0 ? "Selected_" + saveName
        : lastSelected);
    BitSet bsSelected = (BitSet) saved.get(name);
    if (bsSelected == null) {
      viewer.select(new BitSet(), false);
      return false;
    }
    viewer.select(bsSelected, false);
    return true;
  }

  void saveState(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("State_");
      return;
    }
    saveName = lastState = "State_" + saveName;
    saved.put(saveName, viewer.getStateInfo());
  }

  String getSavedState(String saveName) {
    String name = (saveName.length() > 0 ? "State_" + saveName : lastState);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  
  void saveStructure(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("Shape_");
      return;
    }
    saveName = lastShape = "Shape_" + saveName;
    saved.put(saveName, viewer.getStructureState());
  }

  String getSavedStructure(String saveName) {
    String name = (saveName.length() > 0 ? "Shape_" + saveName : lastShape);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  void saveCoordinates(String saveName, BitSet bsSelected) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("Coordinates_");
      return;
    }
    saveName = lastCoordinates = "Coordinates_" + saveName;
    saved.put(saveName, viewer.getCoordinateState(bsSelected));
  }

  String getSavedCoordinates(String saveName) {
    String name = (saveName.length() > 0 ? "Coordinates_" + saveName
        : lastCoordinates);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  Orientation getOrientation() {
    return new Orientation(false);
  }

  String getSavedOrientationText(String saveName) {
    Orientation o;
    if (saveName != null) {
      o = getOrientation(saveName);
      return (o == null ? "" : o.getMoveToText(true));      
    } 
    StringBuffer sb = new StringBuffer();
    Enumeration e = saved.keys();
    while (e.hasMoreElements()) {
       String name = (String) e.nextElement();
       if (!name.startsWith("Orientation_"))
         continue;
       sb.append(((Orientation) saved.get(name)).getMoveToText(true));
    }
    return sb.toString(); 
  }


  void saveOrientation(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("Orientation_");
      return;
    }
    Orientation o = new Orientation(saveName.equals("default"));
    o.saveName = lastOrientation = "Orientation_" + saveName;
    saved.put(o.saveName, o);
  }
  
  boolean restoreOrientation(String saveName, float timeSeconds, boolean isAll) {
    Orientation o = getOrientation(saveName);
    if (o == null)
      return false;
    o.restore(timeSeconds, isAll);
    
    return true;
  }

  private Orientation getOrientation(String saveName) {
    String name = (saveName.length() > 0 ? "Orientation_" + saveName
        : lastOrientation);    
    return (Orientation) saved.get(name);
  }

  public class Orientation {

    String saveName;

    Matrix3f rotationMatrix = new Matrix3f();
    float xTrans, yTrans;
    float zoom, rotationRadius;
    Point3f center = new Point3f();
    Point3f navCenter = new Point3f();
    float xNav = Float.NaN;
    float yNav = Float.NaN;
    float navDepth = Float.NaN;
    boolean windowCenteredFlag;
    boolean navigationMode;
    boolean navigateSurface;
    String moveToText;
    

    Orientation(boolean asDefault) {
      if (asDefault) {
        Matrix3f rotationMatrix = (Matrix3f) viewer
          .getModelSetAuxiliaryInfo("defaultOrientationMatrix");
        if (rotationMatrix == null)
          this.rotationMatrix.setIdentity();
        else
          this.rotationMatrix.set(rotationMatrix);
      } else {
        viewer.getRotation(this.rotationMatrix);
      }
      xTrans = viewer.getTranslationXPercent();
      yTrans = viewer.getTranslationYPercent();
      zoom = viewer.getZoomSetting();
      center.set(viewer.getRotationCenter());
      windowCenteredFlag = viewer.isWindowCentered();
      rotationRadius = viewer.getRotationRadius();
      navigationMode = viewer.getNavigationMode();
      navigateSurface = viewer.getNavigateSurface();
      moveToText = viewer.getMoveToText(-1);
      if (navigationMode) {
        xNav = viewer.getNavigationOffsetPercent('X');
        yNav = viewer.getNavigationOffsetPercent('Y');
        navDepth = viewer.getNavigationDepthPercent();
        navCenter = new Point3f(viewer.getNavigationCenter());
      }
    }

    public String getMoveToText(boolean asCommand) {
      return (asCommand ? "  " + moveToText + "\n  save orientation \"" 
          + saveName.substring(12) + "\"\n" : moveToText);
    }
    
    public void restore(float timeSeconds, boolean isAll) {
      if (!isAll) {
        viewer.moveTo(timeSeconds, null, null, Float.NaN, rotationMatrix, Float.NaN, Float.NaN,
            Float.NaN, Float.NaN, null, Float.NaN, Float.NaN, Float.NaN);
        return;
      }
      viewer.setBooleanProperty("windowCentered", windowCenteredFlag);
      viewer.setBooleanProperty("navigationMode", navigationMode);
      viewer.setBooleanProperty("navigateSurface", navigateSurface);
      viewer.moveTo(timeSeconds, center, null, Float.NaN, rotationMatrix, zoom, xTrans, yTrans,
          rotationRadius, navCenter, xNav, yNav, navDepth);
    }
  }

  void saveBonds(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSaved("Bonds_");
      return;
    }
    Connections b = new Connections();
    b.saveName = lastConnections = "Bonds_" + saveName;
    saved.put(b.saveName, b);
  }

  boolean restoreBonds(String saveName) {
    String name = (saveName.length() > 0 ? "Bonds_" + saveName
        : lastConnections);
    Connections c = (Connections) saved.get(name);
    if (c == null)
      return false;
    c.restore();
    
    return true;
  }

  class Connections {

    String saveName;
    int bondCount;
    Connection[] connections;

    Connections() {
      ModelSet modelSet = viewer.getModelSet();
      if (modelSet == null)
        return;
      bondCount = modelSet.getBondCount();
      connections = new Connection[bondCount + 1];
      Bond[] bonds = modelSet.getBonds();
      for (int i = bondCount; --i >= 0;) {
        Bond b = bonds[i];
        connections[i] = new Connection(b.getAtomIndex1(), b.getAtomIndex2(), b
            .getMad(), b.getColix(), b.getOrder(), b.getShapeVisibilityFlags());
      }
    }

    void restore() {
      ModelSet modelSet = viewer.getModelSet();
      if (modelSet == null)
        return;
      modelSet.deleteAllBonds();
      for (int i = bondCount; --i >= 0;) {
        Connection c = connections[i];
        int atomCount = modelSet.getAtomCount();
        if (c.atomIndex1 >= atomCount || c.atomIndex2 >= atomCount)
          continue;
        Bond b = modelSet.bondAtoms(modelSet.atoms[c.atomIndex1],
            modelSet.atoms[c.atomIndex2], c.order, c.mad, null);
        b.setColix(c.colix);
        b.setShapeVisibilityFlags(c.shapeVisibilityFlags);
      }
      for (int i = bondCount; --i >= 0;)
        modelSet.getBondAt(i).setIndex(i);
      viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "reportAll", null);
    }
  }

  static class Connection {
    int atomIndex1;
    int atomIndex2;
    short mad;
    short colix;
    int order;
    int shapeVisibilityFlags;

    Connection(int atom1, int atom2, short mad, short colix, int order,
        int shapeVisibilityFlags) {
      atomIndex1 = atom1;
      atomIndex2 = atom2;
      this.mad = mad;
      this.colix = colix;
      this.order = order;
      this.shapeVisibilityFlags = shapeVisibilityFlags;
    }
  }

  public static boolean isMeasurementUnit(String units) {
    return Parser.isOneOf(units.toLowerCase(),
        "angstroms;au;bohr;nanometers;nm;picometers;pm");
  }

  private final static Hashtable staticFunctions = new Hashtable();
  private Hashtable localFunctions = new Hashtable();

  Hashtable getFunctions(boolean isStatic) {
    return (isStatic ? staticFunctions : localFunctions);
  }

  String getFunctionCalls(String selectedFunction) {
    if (selectedFunction == null)
      selectedFunction = "";
    StringBuffer s = new StringBuffer();
    int pt = selectedFunction.indexOf("*");
    boolean isGeneric = (pt >= 0);
    boolean isStatic = (selectedFunction.indexOf("static_") == 0);
    boolean namesOnly = (selectedFunction.equalsIgnoreCase("names") || selectedFunction.equalsIgnoreCase("static_names"));
    if (namesOnly)
      selectedFunction = "";
    if (isGeneric)
      selectedFunction = selectedFunction.substring(0, pt);
    selectedFunction = selectedFunction.toLowerCase();
    Hashtable ht = getFunctions(isStatic);
    String[] names = new String[ht.size()];
    Enumeration e = ht.keys();
    int n = 0;
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (selectedFunction.length() == 0 && !name.startsWith("_")
          || name.equalsIgnoreCase(selectedFunction) || isGeneric
          && name.toLowerCase().indexOf(selectedFunction) == 0)
        names[n++] = name;
    }
    Arrays.sort(names, 0, n);
    for (int i = 0; i < n; i++) {
      ScriptFunction f = (ScriptFunction) ht.get(names[i]);
      s.append(namesOnly ? f.getSignature() : f.toString());
      s.append('\n');
    }
    return s.toString();
  }

  public void clearFunctions() {
    staticFunctions.clear();
    localFunctions.clear();
  }

  private static boolean isStaticFunction(String name) {
    return name.startsWith("static_");  
  }
  
  boolean isFunction(String name) {
    return (isStaticFunction(name) ? staticFunctions : localFunctions).containsKey(name);
  }

  void addFunction(ScriptFunction function) {
    (isStaticFunction(function.name) ? staticFunctions
        : localFunctions).put(function.name, function);
  }

  ScriptFunction getFunction(String name) {
    if (name == null)
      return null;
    ScriptFunction function = (ScriptFunction) (isStaticFunction(name) ? staticFunctions
        : localFunctions).get(name);
    return (function == null || function.aatoken == null ? null : function);
  }
  
  protected final static String unreportedProperties =
    
    
    
    
    
    
    
    
    
    
    
    
    ";ambientpercent;animationfps"
        + ";antialiasdisplay;antialiasimages;antialiastranslucent;appendnew;axescolor"
        + ";axesposition;axesmolecular;axesorientationrasmol;axesunitcell;axeswindow;axis1color;axis2color"
        + ";axis3color;backgroundcolor;backgroundmodel;bondsymmetryatoms;boundboxcolor;cameradepth"
        + ";debug;debugscript;defaultlatttice;defaults;diffusepercent;exportdrivers"
        + ";_filecaching;_filecache;fontcaching;fontscaling;language;loglevel;measureStyleChime"
        + ";minimizationsteps;minimizationrefresh;minimizationcriterion;navigationmode;"
        + ";perspectivedepth;phongexponent;perspectivemodel;preservestate;refreshing;rotationradius"
        + ";showaxes;showaxis1;showaxis2;showaxis3;showboundbox;showfrank;showunitcell"
        + ";slabenabled;zshade;zshadepower;specular;specularexponent;specularpercent;specularpower;stateversion"
        + ";statusreporting;stereo;stereostate"
        + ";unitcellcolor;visualrange;windowcentered;zerobasedxyzrasmol;zoomenabled;mousedragfactor;mousewheelfactor"
        +
        
        ";scriptqueue;scriptreportinglevel;syncscript;syncmouse;syncstereo;" +
        ";defaultdirectory;currentlocalpath;defaultdirectorylocal"
        +
        
        ";ambient;bonds;colorrasmol;diffuse;frank;hetero;hidenotselected"
        + ";hoverlabel;hydrogen;languagetranslation;measurementunits;navigationdepth;navigationslab"
        + ";picking;pickingstyle;propertycolorschemeoverload;radius;rgbblue;rgbgreen;rgbred"
        + ";scaleangstromsperinch;selectionhalos;showscript;showselections;solvent;strandcount"
        + ";spinx;spiny;spinz;spinfps;navx;navy;navz;navfps;" + JmolConstants.getCallbackName(-1)
        + ";undo;";

  protected static int getJmolVersionInt() {
    
    String s = JmolConstants.version;
    int version = -1;

    try {
      
      int i = s.indexOf(".");
      if (i < 0) {
        version = 100000 * Integer.parseInt(s);
        return version;
      }
      version = 100000 * Integer.parseInt(s.substring(0, i));

      
      s = s.substring(i + 1);
      i = s.indexOf(".");
      if (i < 0) {
        version += 1000 * Integer.parseInt(s);
        return version;
      }
      version += 1000 * Integer.parseInt(s.substring(0, i));

      
      s = s.substring(i + 1);
      i = s.indexOf("_");
      if (i >= 0)
        s = s.substring(0, i);
      i = s.indexOf(" ");
      if (i >= 0)
        s = s.substring(0, i);
      version += Integer.parseInt(s);
    } catch (NumberFormatException e) {
      
    }

    return version;
  }

  class GlobalSettings {

    Hashtable htNonbooleanParameterValues;
    Hashtable htBooleanParameterFlags;
    Hashtable htPropertyFlagsRemoved;
    Hashtable htUserVariables = new Hashtable();

    

    GlobalSettings(GlobalSettings gsOld) {
      registerAllValues(gsOld);
    }

    void clear() {
      Enumeration e = htUserVariables.keys();
      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        if (key.charAt(0) == '@' || key.startsWith("site_"))
          htUserVariables.remove(key);
      }

      
      setParameterValue("_atompicked", -1);
      setParameterValue("_atomhovered", -1);
      setParameterValue("_pickinfo", "");
      setParameterValue("selectionhalos", false);
      setParameterValue("hidenotselected", false); 
      setParameterValue("measurementlabels", measurementLabels = true);
      setParameterValue("drawHover", drawHover = false);
      

    }

    void registerAllValues(GlobalSettings g) {
      htNonbooleanParameterValues = new Hashtable();
      htBooleanParameterFlags = new Hashtable();
      htPropertyFlagsRemoved = new Hashtable();

      if (g != null) {
        
        debugScript = g.debugScript;
        disablePopupMenu = g.disablePopupMenu;
        messageStyleChime = g.messageStyleChime;
        defaultDirectory = g.defaultDirectory;
        allowGestures = g.allowGestures;
        allowMultiTouch = g.allowMultiTouch;
        allowKeyStrokes = g.allowKeyStrokes;
        useScriptQueue = g.useScriptQueue;
      }

      for (int i = 0;;i++) {        
        String callbackName = JmolConstants.getCallbackName(i);
        if (callbackName == null)
          break;
        resetParameterStringValue(callbackName, g);        
      }

      setParameterValue("historyLevel", 0); 

      
      
      
      
      
      
      
      
      
      

      setParameterValue("gestureSwipeFactor", ActionManager.DEFAULT_GESTURE_SWIPE_FACTOR);
      setParameterValue("hideNotSelected", false); 
      setParameterValue("hoverLabel", ""); 
      setParameterValue("logFile", viewer.getLogFile()); 
      setParameterValue("mouseWheelFactor", ActionManager.DEFAULT_MOUSE_WHEEL_FACTOR);
      setParameterValue("mouseDragFactor", ActionManager.DEFAULT_MOUSE_DRAG_FACTOR);
      setParameterValue("navFps", TransformManager.DEFAULT_NAV_FPS); 
      setParameterValue("navigationDepth", 0);   
      setParameterValue("navigationSlab", 0);    
      setParameterValue("navX", 0);              
      setParameterValue("navY", 0);              
      setParameterValue("navZ", 0);              
      setParameterValue("perspectiveModel", TransformManager.DEFAULT_PERSPECTIVE_MODEL);
      setParameterValue("picking", "identify");      
      setParameterValue("pickingStyle", "toggle");   
      setParameterValue("refreshing", true);         
      setParameterValue("rotationRadius", 0);        
      setParameterValue("scaleAngstromsPerInch", 0); 
      setParameterValue("scriptReportingLevel", 0);  
      setParameterValue("selectionHalos", false);    
      setParameterValue("showaxes", false);          
      setParameterValue("showboundbox", false);      
      setParameterValue("showfrank", false);         
      setParameterValue("showUnitcell", false);      
      setParameterValue("slabEnabled", false);       
      setParameterValue("spinX", 0);                 
      setParameterValue("spinY", TransformManager.DEFAULT_SPIN_Y);
      setParameterValue("spinZ", 0);                 
      setParameterValue("spinFps", TransformManager.DEFAULT_SPIN_FPS);
      setParameterValue("stereoDegrees", JmolConstants.DEFAULT_STEREO_DEGREES); 
      setParameterValue("stateversion", 0); 
      setParameterValue("windowCentered", true); 
      setParameterValue("zoomEnabled", true);    
      setParameterValue("zShade", false);        
      

      
      
      
      
      
      
      setParameterValue("_version", getJmolVersionInt());

      setParameterValue("axesWindow", true);
      setParameterValue("axesMolecular", false);
      setParameterValue("axesPosition", false);
      setParameterValue("axesUnitcell", false);
      setParameterValue("backgroundModel", 0);
      setParameterValue("colorRasmol", false);
      setParameterValue("currentLocalPath", "");
      setParameterValue("defaultLattice", "{0 0 0}");
      setParameterValue("defaultColorScheme", "Jmol");
      setParameterValue("defaultDirectoryLocal", "");
      setParameterValue("defaults", "Jmol");
      setParameterValue("defaultVDW", "Jmol");
      setParameterValue("exportDrivers", JmolConstants.EXPORT_DRIVER_LIST);
      setParameterValue("propertyAtomNumberColumnCount", 0);
      setParameterValue("propertyAtomNumberField", 0);
      setParameterValue("propertyDataColumnCount", 0);
      setParameterValue("propertyDataField", 0);
      setParameterValue("undo", true);

      

      setParameterValue("allowEmbeddedScripts", allowEmbeddedScripts);
      setParameterValue("allowGestures", allowGestures);
      setParameterValue("allowKeyStrokes", allowKeyStrokes);
      setParameterValue("allowMultiTouch", allowMultiTouch);
      setParameterValue("allowRotateSelected", allowRotateSelected);
      setParameterValue("ambientPercent", ambientPercent);
      setParameterValue("animationFps", animationFps);
      setParameterValue("antialiasImages", antialiasImages);
      setParameterValue("antialiasDisplay", antialiasDisplay);
      setParameterValue("antialiasTranslucent", antialiasTranslucent);
      setParameterValue("appendNew", appendNew);
      setParameterValue("appletProxy", appletProxy);
      setParameterValue("applySymmetryToBonds", applySymmetryToBonds);
      setParameterValue("atomPicking", atomPicking);
      setParameterValue("atomTypes", atomTypes);
      setParameterValue("autoBond", autoBond);
      setParameterValue("autoFps", autoFps);
      setParameterValue("autoLoadOrientation", autoLoadOrientation);
      setParameterValue("axesMode", axesMode);
      setParameterValue("axesScale", axesScale);
      setParameterValue("axesOrientationRasmol", axesOrientationRasmol);
      setParameterValue("bondModeOr", bondModeOr);
      setParameterValue("bondPicking", bondPicking);
      setParameterValue("bondRadiusMilliAngstroms", bondRadiusMilliAngstroms);
      setParameterValue("bondTolerance", bondTolerance);
      setParameterValue("cameraDepth", cameraDepth);
      setParameterValue("cartoonRockets", cartoonRockets);
      setParameterValue("chainCaseSensitive", chainCaseSensitive);
      setParameterValue("dataSeparator", dataSeparator);
      setParameterValue("debugScript", debugScript);
      setParameterValue("defaultAngleLabel", defaultAngleLabel);
      setParameterValue("defaultDrawArrowScale", defaultDrawArrowScale);
      setParameterValue("defaultDirectory", defaultDirectory);
      setParameterValue("defaultDistanceLabel", defaultDistanceLabel);
      setParameterValue("defaultLoadScript", defaultLoadScript);
      setParameterValue("defaultTorsionLabel", defaultTorsionLabel);
      setParameterValue("defaultTranslucent", defaultTranslucent);
      setParameterValue("delayMaximumMs", delayMaximumMs);
      setParameterValue("diffusePercent", diffusePercent);
      setParameterValue("dipoleScale", dipoleScale);
      setParameterValue("disablePopupMenu", disablePopupMenu);
      setParameterValue("displayCellParameters", displayCellParameters);
      setParameterValue("dotDensity", dotDensity);
      setParameterValue("dotsSelectedOnly", dotsSelectedOnly);
      setParameterValue("dotSurface", dotSurface);
      setParameterValue("dragSelected", dragSelected);
      setParameterValue("drawHover", drawHover);
      setParameterValue("drawPicking", drawPicking);
      setParameterValue("dynamicMeasurements", dynamicMeasurements);
      setParameterValue("ellipsoidArcs", ellipsoidArcs);
      setParameterValue("ellipsoidAxes", ellipsoidAxes);
      setParameterValue("ellipsoidAxisDiameter", ellipsoidAxisDiameter);
      setParameterValue("ellipsoidBall", ellipsoidBall);
      setParameterValue("ellipsoidDotCount", ellipsoidDotCount);
      setParameterValue("ellipsoidDots", ellipsoidDots);
      setParameterValue("ellipsoidFill", ellipsoidFill);


      setParameterValue("fontScaling", fontScaling);
      setParameterValue("fontCaching", fontCaching);
      setParameterValue("forceAutoBond", forceAutoBond);
      setParameterValue("greyscaleRendering", greyscaleRendering);
      setParameterValue("hbondsAngleMinimum", hbondsAngleMinimum);
      setParameterValue("hbondsDistanceMaximum", hbondsDistanceMaximum);
      setParameterValue("hbondsBackbone", hbondsBackbone);
      setParameterValue("hbondsSolid", hbondsSolid);
      setParameterValue("helixStep", helixStep);
      setParameterValue("helpPath", helpPath);
      setParameterValue("hermiteLevel", hermiteLevel);
      setParameterValue("hideNameInPopup", hideNameInPopup);
      setParameterValue("hideNavigationPoint", hideNavigationPoint);
      setParameterValue("highResolution", highResolutionFlag);
      setParameterValue("hoverDelay", hoverDelayMs / 1000f);
      setParameterValue("imageState", imageState);
      setParameterValue("isosurfacePropertySmoothing",
          isosurfacePropertySmoothing);
      setParameterValue("justifyMeasurements", justifyMeasurements);
      setParameterValue("loadAtomDataTolerance", loadAtomDataTolerance);
      setParameterValue("loadFormat", loadFormat);
      setParameterValue("logCommands", logCommands);
      setParameterValue("logGestures", logGestures);
      setParameterValue("measureAllModels", measureAllModels);
      setParameterValue("measurementLabels", measurementLabels);
      setParameterValue("measurementUnits", measureDistanceUnits);
      setParameterValue("messageStyleChime", messageStyleChime);
      setParameterValue("minBondDistance", minBondDistance);
      setParameterValue("minimizationSteps", minimizationSteps);
      setParameterValue("minimizationRefresh", minimizationRefresh);
      setParameterValue("minimizationCriterion", minimizationCriterion);
      setParameterValue("navigationMode", navigationMode);
      setParameterValue("navigateSurface", navigateSurface);
      setParameterValue("navigationPeriodic", navigationPeriodic);
      setParameterValue("navigationSpeed", navigationSpeed);
      setParameterValue("pdbGetHeader", pdbGetHeader); 
      setParameterValue("pdbSequential", pdbSequential); 
      setParameterValue("perspectiveDepth", perspectiveDepth);
      setParameterValue("percentVdwAtom", percentVdwAtom);
      setParameterValue("phongExponent", phongExponent);
      setParameterValue("pickingSpinRate", pickingSpinRate);
      setParameterValue("pickLabel", pickLabel);
      setParameterValue("pointGroupLinearTolerance", pointGroupLinearTolerance);
      setParameterValue("pointGroupDistanceTolerance", pointGroupDistanceTolerance);
      setParameterValue("preserveState", preserveState);
      setParameterValue("propertyColorScheme", propertyColorScheme);
      setParameterValue("quaternionFrame", quaternionFrame);
      setParameterValue("rangeSelected", rangeSelected);
      setParameterValue("ribbonAspectRatio", ribbonAspectRatio);
      setParameterValue("ribbonBorder", ribbonBorder);
      setParameterValue("rocketBarrels", rocketBarrels);
      setParameterValue("saveProteinStructureState", saveProteinStructureState);
      setParameterValue("scriptqueue", useScriptQueue);
      setParameterValue("selectAllModels", selectAllModels);
      setParameterValue("selectHetero", rasmolHeteroSetting);
      setParameterValue("selectHydrogen", rasmolHydrogenSetting);
      setParameterValue("sheetSmoothing", sheetSmoothing);
      setParameterValue("showHiddenSelectionHalos", showHiddenSelectionHalos);
      setParameterValue("showHydrogens", showHydrogens);
      setParameterValue("showKeyStrokes", showKeyStrokes);
      setParameterValue("showMeasurements", showMeasurements);
      setParameterValue("showMultipleBonds", showMultipleBonds);
      setParameterValue("showNavigationPointAlways", showNavigationPointAlways);
      setParameterValue("showScript", scriptDelay);
      setParameterValue("slabByMolecule", slabByMolecule);
      setParameterValue("slabByAtom", slabByAtom);
      setParameterValue("smartAromatic", smartAromatic);
      setParameterValue("solventProbe", solventOn);
      setParameterValue("solventProbeRadius", solventProbeRadius);
      setParameterValue("specular", specular);
      setParameterValue("specularExponent", specularExponent);
      setParameterValue("specularPercent", specularPercent);
      setParameterValue("specularPower", specularPower);
      setParameterValue("ssbondsBackbone", ssbondsBackbone);
      setParameterValue("statusReporting", statusReporting);
      setParameterValue("strandCount", strandCountForStrands);
      setParameterValue("strandCountForStrands", strandCountForStrands);
      setParameterValue("strandCountForMeshRibbon", strandCountForMeshRibbon);
      setParameterValue("strutDefaultRadius", strutDefaultRadius);
      setParameterValue("strutLengthMaximum", strutLengthMaximum);
      setParameterValue("strutSpacing", strutSpacing);
      setParameterValue("strutsMultiple", strutsMultiple);
      setParameterValue("testFlag1", testFlag1);
      setParameterValue("testFlag2", testFlag2);
      setParameterValue("testFlag3", testFlag3);
      setParameterValue("testFlag4", testFlag4);
      setParameterValue("traceAlpha", traceAlpha);
      setParameterValue("useMinimizationThread", useMinimizationThread);
      setParameterValue("useNumberLocalization", useNumberLocalization);
      setParameterValue("vectorScale", vectorScale);
      setParameterValue("vibrationPeriod", vibrationPeriod);
      setParameterValue("vibrationScale", vibrationScale);
      setParameterValue("visualRange", visualRange);
      setParameterValue("wireframeRotation", wireframeRotation);
      setParameterValue("zoomLarge", zoomLarge);
      setParameterValue("zShade", zShadePower);
      setParameterValue("zeroBasedXyzRasmol", zeroBasedXyzRasmol);
    }

    

    int ambientPercent = 45;
    int diffusePercent = 84;
    boolean specular = true;
    int specularExponent = 6;  
    int phongExponent = 64;    
    int specularPercent = 22;
    int specularPower = 40;
    int zShadePower = 1;
    
    boolean slabByMolecule = false;
    boolean slabByAtom = false;

    

    boolean allowEmbeddedScripts = true;
    boolean appendNew = true;
    String appletProxy = "";
    boolean applySymmetryToBonds = false; 
    String atomTypes = "";
    boolean autoBond = true;
    boolean autoLoadOrientation = false; 
    short bondRadiusMilliAngstroms = JmolConstants.DEFAULT_BOND_MILLIANGSTROM_RADIUS;
    float bondTolerance = JmolConstants.DEFAULT_BOND_TOLERANCE;
    String defaultLoadScript = "";
    String defaultDirectory = "";


    boolean forceAutoBond = false;
    char inlineNewlineChar = '|'; 
    String loadFormat = "http://www.rcsb.org/pdb/files/%FILE.pdb";
    float minBondDistance = JmolConstants.DEFAULT_MIN_BOND_DISTANCE;
    boolean pdbGetHeader = false; 
    boolean pdbSequential = false; 
    int percentVdwAtom = JmolConstants.DEFAULT_PERCENT_VDW_ATOM;
    boolean smartAromatic = true;
    boolean zeroBasedXyzRasmol = false;

    
    String getLoadState() {
      
      
      
      
      StringBuffer str = new StringBuffer();
      appendCmd(str, "set allowEmbeddedScripts false");
      if (allowEmbeddedScripts)
        setParameterValue("allowEmbeddedScripts", true);
      appendCmd(str, "set autoBond " + autoBond);
      appendCmd(str, "set appendNew " + appendNew);
      appendCmd(str, "set appletProxy " + Escape.escape(appletProxy));
      appendCmd(str, "set applySymmetryToBonds " + applySymmetryToBonds);
      if (atomTypes.length() > 0)
        appendCmd(str, "set atomTypes " + Escape.escape(atomTypes));
      if (axesOrientationRasmol)
        appendCmd(str, "set axesOrientationRasmol true");
      appendCmd(str, "set bondRadiusMilliAngstroms " + bondRadiusMilliAngstroms);
      appendCmd(str, "set bondTolerance " + bondTolerance);
      
      appendCmd(str, "set defaultLattice " + Escape.escape(ptDefaultLattice));
      appendCmd(str, "set defaultLoadScript \"\"");
      if (defaultLoadScript.length() > 0)
        setParameterValue("defaultLoadScript", defaultLoadScript);
      String sMode = viewer.getDefaultVdwTypeNameOrData(Integer.MIN_VALUE);
      appendCmd(str, "set defaultVDW " + sMode);
      if (sMode.equals("User"))
        appendCmd(str, viewer.getDefaultVdwTypeNameOrData(Integer.MAX_VALUE));
      appendCmd(str, "set forceAutoBond " + forceAutoBond);
      appendCmd(str, "set loadFormat " + Escape.escape(loadFormat));
      if (autoLoadOrientation)
        appendCmd(str, "set autoLoadOrientation true");
      appendCmd(str, "set minBondDistance " + minBondDistance);
      appendCmd(str, "set pdbSequential " + pdbSequential);
      appendCmd(str, "set pdbGetHeader " + pdbGetHeader);
      appendCmd(str, "set percentVdwAtom " + percentVdwAtom);
      appendCmd(str, "set smartAromatic " + smartAromatic);
      if (zeroBasedXyzRasmol)
        appendCmd(str, "set zeroBasedXyzRasmol true");
      return str.toString();
    }

    private final Point3f ptDefaultLattice = new Point3f();

    void setDefaultLattice(Point3f ptLattice) {
      ptDefaultLattice.set(ptLattice);
    }

    Point3f getDefaultLattice() {
      return ptDefaultLattice;
    }

    

    boolean allowRotateSelected = false;
    boolean perspectiveDepth = true;
    float visualRange = 5f;

    

    boolean solventOn = false;

    

    String defaultAngleLabel = "%VALUE %UNITS";
    String defaultDistanceLabel = "%VALUE %UNITS"; 
    String defaultTorsionLabel = "%VALUE %UNITS";
    boolean justifyMeasurements = false;
    boolean measureAllModels = false;

    

    int minimizationSteps = 100;
    boolean minimizationRefresh = true;
    float minimizationCriterion = 0.001f;

    

    boolean antialiasDisplay = false;
    boolean antialiasImages = true;
    boolean imageState = true;
    boolean antialiasTranslucent = true;
    boolean displayCellParameters = true;
    boolean dotsSelectedOnly = false;
    boolean dotSurface = true;
    int dotDensity = 3;
    boolean dynamicMeasurements = false;
    boolean greyscaleRendering = false;
    boolean isosurfacePropertySmoothing = true;
    boolean showHiddenSelectionHalos = false;
    boolean showKeyStrokes = true;
    boolean showMeasurements = true;
    boolean zoomLarge = true; 
    String backgroundImageFileName;
    
    

    boolean bondModeOr = false;
    boolean hbondsBackbone = false;
    float hbondsAngleMinimum = 90f;
    float hbondsDistanceMaximum = 3.25f;
    boolean hbondsSolid = false;
    byte modeMultipleBond = JmolConstants.MULTIBOND_NOTSMALL;
    boolean showHydrogens = true;
    boolean showMultipleBonds = true;
    boolean ssbondsBackbone = false;

    

    boolean cartoonRockets = false;
    boolean chainCaseSensitive = false;
    int hermiteLevel = 0;
    boolean highResolutionFlag = false;
    boolean rangeSelected = false;
    boolean rasmolHydrogenSetting = true;
    boolean rasmolHeteroSetting = true;
    int ribbonAspectRatio = 16;
    boolean ribbonBorder = false;
    boolean rocketBarrels = false;
    float sheetSmoothing = 1; 
    boolean traceAlpha = true;

    

    boolean allowGestures = false;
    boolean allowMultiTouch = true; 
    boolean allowKeyStrokes = false;
    int animationFps = 10;
    boolean autoFps = false;
    boolean axesOrientationRasmol = false;
    int axesMode = JmolConstants.AXES_MODE_BOUNDBOX;
    float axesScale = 2;
    float cameraDepth = 3.0f;
    String dataSeparator = "~~~";
    boolean debugScript = false;
    float defaultDrawArrowScale = 0.5f;
    float defaultTranslucent = 0.5f;
    int delayMaximumMs = 0;
    float dipoleScale = 1.0f;
    boolean disablePopupMenu = false;
    boolean dragSelected = false;
    boolean drawHover = false;
    boolean drawPicking = false;
    boolean bondPicking = false;
    boolean atomPicking = true;
    String helpPath = JmolConstants.DEFAULT_HELP_PATH;
    boolean fontScaling = false;
    boolean fontCaching = true;
    int helixStep = 1;
    boolean hideNameInPopup = false;
    int hoverDelayMs = 500;
    float loadAtomDataTolerance = 0.01f;
    boolean logCommands = false;
    boolean logGestures = false;
    boolean measurementLabels = true;
    boolean messageStyleChime = false;
    int pickingSpinRate = 10;
    String pickLabel = "";
    float pointGroupDistanceTolerance = 0.2f;
    float pointGroupLinearTolerance = 8.0f;
    boolean preserveState = true;
    String propertyColorScheme = "roygb";
    String quaternionFrame = "p"; 
    boolean saveProteinStructureState = true;
    float solventProbeRadius = 1.2f;
    int scriptDelay = 0;
    boolean selectAllModels = true;
    boolean statusReporting = true;
    int strandCountForStrands = 5;
    int strandCountForMeshRibbon = 7;
    int strutSpacing = 6;
    float strutLengthMaximum = 7.0f;
    float strutDefaultRadius = JmolConstants.DEFAULT_STRUT_RADIUS;
    boolean strutsMultiple = false; 
    boolean useMinimizationThread = true;
    boolean useNumberLocalization = true;
    boolean useScriptQueue = true;
    boolean waitForMoveTo = true; 
    float vectorScale = 1f;
    float vibrationPeriod = 1f;
    float vibrationScale = 1f;
    boolean wireframeRotation = false;

    

    boolean hideNavigationPoint = false;
    boolean navigationMode = false;
    boolean navigateSurface = false;
    boolean navigationPeriodic = false;
    float navigationSpeed = 5;
    boolean showNavigationPointAlways = false;
    String stereoState = null;

    

    int[] objColors = new int[OBJ_MAX];
    boolean[] objStateOn = new boolean[OBJ_MAX];
    int[] objMad = new int[OBJ_MAX];

    boolean ellipsoidAxes = false;
    boolean ellipsoidDots = false;
    boolean ellipsoidArcs = false;
    boolean ellipsoidFill = false;
    boolean ellipsoidBall = true;

    int ellipsoidDotCount = 200;
    float ellipsoidAxisDiameter = 0.02f;

    String getWindowState(StringBuffer sfunc) {
      StringBuffer str = new StringBuffer();
      if (sfunc != null) {
        sfunc
            .append("  initialize;\n  set refreshing false;\n  _setWindowState;\n");
        str.append("\nfunction _setWindowState() {\n");
      }
      str.append("# height " + viewer.getScreenHeight() + ";\n# width "
          + viewer.getScreenWidth() + ";\n");
      appendCmd(str, "stateVersion = " + getParameter("_version"));
      appendCmd(str, "background " + Escape.escapeColor(objColors[0]));
      for (int i = 1; i < OBJ_MAX; i++)
        if (objColors[i] != 0)
          appendCmd(str, getObjectNameFromId(i) + "Color = \""
              + Escape.escapeColor(objColors[i]) + '"');
      if (backgroundImageFileName != null)
        appendCmd(str, "background IMAGE /*file*/" + Escape.escape(backgroundImageFileName));
      str.append(getSpecularState());
      appendCmd(str, "statusReporting  = " + statusReporting);
      if (sfunc != null)
        str.append("}\n\n");
      return str.toString();
    }

    String getSpecularState() {
      StringBuffer str = new StringBuffer("");
      appendCmd(str, "set ambientPercent " + Graphics3D.getAmbientPercent());
      appendCmd(str, "set diffusePercent " + Graphics3D.getDiffusePercent());
      appendCmd(str, "set specular " + Graphics3D.getSpecular());
      appendCmd(str, "set specularPercent " + Graphics3D.getSpecularPercent());
      appendCmd(str, "set specularPower " + Graphics3D.getSpecularPower());
      int se = Graphics3D.getSpecularExponent();
      int pe = Graphics3D.getPhongExponent();
      if (Math.pow(2, se) == pe)
        appendCmd(str, "set specularExponent " + se);
      else
        appendCmd(str, "set phongExponent " + pe);        
      appendCmd(str, "set zShadePower " + Graphics3D.getZShadePower());
      return str.toString();
    }

    

    boolean testFlag1 = false;
    boolean testFlag2 = false;
    boolean testFlag3 = false;
    boolean testFlag4 = false;

    

    private String measureDistanceUnits = "nanometers";

    void setMeasureDistanceUnits(String units) {
      if (units.equalsIgnoreCase("angstroms"))
        measureDistanceUnits = "angstroms";
      else if (units.equalsIgnoreCase("nanometers")
          || units.equalsIgnoreCase("nm"))
        measureDistanceUnits = "nanometers";
      else if (units.equalsIgnoreCase("picometers")
          || units.equalsIgnoreCase("pm"))
        measureDistanceUnits = "picometers";
      else if (units.equalsIgnoreCase("bohr") || units.equalsIgnoreCase("au"))
        measureDistanceUnits = "au";
      setParameterValue("measurementUnits", measureDistanceUnits);
    }

    String getMeasureDistanceUnits() {
      return measureDistanceUnits;
    }

    boolean isJmolVariable(String key) {
      return key.charAt(0) == '_'
          || htNonbooleanParameterValues.containsKey(key = key.toLowerCase())
          || htBooleanParameterFlags.containsKey(key)
          || unreportedProperties.indexOf(";" + key + ";") >= 0;
    }

    private void resetParameterStringValue(String name, GlobalSettings g) {
      setParameterValue(name, g == null ? "" : (String) g.getParameter(name));
    }
    
    void setParameterValue(String name, boolean value) {
      name = name.toLowerCase();
      if (htNonbooleanParameterValues.containsKey(name))
        return; 
      htBooleanParameterFlags.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    void setParameterValue(String name, int value) {
      name = name.toLowerCase();
      if (htBooleanParameterFlags.containsKey(name))
        return; 
      htNonbooleanParameterValues.put(name, new Integer(value));
    }

    void setParameterValue(String name, float value) {
      if (Float.isNaN(value))
        return;
      name = name.toLowerCase();
      if (htBooleanParameterFlags.containsKey(name))
        return; 
      htNonbooleanParameterValues.put(name, new Float(value));
    }

    void setParameterValue(String name, String value) {
      name = name.toLowerCase();
      if (value == null || htBooleanParameterFlags.containsKey(name))
        return; 
      htNonbooleanParameterValues.put(name, value);
    }

    void removeJmolParameter(String key) {
      if (htBooleanParameterFlags.containsKey(key)) {
        htBooleanParameterFlags.remove(key);
        if (!htPropertyFlagsRemoved.containsKey(key))
          htPropertyFlagsRemoved.put(key, Boolean.FALSE);
        return;
      }
      if (htNonbooleanParameterValues.containsKey(key))
        htNonbooleanParameterValues.remove(key);
    }

    ScriptVariable setUserVariable(String key, ScriptVariable var) {
      if (var == null) 
        return null;
      key = key.toLowerCase();
      htUserVariables.put(key, var.setName(key).setGlobal());
      return var;
    }

    void unsetUserVariable(String key) {
      key = key.toLowerCase();
        if (key.equals("all") || key.equals("variables")) {
          htUserVariables.clear();
          Logger.info("all user-defined variables deleted");
        } else if (htUserVariables.containsKey(key)) {
          Logger.info("variable " + key + " deleted");
          htUserVariables.remove(key);
        }
    }

    void removeUserVariable(String key) {
      htUserVariables.remove(key);
    }

    ScriptVariable getUserVariable(String name) {
      if (name == null)
        return null;
      name = name.toLowerCase();
      return (ScriptVariable) htUserVariables.get(name);
    }

    String getParameterEscaped(String name, int nMax) {
      name = name.toLowerCase();
      if (htNonbooleanParameterValues.containsKey(name)) {
        Object v = htNonbooleanParameterValues.get(name);
        return varClip(name, Escape.escape(v), nMax);
      }
      if (htBooleanParameterFlags.containsKey(name))
        return htBooleanParameterFlags.get(name).toString();
      if (htUserVariables.containsKey(name))
        return ((ScriptVariable) htUserVariables.get(name)).escape();
      if (htPropertyFlagsRemoved.containsKey(name))
        return "false";
      return "<not defined>";
    }

    
    Object getParameter(String name) {
      Object v = getParameter(name, false);
      return (v == null ? "" : v);
    }

    
    ScriptVariable getOrSetNewVariable(String name, boolean doSet) {
      if (name == null || name.length() == 0)
        name = "x";
      Object v = getParameter(name, true);
      return (v == null && doSet && name.charAt(0) != '_' ?
        setUserVariable(name, new ScriptVariable())
         : ScriptVariable.getVariable(v));
    }

    Object getParameter(String name, boolean asVariable) {
      name = name.toLowerCase();
      if (name.equals("_memory")) {
        Runtime runtime = Runtime.getRuntime();
        float bTotal = runtime.totalMemory() / 1000000f;
        float bFree = runtime.freeMemory() / 1000000f;
        String value = TextFormat.formatDecimal(bTotal - bFree, 1) + "/"
            + TextFormat.formatDecimal(bTotal, 1);
        htNonbooleanParameterValues.put("_memory", value);
      }
      if (htNonbooleanParameterValues.containsKey(name))
        return htNonbooleanParameterValues.get(name);
      if (htBooleanParameterFlags.containsKey(name))
        return htBooleanParameterFlags.get(name);
      if (htPropertyFlagsRemoved.containsKey(name))
        return Boolean.FALSE;
      if (htUserVariables.containsKey(name)) {
        ScriptVariable v = (ScriptVariable) htUserVariables.get(name);
        return (asVariable ? v : ScriptVariable.oValue(v));
      }
      return null;
    }

    String getAllSettings(String prefix) {
      StringBuffer commands = new StringBuffer("");
      Enumeration e;
      String key;
      String[] list = new String[htBooleanParameterFlags.size()
          + htNonbooleanParameterValues.size()];
      
      int n = 0;
      String _prefix = "_" + prefix;
      e = htBooleanParameterFlags.keys();
      while (e.hasMoreElements()) {
        key = (String) e.nextElement();
        if (prefix == null || key.indexOf(prefix) == 0
            || key.indexOf(_prefix) == 0)
          list[n++] = (key.indexOf("_") == 0 ? key + " = " : "set " + key + " ")
              + htBooleanParameterFlags.get(key);
      }
      
      e = htNonbooleanParameterValues.keys();
      while (e.hasMoreElements()) {
        key = (String) e.nextElement();
        if (key.charAt(0) != '@'
            && (prefix == null || key.indexOf(prefix) == 0 || key
                .indexOf(_prefix) == 0)) {
          Object value = htNonbooleanParameterValues.get(key);
          if (value instanceof String)
            value = Escape.escapeChopped((String) value);
          list[n++] = (key.indexOf("_") == 0 ? key + " = " : "set " + key + " ")
              + value;
        }
      }
      Arrays.sort(list, 0, n);
      for (int i = 0; i < n; i++)
        if (list[i] != null)
          appendCmd(commands, list[i]);
      commands.append("\n");
      return commands.toString();
    }

    String getState(StringBuffer sfunc) {
      String[] list = new String[htBooleanParameterFlags.size()
          + htNonbooleanParameterValues.size()];
      StringBuffer commands = new StringBuffer();
      if (sfunc != null) {
        sfunc.append("  _setVariableState;\n");
        commands.append("function _setVariableState() {\n\n");
      }
      int n = 0;
      Enumeration e;
      String key;
      
      e = htBooleanParameterFlags.keys();
      while (e.hasMoreElements()) {
        key = (String) e.nextElement();
        if (doReportProperty(key))
          list[n++] = "set " + key + " " + htBooleanParameterFlags.get(key);
      }
      e = htNonbooleanParameterValues.keys();
      while (e.hasMoreElements()) {
        key = (String) e.nextElement();
        if (key.charAt(0) != '@' && doReportProperty(key)) {
          Object value = htNonbooleanParameterValues.get(key);
          if (key.charAt(0) == '=') {
            
            
            key = key.substring(1);
          } else {
            if (key.indexOf("default") == 0)
              key = " set " + key;
            else
              key = "set " + key;
            value = Escape.escape(value);
          }
          list[n++] = key + " " + value;
        }
      }
      switch (axesMode) {
      case JmolConstants.AXES_MODE_UNITCELL:
        list[n++] = "set axes unitcell";
        break;
      case JmolConstants.AXES_MODE_BOUNDBOX:
        list[n++] = "set axes window";
        break;
      default:
        list[n++] = "set axes molecular";
      }

      
      e = htNonbooleanParameterValues.keys();
      while (e.hasMoreElements()) {
        key = (String) e.nextElement();
        if (key.charAt(0) == '@')
          list[n++] = key + " " + htNonbooleanParameterValues.get(key);
      }
      Arrays.sort(list, 0, n);
      for (int i = 0; i < n; i++)
        if (list[i] != null)
          appendCmd(commands, list[i]);

      commands.append("\n#user-defined variables; \n");
      commands.append(StateManager.getVariableList(htUserVariables, 0));

      

      viewer.loadShape(JmolConstants.SHAPE_LABELS);
      commands.append(viewer.getShapeProperty(JmolConstants.SHAPE_LABELS,
          "defaultState"));

      if (sfunc != null)
        commands.append("\n}\n\n");
      return commands.toString();
    }

    private boolean doReportProperty(String name) {
      return (name.charAt(0) != '_' && unreportedProperties.indexOf(";" + name
          + ";") < 0);
    }

    String getVariableList() {
      return StateManager.getVariableList(htUserVariables, 0);
    }

  }

  

  public static void setStateInfo(Hashtable ht, int i1, int i2, String key) {
    BitSet bs;
    if (ht.containsKey(key)) {
      bs = (BitSet) ht.get(key);
    } else {
      bs = new BitSet();
      ht.put(key, bs);
    }
    for (int i = i1; i <= i2; i++)
      bs.set(i);
  }

  public static String varClip(String name, String sv, int nMax) {
    if (nMax > 0 && sv.length() > nMax)
      sv = sv.substring(0, nMax) + " #...more (" + sv.length()
          + " bytes -- use SHOW " + name + " or MESSAGE @" + name
          + " to view)";
    return sv;
  }

  public static String getCommands(Hashtable ht) {
    return getCommands(ht, null, -1, "select");
  }

  public static String getCommands(Hashtable htDefine, Hashtable htMore,
                                   int nAll) {
    return getCommands(htDefine, htMore, nAll, "select");
  }

  public static String getCommands(Hashtable htDefine, Hashtable htMore,
                                   int nAll, String selectCmd) {
    StringBuffer s = new StringBuffer();
    String setPrev = getCommands(htDefine, s, null, nAll, selectCmd);
    if (htMore != null)
      getCommands(htMore, s, setPrev, nAll, "select");
    return s.toString();
  }

  public static String getCommands(Hashtable ht, StringBuffer s,
                                   String setPrev, int nAll, String selectCmd) {
    if (ht == null)
      return "";
    String strAll = "({0:" + (nAll - 1) + "})";
    Enumeration e = ht.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      String set = Escape.escape((BitSet) ht.get(key));
      if (set.length() < 5) 
        continue;
      set = selectCmd + " " + (set.equals(strAll) && false ? "*" : set);
      if (!set.equals(setPrev))
        appendCmd(s, set);
      setPrev = set;
      if (key.indexOf("-") != 0) 
        appendCmd(s, key);
    }
    return setPrev;
  }

  public static void appendCmd(StringBuffer s, String cmd) {
    if (cmd.length() == 0)
      return;
    s.append("  ").append(cmd).append(";\n");
  }
}
