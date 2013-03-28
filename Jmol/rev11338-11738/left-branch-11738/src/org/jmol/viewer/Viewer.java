
package org.jmol.viewer;

import org.jmol.popup.JmolPopup;
import org.jmol.shape.Shape;
import org.jmol.i18n.GT;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.modelset.AtomIndexIterator;
import org.jmol.modelset.Bond;
import org.jmol.modelset.BoxInfo;
import org.jmol.modelset.MeasurementPending;
import org.jmol.modelset.ModelLoader;
import org.jmol.modelset.ModelSet;
import org.jmol.modelset.ModelCollection.StateScript;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.*;
import org.jmol.atomdata.AtomData;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.g3d.*;
import org.jmol.util.Base64;
import org.jmol.util.BitSetUtil;
import org.jmol.util.CifDataReader;
import org.jmol.util.CommandHistory;
import org.jmol.util.Escape;
import org.jmol.util.JpegEncoder;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Point3fi;
import org.jmol.util.Quaternion;
import org.jmol.util.TempArray;
import org.jmol.util.TextFormat;
import org.jmol.viewer.StateManager.Orientation;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Event; 
import java.util.Hashtable;
import java.util.BitSet;
import java.util.Properties;
import java.util.Vector;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point4f;
import javax.vecmath.Point3i;
import javax.vecmath.Matrix4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.AxisAngle4f;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;



public class Viewer extends JmolViewer implements AtomDataServer {

  protected void finalize() throws Throwable {
    Logger.debug("viewer finalize " + this);
    super.finalize();
  }

  
  

  private Component display;
  private Graphics3D g3d;
  private JmolAdapter modelAdapter;

  public JmolAdapter getModelAdapter() {
    if (modelAdapter == null)
      modelAdapter = new SmarterJmolAdapter();
    return modelAdapter;
  }

  private CommandHistory commandHistory = new CommandHistory();
  private ColorManager colorManager;

  ScriptCompiler compiler;
  Hashtable definedAtomSets;

  private MinimizerInterface minimizer;

  MinimizerInterface getMinimizer(boolean createNew) {
    if (minimizer == null && createNew) {
      minimizer = (MinimizerInterface) Interface
          .getOptionInterface("minimize.Minimizer");
      minimizer.setProperty("viewer", this);
    }
    return minimizer;
  }

  private SmilesMatcherInterface smilesMatcher;

  SmilesMatcherInterface getSmilesMatcher() {
    if (smilesMatcher == null) {
      smilesMatcher = (SmilesMatcherInterface) Interface
          .getOptionInterface("smiles.PatternMatcher");
    }
    smilesMatcher.setModelSet(modelSet);
    return smilesMatcher;
  }

  private SymmetryInterface symmetry;

  public SymmetryInterface getSymmetry() {
    if (symmetry == null)
      symmetry = (SymmetryInterface) Interface
          .getOptionInterface("symmetry.Symmetry");
    return symmetry;
  }

  public Object getSymmetryInfo(BitSet bsAtoms, String xyz, int op, Point3f pt,
                                String id, int type) {
    return modelSet.getSymmetryInfo(bsAtoms, xyz, op, pt, id, type);
  }
  
  private void clearModelDependentObjects() {
    setFrameOffsets(null);
    if (minimizer != null) {
      minimizer.setProperty("clear", null);
      minimizer = null;
    }
    if (smilesMatcher != null) {
      smilesMatcher.setModelSet(null);
      smilesMatcher = null;
    }
    if (symmetry != null) {
      symmetry = null;
    }
  }

  ScriptEvaluator eval;
  private AnimationManager animationManager;
  private DataManager dataManager;
  private FileManager fileManager;
  private ModelManager modelManager;
  private ModelSet modelSet;
  public MouseManager mouseManager;
  private PickingManager pickingManager;
  private RepaintManager repaintManager;
  private ScriptManager scriptManager;
  private SelectionManager selectionManager;
  private StateManager stateManager;
  private StateManager.GlobalSettings global;

  StateManager.GlobalSettings getGlobalSettings() {
    return global;
  }

  private StatusManager statusManager;
  private TempArray tempManager;
  private TransformManager transformManager;

  private String strJavaVendor;
  private String strJavaVersion;
  private String strOSName;
  private String htmlName = "";

  private String fullName = "";
  private String syncId = "";
  private String appletDocumentBase = "";
  private String appletCodeBase = "";

  private boolean jvm11orGreater = false;
  private boolean jvm12orGreater = false;
  private boolean jvm14orGreater = false;

  public Viewer(Component display, JmolAdapter modelAdapter) {
    if (Logger.debugging) {
      Logger.debug("Viewer constructor " + this);
    }
    this.display = display;
    this.modelAdapter = modelAdapter;
    strJavaVendor = System.getProperty("java.vendor");
    strOSName = System.getProperty("os.name");
    strJavaVersion = System.getProperty("java.version");
    
    jvm11orGreater = (strJavaVersion.compareTo("1.1") >= 0 && !(strJavaVendor
        .startsWith("Netscape")
        && strJavaVersion.compareTo("1.1.5") <= 0 && "Mac OS".equals(strOSName)));
    jvm12orGreater = (strJavaVersion.compareTo("1.2") >= 0);
    jvm14orGreater = (strJavaVersion.compareTo("1.4") >= 0);
    stateManager = new StateManager(this);
    g3d = new Graphics3D(display);
    colorManager = new ColorManager(this, g3d);
    statusManager = new StatusManager(this);
    scriptManager = new ScriptManager(this);
    transformManager = new TransformManager11(this);
    selectionManager = new SelectionManager(this);
    pickingManager = new PickingManager(this);
    if (display != null) {
      if (jvm14orGreater)
        mouseManager = MouseWrapper14.alloc(display, this);
      else if (jvm11orGreater)
        mouseManager = MouseWrapper11.alloc(display, this);
      else
        mouseManager = new MouseManager10(display, this);
    }
    modelManager = new ModelManager(this);
    tempManager = new TempArray();
    dataManager = new DataManager();
    animationManager = new AnimationManager(this);
    repaintManager = new RepaintManager(this);
    initialize();
    fileManager = new FileManager(this);
    compiler = new ScriptCompiler(this);
    definedAtomSets = new Hashtable();
    eval = new ScriptEvaluator(this);
  }

  

  public static JmolViewer allocateViewer(Component display,
                                          JmolAdapter modelAdapter,
                                          String fullName, URL documentBase,
                                          URL codeBase, String commandOptions,
                                          JmolStatusListener statusListener) {
    JmolViewer viewer = new Viewer(display, modelAdapter);
    viewer.setAppletContext(fullName, documentBase, codeBase, commandOptions);
    viewer.setJmolStatusListener(statusListener);
    return viewer;
  }

  
  public static JmolViewer allocateViewer(Component display,
                                          JmolAdapter modelAdapter) {
    return new Viewer(display, modelAdapter);
  }

  private boolean isSilent = false;
  private boolean isApplet = false;

  public boolean isApplet() {
    return isApplet;
  }

  private boolean isPreviewOnly = false;

  boolean isPreviewOnly() {
    return isPreviewOnly;
  }

  boolean autoExit = false;
  boolean haveDisplay = true;
  private boolean isPrintOnly = false;
  private boolean mustRender = true;
  private boolean isCmdLine_c_or_C_Option = false;
  private boolean listCommands = false;
  private boolean isCmdLine_C_Option = true;
  private boolean useCommandThread = false;
  private boolean isSignedApplet = false;
  private boolean isDataOnly;

  public void setAppletContext(String fullName, URL documentBase, URL codeBase,
                               String commandOptions) {
    this.fullName = fullName = (fullName == null ? "" : fullName);
    appletDocumentBase = (documentBase == null ? "" : documentBase.toString());
    appletCodeBase = (codeBase == null ? "" : codeBase.toString());
    int i = fullName.lastIndexOf("[");
    htmlName = (i < 0 ? fullName : fullName.substring(0, i));
    syncId = (i < 0 ? "" : fullName.substring(i + 1, fullName.length() - 1));
    if (commandOptions == null)
      commandOptions = "";
    String str = "" + commandOptions;
    isPrintOnly = (commandOptions.indexOf("-p") >= 0);
    isApplet = (commandOptions.indexOf("-applet") >= 0);
    if (isApplet) {
      Logger.info("applet context: " + commandOptions);
      String appletProxy = null;
      
      if ((i = str.indexOf("-appletProxy ")) >= 0) {
        appletProxy = str.substring(i + 13);
        str = str.substring(0, i);
      }
      fileManager.setAppletContext(documentBase, codeBase, appletProxy);
      isSignedApplet = (str.indexOf("-signed") >= 0);
      if ((i = str.indexOf("-maximumSize ")) >= 0)
        setMaximumSize(Parser.parseInt(str.substring(i + 13)));
      useCommandThread = (str.indexOf("-threaded") >= 0);
      if (useCommandThread)
        scriptManager.startCommandWatcher(true);
    } else {
      
      g3d.setBackgroundTransparent(str.indexOf("-b") >= 0);
      isSilent = (str.indexOf("-i") >= 0);
      if (isSilent)
        Logger.setLogLevel(Logger.LEVEL_WARN); 
                                               
      isCmdLine_c_or_C_Option = (str.toLowerCase().indexOf("-c") >= 0);
      isCmdLine_C_Option = (str.indexOf("-C") >= 0);
      listCommands = (str.indexOf("-l") >= 0);
      autoExit = (str.indexOf("-x") >= 0);
      isDataOnly = (display == null);
      haveDisplay = (display != null && str.indexOf("-n") < 0);
      if (!haveDisplay)
        display = null;
      mustRender = haveDisplay;
      cd(".");
    }
    isPreviewOnly = (str.indexOf("#previewOnly") >= 0);
    setBooleanProperty("_applet", isApplet);
    setBooleanProperty("_signedApplet", isSignedApplet);
    setBooleanProperty("_useCommandThread", useCommandThread);

    
    if (!isSilent) {
      Logger.info(JmolConstants.copyright
          + "\nJmol Version "
          + getJmolVersion()
          + "\njava.vendor:"
          + strJavaVendor
          + "\njava.version:"
          + strJavaVersion
          + "\nos.name:"
          + strOSName
          + "\nmemory:"
          + getParameter("_memory")
          + "\nuseCommandThread: "
          + useCommandThread
          + (!isApplet ? "" : "\nappletId:" + htmlName
              + (isSignedApplet ? " (signed)" : "")));
    }

    zap(false, false); 
    global.setParameterValue("language", GT.getLanguage());
  }

  public boolean isDataOnly() {
    return isDataOnly;
  }
  
  public static String getJmolVersion() {
    return JmolConstants.version + "  " + JmolConstants.date;
  }

  public String getExportDriverList() {
    return (String) global.getParameter("exportDrivers");
  }

  private static int getJmolVersionInt() {
    
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

  String getHtmlName() {
    return htmlName;
  }

  boolean mustRenderFlag() {
    return mustRender && refreshing;
  }

  static int getLogLevel() {
    for (int i = 0; i < Logger.LEVEL_MAX; i++)
      if (Logger.isActiveLevel(i))
        return Logger.LEVEL_MAX - i;
    return 0;
  }

  public Component getDisplay() {
    return display;
  }

  public boolean handleOldJvm10Event(Event e) {
    return mouseManager.handleOldJvm10Event(e);
  }

  void reset() {
    
    
    modelSet.calcBoundBoxDimensions(null);
    axesAreTainted = true;
    transformManager.homePosition();
    if (modelSet.setCrystallographicDefaults())
      stateManager.setCrystallographicDefaults();
    else
      setAxesModeMolecular(false);
    prevFrame = Integer.MIN_VALUE;
    refresh(1, "Viewer:homePosition()");
  }

  public void homePosition() {
    evalString("reset");
  }

  

  Hashtable getAppletInfo() {
    Hashtable info = new Hashtable();
    info.put("htmlName", htmlName);
    info.put("syncId", syncId);
    info.put("fullName", fullName);
    if (isApplet) {
      info.put("documentBase", appletDocumentBase);
      info.put("codeBase", appletCodeBase);
      info.put("registry", statusManager.getRegistryInfo());
    }
    info.put("version", JmolConstants.version);
    info.put("date", JmolConstants.date);
    info.put("javaVendor", strJavaVendor);
    info.put("javaVersion", strJavaVersion);
    info.put("operatingSystem", strOSName);
    return info;
  }

  
  
  

  void initialize() {
    global = stateManager.getGlobalSettings(global);
    setIntProperty("_version", getJmolVersionInt(), true);
    setBooleanProperty("_applet", isApplet);
    setBooleanProperty("_signedApplet", isSignedApplet);
    setBooleanProperty("_useCommandThread", useCommandThread);
    colorManager.resetElementColors();
    setObjectColor("background", "black");
    setObjectColor("axis1", "red");
    setObjectColor("axis2", "green");
    setObjectColor("axis3", "blue");

    

    setAmbientPercent(global.ambientPercent);
    setDiffusePercent(global.diffusePercent);
    setSpecular(global.specular);
    setSpecularPercent(global.specularPercent);
    setSpecularExponent(global.specularExponent);
    setSpecularPower(global.specularPower);

    if (modelSet != null)
      animationManager.setAnimationOn(false);
    animationManager.setAnimationFps(global.animationFps);

    statusManager.setAllowStatusReporting(global.statusReporting);
    setBooleanProperty("antialiasDisplay", global.antialiasDisplay);

    setTransformManagerDefaults();

  }

  String listSavedStates() {
    return stateManager.listSavedStates();
  }

  public void saveOrientation(String saveName) {
    
    stateManager.saveOrientation(saveName);
  }

  public boolean restoreOrientation(String saveName, float timeSeconds) {
    
    return stateManager.restoreOrientation(saveName, timeSeconds, true);
  }

  public void restoreRotation(String saveName, float timeSeconds) {
    stateManager.restoreOrientation(saveName, timeSeconds, false);
  }

  void saveModelOrientation() {
    modelSet.saveModelOrientation(animationManager.currentModelIndex,
        stateManager.getOrientation());
  }

  public Orientation getOrientation() {
    return stateManager.getOrientation();
  }
  
  void restoreModelOrientation(int modelIndex) {
    StateManager.Orientation o = modelSet.getModelOrientation(modelIndex);
    if (o != null)
      o.restore(-1, true);
  }

  void restoreModelRotation(int modelIndex) {
    StateManager.Orientation o = modelSet.getModelOrientation(modelIndex);
    if (o != null)
      o.restore(-1, false);
  }

  void saveBonds(String saveName) {
    
    stateManager.saveBonds(saveName);
  }

  boolean restoreBonds(String saveName) {
    
    clearModelDependentObjects();
    return stateManager.restoreBonds(saveName);
  }

  void saveState(String saveName) {
    
    stateManager.saveState(saveName);
  }

  public String getSavedState(String saveName) {
    return stateManager.getSavedState(saveName);
  }

  void saveStructure(String saveName) {
    
    stateManager.saveStructure(saveName);
  }

  String getSavedStructure(String saveName) {
    return stateManager.getSavedStructure(saveName);
  }

  public void saveCoordinates(String saveName, BitSet bsSelected) {
    
    stateManager.saveCoordinates(saveName, bsSelected);
  }

  String getSavedCoordinates(String saveName) {
    return stateManager.getSavedCoordinates(saveName);
  }

  void saveSelection(String saveName) {
    
    stateManager.saveSelection(saveName, selectionManager.bsSelection);
    stateManager.restoreSelection(saveName); 
                                             
  }

  boolean restoreSelection(String saveName) {
    
    return stateManager.restoreSelection(saveName);
  }

  
  
  

  public Matrix4f getMatrixtransform() {
    return transformManager.getMatrixtransform();
  }

  Quaternion getRotationQuaternion() {
    return transformManager.getRotationQuaternion();
  }
  
  public float getRotationRadius() {
    return transformManager.getRotationRadius();
  }

  void setRotationRadius(float angstroms, boolean doAll) {
    if (doAll)
      angstroms = transformManager.setRotationRadius(angstroms, false);
    if (!modelSet
        .setRotationRadius(animationManager.currentModelIndex, angstroms))
      global.setParameterValue("rotationRadius", angstroms);
  }

  public Point3f getRotationCenter() {
    return transformManager.getRotationCenter();
  }

  void setCenterAt(String relativeTo, Point3f pt) {
    
    if (isJmolDataFrame())
      return;
    transformManager.setCenterAt(relativeTo, pt);
  }

  void setCenterBitSet(BitSet bsCenter, boolean doScale) {
    
    

    Point3f center = (bsCenter != null
        && BitSetUtil.cardinalityOf(bsCenter) > 0 ? getAtomSetCenter(bsCenter)
        : null);
    if (isJmolDataFrame())
      return;
    transformManager.setNewRotationCenter(center, doScale);
  }

  void setNewRotationCenter(Point3f center) {
    
    if (isJmolDataFrame())
      return;
    transformManager.setNewRotationCenter(center, true);
  }

  public Point3f getNavigationCenter() {
    return transformManager.getNavigationCenter();
  }

  public float getNavigationDepthPercent() {
    return transformManager.getNavigationDepthPercent();
  }

  void navigate(int keyWhere, int modifiers) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(keyWhere, modifiers);
    if (!transformManager.vibrationOn)
      refresh(1, "Viewer:navigate()");
  }

  public Point3f getNavigationOffset() {
    return transformManager.getNavigationOffset();
  }

  float getNavigationOffsetPercent(char XorY) {
    return transformManager.getNavigationOffsetPercent(XorY);
  }

  public boolean isNavigating() {
    return transformManager.isNavigating();
  }

  boolean isInPosition(Vector3f axis, float degrees) {
    return transformManager.isInPosition(axis, degrees);
  }

  void move(Vector3f dRot, float dZoom, Vector3f dTrans, float dSlab,
            float floatSecondsTotal, int fps) {
    
    transformManager.move(dRot, dZoom, dTrans, dSlab, floatSecondsTotal, fps);
    moveUpdate(floatSecondsTotal);
  }

  void moveTo(float floatSecondsTotal, Point3f center, Vector3f rotAxis,
              float degrees, float zoom, float xTrans, float yTrans,
              float rotationRadius, Point3f navCenter, float xNav, float yNav,
              float navDepth) {
    
    transformManager.moveTo(floatSecondsTotal, center, rotAxis, degrees, zoom,
        xTrans, yTrans, rotationRadius, navCenter, xNav, yNav, navDepth);
    moveUpdate(floatSecondsTotal);
  }

  void moveTo(float floatSecondsTotal, Point3f center, Matrix3f rotationMatrix, 
              float zoom, float xTrans, float yTrans, float rotationRadius,
              Point3f navCenter, float xNav, float yNav, float navDepth) {
    
    transformManager.moveTo(floatSecondsTotal, center, rotationMatrix, zoom,
        xTrans, yTrans, rotationRadius, navCenter, xNav, yNav, navDepth);
    moveUpdate(floatSecondsTotal);
  }

  private void moveUpdate(float floatSecondsTotal) {
    if (floatSecondsTotal > 0)
      requestRepaintAndWait();
    else if (floatSecondsTotal == 0)
      setSync();
  }

  String getMoveToText(float timespan) {
    return transformManager.getMoveToText(timespan, false);
  }

  void navigate(float timeSeconds, Point3f[] path, float[] theta,
                int indexStart, int indexEnd) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, path, theta, indexStart, indexEnd);
    moveUpdate(timeSeconds);
  }

  public void navigate(float timeSeconds, Point3f center) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, center);
    moveUpdate(timeSeconds);
  }

  void navigate(float timeSeconds, Point3f[][] pathGuide) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, pathGuide);
    moveUpdate(timeSeconds);
  }

  void navigateSurface(float timeSeconds, String name) {
    if (isJmolDataFrame())
      return;
    transformManager.navigateSurface(timeSeconds, name);
    moveUpdate(timeSeconds);
  }

  public void navigate(float timeSeconds, Vector3f rotAxis, float degrees) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, rotAxis, degrees);
    moveUpdate(timeSeconds);
  }

  void navTranslate(float timeSeconds, Point3f center) {
    if (isJmolDataFrame())
      return;
    transformManager.navTranslate(timeSeconds, center);
    moveUpdate(timeSeconds);
  }

  void navTranslatePercent(float timeSeconds, float x, float y) {
    if (isJmolDataFrame())
      return;
    transformManager.navTranslatePercent(timeSeconds, x, y);
    moveUpdate(timeSeconds);
  }

  private boolean mouseEnabled = true;
  public void setMouseEnabled(boolean TF) {
    mouseEnabled = TF;
  }
  
  void zoomBy(int pixels) {
    
    if (mouseEnabled)
      transformManager.zoomBy(pixels);
    refresh(2, statusManager.syncingMouse ? "Mouse: zoomBy " + pixels : "");
  }

  void zoomByFactor(float factor) {
    
    if (mouseEnabled)
      transformManager.zoomByFactor(factor);
    refresh(2, statusManager.syncingMouse ? "Mouse: zoomByFactor " + factor
        : "");
  }

  void rotateXYBy(int xDelta, int yDelta) {
    
    if (mouseEnabled)
      transformManager.rotateXYBy(xDelta, yDelta, null);
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateXYBy " + xDelta + " "
        + yDelta : "");
  }

  void rotateZBy(int zDelta) {
    
    if (mouseEnabled)
      transformManager.rotateZBy(zDelta);
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateZBy " + zDelta : "");
  }

  void rotateMolecule(int deltaX, int deltaY) {
    if (isJmolDataFrame())
      return;
    if (mouseEnabled) {
      transformManager.setRotateMolecule(true);
      transformManager.rotateXYBy(deltaX, deltaY, selectionManager.bsSelection);
      transformManager.setRotateMolecule(false);
      refreshMeasures();
    }
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateMolecule " + deltaX
        + " " + deltaY : "");
  }

  void translateXYBy(int xDelta, int yDelta) {
    
    if (mouseEnabled)
      transformManager.translateXYBy(xDelta, yDelta);
    refresh(2, statusManager.syncingMouse ? "Mouse: translateXYBy " + xDelta
        + " " + yDelta : "");
  }

  public void rotateFront() {
    
    transformManager.rotateFront();
    refresh(1, "Viewer:rotateFront()");
  }

  public void rotateX(float angleRadians) {
    
    transformManager.rotateX(angleRadians);
    refresh(1, "Viewer:rotateX()");
  }

  public void rotateY(float angleRadians) {
    
    transformManager.rotateY(angleRadians);
    refresh(1, "Viewer:rotateY()");
  }

  public void rotateZ(float angleRadians) {
    
    transformManager.rotateZ(angleRadians);
    refresh(1, "Viewer:rotateZ()");
  }

  public void rotateX(int angleDegrees) {
    
    rotateX(angleDegrees * Measure.radiansPerDegree);
  }

  public void rotateY(int angleDegrees) {
    
    rotateY(angleDegrees * Measure.radiansPerDegree);
  }

  void translate(char xyz, float x, char type) {
    int xy = (type == '\0' ? 0 : type == '%' ? transformManager
        .percentToPixels(xyz, x) : transformManager.angstromsToPixels(x
        * (type == 'n' ? 10f : 1f)));
    switch (xyz) {
    case 'x':
      if (type == '\0')
        transformManager.translateToXPercent(x);
      else
        transformManager.translateXYBy(xy, 0);
      break;
    case 'y':
      if (type == '\0')
        transformManager.translateToYPercent(x);
      else
        transformManager.translateXYBy(0, xy);
      break;
    case 'z':
      if (type == '\0')
        transformManager.translateToZPercent(x);
      else
        transformManager.translateZBy(xy);
      break;
    }
    refresh(1, "Viewer:translate()");
  }

  public float getTranslationXPercent() {
    return transformManager.getTranslationXPercent();
  }

  public float getTranslationYPercent() {
    return transformManager.getTranslationYPercent();
  }

  float getTranslationZPercent() {
    return transformManager.getTranslationZPercent();
  }

  public String getTranslationScript() {
    return transformManager.getTranslationScript();
  }

  public int getZoomPercent() {
    
    return (int) getZoomSetting();
  }

  float getZoomSetting() {
    return transformManager.getZoomSetting();
  }

  public float getZoomPercentFloat() {
    
    return transformManager.getZoomPercentFloat();
  }

  float getMaxZoomPercent() {
    return TransformManager.MAXIMUM_ZOOM_PERCENTAGE;
  }

  void slabReset() {
    transformManager.slabReset();
  }

  boolean getZoomEnabled() {
    return transformManager.zoomEnabled;
  }

  public boolean getSlabEnabled() {
    return transformManager.slabEnabled;
  }

  void slabByPixels(int pixels) {
    
    transformManager.slabByPercentagePoints(pixels);
  }

  void depthByPixels(int pixels) {
    
    transformManager.depthByPercentagePoints(pixels);
  }

  void slabDepthByPixels(int pixels) {
    
    transformManager.slabDepthByPercentagePoints(pixels);
  }

  void slabToPercent(int percentSlab) {
    
    transformManager.slabToPercent(percentSlab);
  }

  void slabInternal(Point4f plane, boolean isDepth) {
    transformManager.slabInternal(plane, isDepth);
  }

  void depthToPercent(int percentDepth) {
    
    transformManager.depthToPercent(percentDepth);
  }

  void setSlabDepthInternal(boolean isDepth) {
    transformManager.setSlabDepthInternal(isDepth);
  }

  public int zValueFromPercent(int zPercent) {
    return transformManager.zValueFromPercent(zPercent);
  }

  public Matrix4f getUnscaledTransformMatrix() {
    return transformManager.getUnscaledTransformMatrix();
  }

  void finalizeTransformParameters() {
    
    

    transformManager.finalizeTransformParameters();
    g3d.setSlabAndDepthValues(transformManager.slabValue,
        transformManager.depthValue, global.zShade, 
        transformManager.zSlabValue, transformManager.zDepthValue);
  }

  public void rotatePoint(Point3f pt, Point3f ptRot) {
    transformManager.rotatePoint(pt, ptRot);
  }

  public Point3i transformPoint(Point3f pointAngstroms) {
    return transformManager.transformPoint(pointAngstroms);
  }

  public Point3i transformPoint(Point3f pointAngstroms, Vector3f vibrationVector) {
    return transformManager.transformPoint(pointAngstroms, vibrationVector);
  }

  public void transformPoint(Point3f pointAngstroms, Point3i pointScreen) {
    transformManager.transformPoint(pointAngstroms, pointScreen);
  }

  public void transformPointNoClip(Point3f pointAngstroms, Point3f pt) {
    transformManager.transformPointNoClip(pointAngstroms, pt);
  }

  public void transformPoint(Point3f pointAngstroms, Point3f pointScreen) {
    transformManager.transformPoint(pointAngstroms, pointScreen);
  }

  public void transformPoints(Point3f[] pointsAngstroms, Point3i[] pointsScreens) {
    transformManager.transformPoints(pointsAngstroms.length, pointsAngstroms,
        pointsScreens);
  }

  public void transformVector(Vector3f vectorAngstroms,
                              Vector3f vectorTransformed) {
    transformManager.transformVector(vectorAngstroms, vectorTransformed);
  }

  public void unTransformPoint(Point3f pointScreen, Point3f pointAngstroms) {
    
    transformManager.unTransformPoint(pointScreen, pointAngstroms);
  }

  public float getScalePixelsPerAngstrom(boolean asAntialiased) {
    return transformManager.scalePixelsPerAngstrom
        * (asAntialiased || !global.antialiasDisplay ? 1f : 0.5f);
  }

  public short scaleToScreen(int z, int milliAngstroms) {
    
    return transformManager.scaleToScreen(z, milliAngstroms);
  }

  public float unscaleToScreen(int z, int screenDistance) {
    
    return transformManager.unscaleToScreen(z, screenDistance);
  }

  public float scaleToPerspective(int z, float sizeAngstroms) {
    
    return transformManager.scaleToPerspective(z, sizeAngstroms);
  }

  void setSpin(String key, int value) {
    
    if (!Parser.isOneOf(key, "x;y;z;fps;X;Y;Z;FPS"))
      return;
    int i = "x;y;z;fps;X;Y;Z;FPS".indexOf(key);
    switch (i) {
    case 0:
      transformManager.setSpinXYZ(value, Float.NaN, Float.NaN);
      break;
    case 2:
      transformManager.setSpinXYZ(Float.NaN, value, Float.NaN);
      break;
    case 4:
      transformManager.setSpinXYZ(Float.NaN, Float.NaN, value);
      break;
    case 6:
    default:
      transformManager.setSpinFps(value);
      break;
    case 10:
      transformManager.setNavXYZ(value, Float.NaN, Float.NaN);
      break;
    case 12:
      transformManager.setNavXYZ(Float.NaN, value, Float.NaN);
      break;
    case 14:
      transformManager.setNavXYZ(Float.NaN, Float.NaN, value);
      break;
    case 16:
      transformManager.setNavFps(value);
      break;
    }
    global.setParameterValue((i < 10 ? "spin" : "nav") + key, value);
  }

  String getSpinState() {
    return transformManager.getSpinState(false);
  }

  void setSpinOn(boolean spinOn) {
    
    
    transformManager.setSpinOn(spinOn);
  }

  boolean getSpinOn() {
    return transformManager.getSpinOn();
  }

  void setNavOn(boolean navOn) {
    
    
    transformManager.setNavOn(navOn);
  }

  boolean getNavOn() {
    return transformManager.getNavOn();
  }

  void setNavXYZ(float x, float y, float z) {
    transformManager.setNavXYZ((int)x, (int)y, (int)z);  
  }
  
  public String getOrientationText(int type) {
    return transformManager.getOrientationText(type);
  }

  Hashtable getOrientationInfo() {
    return transformManager.getOrientationInfo();
  }

  Matrix3f getMatrixRotate() {
    return transformManager.getMatrixRotate();
  }

  public void getAxisAngle(AxisAngle4f axisAngle) {
    transformManager.getAxisAngle(axisAngle);
  }

  String getTransformText() {
    return transformManager.getTransformText();
  }

  void getRotation(Matrix3f matrixRotation) {
    transformManager.getRotation(matrixRotation);
  }

  
  
  

  private void setDefaultColors(String colorScheme) {
    colorManager.setDefaultColors(colorScheme);
    global.setParameterValue("colorRasmol", (colorScheme.equals("rasmol")));
  }

  float getDefaultTranslucent() {
    return global.defaultTranslucent;
  }

  public int getColixArgb(short colix) {
    return g3d.getColixArgb(colix);
  }

  void setRubberbandArgb(int argb) {
    
    colorManager.setRubberbandArgb(argb);
  }

  public short getColixRubberband() {
    return colorManager.colixRubberband;
  }

  void setElementArgb(int elementNumber, int argb) {
    
    global.setParameterValue("=color "
        + JmolConstants.elementNameFromNumber(elementNumber), Escape
        .escapeColor(argb));
    colorManager.setElementArgb(elementNumber, argb);
  }

  public float getVectorScale() {
    return global.vectorScale;
  }

  public void setVectorScale(float scale) {
    global.setParameterValue("vectorScale", scale);
    global.vectorScale = scale;
  }

  public float getDefaultDrawArrowScale() {
    return global.defaultDrawArrowScale;
  }

  public void setDefaultDrawArrowScale(float scale) {
    global.setParameterValue("defaultDrawArrowScale", scale);
    global.defaultDrawArrowScale = scale;
  }

  float getVibrationScale() {
    return global.vibrationScale;
  }

  float getVibrationPeriod() {
    return global.vibrationPeriod;
  }

  public boolean isVibrationOn() {
    return transformManager.vibrationOn;
  }

  public void setVibrationScale(float scale) {
    

    transformManager.setVibrationScale(scale);
    global.vibrationScale = scale;
    
    global.setParameterValue("vibrationScale", scale);
  }

  void setVibrationOff() {
    transformManager.setVibrationPeriod(0);
  }

  public void setVibrationPeriod(float period) {
    
    transformManager.setVibrationPeriod(period);
    period = Math.abs(period);
    global.vibrationPeriod = period;
    
    global.setParameterValue("vibrationPeriod", period);
  }

  void setObjectColor(String name, String colorName) {
    if (colorName == null || colorName.length() == 0)
      return;
    setObjectArgb(name, Graphics3D.getArgbFromString(colorName));
  }

  void setObjectArgb(String name, int argb) {
    int objId = StateManager.getObjectIdFromName(name);
    if (objId < 0)
      return;
    global.objColors[objId] = argb;
    switch (objId) {
    case StateManager.OBJ_BACKGROUND:
      g3d.setBackgroundArgb(argb);
      colorManager.setColixBackgroundContrast(argb);
      global.backgroundImageFileName = null;
      break;
    }
    global.setParameterValue(name + "Color", Escape.escapeColor(argb));
  }

  void setBackgroundImage(String fileName, Image image) {
    global.backgroundImageFileName = fileName;
    g3d.setBackgroundImage(image);
  }

  int getObjectArgb(int objId) {
    return global.objColors[objId];
  }

  public short getObjectColix(int objId) {
    int argb = getObjectArgb(objId);
    if (argb == 0)
      return getColixBackgroundContrast();
    return Graphics3D.getColix(argb);
  }

  public String getObjectState(String name) {
    int objId = StateManager
        .getObjectIdFromName(name.equalsIgnoreCase("axes") ? "axis" : name);
    if (objId < 0)
      return "";
    int mad = getObjectMad(objId);
    StringBuffer s = new StringBuffer("\n");
    Shape.appendCmd(s, name
        + (mad == 0 ? " off" : mad == 1 ? " on" : mad == -1 ? " dotted"
            : mad < 20 ? " " + mad : " " + (mad / 2000f)));
    return s.toString();
  }

  

  public void setColorBackground(String colorName) {
    setObjectColor("background", colorName);
  }

  public int getBackgroundArgb() {
    return getObjectArgb(StateManager.OBJ_BACKGROUND);
  }

  void setObjectMad(int iShape, String name, int mad) {
    int objId = StateManager
        .getObjectIdFromName(name.equalsIgnoreCase("axes") ? "axis" : name);
    if (objId < 0)
      return;
    if (mad == -2 || mad == -4) { 
      int m = mad + 3;
      mad = getObjectMad(objId);
      if (mad == 0)
        mad = m;
    }
    global.setParameterValue("show" + name, mad != 0);
    global.objStateOn[objId] = (mad != 0);
    if (mad == 0)
      return;
    global.objMad[objId] = mad;
    setShapeSize(iShape, mad, Float.NaN); 
  }

  public int getObjectMad(int objId) {
    return (global.objStateOn[objId] ? global.objMad[objId] : 0);
  }

  public void setPropertyColorScheme(String scheme, boolean isOverloaded) {
    global.propertyColorScheme = scheme;
    colorManager.setColorScheme(scheme, isOverloaded);
  }

  public String getPropertyColorScheme() {
    return global.propertyColorScheme;
  }

  public short getColixBackgroundContrast() {
    return colorManager.colixBackgroundContrast;
  }

  String getSpecularState() {
    return global.getSpecularState();
  }

  private static void setSpecular(boolean specular) {
    
    ColorManager.setSpecular(specular);
  }

  boolean getSpecular() {
    return ColorManager.getSpecular();
  }

  private static void setSpecularPower(int specularPower) {
    
    ColorManager.setSpecularPower(Math.abs(specularPower));
  }

  private static void setSpecularExponent(int specularExponent) {
    
    ColorManager.setSpecularPower(-Math.abs(specularExponent));
  }

  private static void setAmbientPercent(int ambientPercent) {
    
    ColorManager.setAmbientPercent(ambientPercent);
  }

  static int getAmbientPercent() {
    return ColorManager.getAmbientPercent();
  }

  private static void setDiffusePercent(int diffusePercent) {
    
    ColorManager.setDiffusePercent(diffusePercent);
  }

  static int getDiffusePercent() {
    return ColorManager.getDiffusePercent();
  }

  private static void setSpecularPercent(int specularPercent) {
    
    ColorManager.setSpecularPercent(specularPercent);
  }

  static int getSpecularPercent() {
    return ColorManager.getSpecularPercent();
  }

  boolean getZShade() {
    return global.zShade;
  }

  public short getColixAtomPalette(Atom atom, byte pid) {
    return colorManager.getColixAtomPalette(atom, pid);
  }

  public short getColixBondPalette(Bond bond, byte pid) {
    return colorManager.getColixBondPalette(bond, pid);
  }

  public int[] getColorSchemeArray(String colorScheme) {
    return colorManager.getColorSchemeArray(colorScheme);
  }

  public String getColorSchemeList(String colorScheme, boolean ifDefault) {
    return colorManager.getColorSchemeList(colorScheme, ifDefault);
  }

  public static void setUserScale(int[] scale) {
    ColorManager.setUserScale(scale);
  }

  public short getColixForPropertyValue(float val) {
    
    return colorManager.getColixForPropertyValue(val);
  }

  Point3f getColorPointForPropertyValue(float val) {
    
    short colix = colorManager.getColixForPropertyValue(val);
    Point3f pt = new Point3f();
    return Graphics3D.colorPointFromInt(g3d.getColixArgb(colix), pt);
  }

  
  
  

  void select(BitSet bs, boolean isQuiet) {
    
    selectionManager.select(bs, isQuiet);
    modelSet.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MAX_VALUE,
        Float.NaN, null);
  }

  void selectBonds(BitSet bs) {
    modelSet.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MAX_VALUE,
        Float.NaN, bs);
  }

  void hide(BitSet bs, boolean isQuiet) {
    
    selectionManager.hide(bs, isQuiet);
  }

  void display(BitSet bs, boolean isQuiet) {
    
    selectionManager.display(getModelAtomBitSet(-1, false), bs, isQuiet);
  }

  BitSet getHiddenSet() {
    return selectionManager.getHiddenSet();
  }

  public boolean isSelected(int atomIndex) {
    return selectionManager.isSelected(atomIndex);
  }

  boolean isInSelectionSubset(int atomIndex) {
    return selectionManager.isInSelectionSubset(atomIndex);
  }

  void reportSelection(String msg) {
    if (modelSet.getSelectionHaloEnabled())
      setTainted(true);
    if (isScriptQueued || global.debugScript)
      scriptStatus(msg);
  }

  public Point3f getAtomSetCenter(BitSet bs) {
    return modelSet.getAtomSetCenter(bs);
  }

  private void clearAtomSets() {
    setSelectionSubset(null);
    definedAtomSets.clear();
  }

  public void selectAll() {
    
    selectionManager.selectAll(false);
  }

  private boolean noneSelected;

  void setNoneSelected(boolean noneSelected) {
    this.noneSelected = noneSelected;
  }

  Boolean getNoneSelected() {
    return (noneSelected ? Boolean.TRUE : Boolean.FALSE);
  }

  public void clearSelection() {
    
    selectionManager.clearSelection(false);
    global.setParameterValue("hideNotSelected", false);
  }

  public void setSelectionSet(BitSet set) {
    
    selectionManager.setSelectionSet(set);
  }

  void setSelectionSubset(BitSet subset) {
    selectionManager.setSelectionSubset(subset);
  }

  public BitSet getSelectionSubset() {
    return selectionManager.bsSubset;
  }

  void invertSelection() {
    
    selectionManager.invertSelection();
    
  }

  public BitSet getSelectionSet() {
    return selectionManager.bsSelection;
  }

  public int getSelectionCount() {
    return selectionManager.getSelectionCount();
  }

  void setFormalCharges(int formalCharge) {
    modelSet.setFormalCharges(selectionManager.bsSelection, formalCharge);
  }

  public void addSelectionListener(JmolSelectionListener listener) {
    selectionManager.addListener(listener);
  }

  public void removeSelectionListener(JmolSelectionListener listener) {
    selectionManager.addListener(listener);
  }

  BitSet getAtomBitSet(Object atomExpression) {
    
    return ScriptEvaluator.getAtomBitSet(eval, atomExpression);
  }

  Vector getAtomBitSetVector(Object atomExpression) {
    return ScriptEvaluator.getAtomBitSetVector(eval, getAtomCount(), atomExpression);
  }

  
  
  

  public void setModeMouse(int modeMouse) {
    
    if (haveDisplay)
      mouseManager.setModeMouse(modeMouse);
    if (modeMouse == JmolConstants.MOUSE_NONE) {
      
      clearScriptQueue();
      haltScriptExecution();
      transformManager.setSpinOn(false);
      transformManager.setNavOn(false);
      transformManager.setVibrationPeriod(0);
      scriptManager.startCommandWatcher(false);
      scriptManager.interruptQueueThreads();
      g3d.destroy();
      if (appConsole != null) {
        appConsole.dispose();
        appConsole = null;
      }
      if (scriptEditor != null) {
        scriptEditor.dispose();
        scriptEditor = null;
      }
    }
  }

  Rectangle getRubberBandSelection() {
    return mouseManager.getRubberBand();
  }

  public int getCursorX() {
    return mouseManager.xCurrent;
  }

  public int getCursorY() {
    return mouseManager.yCurrent;
  }

  
  
  

  String getDefaultDirectory() {
    return global.defaultDirectory;
  }

  public BufferedInputStream getBufferedInputStream(String fullPathName) {
    Object ret = getBufferedReaderOrErrorMessageFromName(fullPathName,
        new String[2], true);
    return (ret instanceof BufferedInputStream ? (BufferedInputStream) ret
        : null);
  }

  Object getBufferedReaderOrErrorMessageFromName(String name,
                                                 String[] fullPathNameReturn,
                                                 boolean isBinary) {
    return fileManager.getBufferedReaderOrErrorMessageFromName(name,
        fullPathNameReturn, isBinary, true);
  }

  void addLoadScript(String script) {
    fileManager.addLoadScript(script);
  }

  private Hashtable setLoadParameters(Hashtable htParams) {
    if (htParams == null)
      htParams = new Hashtable();
    if (global.atomTypes.length() > 0)
      htParams.put("atomTypes", global.atomTypes);
    if (!htParams.containsKey("lattice"))
      htParams.put("lattice", global.getDefaultLattice());
    if (global.applySymmetryToBonds)
      htParams.put("applySymmetryToBonds", Boolean.TRUE);
    if (getPdbLoadInfo(2))
      htParams.put("getHeader", Boolean.TRUE);
    return htParams;
  }

  
  public String openFile(String fileName) {
    zap(false, false);
    fileManager.createAtomSetCollectionFromFile(fileName, new Hashtable(), null, false);
    return createModelSetAndReturnError(false);
  }

  public void openFileAsynchronously(String fileName) {
    
    boolean allowScript = (!fileName.startsWith("\t"));
    if (!allowScript)
      fileName = fileName.substring(1);
    fileName = fileName.replace('\\', '/');
    String type = fileManager.getFileTypeName(fileName);
    checkHalt("exit");
    
    allowScript &= (type == null);
    if (scriptEditorVisible && allowScript)
      showEditor(new String[] { fileName, getFileAsString(fileName) });
    else
      evalString((allowScript ? "script " : "zap;load ")
         + Escape.escape(fileName));
  }

  private final static int FILE_STATUS_NOT_LOADED = -1;
  private final static int FILE_STATUS_ZAPPED = 0;
  private final static int FILE_STATUS_CREATING_MODELSET = 2;
  private final static int FILE_STATUS_MODELSET_CREATED = 3;
  private final static int FILE_STATUS_MODELS_DELETED = 5;

  void openFile(String fileName, Hashtable htParams, String loadScript,
                boolean isAppend) {
    
    
    if (fileName == null)
      return;
    if (fileName.equalsIgnoreCase("string[]")) {
      
      
      return;
    }
    Logger.startTimer();
    if (fileName.equalsIgnoreCase("string")) {
      String fileData = (htParams.containsKey("fileData") 
          ? (String) htParams.get("fileData")
          : fileManager.getInlineData(-1)); 
      createModelSetInline(fileData, htParams, isAppend,
          false);
    } else {
      if (!isAppend && fileName.charAt(0) != '?')
        zap(false, false);
      fileManager.createAtomSetCollectionFromFile(fileName,
          setLoadParameters(htParams), loadScript, isAppend);
    }
    Logger.checkTimer("openFile(" + fileName + ")");
  }

  public String openFiles(String modelName, String[] fileNames) {
    openFiles(modelName, fileNames, null, false, null);
    return createModelSetAndReturnError(false);
  }

  String openFiles(String modelName, String[] fileNames, String loadScript,
                   boolean isAppend, Hashtable htParams) {
    
    
    if (!isAppend)
      zap(false, false);
    
    
    long timeBegin = System.currentTimeMillis();
    fileManager.createAtomSetCollectionFromFiles(modelName, fileNames,
        loadScript, isAppend, setLoadParameters(htParams));
    long ms = System.currentTimeMillis() - timeBegin;
    String msg = "";
    for (int i = 0; i < fileNames.length; i++)
      msg += (i == 0 ? "" : ",") + fileNames[i];
    Logger.info("openFiles(" + fileNames.length + ") " + ms + " ms");
    return msg;
  }

  public String openStringInline(String strModel) {
    
    return createModelSetInline(strModel, null, false, true);
  }

  private String createModelSetInline(String strModel, Hashtable htParams,
                                      boolean isAppend, boolean createModelSet) {
    
    if (!isAppend)
      zap(true, false);
    fileManager.createAtomSetCollectionFromString(strModel,
        setLoadParameters(htParams), isAppend);
    return (createModelSet ? createModelSetAndReturnError(isAppend) : null);
  }

  private String openStringsInline(String[] arrayModels, Hashtable htParams,
                                   boolean isAppend) {
    
    if (!isAppend)
      zap(true, false);
    fileManager.createAtomSeCollectionFromStrings(arrayModels,
        setLoadParameters(htParams), isAppend);
    return createModelSetAndReturnError(isAppend);
  }

  public char getInlineChar() {
    return global.inlineNewlineChar;
  }

  public String loadInline(String strModel) {
    
    return loadInline(strModel, global.inlineNewlineChar, false);
  }

  public String loadInline(String strModel, boolean isAppend) {
    
    return loadInline(strModel, (char) 0, isAppend);
  }

  public String loadInline(String strModel, char newLine) {
    return loadInline(strModel, newLine, false);
  }

  String loadInline(String strModel, char newLine, boolean isAppend) {
    if (strModel == null)
      return null;
    int i;
    Hashtable htParams = new Hashtable();
    Logger.debug(strModel);
    String rep = (strModel.indexOf('\n') >= 0 ? "" : "\n");
    if (newLine != 0 && newLine != '\n') {
      int len = strModel.length();
      for (i = 0; i < len && strModel.charAt(i) == ' '; ++i) {
      }
      if (i < len && strModel.charAt(i) == newLine)
        strModel = strModel.substring(i + 1);
      strModel = TextFormat.simpleReplace(strModel, "" + newLine, rep);
    }
    String datasep = getDataSeparator();
    if (datasep != null && datasep != ""
        && (i = strModel.indexOf(datasep)) >= 0) {
      int n = 2;
      while ((i = strModel.indexOf(datasep, i + 1)) >= 0)
        n++;
      String[] strModels = new String[n];
      int pt = 0, pt0 = 0;
      for (i = 0; i < n; i++) {
        pt = strModel.indexOf(datasep, pt0);
        if (pt < 0)
          pt = strModel.length();
        strModels[i] = strModel.substring(pt0, pt);
        pt0 = pt + datasep.length();
      }
      return openStringsInline(strModels, htParams, isAppend);
    }
    return createModelSetInline(strModel, htParams, isAppend, true);
  }

  String getDataSeparator() {
    return (String) global.getParameter("dataseparator");
  }

  public String loadInline(String[] arrayModels) {
    return loadInline(arrayModels, false);
  }

  public String loadInline(String[] arrayModels, boolean isAppend) {
    
    
    return (arrayModels == null || arrayModels.length == 0 ? null
        : openStringsInline(arrayModels, null, isAppend));
  }

  public boolean getApplySymmetryToBonds() {
    return global.applySymmetryToBonds;
  }

  void setApplySymmetryToBonds(boolean TF) {
    global.applySymmetryToBonds = TF;
  }

  void setAtomData(int type, String name, String coordinateData) {
    modelSet.setAtomData(type, name, coordinateData);
  }

  public String openDOM(Object DOMNode) {
    
    zap(true, false);
    long timeBegin = System.currentTimeMillis();
    fileManager.createAtomSetCollectionFromDOM(DOMNode);
    long ms = System.currentTimeMillis() - timeBegin;
    Logger.info("openDOM " + ms + " ms");
    return createModelSetAndReturnError(false);
  }

  

  public String openReader(String fullPathName, String fileName, Reader reader) {
    zap(true, false);
    fileManager.createAtomSetCollectionFromReader(fullPathName, fileName,
        reader);
    return createModelSetAndReturnError(false);
  }

  
  String createModelSetAndReturnError(boolean isAppend) {
    String fullPathName = getFullPathName();
    String fileName = getFileName();
    Object atomSetCollection = fileManager.getAtomSetCollectionOrError();
    fileManager.deallocateReaderThreads();
    String errMsg;
    if (atomSetCollection instanceof String || atomSetCollection == null) {
      errMsg = (String) atomSetCollection;
      setFileLoadStatus(FILE_STATUS_NOT_LOADED, fullPathName, null, null,
          errMsg);
      if (errMsg != null && !isAppend && !errMsg.equals("#CANCELED#"))
        zap(errMsg);
    } else {
      if (isAppend)
        clearAtomSets();
      setFileLoadStatus(FILE_STATUS_CREATING_MODELSET, fullPathName, fileName,
          null, null);
      errMsg = createModelSet(fullPathName, fileName, atomSetCollection,
          isAppend);
      setFileLoadStatus(FILE_STATUS_MODELSET_CREATED, fullPathName, fileName,
          getModelSetName(), errMsg);
      if (isAppend) {
        selectAll(); 
                     
        setTainted(true);
      }
      atomSetCollection = null;
      System.gc();
    }
    return errMsg;
  }

  String loadAtomDataAndReturnError(int tokType) {
    Object atomSetCollection = fileManager.getAtomSetCollectionOrError();
    fileManager.deallocateReaderThreads();
    String errMsg;
    if (atomSetCollection instanceof String || atomSetCollection == null) {
      errMsg = (String) atomSetCollection;
    } else {
      errMsg = createAtomDataSet(tokType, atomSetCollection);
    }
    return errMsg;
  }

  private String createAtomDataSet(int tokType, Object atomSetCollection) {
    
    
    
    
    setErrorMessage(null);
    try {
      ((ModelLoader) modelSet).createAtomDataSet(tokType, atomSetCollection,
          selectionManager.bsSelection);
      if (tokType == Token.vibration)
        setStatusFrameChanged(Integer.MIN_VALUE);
    } catch (Error er) {
      handleError(er, true);
      String errMsg = getShapeErrorState();
      errMsg = ("ERROR adding vibrations: " + er + (errMsg.length() == 0 ? ""
          : "|" + errMsg));
      zap(errMsg);
      setErrorMessage(errMsg);
    }
    return getErrorMessage();
  }

  void deallocateReaderThreads() {
    fileManager.deallocateReaderThreads();
  }

  
  public void openClientFile(String fullPathName, String fileName,
                             Object clientFile) {
    createModelSet(fullPathName, fileName, clientFile, false);
  }

  
  private String createModelSet(String fullPathName, String fileName,
                                Object atomSetCollection, boolean isAppend) {
    
    
    
    
    pushHoldRepaint("createModelSet");
    setErrorMessage(null);
    try {
      modelSet = modelManager.createModelSet(fullPathName, fileName,
          atomSetCollection, isAppend);
      if (!isAppend)
        initializeModel();
    } catch (Error er) {
      handleError(er, true);
      String errMsg = getShapeErrorState();
      errMsg = ("ERROR creating model: " + er + (errMsg.length() == 0 ? ""
          : "|" + errMsg));
      zap(errMsg);
      setErrorMessage(errMsg);
    }
    popHoldRepaint("createModelSet");
    return getErrorMessage();
  }

  public Object getCurrentFileAsBytes() {
    String filename = getFullPathName();
    if (filename.equals("string") || filename.equals("string[]")
        || filename.equals("JSNode")) {
      String str = getCurrentFileAsString();
      try {
        return str.getBytes("UTF8");
      } catch (UnsupportedEncodingException e) {
        return str;
      }
    }
    String pathName = modelManager.getModelSetPathName();
    if (pathName == null)
      return "";
    return fileManager.getFileAsBytes(pathName);
  }

  public Object getFileAsBytes(String pathName) {
    return fileManager.getFileAsBytes(pathName);
  }

  public String getCurrentFileAsString() {
    String filename = getFullPathName();
    if (filename == "string") {
      return fileManager.getInlineData(-1);
    }
    if (filename == "string[]") {
      int modelIndex = getDisplayModelIndex();
      if (modelIndex < 0)
        return "";
      return fileManager.getInlineData(modelIndex);
    }
    if (filename == "JSNode") {
      return "<DOM NODE>";
    }
    String pathName = modelManager.getModelSetPathName();
    if (pathName == null)
      return null;
    return getFileAsString(pathName, Integer.MAX_VALUE, true);
  }

  public String getFullPathName() {
    return fileManager.getFullPathName();
  }

  public String getFileName() {
    return fileManager.getFileName();
  }

  public String getFileAsString(String name) {
    return getFileAsString(name, Integer.MAX_VALUE, false);
  }
  
  public String getFileAsString(String name, int nBytesMax, boolean doSpecialLoad) {
    if (name == null)
      return getCurrentFileAsString();
    String[] data = new String[2];
    data[0] = name;
    
    getFileAsString(data, nBytesMax, doSpecialLoad);
    return data[1];
  }

  public String getFullPath(String name) {
    return fileManager.getFullPath(name, false);
  }

  public boolean getFileAsString(String[] data, int nBytesMax, boolean doSpecialLoad) {
    return fileManager.getFileDataOrErrorAsString(data, nBytesMax, doSpecialLoad);
  }

  String[] getFileInfo() {
    return fileManager.getFileInfo();
  }

  void setFileInfo(String[] fileInfo) {
    fileManager.setFileInfo(fileInfo);
  }

  
  
  

  public void autoCalculate(int tokProperty) {
    switch (tokProperty) {
    case Token.surfacedistance:
      modelSet.getSurfaceDistanceMax();
      break;
    case Token.straightness:
      modelSet.calculateStraightness();
      break;
    }
  }
  
  int getSurfaceDistanceMax() {
    return modelSet.getSurfaceDistanceMax();
  }

  public void calculateStraightness() {
    modelSet.setHaveStraightness(false);
    modelSet.calculateStraightness();
  }

  public Point3f[] calculateSurface(BitSet bsSelected, float envelopeRadius) {
    if (bsSelected == null)
      bsSelected = selectionManager.bsSelection;
    addStateScript("calculate surfaceDistance "
        + (envelopeRadius == Float.MAX_VALUE ? "FROM" : "WITHIN"), null,
        bsSelected, null, "", false, true);
    return modelSet.calculateSurface(bsSelected, envelopeRadius);
  }

  void calculateStructures(BitSet bsAtoms) {
    
    modelSet.calculateStructures(bsAtoms);
  }

  public AtomIndexIterator getWithinModelIterator(Atom atom, float distance) {
    return modelSet.getWithinModelIterator(atom, distance);
  }

  public AtomIndexIterator getWithinAtomSetIterator(int atomIndex,
                                                    float distance,
                                                    BitSet bsSelected,
                                                    boolean isGreaterOnly,
                                                    boolean modelZeroBased) {
    return modelSet.getWithinAtomSetIterator(atomIndex, distance, bsSelected,
        isGreaterOnly, modelZeroBased);
  }

  public void fillAtomData(AtomData atomData, int mode) {
    atomData.programInfo = "Jmol Version " + getJmolVersion();
    atomData.fileName = getFileName();
    modelSet.fillAtomData(atomData, mode);
  }

  StateScript addStateScript(String script, boolean addFrameNumber,
                      boolean postDefinitions) {
    return addStateScript(script, null, null, null, null, addFrameNumber,
        postDefinitions);
  }

  StateScript addStateScript(String script1, BitSet bsBonds, BitSet bsAtoms1,
                      BitSet bsAtoms2, String script2, boolean addFrameNumber,
                      boolean postDefinitions) {
    return modelSet.addStateScript(script1, bsBonds, bsAtoms1, bsAtoms2, script2,
        addFrameNumber, postDefinitions);
  }

  public boolean getEchoStateActive() {
    return modelSet.getEchoStateActive();
  }

  void setEchoStateActive(boolean TF) {
    modelSet.setEchoStateActive(TF);
  }

  public void zap(boolean notify, boolean resetUndo) {
    stopAnimationThreads();
    if (modelSet != null) {
      clearModelDependentObjects();
      fileManager.clear();
      repaintManager.clear();
      animationManager.clear();
      transformManager.clear();
      pickingManager.clear();
      selectionManager.clear();
      clearAllMeasurements();
      if (minimizer != null)
        minimizer.setProperty("clear", null);
      modelSet = modelManager.clear();
      if (haveDisplay) {
        mouseManager.clear();
      }
      stateManager.clear();
      global.clear();
      tempManager.clear();
      colorManager.clear();
      definedAtomSets.clear();
      dataManager.clear();
      System.gc();
    }
    modelSet = modelManager.zap();
    initializeModel();
    if (notify)
      setFileLoadStatus(FILE_STATUS_ZAPPED, null, (resetUndo ? "resetUndo"
          : null), null, null);
    if (Logger.debugging)
      Logger.checkMemory();
  }

  private void zap(String msg) {
    zap(true, false);
    echoMessage(msg);
  }

  void echoMessage(String msg) {
    int iShape = JmolConstants.SHAPE_ECHO;
    loadShape(iShape);
    setShapeProperty(iShape, "font", getFont3D("SansSerif", "Plain", 9));
    setShapeProperty(iShape, "target", "error");
    setShapeProperty(iShape, "text", msg);
  }

  public String getMinimizationInfo() {
    return (minimizer == null ? "" : (String) minimizer.getProperty("log", 0));
  }

  public boolean useMinimizationThread() {
    return global.useMinimizationThread && !autoExit;
  }

  private void initializeModel() {
    stopAnimationThreads();
    reset();
    selectAll();
    noneSelected = false;
    transformManager.setCenter();
    clearAtomSets();
    animationManager.initializePointers(1);
    setCurrentModelIndex(0);
    setBackgroundModelIndex(-1);
    setFrankOn(getShowFrank());
    if (haveDisplay)
      mouseManager.startHoverWatcher(true);
    setTainted(true);
    finalizeTransformParameters();
  }

  public String getModelSetName() {
    if (modelSet == null)
      return null;
    return modelSet.getModelSetName();
  }

  public String getModelSetFileName() {
    return modelManager.getModelSetFileName();
  }

  public String getUnitCellInfoText() {
    return modelSet.getUnitCellInfoText();
  }

  public Hashtable getSpaceGroupInfo(String spaceGroup) {
    return modelSet.getSpaceGroupInfo(spaceGroup);
  }

  void getPolymerPointsAndVectors(BitSet bs, Vector vList) {
    modelSet.getPolymerPointsAndVectors(bs, vList);
  }

  public String getModelSetProperty(String strProp) {
    
    return modelSet.getModelSetProperty(strProp);
  }

  public Object getModelSetAuxiliaryInfo(String strKey) {
    return modelSet.getModelSetAuxiliaryInfo(strKey);
  }

  public String getModelSetPathName() {
    return modelManager.getModelSetPathName();
  }

  public String getModelSetTypeName() {
    return modelSet.getModelSetTypeName();
  }

  public boolean haveFrame() {
    return haveModelSet();
  }

  boolean haveModelSet() {
    return modelSet != null;
  }

  void clearBfactorRange() {
    
    modelSet.clearBfactorRange();
  }

  public String getHybridizationAndAxes(int atomIndex, Vector3f z, Vector3f x,
                                        String lcaoType,
                                        boolean hybridizationCompatible) {
    return modelSet.getHybridizationAndAxes(atomIndex, z, x, lcaoType,
        hybridizationCompatible);
  }

  public BitSet getModelAtomBitSet(int modelIndex, boolean asCopy) {
    return modelSet.getModelAtomBitSet(modelIndex, asCopy);
  }

  public BitSet getModelBitSet(BitSet atomList, boolean allTrajectories) {
    return modelSet.getModelBitSet(atomList, allTrajectories);
  }

  Object getClientFile() {
    
    return null;
  }

  
  

  public String getClientAtomStringProperty(Object clientAtom,
                                            String propertyName) {
    return (modelAdapter == null || propertyName == null
        || propertyName.length() == 0 ? null : modelAdapter
        .getClientAtomStringProperty(clientAtom, propertyName));
  }

  

  JmolAdapter getExportJmolAdapter() {
    
    return null;
  }

  public ModelSet getModelSet() {
    return modelSet;
  }

  public String getBoundBoxCommand(boolean withOptions) {
    return modelSet.getBoundBoxCommand(withOptions);
  }

  void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner) {
    modelSet.setBoundBox(pt1, pt2, byCorner);
  }

  public Point3f getBoundBoxCenter() {
    return modelSet.getBoundBoxCenter(animationManager.currentModelIndex);
  }

  Point3f getAverageAtomPoint() {
    return modelSet.getAverageAtomPoint();
  }

  void calcBoundBoxDimensions(BitSet bs) {
    modelSet.calcBoundBoxDimensions(bs);
    axesAreTainted = true;
  }

  BoxInfo getBoxInfo(BitSet bs) {
    return modelSet.getBoxInfo(bs);
  }

  float calcRotationRadius(Point3f center) {
    return modelSet
        .calcRotationRadius(animationManager.currentModelIndex, center);
  }

  public float calcRotationRadius(BitSet bs) {
    return modelSet.calcRotationRadius(bs);
  }

  public Vector3f getBoundBoxCornerVector() {
    return modelSet.getBoundBoxCornerVector();
  }

  Hashtable getBoundBoxInfo() {
    return modelSet.getBoundBoxInfo();
  }

  public BitSet getBoundBoxModels() {
    return modelSet.getBoundBoxModels();
  }

  public int getBoundBoxCenterX() {
    
    return dimScreen.width / 2;
  }

  public int getBoundBoxCenterY() {
    return dimScreen.height / 2;
  }

  public int getModelCount() {
    return modelSet.getModelCount();
  }

  String getModelInfoAsString() {
    return modelSet.getModelInfoAsString();
  }

  String getSymmetryInfoAsString() {
    return modelSet.getSymmetryInfoAsString();
  }

  public Properties getModelSetProperties() {
    return modelSet.getModelSetProperties();
  }

  public Hashtable getModelSetAuxiliaryInfo() {
    return modelSet.getModelSetAuxiliaryInfo();
  }

  public int getModelNumber(int modelIndex) {
    if (modelIndex < 0)
      return modelIndex;
    return modelSet.getModelNumber(modelIndex);
  }

  public int getModelFileNumber(int modelIndex) {
    if (modelIndex < 0)
      return 0;
    return modelSet.getModelFileNumber(modelIndex);
  }

  public String getModelNumberDotted(int modelIndex) {
    return modelIndex < 0 ? "0" : modelSet == null ? null : modelSet
        .getModelNumberDotted(modelIndex);
  }

  public String getModelName(int modelIndex) {
    return modelSet == null ? null : modelSet.getModelName(modelIndex);
  }

  public Properties getModelProperties(int modelIndex) {
    return modelSet.getModelProperties(modelIndex);
  }

  public String getModelProperty(int modelIndex, String propertyName) {
    return modelSet.getModelProperty(modelIndex, propertyName);
  }

  public String getModelFileInfo() {
    return modelSet.getModelFileInfo(getVisibleFramesBitSet());
  }

  String getModelFileInfoAll() {
    return modelSet.getModelFileInfo(null);
  }

  public Hashtable getModelAuxiliaryInfo(int modelIndex) {
    return modelSet.getModelAuxiliaryInfo(modelIndex);
  }

  public Object getModelAuxiliaryInfo(int modelIndex, String keyName) {
    return modelSet.getModelAuxiliaryInfo(modelIndex, keyName);
  }

  int getModelNumberIndex(int modelNumber, boolean useModelNumber,
                          boolean doSetTrajectory) {
    return modelSet.getModelNumberIndex(modelNumber, useModelNumber,
        doSetTrajectory);
  }

  boolean modelSetHasVibrationVectors() {
    return modelSet.modelSetHasVibrationVectors();
  }

  public boolean modelHasVibrationVectors(int modelIndex) {
    return modelSet.modelHasVibrationVectors(modelIndex);
  }

  public int getChainCount() {
    return modelSet.getChainCount(true);
  }

  public int getChainCountInModel(int modelIndex) {
    
    return modelSet.getChainCountInModel(modelIndex, false);
  }

  public int getChainCountInModel(int modelIndex, boolean countWater) {
    return modelSet.getChainCountInModel(modelIndex, countWater);
  }

  public int getGroupCount() {
    return modelSet.getGroupCount();
  }

  public int getGroupCountInModel(int modelIndex) {
    return modelSet.getGroupCountInModel(modelIndex);
  }

  public int getPolymerCount() {
    return modelSet.getBioPolymerCount();
  }

  public int getPolymerCountInModel(int modelIndex) {
    return modelSet.getBioPolymerCountInModel(modelIndex);
  }

  public int getAtomCount() {
    return modelSet.getAtomCount();
  }

  public int getAtomCountInModel(int modelIndex) {
    return modelSet.getAtomCountInModel(modelIndex);
  }

  
  public int getBondCount() {
    return modelSet.getBondCount();
  }

  
  public int getBondCountInModel(int modelIndex) {
    return modelSet.getBondCountInModel(modelIndex);
  }

  BitSet getBondsForSelectedAtoms(BitSet bsAtoms) {
    
    return modelSet.getBondsForSelectedAtoms(bsAtoms, global.bondModeOr || BitSetUtil.cardinalityOf(bsAtoms) == 1);
  }

  boolean frankClicked(int x, int y) {
    return frankOn && modelSet.frankClicked(x, y);
  }

  public int findNearestAtomIndex(int x, int y) {
    return (modelSet == null || !getAtomPicking() ? -1 : modelSet
        .findNearestAtomIndex(x, y));
  }

  void selectRectangle(Rectangle rect, int modifiers) {
    BitSet bs = modelSet.findAtomsInRectangle(rect, getVisibleFramesBitSet());
    if (BitSetUtil.firstSetBit(bs) < 0)
      return;
    pickingManager.atomsPicked(bs, modifiers);
  }

  void toCartesian(Point3f pt) {
    int modelIndex = animationManager.currentModelIndex;
    if (modelIndex < 0)
      return;
    modelSet.toCartesian(modelIndex, pt);
  }

  void toUnitCell(Point3f pt, Point3f offset) {
    int modelIndex = animationManager.currentModelIndex;
    if (modelIndex < 0)
      return;
    modelSet.toUnitCell(modelIndex, pt, offset);
  }

  void toFractional(Point3f pt) {
    int modelIndex = animationManager.currentModelIndex;
    if (modelIndex < 0)
      return;
    modelSet.toFractional(modelIndex, pt);
  }

  public void setCenterSelected() {
    
    setCenterBitSet(selectionManager.bsSelection, true);
  }

  public void setBondTolerance(float bondTolerance) {
    global.setParameterValue("bondTolerance", bondTolerance);
    global.bondTolerance = bondTolerance;
  }

  public float getBondTolerance() {
    return global.bondTolerance;
  }

  public void setMinBondDistance(float minBondDistance) {
    
    global.setParameterValue("minBondDistance", minBondDistance);
    global.minBondDistance = minBondDistance;
  }

  public float getMinBondDistance() {
    return global.minBondDistance;
  }

  int[] getAtomIndices(BitSet bs) {
    return modelSet.getAtomIndices(bs);
  }

  public BitSet getAtomBits(int tokType, Object specInfo) {
    return modelSet.getAtomBits(tokType, specInfo);
  }

  BitSet getSequenceBits(String specInfo, BitSet bs) {
    return modelSet.getSequenceBits(specInfo, bs);
  }

  BitSet getAtomsWithin(float distance, Point3f coord) {
    BitSet bs = new BitSet();
    modelSet.getAtomsWithin(distance, coord, bs, -1);
    if (distance < 0)
      modelSet.getAtomsWithin(-distance, coord, bs, -1);
    return bs;
  }

  BitSet getAtomsWithin(float distance, Point4f plane) {
    return modelSet.getAtomsWithin(distance, plane);
  }

  BitSet getAtomsWithin(float distance, BitSet bs, boolean isWithinModelSet) {
    return modelSet.getAtomsWithin(distance, bs, isWithinModelSet);
  }

  BitSet getAtomsConnected(float min, float max, int intType, BitSet bs) {
    return modelSet.getAtomsConnected(min, max, intType, bs);
  }

  BitSet getBranchBitSet(int atomIndex, int atomIndexNot) {
    return modelSet.getBranchBitSet(atomIndex, atomIndexNot);
  }

  int getAtomIndexFromAtomNumber(int atomNumber) {
    return modelSet.getAtomIndexFromAtomNumber(atomNumber,
        getVisibleFramesBitSet());
  }

  public BitSet getElementsPresentBitSet(int modelIndex) {
    return modelSet.getElementsPresentBitSet(modelIndex);
  }

  public Hashtable getHeteroList(int modelIndex) {
    return modelSet.getHeteroList(modelIndex);
  }

  BitSet getVisibleSet() {
    return modelSet.getVisibleSet();
  }

  BitSet getClickableSet() {
    return modelSet.getClickableSet();
  }

  void calcSelectedGroupsCount() {
    modelSet.calcSelectedGroupsCount(selectionManager.bsSelection);
  }

  void calcSelectedMonomersCount() {
    modelSet.calcSelectedMonomersCount(selectionManager.bsSelection);
  }

  void calcSelectedMoleculesCount() {
    modelSet.calcSelectedMoleculesCount(selectionManager.bsSelection);
  }

  String getFileHeader() {
    return modelSet.getFileHeader(animationManager.currentModelIndex);
  }

  Object getFileData() {
    return modelSet.getFileData(animationManager.currentModelIndex);
  }

  public Hashtable getCifData(int modelIndex) {
    String name = getModelFileName(modelIndex);
    String data = getFileAsString(name);
    if (data == null)
      return null;
    return CifDataReader.readCifData(new BufferedReader(new StringReader(data)));
  }

  String getPDBHeader() {
    return modelSet.getPDBHeader(animationManager.currentModelIndex);
  }

  public Hashtable getModelInfo(Object atomExpression) {
    return modelSet.getModelInfo(getModelBitSet(getAtomBitSet(atomExpression), false));
  }

  public Hashtable getAuxiliaryInfo(Object atomExpression) {
    return modelSet
        .getAuxiliaryInfo(getModelBitSet(getAtomBitSet(atomExpression), false));
  }

  public Hashtable getShapeInfo() {
    return modelSet.getShapeInfo();
  }

  int getShapeIdFromObjectName(String objectName) {
    return modelSet.getShapeIdFromObjectName(objectName);
  }

  Vector getAllAtomInfo(Object atomExpression) {
    return modelSet.getAllAtomInfo(getAtomBitSet(atomExpression));
  }

  Vector getAllBondInfo(Object atomExpression) {
    return modelSet.getAllBondInfo(getAtomBitSet(atomExpression));
  }

  Vector getMoleculeInfo(Object atomExpression) {
    return modelSet.getMoleculeInfo(getAtomBitSet(atomExpression));
  }

  public String getChimeInfo(int tok) {
    return modelSet.getChimeInfo(tok, selectionManager.bsSelection);
  }

  public Hashtable getAllChainInfo(Object atomExpression) {
    return modelSet.getAllChainInfo(getAtomBitSet(atomExpression));
  }

  public Hashtable getAllPolymerInfo(Object atomExpression) {
    return modelSet.getAllPolymerInfo(getAtomBitSet(atomExpression));
  }

  public String getStateInfo() {
    return getStateInfo(null);
  }

  final static String STATE_VERSION_STAMP = "# Jmol state version ";

  public String getStateInfo(String type) {
    boolean isAll = (type == null || type.equalsIgnoreCase("all"));
    StringBuffer s = new StringBuffer("");
    StringBuffer sfunc = (isAll ? new StringBuffer("function _setState() {\n")
        : null);
    if (isAll)
      s.append(STATE_VERSION_STAMP + getJmolVersion() + ";\n");
    if (isApplet && isAll) {
      StateManager.appendCmd(s, "# fullName = " + Escape.escape(fullName));
      StateManager.appendCmd(s, "# documentBase = "
          + Escape.escape(appletDocumentBase));
      StateManager
          .appendCmd(s, "# codeBase = " + Escape.escape(appletCodeBase));
      s.append("\n");
    }
    
    if (isAll || type.equalsIgnoreCase("windowState"))
      s.append(global.getWindowState(sfunc));
    if (isAll)
      s.append(getFunctionCalls(null));
    
    if (isAll || type.equalsIgnoreCase("fileState"))
      s.append(fileManager.getState(sfunc));
    
    
    if (isAll || type.equalsIgnoreCase("definedState"))
      s.append(modelSet.getDefinedState(sfunc, true));
    
    if (isAll || type.equalsIgnoreCase("variableState"))
      s.append(global.getState(sfunc));
    if (isAll || type.equalsIgnoreCase("dataState"))
      dataManager.getDataState(s, sfunc, modelSet.atoms, getAtomCount(),
          modelSet.getAtomicPropertyState(-1, null));
    
    if (isAll || type.equalsIgnoreCase("modelState"))
      s.append(modelSet.getState(sfunc, true));
    
    if (isAll || type.equalsIgnoreCase("colorState"))
      s.append(ColorManager.getState(sfunc));
    
    if (isAll || type.equalsIgnoreCase("frameState"))
      s.append(animationManager.getState(sfunc));
    
    if (isAll || type.equalsIgnoreCase("perspectiveState"))
      s.append(transformManager.getState(sfunc));
    
    if (isAll || type.equalsIgnoreCase("selectionState"))
      s.append(selectionManager.getState(sfunc));
    if (sfunc != null) {
      StateManager.appendCmd(sfunc, "set refreshing true");
      StateManager.appendCmd(sfunc, "set antialiasDisplay "
          + global.antialiasDisplay);
      StateManager.appendCmd(sfunc, "set antialiasTranslucent "
          + global.antialiasTranslucent);
      StateManager.appendCmd(sfunc, "set antialiasImages "
          + global.antialiasImages);
      if (getSpinOn())
        StateManager.appendCmd(sfunc, "spin on");
      sfunc.append("}\n\n_setState;\n");
    }
    if (isAll)
      s.append(sfunc);
    return s.toString();
  }

  public String getStructureState() {
    return modelSet.getState(null, false);
  }

  String getProteinStructureState() {
    return modelSet.getProteinStructureState(selectionManager.bsSelection,
        false, false, false);
  }

  public String getCoordinateState(BitSet bsSelected) {
    return modelSet.getAtomicPropertyState(AtomCollection.TAINT_COORD,
        bsSelected);
  }

  void setCurrentColorRange(String label) {
    float[] data = getDataFloat(label);
    BitSet bs = (data == null ? null : (BitSet) ((Object[]) dataManager
        .getData(label))[2]);
    setCurrentColorRange(data, bs);
  }

  void setCurrentColorRange(float[] data, BitSet bs) {
    colorManager.setCurrentColorRange(data, bs, global.propertyColorScheme);
  }

  public void setCurrentColorRange(float min, float max) {
    colorManager.setCurrentColorRange(min, max);
  }

  public float[] getCurrentColorRange() {
    return colorManager.getCurrentColorRange();
  }

  public void setData(String type, Object[] data, int atomCount,
                      int matchField, int matchFieldColumnCount, int field,
                      int fieldColumnCount) {
    dataManager.setData(this, type, data, atomCount, matchField,
        matchFieldColumnCount, field, fieldColumnCount);
  }

  public static Object testData; 
  public static Object testData2; 

  Object[] getData(String type) {
    return dataManager.getData(type);
  }

  public float[] getDataFloat(String label) {
    return dataManager.getDataFloat(label);
  }

  float[][] getDataFloat2D(String label) {
    return dataManager.getDataFloat2D(label);
  }

  public float[][][] getDataFloat3D(String label) {
    return dataManager.getDataFloat3D(label);
  }

  public float getDataFloat(String label, int atomIndex) {
    return dataManager.getDataFloat(label, atomIndex);
  }

  public String getAltLocListInModel(int modelIndex) {
    return modelSet.getAltLocListInModel(modelIndex);
  }

  public BitSet setConformation() {
    
    

    return modelSet.setConformation(-1, selectionManager.bsSelection);
  }

  
  public BitSet setConformation(int conformationIndex) {
    return modelSet.setConformation(animationManager.currentModelIndex,
        conformationIndex);
  }

  int autoHbond(BitSet bsBonds) {
    
    return autoHbond(selectionManager.bsSelection,
        selectionManager.bsSelection, bsBonds, 0, 0);
  }

  int autoHbond(BitSet bsFrom, BitSet bsTo, BitSet bsBonds,
                float maxXYDistance, float minAttachedAngle) {
    
    if (maxXYDistance < 0)
      maxXYDistance = global.hbondsDistanceMaximum;
    if (minAttachedAngle < 0)
      minAttachedAngle = global.hbondsAngleMinimum;
    minAttachedAngle *= (float) (Math.PI / 180);
    return modelSet.autoHbond(bsFrom, bsTo, bsBonds, maxXYDistance,
        minAttachedAngle);
  }

  public boolean hasCalculatedHBonds(BitSet bsAtoms) {
    return modelSet.hasCalculatedHBonds(bsAtoms);
  }

  public boolean havePartialCharges() {
    return modelSet.getPartialCharges() != null;
  }

  public SymmetryInterface getCurrentUnitCell() {
    return modelSet.getUnitCell(getDisplayModelIndex());
  }

  void setCurrentUnitCellOffset(int offset) {
    int modelIndex = animationManager.currentModelIndex;
    if (modelSet.setUnitCellOffset(modelIndex, offset))
      global.setParameterValue("=frame " + getModelNumberDotted(modelIndex)
          + "; set unitcell ", offset);
  }

  void setCurrentUnitCellOffset(Point3f pt) {
    int modelIndex = animationManager.currentModelIndex;
    if (modelSet.setUnitCellOffset(modelIndex, pt))
      global.setParameterValue("=frame " + getModelNumberDotted(modelIndex)
          + "; set unitcell ", Escape.escape(pt));
  }

  

  public String getDefaultMeasurementLabel(int nPoints) {
    switch (nPoints) {
    case 2:
      return global.defaultDistanceLabel;
    case 3:
      return global.defaultAngleLabel;
    default:
      return global.defaultTorsionLabel;
    }
  }

  public int getMeasurementCount() {
    int count = getShapePropertyAsInt(JmolConstants.SHAPE_MEASURES, "count");
    return count <= 0 ? 0 : count;
  }

  public String getMeasurementStringValue(int i) {
    String str = ""
        + getShapeProperty(JmolConstants.SHAPE_MEASURES, "stringValue", i);
    return str;
  }

  Vector getMeasurementInfo() {
    return (Vector) getShapeProperty(JmolConstants.SHAPE_MEASURES, "info");
  }

  public String getMeasurementInfoAsString() {
    return (String) getShapeProperty(JmolConstants.SHAPE_MEASURES, "infostring");
  }

  public int[] getMeasurementCountPlusIndices(int i) {
    int[] List = (int[]) getShapeProperty(JmolConstants.SHAPE_MEASURES,
        "countPlusIndices", i);
    return List;
  }

  void setPendingMeasurement(MeasurementPending measurementPending) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "pending",
        measurementPending);
  }

  MeasurementPending getPendingMeasurement() {
    return (MeasurementPending) getShapeProperty(JmolConstants.SHAPE_MEASURES,
        "pending");
  }

  void clearAllMeasurements() {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "clear", null);
  }

  public void clearMeasurements() {
    
    
    evalString("measures delete");
  }

  public boolean getJustifyMeasurements() {
    return global.justifyMeasurements;
  }

  void setMeasurementFormats(String strFormat) {
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "setFormats", strFormat);
  }

  void defineMeasurement(Vector monitorExpressions, float[] rangeMinMax,
                         boolean isDelete, boolean isAll,
                         boolean isAllConnected, boolean isOn, boolean isOff,
                         String strFormat) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "setConnected",
        isAllConnected ? Boolean.TRUE : Boolean.FALSE);
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "setRange", rangeMinMax);
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "setFormat", strFormat);
    setShapeProperty(JmolConstants.SHAPE_MEASURES, (isDelete ? "deleteVector"
        : isOn ? "showVector" : isOff ? "hideVector" : "defineVector")
        + (isAll ? "_All" : ""), monitorExpressions);
  }

  public void deleteMeasurement(int i) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "delete", new Integer(i));
  }

  void deleteMeasurement(int[] atomCountPlusIndices) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "delete",
        atomCountPlusIndices);
  }

  public void showMeasurement(int[] atomCountPlusIndices, boolean isON) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, isON ? "show" : "hide",
        atomCountPlusIndices);
  }

  void hideMeasurements(boolean isOFF) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "hideAll", Boolean
        .valueOf(isOFF));
  }

  void toggleMeasurement(int[] atomCountPlusIndices, String strFormat) {
    
    setShapeProperty(JmolConstants.SHAPE_MEASURES,
        (strFormat == null ? "toggle" : "toggleOn"), atomCountPlusIndices);
    if (strFormat != null)
      setShapeProperty(JmolConstants.SHAPE_MEASURES, "setFormats", strFormat);
  }

  
  
  

  void setAnimationDirection(int direction) {
    
    animationManager.setAnimationDirection(direction);
  }

  void reverseAnimation() {
    
    animationManager.reverseAnimation();
  }

  int getAnimationDirection() {
    return animationManager.animationDirection;
  }

  Hashtable getAnimationInfo() {
    return animationManager.getAnimationInfo();
  }

  public void setAnimationFps(int fps) {
    if (fps < 1)
      fps = 1;
    if (fps > 50)
      fps = 50;
    global.setParameterValue("animationFps", fps);
    
    
    animationManager.setAnimationFps(fps);
  }

  public int getAnimationFps() {
    return animationManager.animationFps;
  }

  void setAnimationReplayMode(int replay, float firstFrameDelay,
                              float lastFrameDelay) {
    

    
    
    
    animationManager.setAnimationReplayMode(replay, firstFrameDelay,
        lastFrameDelay);
  }

  int getAnimationReplayMode() {
    return animationManager.animationReplayMode;
  }

  void setAnimationOn(boolean animationOn) {
    
    boolean wasAnimating = animationManager.animationOn;
    if (animationOn == wasAnimating)
      return;
    animationManager.setAnimationOn(animationOn);
  }

  void resumeAnimation() {
    
    if (animationManager.animationOn) {
      Logger.debug("animation is ON in resumeAnimation");
      return;
    }
    animationManager.resumeAnimation();
  }

  void pauseAnimation() {
    
    if (!animationManager.animationOn || animationManager.animationPaused) {
      return;
    }
    animationManager.pauseAnimation();
  }

  void setAnimationRange(int modelIndex1, int modelIndex2) {
    animationManager.setAnimationRange(modelIndex1, modelIndex2);
  }

  public BitSet getVisibleFramesBitSet() {
    BitSet bs = BitSetUtil.copy(animationManager.getVisibleFramesBitSet());
    modelSet.selectDisplayedTrajectories(bs);
    return bs;
  }

  boolean isAnimationOn() {
    return animationManager.animationOn;
  }

  void setAnimationNext() {
    
    animationManager.setAnimationNext();
  }

  void setAnimationPrevious() {
    
    animationManager.setAnimationPrevious();
  }

  void setAnimationLast() {
    
    animationManager.setAnimationLast();
  }

  void rewindAnimation() {
    
    animationManager.rewindAnimation();
  }

  void setCurrentModelIndex(int modelIndex) {
    
    
    if (modelIndex == Integer.MIN_VALUE) {
      
      prevFrame = Integer.MIN_VALUE;
      setCurrentModelIndex(animationManager.currentModelIndex, true);
      return;
    }
    animationManager.setCurrentModelIndex(modelIndex);
  }

  void setTrajectory(int modelIndex) {
    modelSet.setTrajectory(modelIndex);
  }

  public void setTrajectory(BitSet bsModels) {
    modelSet.setTrajectory(bsModels);
  }

  boolean isTrajectory(int modelIndex) {
    return modelSet.isTrajectory(modelIndex);
  }

  public BitSet getBitSetTrajectories() {
    return modelSet.getBitSetTrajectories();
  }

  String getTrajectoryInfo() {
    return modelSet.getTrajectoryInfo();
  }

  void setFrameOffset(int modelIndex) {
    transformManager.setFrameOffset(modelIndex);
  }

  BitSet bsFrameOffsets;
  Point3f[] frameOffsets;

  public void setFrameOffsets(BitSet bsAtoms) {
    bsFrameOffsets = bsAtoms;
    transformManager.setFrameOffsets(frameOffsets = modelSet
        .getFrameOffsets(bsFrameOffsets));
  }

  public BitSet getFrameOffsets() {
    return bsFrameOffsets;
  }

  public void setCurrentModelIndex(int modelIndex, boolean clearBackground) {
    
    
    animationManager.setCurrentModelIndex(modelIndex, clearBackground);
  }

  public int getCurrentModelIndex() {
    return animationManager.currentModelIndex;
  }

  public int getDisplayModelIndex() {
    
    int modelIndex = animationManager.currentModelIndex;
    int backgroundIndex = getBackgroundModelIndex();
    return (backgroundIndex >= 0 ? -2 - modelIndex : modelIndex);
  }

  boolean haveFileSet() {
    return (getModelCount() > 1 && getModelNumber(0) > 1000000);
  }

  void setBackgroundModelIndex(int modelIndex) {
    
    animationManager.setBackgroundModelIndex(modelIndex);
    global.setParameterValue("backgroundModel", modelSet
        .getModelNumberDotted(modelIndex));
  }

  public int getBackgroundModelIndex() {
    return animationManager.backgroundModelIndex;
  }
  
  void setFrameVariables(int firstModelIndex, int lastModelIndex) {
    global.setParameterValue("_firstFrame",
        getModelNumberDotted(firstModelIndex));
    global
        .setParameterValue("_lastFrame", getModelNumberDotted(lastModelIndex));
  }

  boolean wasInMotion = false;
  int motionEventNumber;

  public int getMotionEventNumber() {
    return motionEventNumber;
  }

  void setInMotion(boolean inMotion) {
    
    if (wasInMotion ^ inMotion) {
      animationManager.setInMotion(inMotion);
      if (inMotion) {
        ++motionEventNumber;
      } else {
        repaintManager.refresh();
      }
      wasInMotion = inMotion;
    }
  }

  public boolean getInMotion() {
    
    return animationManager.inMotion;
  }

  public void pushHoldRepaint() {
    pushHoldRepaint(null);
  }

  void pushHoldRepaint(String why) {
    repaintManager.pushHoldRepaint();
  }

  public void popHoldRepaint() {
    repaintManager.popHoldRepaint();
  }

  void popHoldRepaint(String why) {
    repaintManager.popHoldRepaint();
  }

  private boolean refreshing = true;

  void setRefreshing(boolean TF) {
    
    refreshing = TF;
  }

  boolean getRefreshing() {
    return refreshing;
  }

  
  public void refresh(int mode, String strWhy) {
    
    
    
    
    if (repaintManager == null || !refreshing)
      return;
    
    if (mode > 0)
      repaintManager.refresh();
    if (mode % 3 != 0 && statusManager.doSync())
      statusManager.setSync(mode == 2 ? strWhy : null);
  }

  void requestRepaintAndWait() {
    if (!haveDisplay)
      return;
    repaintManager.requestRepaintAndWait();
    if (statusManager.doSync())
      statusManager.setSync(null);
  }

  void setSync() {
    if (statusManager.doSync())
      statusManager.setSync(null);
  }

  public void repaintView() {
    repaintManager.repaintDone();
  }

  private boolean axesAreTainted = false;

  public boolean areAxesTainted() {
    boolean TF = axesAreTainted;
    axesAreTainted = false;
    return TF;
  }

  

  final Dimension dimScreen = new Dimension();

  

  private int maximumSize = Integer.MAX_VALUE;

  private void setMaximumSize(int x) {
    maximumSize = Math.max(x, 100);
  }

  public void setScreenDimension(Dimension dim) {
    
    
    dim.height = Math.min(dim.height, maximumSize);
    dim.width = Math.min(dim.width, maximumSize);
    int height = dim.height;
    int width = dim.width;
    if (transformManager.stereoMode == JmolConstants.STEREO_DOUBLE)
      width = (width + 1) / 2;
    if (dimScreen.width == width && dimScreen.height == height)
      return;
    resizeImage(width, height, false, false, true);
  }

  private float imageFontScaling = 1;

  public float getImageFontScaling() {
    return imageFontScaling;
  }

  private void resizeImage(int width, int height, boolean isImageWrite,
                           boolean isGenerator, boolean isReset) {
    if (!isImageWrite && creatingImage)
      return;
    if (width > 0) {
      if (isImageWrite && !isReset)
        setImageFontScaling(width, height);
      dimScreen.width = width;
      dimScreen.height = height;
    }

    antialiasDisplay = false;
    
    if (isReset) {
      imageFontScaling = 1;
      antialiasDisplay = global.antialiasDisplay;
    } else if (isImageWrite && !isGenerator) {
      antialiasDisplay = global.antialiasImages;
    }
    if (antialiasDisplay)
      imageFontScaling *= 2;
    if (width > 0 && !isImageWrite) {
      global.setParameterValue("_width", width);
      global.setParameterValue("_height", height);
      setStatusResized(width, height);
    }
    if (width <= 0) {
      width = dimScreen.width;
      height = dimScreen.height;
    }
    transformManager.setScreenParameters(width, height,
        isImageWrite || isReset ? global.zoomLarge : false, antialiasDisplay,
        false, false);
    g3d.setWindowParameters(width, height, antialiasDisplay);
  }

  public int getScreenWidth() {
    return dimScreen.width;
  }

  public int getScreenHeight() {
    return dimScreen.height;
  }

  public int getScreenDim() {
    return (global.zoomLarge == (dimScreen.height > dimScreen.width) ? dimScreen.height
        : dimScreen.width);
  }

  public String generateOutput(String type, String fileName, int width,
                               int height) {
    if (isDataOnly)
      return "";
    mustRender = true;
    saveState("_Export");
    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    resizeImage(width, height, true, true, false);
    setModelVisibility();
    String data = repaintManager.generateOutput(type, g3d, modelSet, fileName);
    
    
    
    resizeImage(saveWidth, saveHeight, true, true, true);
    return data;
  }

  public void renderScreenImage(Graphics gLeft, Graphics gRight,
                                Dimension size, Rectangle clip) {
    
    
    
    
    

    if (creatingImage)
      return;
    if (isTainted || getSlabEnabled())
      setModelVisibility();
    isTainted = false;
    if (size != null)
      setScreenDimension(size);
    if (gRight == null) {
      Image image = getScreenImage();
      if (transformManager.stereoMode == JmolConstants.STEREO_DOUBLE) {
        render1(gLeft, image, dimScreen.width, 0);
        image = getImage(false);
      }
      render1(gLeft, image, 0, 0);
    } else {
      render1(gRight, getImage(true), 0, 0);
      render1(gLeft, getImage(false), 0, 0);
    }
    repaintView();
  }

  public void renderScreenImage(Graphics g, Dimension size, Rectangle clip) {
    renderScreenImage(g, null, size, clip);
  }

  private Image getImage(boolean isDouble) {
    Image image = null;
    try {
      g3d.beginRendering(transformManager.getStereoRotationMatrix(isDouble));
      render();
      g3d.endRendering();
      image = g3d.getScreenImage();
    } catch (Error er) {
      handleError(er, false);
      setErrorMessage("Error during rendering: " + er);
    }
    return image;
  }

  private boolean antialiasDisplay;

  private void render() {
    boolean antialias2 = antialiasDisplay && global.antialiasTranslucent;
    repaintManager.render(g3d, modelSet);
    if (g3d.setPass2(antialias2)) {
      transformManager.setAntialias(antialias2);
      repaintManager.render(g3d, modelSet);
      transformManager.setAntialias(antialiasDisplay);
    }
  }

  private Image getStereoImage(int stereoMode) {
    g3d.beginRendering(transformManager.getStereoRotationMatrix(true));
    render();
    g3d.endRendering();
    g3d.snapshotAnaglyphChannelBytes();
    g3d.beginRendering(transformManager.getStereoRotationMatrix(false));
    render();
    g3d.endRendering();
    switch (stereoMode) {
    case JmolConstants.STEREO_REDCYAN:
      g3d.applyCyanAnaglyph();
      break;
    case JmolConstants.STEREO_CUSTOM:
      g3d.applyCustomAnaglyph(transformManager.stereoColors);
      break;
    case JmolConstants.STEREO_REDBLUE:
      g3d.applyBlueAnaglyph();
      break;
    default:
      g3d.applyGreenAnaglyph();
    }
    return g3d.getScreenImage();
  }

  private void render1(Graphics g, Image img, int x, int y) {
    if (g != null && img != null) {
      try {
        g.drawImage(img, x, y, null);
      } catch (NullPointerException npe) {
        Logger.error("Sun!! ... fix graphics your bugs!");
      }
    }
    g3d.releaseScreenImage();
  }

  public Image getScreenImage() {
    return (transformManager.stereoMode <= JmolConstants.STEREO_DOUBLE ? getImage(transformManager.stereoMode == JmolConstants.STEREO_DOUBLE)
        : getStereoImage(transformManager.stereoMode));
  }

  
  public Object getImageAs(String type, int quality, int width, int height,
                           String fileName, OutputStream os) {
    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    mustRender = true;
    resizeImage(width, height, true, false, false);
    setModelVisibility();
    creatingImage = true;
    JmolImageCreatorInterface c = null;
    Object bytes = null;
    type = type.toLowerCase();
    if (!Parser.isOneOf(type, "jpg;jpeg;jpg64;jpeg64"))
      try {
        c = (JmolImageCreatorInterface) Interface
            .getOptionInterface("export.image.ImageCreator");
      } catch (Error er) {
        
        
      }
    if (c == null) {
      Image eImage = getScreenImage();
      if (eImage != null) {
        try {
          if (quality < 0)
            quality = 75;
          bytes = JpegEncoder.getBytes(eImage, quality, "");
          releaseScreenImage();
          if (type.equals("jpg64") || type.equals("jpeg64"))
            bytes = (bytes == null ? "" : Base64.getBase64((byte[]) bytes)
                .toString());
        } catch (Error er) {
          releaseScreenImage();
          handleError(er, false);
          setErrorMessage("Error creating image: " + er);
          bytes = getErrorMessage();
        }
      }
    } else {
      c.setViewer(this);
      try {
        bytes = c.getImageBytes(type, quality, fileName, null, os);
      } catch (IOException e) {
        bytes = e;
        setErrorMessage("Error creating image: " + e);
      } catch (Error er) {
        handleError(er, false);
        setErrorMessage("Error creating image: " + er);
        bytes = getErrorMessage();
      }
    }
    creatingImage = false;
    resizeImage(saveWidth, saveHeight, true, false, true);
    return bytes;
  }

  public void releaseScreenImage() {
    g3d.releaseScreenImage();
  }

  
  
  

  boolean getAllowEmbeddedScripts() {
    return global.allowEmbeddedScripts;
  }

  public String evalFile(String strFilename) {
    
    int ptWait = strFilename.indexOf(" -noqueue"); 
    if (ptWait >= 0) {
      return (String) evalStringWaitStatus("String", strFilename.substring(0,
          ptWait), "", true, false, false);
    }
    return scriptManager.addScript(strFilename, true, false);
  }

  public String script(String strScript) {
    return evalString(strScript);
  }

  String interruptScript = "";

  String getInterruptScript() {
    String s = interruptScript;
    interruptScript = "";
    if (Logger.debugging && s != "")
      Logger.debug("interrupt: " + s);
    return s;
  }

  public String evalString(String strScript) {
    return evalStringQuiet(strScript, false, true);
  }

  public String evalStringQuiet(String strScript) {
    return evalStringQuiet(strScript, true, true);
  }

  String evalStringQuiet(String strScript, boolean isQuiet,
                         boolean allowSyncScript) {
    
    
    
    
    
    
    if (allowSyncScript && statusManager.syncingScripts
        && strScript.indexOf("#NOSYNC;") < 0)
      syncScript(strScript + " #NOSYNC;", null);
    if (eval.isExecutionPaused() && strScript.charAt(0) != '!')
      strScript = '!' + TextFormat.trim(strScript, "\n\r\t ");
    boolean isInterrupt = (strScript.length() > 0 && strScript.charAt(0) == '!');
    if (isInterrupt)
      strScript = strScript.substring(1);
    String msg = checkScriptExecution(strScript);
    if (msg != null)
      return msg;
    if (isScriptExecuting() && (isInterrupt || eval.isExecutionPaused())) {
      interruptScript = strScript;
      if (strScript.indexOf("moveto ") == 0)
        scriptManager.flushQueue("moveto ");
      return "!" + strScript;
    }
    interruptScript = "";
    if (isQuiet)
      strScript += JmolConstants.SCRIPT_EDITOR_IGNORE;
    return scriptManager.addScript(strScript, false, isQuiet
        && !getMessageStyleChime());
  }

  private String checkScriptExecution(String strScript) {
    String str = strScript;
    if (str.indexOf("\0##") >= 0)
      str = str.substring(0, str.indexOf("\0##"));
    if (checkResume(str))
      return "script processing resumed";
    if (checkStepping(str))
      return "script processing stepped";
    if (checkHalt(str))
      return "script execution halted";
    return null;
  }

  boolean usingScriptQueue() {
    return scriptManager.useQueue;
  }

  public void clearScriptQueue() {
    
    
    scriptManager.clearQueue();
  }

  public boolean checkResume(String str) {
    if (str.equalsIgnoreCase("resume")) {
      scriptStatus("", "execution resumed", 0, null);
      resumeScriptExecution();
      return true;
    }
    return false;
  }

  public boolean checkStepping(String str) {
    if (str.equalsIgnoreCase("step")) {
      stepScriptExecution();
      return true;
    }
    if (str.equalsIgnoreCase("?")) {
      scriptStatus(eval.getNextStatement());
      return true;
    }
    return false;
  }

  public boolean checkHalt(String str) {
    if (str.equalsIgnoreCase("pause")) {
      pauseScriptExecution();
      if (scriptEditorVisible)
        scriptStatus("", "paused -- type RESUME to continue", 0, null);
      return true;
    }
    str = str.toLowerCase();
    if (str.startsWith("exit")) {
      haltScriptExecution();
      clearScriptQueue();
      if (isCmdLine_c_or_C_Option)
        Logger.info("exit -- stops script checking");
      isCmdLine_c_or_C_Option = false;
      return str.equals("exit");
    }
    if (str.startsWith("quit")) {
      haltScriptExecution();
      if (isCmdLine_c_or_C_Option)
        Logger.info("quit -- stops script checking");
      isCmdLine_c_or_C_Option = false;
      return str.equals("quit");
    }
    return false;
  }

  

  public String scriptWait(String strScript) {
    scriptManager.waitForQueue();
    boolean doTranslateTemp = GT.getDoTranslate();
    GT.setDoTranslate(false);
    String str = (String) evalStringWaitStatus("JSON", strScript,
        "+scriptStarted,+scriptStatus,+scriptEcho,+scriptTerminated", false,
        false, false);
    GT.setDoTranslate(doTranslateTemp);
    return str;
  }

  public Object scriptWaitStatus(String strScript, String statusList) {
    scriptManager.waitForQueue();
    boolean doTranslateTemp = GT.getDoTranslate();
    GT.setDoTranslate(false);
    Object ret = evalStringWaitStatus("object", strScript, statusList, false,
        false, false);
    GT.setDoTranslate(doTranslateTemp);
    return ret;
  }

  public Object evalStringWaitStatus(String returnType, String strScript,
                                     String statusList) {
    scriptManager.waitForQueue();
    return evalStringWaitStatus(returnType, strScript, statusList, false,
        false, false);
  }

  int scriptIndex;
  boolean isScriptQueued = true;

  synchronized Object evalStringWaitStatus(String returnType, String strScript,
                                           String statusList,
                                           boolean isScriptFile,
                                           boolean isQuiet, boolean isQueued) {
    
    
    
    
    if (strScript == null)
      return null;
    String str = checkScriptExecution(strScript);
    if (str != null)
      return str;

    
    
    
    
    String oldStatusList = statusManager.getStatusList();
    getProperty("String", "jmolStatus", statusList);
    if (isCmdLine_c_or_C_Option)
      Logger.info("--checking script:\n" + eval.getScript() + "\n----\n");
    boolean historyDisabled = (strScript.indexOf(")") == 0);
    if (historyDisabled)
      strScript = strScript.substring(1);
    historyDisabled = historyDisabled || !isQueued; 
                                                    
    setErrorMessage(null);
    boolean isOK = (isScriptFile ? eval.compileScriptFile(strScript, isQuiet)
        : eval.compileScriptString(strScript, isQuiet));
    String strErrorMessage = eval.getErrorMessage();
    String strErrorMessageUntranslated = eval.getErrorMessageUntranslated();
    setErrorMessage(strErrorMessage, strErrorMessageUntranslated);
    if (isOK) {
      isScriptQueued = isQueued;
      if (!isQuiet)
        scriptStatus(null, strScript, -2 - (++scriptIndex), null);
      eval.evaluateCompiledScript(isCmdLine_c_or_C_Option, isCmdLine_C_Option,
          historyDisabled, listCommands);
      setErrorMessage(strErrorMessage = eval.getErrorMessage(),
          strErrorMessageUntranslated = eval.getErrorMessageUntranslated());
      if (!isQuiet)
        scriptStatus("Jmol script terminated", strErrorMessage, 1 + eval
            .getExecutionWalltime(), strErrorMessageUntranslated);
    } else {
      scriptStatus(strErrorMessage);
      scriptStatus("Jmol script terminated", strErrorMessage, 1,
          strErrorMessageUntranslated);
    }
    if (strErrorMessage != null && autoExit)
      exitJmol();      
    if (isCmdLine_c_or_C_Option) {
      if (strErrorMessage == null)
        Logger.info("--script check ok");
      else
        Logger.error("--script check error\n" + strErrorMessageUntranslated);
    }
    if (isCmdLine_c_or_C_Option)
      Logger.info("(use 'exit' to stop checking)");
    isScriptQueued = true;
    if (returnType.equalsIgnoreCase("String"))
      return strErrorMessageUntranslated;
    
    Object info = getProperty(returnType, "jmolStatus", statusList);
    
    getProperty("object", "jmolStatus", oldStatusList);
    return info;
  }

  void exitJmol() {
    Logger.debug("exitJmol -- exiting");
    System.out.flush();
    System.exit(0);
  }

  private Object scriptCheck(String strScript, boolean returnContext) {
    
    if (strScript.indexOf(")") == 0 || strScript.indexOf("!") == 0) 
      strScript = strScript.substring(1);
    ScriptContext sc = (new ScriptEvaluator(this)).checkScriptSilent(strScript);
    if (returnContext || sc.errorMessage == null)
      return sc;
    return sc.errorMessage;
  }

  public synchronized Object scriptCheck(String strScript) {
    return scriptCheck(strScript, false);
  }

  public boolean isScriptExecuting() {
    return eval.isScriptExecuting();
  }

  public void haltScriptExecution() {
    eval.haltExecution();
  }

  public void resumeScriptExecution() {
    eval.resumePausedExecution();
  }

  public void stepScriptExecution() {
    eval.stepPausedExecution();
  }

  public void pauseScriptExecution() {
    eval.pauseExecution();
  }

  String getDefaultLoadScript() {
    return global.defaultLoadScript;
  }

  String getLoadFormat() {
    return global.loadFormat;
  }

  public String getStandardLabelFormat() {
    return stateManager.getStandardLabelFormat();
  }

  public int getRibbonAspectRatio() {
    
    return global.ribbonAspectRatio;
  }

  public float getSheetSmoothing() {
    
    return global.sheetSmoothing;
  }

  public boolean getSsbondsBackbone() {
    return global.ssbondsBackbone;
  }

  public boolean getHbondsBackbone() {
    return global.hbondsBackbone;
  }

  public boolean getHbondsSolid() {
    return global.hbondsSolid;
  }

  public void setMarBond(short marBond) {
    global.bondRadiusMilliAngstroms = marBond;
    global.setParameterValue("bondRadiusMilliAngstroms", marBond);
    setShapeSize(JmolConstants.SHAPE_STICKS, marBond * 2, Float.NaN, BitSetUtil
        .setAll(getAtomCount()));
  }

  int hoverAtomIndex = -1;
  String hoverText;

  void hoverOn(int atomIndex, int modifiers) {
    if (eval != null && isScriptExecuting() || atomIndex == hoverAtomIndex
        || global.hoverDelayMs == 0)
      return;
    if (!isInSelectionSubset(atomIndex))
      return;
    loadShape(JmolConstants.SHAPE_HOVER);
    Atom atom;
    if (modifiers == MouseManager.SHIFT 
        && getPickingMode() == JmolConstants.PICKING_LABEL
        && (atom = modelSet.getAtomAt(atomIndex)) != null
        && atom.isShapeVisible(JmolConstants.getShapeVisibilityFlag(JmolConstants.SHAPE_LABELS))) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", GT._("Drag to move label"));
    }
    setShapeProperty(JmolConstants.SHAPE_HOVER, "text", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "target", new Integer(atomIndex));
    hoverText = null;
    hoverAtomIndex = atomIndex;
    refresh(3, "hover on atom");
  }

  int getHoverDelay() {
    return global.hoverDelayMs;
  }

  public void hoverOn(int x, int y, String text) {
    
    if (eval != null && isScriptExecuting())
      return;
    loadShape(JmolConstants.SHAPE_HOVER);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "xy", new Point3i(x, y, 0));
    setShapeProperty(JmolConstants.SHAPE_HOVER, "target", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "text", text);
    hoverAtomIndex = -1;
    hoverText = text;
    refresh(3, "hover on point");
  }

  void hoverOff() {
    if (hoverAtomIndex >= 0) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "target", null);
      hoverAtomIndex = -1;
    }
    if (hoverText != null) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "text", null);
      hoverText = null;
    }
    setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", null);
    refresh(3, "hover off");
  }

  void setLabel(String strLabel) {
    modelSet.setLabel(strLabel, selectionManager.bsSelection);
  }

  void togglePickingLabel(BitSet bs) {
    
    if (bs == null)
      bs = selectionManager.bsSelection;
    loadShape(JmolConstants.SHAPE_LABELS);
    
    modelSet.setShapeProperty(JmolConstants.SHAPE_LABELS, "toggleLabel", null,
        bs);
  }

  BitSet getBitSetSelection() {
    return selectionManager.bsSelection;
  }

  public void clearShapes() {
    repaintManager.clear();
  }

  public void loadShape(int shapeID) {
    modelSet.loadShape(shapeID);
  }

  void setShapeSize(int shapeID, int size, float fsize) {
    
    
    
    setShapeSize(shapeID, size, fsize, selectionManager.bsSelection);
  }

  public void setShapeSize(int shapeID, int size, BitSet bsAtoms) {
    
    setShapeSize(shapeID, size, Float.NaN, bsAtoms);
  }
  
  public void setShapeSize(int shapeID, int size, float fsize, BitSet bsAtoms) {
    
    
    
    
    
    modelSet.setShapeSize(shapeID, size, fsize, bsAtoms);
  }

  public void setShapeProperty(int shapeID, String propertyName, Object value) {
    
    if (shapeID < 0)
      return; 
    modelSet.setShapeProperty(shapeID, propertyName, value,
        selectionManager.bsSelection);
  }

  void setShapeProperty(int shapeID, String propertyName, Object value,
                        BitSet bs) {
    
    if (shapeID < 0)
      return; 
    modelSet.setShapeProperty(shapeID, propertyName, value, bs);
  }

  void setShapePropertyArgb(int shapeID, String propertyName, int argb) {
    
    setShapeProperty(shapeID, propertyName, argb == 0 ? null : new Integer(
        argb | 0xFF000000));
  }

  public Object getShapeProperty(int shapeType, String propertyName) {
    return modelSet
        .getShapeProperty(shapeType, propertyName, Integer.MIN_VALUE);
  }

  public boolean getShapeProperty(int shapeType, String propertyName, Object[] data) {
    return modelSet.getShapeProperty(shapeType, propertyName, data);
  }

  Object getShapeProperty(int shapeType, String propertyName, int index) {
    return modelSet.getShapeProperty(shapeType, propertyName, index);
  }

  int getShapePropertyAsInt(int shapeID, String propertyName) {
    Object value = getShapeProperty(shapeID, propertyName);
    return value == null || !(value instanceof Integer) ? Integer.MIN_VALUE
        : ((Integer) value).intValue();
  }

  short getColix(Object object) {
    return Graphics3D.getColix(object);
  }

  boolean getRasmolHydrogenSetting() {
    return global.rasmolHydrogenSetting;
  }

  boolean getRasmolHeteroSetting() {
    return global.rasmolHeteroSetting;
  }

  boolean getDebugScript() {
    return global.debugScript;
  }

  public void setDebugScript(boolean debugScript) {
    global.debugScript = debugScript;
    global.setParameterValue("debugScript", debugScript);
    eval.setDebugging();
  }

  void atomPicked(int atomIndex, Point3fi ptClicked, int modifiers,
                  boolean isDoubleClick) {
    pickingManager.atomPicked(atomIndex, ptClicked, modifiers, isDoubleClick);
  }

  void clearClickCount() {
    
    setTainted(true);
  }

  public final static int CURSOR_DEFAULT = 0;
  public final static int CURSOR_HAND = 1;
  public final static int CURSOR_CROSSHAIR = 2;
  public final static int CURSOR_MOVE = 3;
  public final static int CURSOR_WAIT = 4;

  private int currentCursor = CURSOR_DEFAULT;

  public void setCursor(int cursor) {
    if (currentCursor == cursor || display == null)
      return;
    int c;
    switch (currentCursor = cursor) {
    case CURSOR_HAND:
      c = Cursor.HAND_CURSOR;
      break;
    case CURSOR_MOVE:
      c = Cursor.MOVE_CURSOR;
      break;
    case CURSOR_CROSSHAIR:
      c = Cursor.CROSSHAIR_CURSOR;
      break;
    case CURSOR_WAIT:
      c = Cursor.WAIT_CURSOR;
      break;
    default:
      display.setCursor(Cursor.getDefaultCursor());
      return;
    }
    display.setCursor(Cursor.getPredefinedCursor(c));
  }

  private void setPickingMode(String mode) {
    int pickingMode = JmolConstants.getPickingMode(mode);
    if (pickingMode < 0)
      pickingMode = JmolConstants.PICKING_IDENT;
    pickingManager.setPickingMode(pickingMode);
    global.setParameterValue("picking", JmolConstants
        .getPickingModeName(pickingManager.getPickingMode()));
  }

  public int getPickingMode() {
    return pickingManager.getPickingMode();
  }

  public boolean getDrawPicking() {
    return global.drawPicking;
  }

  public boolean getBondPicking() {
    return global.bondPicking;
  }

  private boolean getAtomPicking() {
    return global.atomPicking;
  }

  private void setPickingStyle(String style) {
    int pickingStyle = JmolConstants.getPickingStyle(style);
    if (pickingStyle < 0)
      pickingStyle = JmolConstants.PICKINGSTYLE_SELECT_JMOL;
    pickingManager.setPickingStyle(pickingStyle);
    global.setParameterValue("pickingStyle", JmolConstants
        .getPickingStyleName(pickingManager.getPickingStyle()));
  }

  int getPickingStyle() {
    return pickingManager.getPickingStyle();
  }

  public boolean getDrawHover() {
    return pickingManager.getDrawHover();
  }

  public String getAtomInfo(int atomIndex) {
    
    return (atomIndex >= 0 ? modelSet.getAtomInfo(atomIndex, null)
        : (String) modelSet.getShapeProperty(JmolConstants.SHAPE_MEASURES,
            "pointInfo", -atomIndex));
  }

  public String getAtomInfoXYZ(int atomIndex, boolean useChimeFormat) {
    return modelSet.getAtomInfoXYZ(atomIndex, useChimeFormat);
  }

  

  public void setJmolCallbackListener(JmolCallbackListener jmolCallbackListener) {
    statusManager.setJmolCallbackListener(jmolCallbackListener);
  }

  public void setJmolStatusListener(JmolStatusListener jmolStatusListener) {
    statusManager.setJmolStatusListener(jmolStatusListener, null);
  }

  public Hashtable getMessageQueue() {
    
    return statusManager.getMessageQueue();
  }

  Object getStatusChanged(String statusNameList) {
    return statusManager.getStatusChanged(statusNameList);
  }

  void popupMenu(int x, int y) {
    if (isPreviewOnly || global.disablePopupMenu)
      return;
    if (jmolpopup == null)
      jmolpopup = JmolPopup.newJmolPopup(this, true, menuStructure, true);
    jmolpopup.show(x, y);
  }

  String getMenu(String type) {
    if (jmolpopup == null)
      jmolpopup = JmolPopup.newJmolPopup(this, true, menuStructure, true);
    return (jmolpopup == null ? "" : jmolpopup.getMenu("Jmol version "
            + Viewer.getJmolVersion() + "|_GET_MENU|" + type));
  }

  void setMenu(String fileOrText, boolean isFile) {
    if (isFile)
      Logger.info("Setting menu "
          + (fileOrText.length() == 0 ? "to Jmol defaults" : "from file "
              + fileOrText));
    if (fileOrText.length() == 0)
      fileOrText = null;
    else if (isFile)
      fileOrText = getFileAsString(fileOrText);
    getProperty("DATA_API", "setMenu", fileOrText);
    statusManager.setCallbackFunction("menu", fileOrText);
  }

  

  

  

  int prevFrame = Integer.MIN_VALUE;

  void setStatusFrameChanged(int frameNo) {
    int modelIndex = animationManager.currentModelIndex;
    if (frameNo == Integer.MIN_VALUE) {
      
      prevFrame = Integer.MIN_VALUE;
      frameNo = modelIndex;
    }
    transformManager.setVibrationPeriod(Float.NaN);

    int firstIndex = animationManager.firstModelIndex;
    int lastIndex = animationManager.lastModelIndex;

    if (firstIndex == lastIndex)
      modelIndex = firstIndex;
    int frameID = getModelFileNumber(modelIndex);
    int fileNo = frameID;
    int modelNo = frameID % 1000000;
    int firstNo = getModelFileNumber(firstIndex);
    int lastNo = getModelFileNumber(lastIndex);
    String strModelNo;
    if (fileNo == 0) {
      strModelNo = getModelNumberDotted(firstIndex);
      if (firstIndex != lastIndex)
        strModelNo += " - " + getModelNumberDotted(lastIndex);
      if (firstNo / 1000000 == lastNo / 1000000)
        fileNo = firstNo;
    } else {
      strModelNo = getModelNumberDotted(modelIndex);
    }
    if (fileNo != 0)
      fileNo = (fileNo < 1000000 ? 1 : fileNo / 1000000);

    global.setParameterValue("_currentFileNumber", fileNo);
    global.setParameterValue("_currentModelNumberInFile", modelNo);
    global.setParameterValue("_frameID", frameID);
    global.setParameterValue("_modelNumber", strModelNo);
    global.setParameterValue("_modelName", (modelIndex < 0 ? ""
        : getModelName(modelIndex)));
    global.setParameterValue("_modelTitle", (modelIndex < 0 ? ""
        : getModelTitle(modelIndex)));
    global.setParameterValue("_modelFile", (modelIndex < 0 ? ""
        : getModelFileName(modelIndex)));

    if (modelIndex == prevFrame) {
      return;
    }
    prevFrame = modelIndex;

    statusManager.setStatusFrameChanged(frameNo, fileNo, modelNo,
        (animationManager.animationDirection < 0 ? -firstNo : firstNo),
        (animationManager.currentDirection < 0 ? -lastNo : lastNo));
  }

  

  public void scriptEcho(String strEcho) {
    statusManager.setScriptEcho(strEcho, isScriptQueued);
    if (listCommands && strEcho != null && strEcho.indexOf("$[") == 0)
      Logger.info(strEcho);
  }

  
  void notifyError(String errType, String errMsg, String errMsgUntranslated) {
    statusManager.notifyError(errType, errMsg, errMsgUntranslated);
  }

  

  String jsEval(String strEval) {
    return statusManager.jsEval(strEval);
  }

  

  public void setStatusAtomHovered(int atomIndex, String info) {
    global.setParameterValue("_atomhovered", atomIndex);
    statusManager.setStatusAtomHovered(atomIndex, info);
  }

  
  private void setFileLoadStatus(int ptLoad, String fullPathName,
                                 String fileName, String modelName,
                                 String strError) {
    setErrorMessage(strError);
    global.setParameterValue("_loadPoint", ptLoad);
    boolean doCallback = (ptLoad == FILE_STATUS_MODELSET_CREATED
        || ptLoad == FILE_STATUS_ZAPPED || ptLoad == FILE_STATUS_NOT_LOADED);
    statusManager.setFileLoadStatus(fullPathName, fileName, modelName,
        strError, ptLoad, doCallback);
  }

  

  public void setStatusMeasuring(String status, int intInfo, String strMeasure) {
    
    statusManager.setStatusMeasuring(status, intInfo, strMeasure);
  }

  

  public void notifyMinimizationStatus() {
    Object step =  getParameter("_minimizationStep");
    statusManager.notifyMinimizationStatus(
        (String) getParameter("_minimizationStatus"),
        step instanceof String ? new Integer(0) : (Integer) step,
        (Float) getParameter("_minimizationEnergy"),
        (Float) getParameter("_minimizationEnergyDiff"));
  }

  

  public void setStatusAtomPicked(int atomIndex, String info) {
    if (info == null) {
      info = global.pickLabel;
      if (info.length() == 0)
        info = getAtomInfoXYZ(atomIndex, getMessageStyleChime());
      else
        info = modelSet.getAtomInfo(atomIndex, info);
    }
    global.setParameterValue("_atompicked", atomIndex);
    global.setParameterValue("_pickinfo", info);
    statusManager.setStatusAtomPicked(atomIndex, info);
  }

  

  public void setStatusResized(int width, int height) {
    statusManager.setStatusResized(width, height);
  }

  

  void scriptStatus(String strStatus) {
    scriptStatus(strStatus, "", 0, null);
  }

  void scriptStatus(String strStatus, String statusMessage) {
    scriptStatus(strStatus, statusMessage, 0, null);
  }

  void scriptStatus(String strStatus, String statusMessage,
                            int msWalltime, String strErrorMessageUntranslated) {
    statusManager.setScriptStatus(strStatus, statusMessage, msWalltime,
        strErrorMessageUntranslated);
  }

  

  

  private String getModelTitle(int modelIndex) {
    
    return modelSet == null ? null : modelSet.getModelTitle(modelIndex);
  }

  private String getModelFileName(int modelIndex) {
    
    return modelSet == null ? null : modelSet.getModelFileName(modelIndex);
  }

  public String dialogAsk(String type, String fileName) {
    return statusManager.dialogAsk(type, fileName);
  }

  int getScriptDelay() {
    return global.scriptDelay;
  }

  public void showUrl(String urlString) {
    
    
    
    if (urlString == null)
      return;
    if (urlString.indexOf(":") < 0) {
      String base = fileManager.getAppletDocumentBase();
      if (base == "")
        base = fileManager.getFullPathName();
      if (base.indexOf("/") >= 0) {
        base = base.substring(0, base.lastIndexOf("/") + 1);
      } else if (base.indexOf("\\") >= 0) {
        base = base.substring(0, base.lastIndexOf("\\") + 1);
      }
      urlString = base + urlString;
    }
    Logger.info("showUrl:" + urlString);
    statusManager.showUrl(urlString);
  }

  
  public void setMeshCreator(Object meshCreator) {
    loadShape(JmolConstants.SHAPE_ISOSURFACE);
    setShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "meshCreator", meshCreator);
  }
  
  void showConsole(boolean showConsole) {
    
      if (appConsole == null)
        getProperty("DATA_API", "getAppConsole", Boolean.TRUE);
      appConsole.setVisible(showConsole);
  }

  void clearConsole() {
    
    statusManager.clearConsole();
  }

  Object getParameterEscaped(String key) {
    return global.getParameterEscaped(key, 0);
  }

  public Object getParameter(String key) {
    return global.getParameter(key);
  }

  ScriptVariable getOrSetNewVariable(String key, boolean doSet) {
    return global.getOrSetNewVariable(key, doSet);
  }

  ScriptVariable setUserVariable(String name, ScriptVariable value) {
    return global.setUserVariable(name, value);
  }

  void unsetProperty(String name) {
    global.unsetUserVariable(name);
  }
  
  String getVariableList() {
    return global.getVariableList();
  }

  public boolean getBooleanProperty(String key) {
    return getBooleanProperty(key, true);
  }

  

  public boolean getBooleanProperty(String key, boolean doICare) {
    
    key = key.toLowerCase();
    if (global.htPropertyFlags.containsKey(key)) {
      return ((Boolean) global.htPropertyFlags.get(key)).booleanValue();
    }
    
    if (key.equalsIgnoreCase("executionPaused"))
      return eval.isExecutionPaused();
    if (key.equalsIgnoreCase("executionStepping"))
      return eval.isExecutionStepping();
    if (key.equalsIgnoreCase("haveBFactors"))
      return (modelSet.getBFactors() != null);
    if (key.equalsIgnoreCase("colorRasmol"))
      return colorManager.getDefaultColorRasmol();
    if (key.equalsIgnoreCase("frank"))
      return getShowFrank();
    if (key.equalsIgnoreCase("showSelections"))
      return getSelectionHaloEnabled();
    if (global.htUserVariables.containsKey(key)) {
      ScriptVariable t = global.getUserVariable(key);
      if (t.tok == Token.on)
        return true;
      if (t.tok == Token.off)
        return false;
    }
    if (doICare)
      Logger.error("viewer.getBooleanProperty(" + key + ") - unrecognized");
    return false;
  }

  public void setStringProperty(String key, String value) {
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    
    boolean notFound = false;
    while (true) {

      
      if (key.equalsIgnoreCase("atomTypes")) {
        global.atomTypes = value;
        break;
      }

      
      if (key.equalsIgnoreCase("currentLocalPath"))
        break;
      
      
      if (key.equalsIgnoreCase("pickLabel")) {
        global.pickLabel = value;
        break;
      }

      
      if (key.equalsIgnoreCase("quaternionFrame")) {
        if (value.length() == 2 && value.startsWith("R"))
          
          global.quaternionFrame = value.substring(0, 2);
        else
          global.quaternionFrame = "" + (value.toLowerCase() + "p").charAt(0);
        if (!Parser.isOneOf(global.quaternionFrame, "a;n;c;p;q;RC;RP"))
          global.quaternionFrame = "p";
        modelSet.setHaveStraightness(false);
        break;
      }

      
      if (key.equalsIgnoreCase("defaultVDW")) {
        setDefaultVdw(value);
        return;
      }

      
      if (key.equalsIgnoreCase("language")) {
        
        setLanguage(value);
        value = GT.getLanguage();
        break;
      }

      

      if (key.equalsIgnoreCase("loadFormat")) {
        global.loadFormat = value;
        break;
      }

      

      if (key.equalsIgnoreCase("backgroundColor")) {
        setObjectColor("background", value);
        return;
      }

      if (key.equalsIgnoreCase("axesColor")) {
        setObjectColor("axis1", value);
        setObjectColor("axis2", value);
        setObjectColor("axis3", value);
        return;
      }

      if (key.equalsIgnoreCase("axis1Color")) {
        setObjectColor("axis1", value);
        return;
      }

      if (key.equalsIgnoreCase("axis2Color")) {
        setObjectColor("axis2", value);
        return;
      }

      if (key.equalsIgnoreCase("axis3Color")) {
        setObjectColor("axis3", value);
        return;
      }

      if (key.equalsIgnoreCase("boundBoxColor")) {
        setObjectColor("boundbox", value);
        return;
      }

      if (key.equalsIgnoreCase("unitCellColor")) {
        setObjectColor("unitcell", value);
        return;
      }

      if (key.equalsIgnoreCase("propertyColorScheme")) {
        setPropertyColorScheme(value, false);
        return;
      }

      if (key.equalsIgnoreCase("propertyColorSchemeOverload")) {
        setPropertyColorScheme(value, true);
        return;
      }

      if (key.equalsIgnoreCase("hoverLabel")) {
        setShapeProperty(JmolConstants.SHAPE_HOVER, "atomLabel", value);
        break;
      }
      
      if (key.equalsIgnoreCase("defaultDistanceLabel")) {
        global.defaultDistanceLabel = value;
        break;
      }
      if (key.equalsIgnoreCase("defaultAngleLabel")) {
        global.defaultAngleLabel = value;
        break;
      }
      if (key.equalsIgnoreCase("defaultTorsionLabel")) {
        global.defaultTorsionLabel = value;
        break;
      }
      if (key.equalsIgnoreCase("defaultLoadScript")) {
        global.defaultLoadScript = value;
        break;
      }
      if (key.equalsIgnoreCase("appletProxy")) {
        fileManager.setAppletProxy(value);
        break;
      }
      if (key.equalsIgnoreCase("defaultDirectory")) {
        if (value == null)
          value = "";
        value = value.replace('\\', '/');
        global.defaultDirectory = value;
        break;
      }
      if (key.equalsIgnoreCase("helpPath")) {
        global.helpPath = value;
        break;
      }
      if (key.equalsIgnoreCase("defaults")) {
        setDefaults(value);
        break;
      }
      if (key.equalsIgnoreCase("defaultColorScheme")) {
        setDefaultColors(value);
        break;
      }
      if (key.equalsIgnoreCase("picking")) {
        setPickingMode(value);
        return;
      }
      if (key.equalsIgnoreCase("pickingStyle")) {
        setPickingStyle(value);
        return;
      }
      if (key.equalsIgnoreCase("dataSeparator")) {
        
        break;
      }
      if (key.toLowerCase().indexOf("callback") >= 0) {
        statusManager.setCallbackFunction(key, (value.length() == 0
            || value.equalsIgnoreCase("none") ? null : value));
        break;
      }
      notFound = true;
      break;
    }
    key = key.toLowerCase();
    boolean isJmol = global.htParameterValues.containsKey(key);
    if (!isJmol && notFound && key.charAt(0) != '@') {
      
      if (global.htPropertyFlags.containsKey(key)
          || global.htPropertyFlagsRemoved.containsKey(key)) {
        setPropertyError(GT._(
            "ERROR: cannot set boolean flag to string value: {0}", key));
        return;
      }
    }
    if (isJmol)
      global.setParameterValue(key, value);
    else
      global.setUserVariable(key, new ScriptVariable(Token.string, value));
  }

  private String language = GT.getLanguage();
  public String getLanguage() {
    return language;
  }
  
  private void setLanguage(String value) {
    
      new GT(value);
      language = GT.getLanguage();
      if (jmolpopup != null)
        jmolpopup = JmolPopup.newJmolPopup(this, true, menuStructure, true);
      statusManager.setCallbackFunction("language", language);
  }

  private void setPropertyError(String msg) {
    Logger.error(msg);
    scriptEcho(msg);
  }

  void removeUserVariable(String key) {
    global.removeUserVariable(key);
    if (key.indexOf("callback") >= 0)
      statusManager.setCallbackFunction(key, null);
  }

  boolean isJmolVariable(String key) {
    return global.isJmolVariable(key);
  }

  public void setFloatProperty(String key, float value) {
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setFloatProperty(key, value, false);
  }

  private boolean setFloatProperty(String key, float value, boolean isInt) {
    
    boolean notFound = false;
    while (true) {
      
      if (key.equalsIgnoreCase("navX")) {
        setSpin("X", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("navY")) {
        setSpin("Y", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("navZ")) {
        setSpin("Z", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("navFPS")) {
        if (Float.isNaN(value))
          return true;
        setSpin("FPS", (int) value);
        break;
      }

      
      if (key.equalsIgnoreCase("hbondsAngleMinimum")) {
        global.hbondsAngleMinimum = value;
        break;
      }

      
      if (key.equalsIgnoreCase("hbondsDistanceMaximum")) {
        global.hbondsDistanceMaximum = value;
        break;
      }

      
      if (key.equalsIgnoreCase("pointGroupDistanceTolerance")) {
        global.pointGroupDistanceTolerance = value;
        break;
      }
      if (key.equalsIgnoreCase("pointGroupLinearTolerance")) {
        global.pointGroupLinearTolerance = value;
        break;
      }

      
      if (key.equalsIgnoreCase("ellipsoidAxisDiameter")) {
        if (isInt)
          value = value / 1000;
        break;
      }

      
      if (key.equalsIgnoreCase("spinX")) {
        setSpin("x", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("spinY")) {
        setSpin("y", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("spinZ")) {
        setSpin("z", (int) value);
        break;
      }
      if (key.equalsIgnoreCase("spinFPS")) {
        setSpin("fps", (int) value);
        break;
      }

      

      if (key.equalsIgnoreCase("defaultDrawArrowScale")) {
        setDefaultDrawArrowScale(value);
        break;
      }

      
      if (key.equalsIgnoreCase("defaultTranslucent")) {
        global.defaultTranslucent = value;
        break;
      }

      if (key.equalsIgnoreCase("axesScale")) {
        setAxesScale(value);
        break;
      }
      if (key.equalsIgnoreCase("visualRange")) {
        setVisualRange(value);
        break;
      }
      if (key.equalsIgnoreCase("navigationDepth")) {
        setNavigationDepthPercent(0, value);
        break;
      }
      if (key.equalsIgnoreCase("navigationSpeed")) {
        global.navigationSpeed = value;
        break;
      }
      if (key.equalsIgnoreCase("navigationSlab")) {
        transformManager.setNavigationSlabOffsetPercent(value);
        break;
      }
      if (key.equalsIgnoreCase("cameraDepth")) {
        transformManager.setCameraDepthPercent(value);
        refresh(1, "set cameraDepth");
        break;
      }
      if (key.equalsIgnoreCase("rotationRadius")) {
        setRotationRadius(value, true);
        return true;
      }
      if (key.equalsIgnoreCase("hoverDelay")) {
        global.hoverDelayMs = (int) (value * 1000);
        break;
      }
      
      if (key.equalsIgnoreCase("sheetSmoothing")) {
        global.sheetSmoothing = value;
        break;
      }
      if (key.equalsIgnoreCase("dipoleScale")) {
        global.dipoleScale = value;
        break;
      }
      if (key.equalsIgnoreCase("stereoDegrees")) {
        transformManager.setStereoDegrees(value);
        break;
      }
      if (key.equalsIgnoreCase("vectorScale")) {
        
        setVectorScale(value);
        return true;
      }
      if (key.equalsIgnoreCase("vibrationPeriod")) {
        
        setVibrationPeriod(value);
        return true;
      }
      if (key.equalsIgnoreCase("vibrationScale")) {
        
        setVibrationScale(value);
        return true;
      }
      if (key.equalsIgnoreCase("bondTolerance")) {
        setBondTolerance(value);
        return true;
      }
      if (key.equalsIgnoreCase("minBondDistance")) {
        setMinBondDistance(value);
        return true;
      }
      if (key.equalsIgnoreCase("scaleAngstromsPerInch")) {
        transformManager.setScaleAngstromsPerInch(value);
        break;
      }
      if (key.equalsIgnoreCase("solventProbeRadius")) {
        global.solventProbeRadius = value;
        break;
      }
      if (key.equalsIgnoreCase("radius")) { 
        setFloatProperty("solventProbeRadius", value);
        return true;
      }
      
      if (isInt)
        return false;
      notFound = true;
      break;
    }
    key = key.toLowerCase();
    boolean isJmol = global.htParameterValues.containsKey(key);
    if (!isJmol && notFound) {
      if (global.htPropertyFlags.containsKey(key)) {
        setPropertyError(GT._(
            "ERROR: cannot set boolean flag to numeric value: {0}", key));
        return true;
      }
    }
    if (isJmol)
      global.setParameterValue(key, value);
    else
      global.setUserVariable(key, new ScriptVariable(Token.decimal, new Float(value)));
    return true;
  }

  public void setIntProperty(String key, int value) {
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }

    
    setIntProperty(key, value, true);
  }

  private void setIntProperty(String key, int value, boolean defineNew) {
    boolean notFound = false;
    while (true) {

      
      if (key.equalsIgnoreCase("helixStep")) {
        global.helixStep = value;
        modelSet.setHaveStraightness(false);
        break;
      }

      
      if (key.equalsIgnoreCase("propertyDataColumnCount")) {
        break;
      }

      if (key.equalsIgnoreCase("propertyAtomNumberColumnCount")) {
        break;
      }

      
      if (key.equalsIgnoreCase("dotDensity")) {
        global.dotDensity = value;
        break;
      }

      
      

      
      if (key.equalsIgnoreCase("delayMaximumMs")) {
        global.delayMaximumMs = value;
        break;
      }

      
      
      if (key.equalsIgnoreCase("logLevel")) {
        Logger.setLogLevel(value);
        Logger.info("logging level set to " + value);
        global.setParameterValue("logLevel", value);
        eval.setDebugging();
        return;
      }

      if (key.equalsIgnoreCase("axesMode")) {
        switch (value) {
        case JmolConstants.AXES_MODE_MOLECULAR:
          setAxesModeMolecular(true);
          return;
        case JmolConstants.AXES_MODE_BOUNDBOX:
          setAxesModeMolecular(false);
          return;
        case JmolConstants.AXES_MODE_UNITCELL:
          setAxesModeUnitCell(true);
          return;
        }
        return;
      }
      

      if (key.equalsIgnoreCase("propertyDataField")) {
        break;
      }

      

      if (key.equalsIgnoreCase("strandCount")) {
        setStrandCount(0, value);
        return;
      }
      if (key.equalsIgnoreCase("strandCountForStrands")) {
        setStrandCount(JmolConstants.SHAPE_STRANDS, value);
        return;
      }
      if (key.equalsIgnoreCase("strandCountForMeshRibbon")) {
        setStrandCount(JmolConstants.SHAPE_MESHRIBBON, value);
        return;
      }
      if (key.equalsIgnoreCase("perspectiveModel")) {
        setPerspectiveModel(value);
        break;
      }
      if (key.equalsIgnoreCase("showScript")) {
        global.scriptDelay = value;
        break;
      }
      if (key.equalsIgnoreCase("specularPower")) {
        setSpecularPower(value);
        break;
      }
      if (key.equalsIgnoreCase("specularExponent")) {
        setSpecularExponent(value);
        break;
      }
      if (key.equalsIgnoreCase("specular")) {
        setIntProperty("specularPercent", value);
        return;
      }
      if (key.equalsIgnoreCase("diffuse")) {
        setIntProperty("diffusePercent", value);
        return;
      }
      if (key.equalsIgnoreCase("ambient")) {
        setIntProperty("ambientPercent", value);
        return;
      }
      if (key.equalsIgnoreCase("specularPercent")) {
        setSpecularPercent(value);
        break;
      }
      if (key.equalsIgnoreCase("diffusePercent")) {
        setDiffusePercent(value);
        break;
      }
      if (key.equalsIgnoreCase("ambientPercent")) {
        setAmbientPercent(value);
        break;
      }

      if (key.equalsIgnoreCase("ribbonAspectRatio")) {
        global.ribbonAspectRatio = value;
        break;
      }
      if (key.equalsIgnoreCase("pickingSpinRate")) {
        global.pickingSpinRate = (value < 1 ? 1 : value);
        break;
      }
      if (key.equalsIgnoreCase("animationFps")) {
        setAnimationFps(value);
        break;
      }
      if (key.equalsIgnoreCase("percentVdwAtom")) {
        setPercentVdwAtom(value);
        break;
      }
      if (key.equalsIgnoreCase("bondRadiusMilliAngstroms")) {
        setMarBond((short) value);
        
        return;
      }
      if (key.equalsIgnoreCase("hermiteLevel")) {
        global.hermiteLevel = value;
        break;
      }
      
      if ((value != 0 && value != 1)
          || !setBooleanProperty(key, value == 1, false)) {
        if (setFloatProperty(key, value, true))
          return;
      }
      notFound = true;
      break;
    }
    key = key.toLowerCase();
    boolean isJmol = global.htParameterValues.containsKey(key);
    if (!isJmol && notFound) {
      if (global.htPropertyFlags.containsKey(key)) {
        setPropertyError(GT._(
            "ERROR: cannot set boolean flag to numeric value: {0}", key));
        return;
      }
    }
    if (!defineNew)
      return;
    if (isJmol) {
      global.setParameterValue(key, value);
    } else {
      global.setUserVariable(key, ScriptVariable.intVariable(value));
    }
  }

  int getDelayMaximum() {
    return global.delayMaximumMs;
  }

  public void setBooleanProperty(String key, boolean value) {
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setBooleanProperty(key, value, true);
  }

  boolean setBooleanProperty(String key, boolean value, boolean defineNew) {
    boolean notFound = false;
    boolean doRepaint = true;
    while (true) {

      
      
      if (key.equalsIgnoreCase("imageState")) {
        global.imageState = value;
        break;
      }
      
      
      
      if (key.equalsIgnoreCase("useMinimizationThread")) {
        global.useMinimizationThread = value;
        break;
      }
      
      

      if (key.equalsIgnoreCase("autoLoadOrientation")) {
        global.autoLoadOrientation = value;
        break;
      }

      

      if (key.equalsIgnoreCase("allowKeyStrokes")) {
        if (global.disablePopupMenu)
          value = false;
        global.allowKeyStrokes = value;
        break;
      }

      if (key.equalsIgnoreCase("showKeyStrokes")) {
        global.showKeyStrokes = value;
        break;
      }
      
      if (key.equalsIgnoreCase("fontCaching")) {
        global.fontCaching = value;
        break;
      }

      
      if (key.equalsIgnoreCase("atomPicking")) {
        global.atomPicking = value;
        break;
      }

      
      if (key.equalsIgnoreCase("bondPicking")) {
        global.bondPicking = value;
        break;
      }

      
      if (key.equalsIgnoreCase("selectAllModels")) {
        global.selectAllModels = value;
        break;
      }
      
      if (key.equalsIgnoreCase("messageStyleChime")) {
        global.messageStyleChime = value;
        break;
      }
      if (key.equalsIgnoreCase("pdbSequential")) {
        global.pdbSequential = value;
        break;
      }
      if (key.equalsIgnoreCase("pdbGetHeader")) {
        global.pdbGetHeader = value;
        break;
      }

      
      
      
      
      
      
      if (key.equalsIgnoreCase("fontScaling")) {
        global.fontScaling = value;
        break;
      }
      
      if (key.equalsIgnoreCase("syncMouse")) {
        setSyncTarget(0, value);
        break;
      }
      if (key.equalsIgnoreCase("syncScript")) {
        setSyncTarget(1, value);
        break;
      }

      

      if (key.equalsIgnoreCase("wireframeRotation")) {
        global.wireframeRotation = value;
        break;
      }

      

      if (key.equalsIgnoreCase("isosurfacePropertySmoothing")) {
        global.isosurfacePropertySmoothing = value;
        break;
      }

      

      if (key.equalsIgnoreCase("drawPicking")) {
        global.drawPicking = value;
        break;
      }

      

      if (key.equalsIgnoreCase("antialiasDisplay")) {
        setAntialias(0, value);
        break;
      }

      if (key.equalsIgnoreCase("antialiasTranslucent")) {
        setAntialias(1, value);
        break;
      }

      if (key.equalsIgnoreCase("antialiasImages")) {
        setAntialias(2, value);
        break;
      }

      

      if (key.equalsIgnoreCase("smartAromatic")) {
        global.smartAromatic = value;
        break;
      }

      

      if (key.equalsIgnoreCase("applySymmetryToBonds")) {
        setApplySymmetryToBonds(value);
        break;
      }

      

      if (key.equalsIgnoreCase("appendNew")) {
        setAppendNew(value);
        break;
      }

      if (key.equalsIgnoreCase("autoFPS")) {
        global.autoFps = value;
        break;
      }

      

      if (key.equalsIgnoreCase("useNumberLocalization")) {
        TextFormat
            .setUseNumberLocalization(global.useNumberLocalization = value);
        break;
      }

      

      if (key.equalsIgnoreCase("showFrank")) {
        setFrankOn(value);
        break;
      }

      

      if (key.equalsIgnoreCase("solventProbe")) {
        global.solventOn = value;
        break;
      }

      if (key.equalsIgnoreCase("dynamicMeasurements")) {
        setDynamicMeasurements(value);
        break;
      }

      

      if (key.equalsIgnoreCase("allowRotateSelected")) {
        setAllowRotateSelected(value);
        break;
      }

      

      if (key.equalsIgnoreCase("showScript")) {
        setIntProperty("showScript", value ? 1 : 0);
        return true;
      }
      
      if (key.equalsIgnoreCase("allowEmbeddedScripts")) {
        global.allowEmbeddedScripts = value;
        break;
      }
      if (key.equalsIgnoreCase("navigationPeriodic")) {
        global.navigationPeriodic = value;
        break;
      }
      if (key.equalsIgnoreCase("zShade")) {
        global.zShade = value;
        break;
      }
      if (key.equalsIgnoreCase("drawHover")) {
        pickingManager.setDrawHover(value);
        break;
      }
      if (key.equalsIgnoreCase("navigationMode")) {
        setNavigationMode(value);
        break;
      }
      if (key.equalsIgnoreCase("navigateSurface")) {
        global.navigateSurface = value;
        break;
      }
      if (key.equalsIgnoreCase("hideNavigationPoint")) {
        global.hideNavigationPoint = value;
        break;
      }
      if (key.equalsIgnoreCase("showNavigationPointAlways")) {
        global.showNavigationPointAlways = value;
        break;
      }

      
      if (key.equalsIgnoreCase("refreshing")) {
        setRefreshing(value);
        break;
      }
      if (key.equalsIgnoreCase("justifyMeasurements")) {
        global.justifyMeasurements = value;
        break;
      }
      if (key.equalsIgnoreCase("ssBondsBackbone")) {
        global.ssbondsBackbone = value;
        break;
      }
      if (key.equalsIgnoreCase("hbondsBackbone")) {
        global.hbondsBackbone = value;
        break;
      }
      if (key.equalsIgnoreCase("hbondsSolid")) {
        global.hbondsSolid = value;
        break;
      }
      if (key.equalsIgnoreCase("specular")) {
        setSpecular(value);
        break;
      }
      if (key.equalsIgnoreCase("slabEnabled")) {
        
        transformManager.setSlabEnabled(value); 
        break;
      }
      if (key.equalsIgnoreCase("zoomEnabled")) {
        transformManager.setZoomEnabled(value);
        break;
      }
      if (key.equalsIgnoreCase("highResolution")) {
        global.highResolutionFlag = value;
        break;
      }
      if (key.equalsIgnoreCase("traceAlpha")) {
        global.traceAlpha = value;
        break;
      }
      if (key.equalsIgnoreCase("zoomLarge")) {
        global.zoomLarge = value;
        transformManager.scaleFitToScreen(false, value, false, true);
        break;
      }
      if (key.equalsIgnoreCase("languageTranslation")) {
        GT.setDoTranslate(value);
        break;
      }
      if (key.equalsIgnoreCase("hideNotSelected")) {
        selectionManager.setHideNotSelected(value);
        break;
      }
      if (key.equalsIgnoreCase("colorRasmol")) {
        setDefaultColors(value ? "rasmol" : "jmol");
        break;
      }
      if (key.equalsIgnoreCase("scriptQueue")) {
        scriptManager.setQueue(value);
        break;
      }
      if (key.equalsIgnoreCase("dotSurface")) {
        global.dotSurface = value;
        break;
      }
      if (key.equalsIgnoreCase("dotsSelectedOnly")) {
        global.dotsSelectedOnly = value;
        break;
      }
      if (key.equalsIgnoreCase("selectionHalos")) {
        setSelectionHalos(value); 
        break;
      }
      if (key.equalsIgnoreCase("selectHydrogen")) {
        global.rasmolHydrogenSetting = value;
        break;
      }
      if (key.equalsIgnoreCase("selectHetero")) {
        global.rasmolHeteroSetting = value;
        break;
      }
      if (key.equalsIgnoreCase("showMultipleBonds")) {
        global.showMultipleBonds = value;
        break;
      }
      if (key.equalsIgnoreCase("showHiddenSelectionHalos")) {
        global.showHiddenSelectionHalos = value;
        break;
      }
      if (key.equalsIgnoreCase("windowCentered")) {
        transformManager.setWindowCentered(value);
        break;
      }
      if (key.equalsIgnoreCase("displayCellParameters")) {
        global.displayCellParameters = value;
        break;
      }
      if (key.equalsIgnoreCase("testFlag1")) {
        global.testFlag1 = value;
        break;
      }
      if (key.equalsIgnoreCase("testFlag2")) {
        global.testFlag2 = value;
        break;
      }
      if (key.equalsIgnoreCase("testFlag3")) {
        global.testFlag3 = value;
        break;
      }
      if (key.equalsIgnoreCase("testFlag4")) {
        global.testFlag4 = value;
        break;
      }
      if (key.equalsIgnoreCase("ribbonBorder")) {
        global.ribbonBorder = value;
        break;
      }
      if (key.equalsIgnoreCase("cartoonRockets")) {
        global.cartoonRockets = value;
        break;
      }
      if (key.equalsIgnoreCase("rocketBarrels")) {
        global.rocketBarrels = value;
        break;
      }
      if (key.equalsIgnoreCase("greyscaleRendering")) {
        g3d.setGreyscaleMode(global.greyscaleRendering = value);
        break;
      }
      if (key.equalsIgnoreCase("measurementLabels")) {
        global.measurementLabels = value;
        break;
      }

      

      if (key.equalsIgnoreCase("axesWindow")) {
        setAxesModeMolecular(!value);
        return true;
      }
      if (key.equalsIgnoreCase("axesMolecular")) {
        setAxesModeMolecular(value);
        return true;
      }
      if (key.equalsIgnoreCase("axesUnitCell")) {
        setAxesModeUnitCell(value);
        return true;
      }
      
      if (key.equalsIgnoreCase("axesOrientationRasmol")) {
        setAxesOrientationRasmol(value);
        return true;
      }
      if (key.equalsIgnoreCase("debugScript")) {
        setDebugScript(value);
        return true;
      }
      if (key.equalsIgnoreCase("perspectiveDepth")) {
        setPerspectiveDepth(value);
        return true;
      }
      if (key.equalsIgnoreCase("showAxes")) {
        setShowAxes(value);
        return true;
      }
      if (key.equalsIgnoreCase("showBoundBox")) {
        setShowBbcage(value);
        return true;
      }
      if (key.equalsIgnoreCase("showHydrogens")) {
        setShowHydrogens(value);
        return true;
      }
      if (key.equalsIgnoreCase("showMeasurements")) {
        setShowMeasurements(value);
        return true;
      }
      if (key.equalsIgnoreCase("showUnitcell")) {
        setShowUnitCell(value);
        return true;
      }
      
      
      if (key.equalsIgnoreCase("frank"))
        return setBooleanProperty("showFrank", value, true);
      if (key.equalsIgnoreCase("solvent"))
        return setBooleanProperty("solventProbe", value, true);
      if (key.equalsIgnoreCase("bonds"))
        return setBooleanProperty("showMultipleBonds", value, true);
      if (key.equalsIgnoreCase("hydrogen")) 
        return setBooleanProperty("selectHydrogen", value, true);
      if (key.equalsIgnoreCase("hetero")) 
        return setBooleanProperty("selectHetero", value, true);
      if (key.equalsIgnoreCase("showSelections")) 
                                                  
        return setBooleanProperty("selectionHalos", value, true);
      
      while (true) {
        doRepaint = false;
        if (key.equalsIgnoreCase("bondModeOr")) {
          global.bondModeOr = value;
          break;
        }
        if (key.equalsIgnoreCase("zeroBasedXyzRasmol")) {
          global.zeroBasedXyzRasmol = value;
          reset();
          break;
        }
        if (key.equalsIgnoreCase("rangeSelected")) {
          global.rangeSelected = value;
          break;
        }
        if (key.equalsIgnoreCase("measureAllModels")) {
          global.measureAllModels = value;
          break;
        }
        if (key.equalsIgnoreCase("statusReporting")) {
          
          statusManager.setAllowStatusReporting(value);
          break;
        }
        if (key.equalsIgnoreCase("chainCaseSensitive")) {
          global.chainCaseSensitive = value;
          break;
        }
        if (key.equalsIgnoreCase("hideNameInPopup")) {
          global.hideNameInPopup = value;
          break;
        }
        if (key.equalsIgnoreCase("disablePopupMenu")) {
          global.disablePopupMenu = value;
          break;
        }
        if (key.equalsIgnoreCase("forceAutoBond")) {
          global.forceAutoBond = value;
          break;
        }
        
        if (key.equalsIgnoreCase("autobond")) {
          setAutoBond(value);
          return true;
        }
        notFound = true;
        break;
      }
      if (!defineNew)
        return !notFound;
      notFound = true;
      break;
    }
    if (!defineNew)
      return !notFound;
    key = key.toLowerCase();
    boolean isJmol = global.htPropertyFlags.containsKey(key);
    if (!isJmol && notFound) {
      if (global.htParameterValues.containsKey(key)) {
        setPropertyError(GT._(
            "ERROR: Cannot set value of this variable to a boolean: {0}", key));
        return true;
      }
    }
    if (isJmol)
      global.setParameterValue(key, value);
    else
      global.setUserVariable(key, ScriptVariable.getBoolean(value));
    if (notFound)
      return false;
    if (doRepaint) {
      setTainted(true);
    }
    return true;
  }

  public boolean getPdbLoadInfo(int type) {
    switch (type) {
    case 1:
      return global.pdbSequential;
    case 2:
      return global.pdbGetHeader;
    }
    return false;
  }

  boolean getSelectAllModels() {
    return global.selectAllModels;
  }

  boolean getMessageStyleChime() {
    return global.messageStyleChime;
  }

  public boolean getFontCaching() {
    return global.fontCaching;
  }

  public boolean getFontScaling() {
    return global.fontScaling;
  }

  void showParameter(String key, boolean ifNotSet, int nMax) {
    String sv = "" + global.getParameterEscaped(key, nMax);
    if (ifNotSet || sv.indexOf("<not defined>") < 0)
      showString(key + " = " + sv, false);
  }

  public void showString(String str, boolean isPrint) {
    if (isScriptQueued && (!isSilent || isPrint))
      Logger.warn(str);
    scriptEcho(str);
  }

  String getAllSettings(String prefix) {
    return global.getAllSettings(prefix);
  }

  

  public boolean getDotSurfaceFlag() {
    return global.dotSurface;
  }

  public boolean getDotsSelectedOnlyFlag() {
    return global.dotsSelectedOnly;
  }

  public int getDotDensity() {
    return global.dotDensity;
  }

  public boolean isRangeSelected() {
    return global.rangeSelected;
  }

  boolean getIsosurfacePropertySmoothing() {
    
    return global.isosurfacePropertySmoothing;
  }

  public boolean getWireframeRotation() {
    return global.wireframeRotation;
  }

  boolean isWindowCentered() {
    return transformManager.isWindowCentered();
  }

  void setNavigationDepthPercent(float timeSec, float percent) {
    transformManager.setNavigationDepthPercent(timeSec, percent);
    refresh(1, "set navigationDepth");
  }

  float getNavigationSpeed() {
    return global.navigationSpeed;
  }

  public boolean getShowNavigationPoint() {
    if (!global.navigationMode || !transformManager.canNavigate())
      return false;
    return (isNavigating() && !global.hideNavigationPoint
        || global.showNavigationPointAlways || getInMotion());
  }

  public void setVisualRange(float angstroms) {
    transformManager.setVisualRange(angstroms);
    refresh(1, "set visualRange");
  }

  float getSolventProbeRadius() {
    return global.solventProbeRadius;
  }

  public float getCurrentSolventProbeRadius() {
    return global.solventOn ? global.solventProbeRadius : 0;
  }

  boolean getSolventOn() {
    return global.solventOn;
  }

  public boolean getTestFlag1() {
    return global.testFlag1;
  }

  public boolean getTestFlag2() {
    return global.testFlag2;
  }

  public boolean getTestFlag3() {
    return global.testFlag3;
  }

  public boolean getTestFlag4() {
    return global.testFlag4;
  }

  public void setPerspectiveDepth(boolean perspectiveDepth) {
    
    
    
    global.setParameterValue("perspectiveDepth", perspectiveDepth);
    transformManager.setPerspectiveDepth(perspectiveDepth);
  }

  public void setAxesOrientationRasmol(boolean TF) {
    
    
    
    
    global.setParameterValue("axesOrientationRasmol", TF);
    global.axesOrientationRasmol = TF;
    reset();
  }

  public boolean getAxesOrientationRasmol() {
    return global.axesOrientationRasmol;
  }

  void setAxesScale(float scale) {
    global.axesScale = scale;
    axesAreTainted = true;
  }

  public Point3f[] getAxisPoints() {
    
    return (getObjectMad(StateManager.OBJ_AXIS1) == 0
        || getAxesMode() != JmolConstants.AXES_MODE_UNITCELL
        || ((Boolean) getShapeProperty(JmolConstants.SHAPE_AXES, "axesTypeXY"))
            .booleanValue() ? null : (Point3f[]) getShapeProperty(
        JmolConstants.SHAPE_AXES, "axisPoints"));
  }

  public float getAxesScale() {
    return global.axesScale;
  }

  private void setAxesModeMolecular(boolean TF) {
    global.axesMode = (TF ? JmolConstants.AXES_MODE_MOLECULAR
        : JmolConstants.AXES_MODE_BOUNDBOX);
    axesAreTainted = true;
    global.removeJmolParameter("axesunitcell");
    global.removeJmolParameter(TF ? "axeswindow" : "axesmolecular");
    global.setParameterValue("axesMode", global.axesMode);
    global.setParameterValue(TF ? "axesMolecular" : "axesWindow", true);

  }

  void setAxesModeUnitCell(boolean TF) {
    
    
    global.axesMode = (TF ? JmolConstants.AXES_MODE_UNITCELL
        : JmolConstants.AXES_MODE_BOUNDBOX);
    axesAreTainted = true;
    global.removeJmolParameter("axesmolecular");
    global.removeJmolParameter(TF ? "axeswindow" : "axesunitcell");
    global.setParameterValue(TF ? "axesUnitcell" : "axesWindow", true);
    global.setParameterValue("axesMode", global.axesMode);
  }

  public int getAxesMode() {
    return global.axesMode;
  }

  public boolean getDisplayCellParameters() {
    return global.displayCellParameters;
  }

  public boolean getPerspectiveDepth() {
    return transformManager.getPerspectiveDepth();
  }

  public void setSelectionHalos(boolean TF) {
    
    if (modelSet == null || TF == getSelectionHaloEnabled())
      return;
    global.setParameterValue("selectionHalos", TF);
    loadShape(JmolConstants.SHAPE_HALOS);
    
    modelSet.setSelectionHaloEnabled(TF);
  }

  public boolean getSelectionHaloEnabled() {
    return modelSet.getSelectionHaloEnabled();
  }

  public boolean getBondSelectionModeOr() {
    return global.bondModeOr;
  }

  public boolean getChainCaseSensitive() {
    return global.chainCaseSensitive;
  }

  public boolean getRibbonBorder() {
    
    return global.ribbonBorder;
  }

  public boolean getCartoonRocketFlag() {
    return global.cartoonRockets;
  }

  public boolean getRocketBarrelFlag() {
    return global.rocketBarrels;
  }

  private void setStrandCount(int type, int value) {
    switch (type) {
    case JmolConstants.SHAPE_STRANDS:
      global.strandCountForStrands = value;
      break;
    case JmolConstants.SHAPE_MESHRIBBON:
      global.strandCountForMeshRibbon = value;
      break;
    default:
      global.strandCountForStrands = value;
      global.strandCountForMeshRibbon = value;
      break;
    }
    global.setParameterValue("strandCount", value);
    global.setParameterValue("strandCountForStrands",
        global.strandCountForStrands);
    global.setParameterValue("strandCountForMeshRibbon",
        global.strandCountForMeshRibbon);
  }

  public int getStrandCount(int type) {
    return (type == JmolConstants.SHAPE_STRANDS ? global.strandCountForStrands
        : global.strandCountForMeshRibbon);
  }

  boolean getHideNameInPopup() {
    return global.hideNameInPopup;
  }

  boolean getNavigationPeriodic() {
    return global.navigationPeriodic;
  }

  private void stopAnimationThreads() {
    setVibrationOff();
    setSpinOn(false);
    setNavOn(false);
    setAnimationOn(false);
  }

  private void setNavigationMode(boolean TF) {
    global.navigationMode = TF;
    if (TF && !transformManager.canNavigate()) {
      stopAnimationThreads();
      transformManager = transformManager.getNavigationManager(this,
          dimScreen.width, dimScreen.height);
      transformManager.homePosition();
    }
    transformManager.setNavigationMode(TF);
  }

  public boolean getNavigationMode() {
    return global.navigationMode;
  }

  public boolean getNavigateSurface() {
    return global.navigateSurface;
  }

  
  public void setTransformManager(TransformManager transformManager) {
    stopAnimationThreads();
    this.transformManager = transformManager;
    transformManager.setViewer(this, dimScreen.width, dimScreen.height);
    setTransformManagerDefaults();
    transformManager.homePosition();      
  }
  
  private void setPerspectiveModel(int mode) {
    if (transformManager.perspectiveModel == mode)
      return;
    stopAnimationThreads();
    switch (mode) {
    case 10:
      transformManager = new TransformManager10(this, dimScreen.width,
          dimScreen.height);
      break;
    default:
      transformManager = transformManager.getNavigationManager(this,
          dimScreen.width, dimScreen.height);
    }
    setTransformManagerDefaults();
    transformManager.homePosition();
  }

  private void setTransformManagerDefaults() {
    transformManager.setCameraDepthPercent(global.cameraDepth);
    transformManager.setPerspectiveDepth(global.perspectiveDepth);
    transformManager.setStereoDegrees(TransformManager.DEFAULT_STEREO_DEGREES);
    transformManager.setVisualRange(global.visualRange);
    transformManager.setSpinOn(false);
    transformManager.setVibrationPeriod(0);
    transformManager.setFrameOffsets(frameOffsets);
  }

  public float getCameraDepth() {
    return global.cameraDepth;
  }
  
  boolean getZoomLarge() {
    return global.zoomLarge;
  }

  public boolean getTraceAlpha() {
    
    return global.traceAlpha;
  }

  public int getHermiteLevel() {
    
    return global.hermiteLevel;
  }

  public boolean getHighResolution() {
    
    return global.highResolutionFlag;
  }

  String getLoadState() {
    return global.getLoadState();
  }

  public void setAutoBond(boolean TF) {
    
    global.setParameterValue("autobond", TF);
    global.autoBond = TF;
  }

  public boolean getAutoBond() {
    return global.autoBond;
  }

  int[] makeConnections(float minDistance, float maxDistance, short order,
                        int connectOperation, BitSet bsA, BitSet bsB,
                        BitSet bsBonds, boolean isBonds) {
    
    clearModelDependentObjects();
    clearAllMeasurements(); 
    return modelSet.makeConnections(minDistance, maxDistance, order,
        connectOperation, bsA, bsB, bsBonds, isBonds);
  }

  public void rebond() {
    
    clearModelDependentObjects();
    modelSet.deleteAllBonds();
    modelSet.autoBond(null, null, null, null);
    addStateScript("connect;", false, true);
  }

  void setPdbConectBonding(boolean isAuto) {
    
    clearModelDependentObjects();
    modelSet.deleteAllBonds();
    BitSet bsExclude = new BitSet();
    modelSet.setPdbConectBonding(0, 0, bsExclude);
    if (isAuto) {
      modelSet.autoBond(null, null, bsExclude, null);
      addStateScript("connect PDB AUTO;", false, true);
      return;
    }
    addStateScript("connect PDB;", false, true);
  }

  
  
  

  boolean getGreyscaleRendering() {
    return global.greyscaleRendering;
  }

  boolean getDisablePopupMenu() {
    return global.disablePopupMenu;
  }

  public boolean getForceAutoBond() {
    return global.forceAutoBond;
  }

  
  
  

  public void setPercentVdwAtom(int percentVdwAtom) {
    global.setParameterValue("percentVdwAtom", percentVdwAtom);
    global.percentVdwAtom = percentVdwAtom;
    setShapeSize(JmolConstants.SHAPE_BALLS, -percentVdwAtom, Float.NaN);
  }

  public int getPercentVdwAtom() {
    return global.percentVdwAtom;
  }

  public short getDefaultMadAtom() {
    return (short) (global.percentVdwAtom == 0 ? 0 : -2000
        - global.percentVdwAtom);
  }

  public short getMadBond() {
    return (short) (global.bondRadiusMilliAngstroms * 2);
  }

  public short getMarBond() {
    return global.bondRadiusMilliAngstroms;
  }

  

  public byte getModeMultipleBond() {
    
    return global.modeMultipleBond;
  }

  public boolean getShowMultipleBonds() {
    return global.showMultipleBonds;
  }

  public void setShowHydrogens(boolean TF) {
    
    
    global.setParameterValue("showHydrogens", TF);
    global.showHydrogens = TF;
  }

  public boolean getShowHydrogens() {
    return global.showHydrogens;
  }

  public boolean getShowHiddenSelectionHalos() {
    return global.showHiddenSelectionHalos;
  }

  public void setShowBbcage(boolean value) {
    setObjectMad(JmolConstants.SHAPE_BBCAGE, "boundbox", (short) (value ? -4
        : 0));
    global.setParameterValue("showBoundBox", value);
  }

  public boolean getShowBbcage() {
    return getObjectMad(StateManager.OBJ_BOUNDBOX) != 0;
  }

  public void setShowUnitCell(boolean value) {
    setObjectMad(JmolConstants.SHAPE_UCCAGE, "unitcell", (short) (value ? -2
        : 0));
    global.setParameterValue("showUnitCell", value);
  }

  public boolean getShowUnitCell() {
    return getObjectMad(StateManager.OBJ_UNITCELL) != 0;
  }

  public void setShowAxes(boolean value) {
    setObjectMad(JmolConstants.SHAPE_AXES, "axes", (short) (value ? -2 : 0));
    global.setParameterValue("showAxes", value);
  }

  public boolean getShowAxes() {
    return getObjectMad(StateManager.OBJ_AXIS1) != 0;
  }

  boolean frankOn = true;

  public void setFrankOn(boolean TF) {
    if (isPreviewOnly)
      TF = false;
    frankOn = TF;
    setObjectMad(JmolConstants.SHAPE_FRANK, "frank", (short) (TF ? 1 : 0));
  }

  public boolean getShowFrank() {
    if (isPreviewOnly || isApplet && creatingImage)
      return false;
    return (isSignedApplet || frankOn);
  }

  public boolean isSignedApplet() {
    return isSignedApplet;
  }

  public void setShowMeasurements(boolean TF) {
    
    global.setParameterValue("showMeasurements", TF);
    global.showMeasurements = TF;
  }

  public boolean getShowMeasurements() {
    return global.showMeasurements;
  }

  public boolean getShowMeasurementLabels() {
    return global.measurementLabels;
  }

  public boolean getMeasureAllModelsFlag() {
    return global.measureAllModels;
  }

  void setMeasureDistanceUnits(String units) {
    
    
    global.setMeasureDistanceUnits(units);
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "reformatDistances", null);
  }

  public String getMeasureDistanceUnits() {
    return global.getMeasureDistanceUnits();
  }

  public boolean getUseNumberLocalization() {
    return global.useNumberLocalization;
  }

  void setAppendNew(boolean value) {
    
    global.appendNew = value;
  }

  public boolean getAppendNew() {
    return global.appendNew;
  }

  boolean getAutoFps() {
    return global.autoFps;
  }

  public void setRasmolDefaults() {
    setDefaults("RasMol");
  }

  public void setJmolDefaults() {
    setDefaults("Jmol");
  }

  private void setDefaults(String type) {
    if (type.equalsIgnoreCase("RasMol")) {
      stateManager.setRasMolDefaults();
      return;
    }
    stateManager.setJmolDefaults();
    setShapeSize(JmolConstants.SHAPE_BALLS, getDefaultMadAtom(),
        Float.NaN, getModelAtomBitSet(-1, true));
  }

  public boolean getZeroBasedXyzRasmol() {
    return global.zeroBasedXyzRasmol;
  }

  private void setAntialias(int mode, boolean TF) {
    switch (mode) {
    case 0: 
      global.antialiasDisplay = TF;
      break;
    case 1: 
      global.antialiasTranslucent = TF;
      break;
    case 2: 
      global.antialiasImages = TF;
      return;
    }
    resizeImage(0, 0, false, false, true);
  }

  
  
  

  public Point3f[] allocTempPoints(int size) {
    
    return tempManager.allocTempPoints(size);
  }

  public void freeTempPoints(Point3f[] tempPoints) {
    tempManager.freeTempPoints(tempPoints);
  }

  public Point3i[] allocTempScreens(int size) {
    
    return tempManager.allocTempScreens(size);
  }

  public void freeTempScreens(Point3i[] tempScreens) {
    tempManager.freeTempScreens(tempScreens);
  }

  

  public byte[] allocTempBytes(int size) {
    
    return tempManager.allocTempBytes(size);
  }

  public void freeTempBytes(byte[] tempBytes) {
    tempManager.freeTempBytes(tempBytes);
  }

  
  
  
  Font3D getFont3D(String fontFace, String fontStyle, float fontSize) {
    return g3d.getFont3D(fontFace, fontStyle, fontSize);
  }

  public String formatText(String text0) {
    int i;
    if ((i = text0.indexOf("@{")) < 0 && (i = text0.indexOf("%{")) < 0)
      return text0;

    

    String text = TextFormat.simpleReplace(text0, "%{", "@{");
    String name;
    while ((i = text.indexOf("@{")) >= 0) {
      i++;
      int i0 = i + 1;
      int len = text.length();
      i = ScriptCompiler.ichMathTerminator(text, i, len);
      if (i >= len)
        return text;
      name = text.substring(i0, i);
      if (name.length() == 0)
        return text;
      Object v = evaluateExpression(name);
      if (v instanceof Point3f)
        v = Escape.escape((Point3f) v);
      text = text.substring(0, i0 - 2) + v.toString() + text.substring(i + 1);
    }
    return text;
  }

  
  
  

  String getElementSymbol(int i) {
    return modelSet.getElementSymbol(i);
  }

  int getElementNumber(int i) {
    return modelSet.getElementNumber(i);
  }

  public String getAtomName(int i) {
    return modelSet.getAtomName(i);
  }

  public int getAtomNumber(int i) {
    return modelSet.getAtomNumber(i);
  }

  float getAtomX(int i) {
    return modelSet.getAtomX(i);
  }

  float getAtomY(int i) {
    return modelSet.getAtomY(i);
  }

  float getAtomZ(int i) {
    return modelSet.getAtomZ(i);
  }

  public Point3f getAtomPoint3f(int i) {
    return modelSet.getAtomAt(i);
  }

  public float getAtomRadius(int i) {
    return modelSet.getAtomRadius(i);
  }

  public float getAtomVdwRadius(int i) {
    return modelSet.getAtomVdwRadius(i);
  }

  public int getAtomArgb(int i) {
    return g3d.getColixArgb(modelSet.getAtomColix(i));
  }

  String getAtomChain(int i) {
    return modelSet.getAtomChain(i);
  }

  public int getAtomModelIndex(int i) {
    return modelSet.getAtomModelIndex(i);
  }

  String getAtomSequenceCode(int i) {
    return modelSet.getAtomSequenceCode(i);
  }

  public float getBondRadius(int i) {
    return modelSet.getBondRadius(i);
  }

  public short getBondOrder(int i) {
    return modelSet.getBondOrder(i);
  }

  void assignAromaticBonds() {
    modelSet.assignAromaticBonds();
  }

  public boolean getSmartAromatic() {
    return global.smartAromatic;
  }

  void resetAromatic() {
    modelSet.resetAromatic();
  }

  public int getBondArgb1(int i) {
    return g3d.getColixArgb(modelSet.getBondColix1(i));
  }

  public int getBondModelIndex(int i) {
    
    return modelSet.getBondModelIndex(i);
  }

  public int getBondArgb2(int i) {
    return g3d.getColixArgb(modelSet.getBondColix2(i));
  }

  public Point3f[] getPolymerLeadMidPoints(int modelIndex, int polymerIndex) {
    return modelSet.getPolymerLeadMidPoints(modelIndex, polymerIndex);
  }

  
  
  

  void setStereoMode(int[] twoColors, int stereoMode, float degrees) {
    setFloatProperty("stereoDegrees", degrees);
    setBooleanProperty("greyscaleRendering",
        stereoMode > JmolConstants.STEREO_DOUBLE);
    if (twoColors != null)
      transformManager.setStereoMode(twoColors);
    else
      transformManager.setStereoMode(stereoMode);
  }

  
  
  

  public boolean isJvm12orGreater() {
    return jvm12orGreater;
  }

  public String getOperatingSystemName() {
    return strOSName;
  }

  public String getJavaVendor() {
    return strJavaVendor;
  }

  public String getJavaVersion() {
    return strJavaVersion;
  }

  public Graphics3D getGraphics3D() {
    return g3d;
  }

  public boolean showModelSetDownload() {
    return true; 
  }

  

  public Object getProperty(String returnType, String infoType, String paramInfo) {
    return getProperty(returnType, infoType, (Object) paramInfo);
  }

  private boolean scriptEditorVisible;
  
  boolean isScriptEditorVisible() {
    return scriptEditorVisible;
  }
  
  JmolAppConsoleInterface appConsole;
  JmolScriptEditorInterface scriptEditor;
  JmolPopup jmolpopup;
  String menuStructure;
  
  public Object getProperty(String returnType, String infoType, Object paramInfo) {
    
    
    
    
    

    if ("DATA_API".equals(returnType)) {
      switch (("scriptCheck........." 
              +"scriptContext......."
              +"scriptEditor........"
              +"scriptEditorState..."
              +"getAppConsole......."
              +"getScriptEditor....."
              +"setMenu............."
              +"wrappedState........"
              +"spaceGroupInfo......"
              +"disablePopupMenu...."
              ).indexOf(infoType)) {

      case 0:
        return scriptCheck((String) paramInfo, true);
      case 20:
        return eval.getScriptContext();
      case 40:
        showEditor((String[]) paramInfo);
        return null;
      case 60:
        scriptEditorVisible = ((Boolean)paramInfo).booleanValue();
        return null;
      case 80:
        if (paramInfo instanceof JmolAppConsoleInterface) {
          appConsole = (JmolAppConsoleInterface) paramInfo;
        } else if (appConsole == null && paramInfo != null && ((Boolean) paramInfo).booleanValue()) {
          appConsole = (isApplet ? 
              (JmolAppConsoleInterface) Interface.getOptionInterface("console.AppletConsole")
              : (JmolAppConsoleInterface) Interface.getApplicationInterface("jmolpanel.AppConsole"))
                  .getAppConsole(this, display);
        }
        scriptEditor = (appConsole == null ? null : appConsole.getScriptEditor());
        return appConsole;
      case 100:
        if (appConsole == null && paramInfo != null && ((Boolean) paramInfo).booleanValue()) {
          getProperty("DATA_API", "appConsole", Boolean.TRUE);
          scriptEditor = (appConsole == null ? null : appConsole.getScriptEditor());
        }
        return scriptEditor;
      case 120:
        return menuStructure = (String) paramInfo;
      case 140:
        if (!global.imageState)
          return "";
        return "\n/**" + JmolConstants.EMBEDDED_SCRIPT_TAG + " \n" + getStateInfo() + "\n**/";
      case 160:
        return getSpaceGroupInfo(null);
      case 180:
        global.disablePopupMenu = true; 
        return null;
      default:
        System.out.println("ERROR IN getProperty DATA_API: " + returnType);
        return null;
      }
        
    } 
    return PropertyManager.getProperty(this, returnType, infoType, paramInfo);
  }

  void showEditor(String[] file_text) {
    if (file_text == null)
      file_text = new String[] { null, null };
    if (file_text[1] == null)
      file_text[1] = "<no data>";
    String filename = file_text[1];
    String msg = file_text[0];
    JmolScriptEditorInterface scriptEditor = (JmolScriptEditorInterface) getProperty(
        "DATA_API", "getScriptEditor", Boolean.TRUE);
    if (msg != null) {
      scriptEditor.setFilename(filename);
      scriptEditor.output(msg);
    }
    scriptEditor.setVisible(true);
  }
  
  String getModelExtract(Object atomExpression) {
    return fileManager.getFullPathName() + "\nJmol version " + getJmolVersion()
        + "\nEXTRACT: " + atomExpression + "\n"
        + modelSet.getModelExtract(getAtomBitSet(atomExpression));
  }

  public String getHexColorFromIndex(short colix) {
    return g3d.getHexColorFromIndex(colix);
  }

  

  void setModelVisibility() {
    
    if (modelSet == null) 
      return;
    modelSet.setModelVisibility();
  }

  void setFrameTitle(int modelIndex, String title) {
    modelSet.setFrameTitle(modelIndex, title);
  }

  String getFrameTitle(int modelIndex) {
    return modelSet.getFrameTitle(modelIndex);
  }

  boolean isTainted = true;

  void setTainted(boolean TF) {
    isTainted = TF && refreshing;
    axesAreTainted = TF && refreshing;
  }

  Point3fi checkObjectClicked(int x, int y, int modifiers) {
    return modelSet.checkObjectClicked(x, y, modifiers,
        getVisibleFramesBitSet());
  }

  boolean checkObjectHovered(int x, int y) {
    if (modelSet == null)
      return false;
    return modelSet.checkObjectHovered(x, y, getVisibleFramesBitSet());
  }

  void checkObjectDragged(int prevX, int prevY, int x, int y, int modifiers) {
    int iShape = 0;
    switch (getPickingMode()) {
    case JmolConstants.PICKING_LABEL:
      iShape = JmolConstants.SHAPE_LABELS;
      break;
    case JmolConstants.PICKING_DRAW:
      iShape = JmolConstants.SHAPE_DRAW;
      break;
    }
    modelSet.checkObjectDragged(prevX, prevY, x, y, modifiers,
        getVisibleFramesBitSet(), iShape);
  }

  void rotateAxisAngleAtCenter(Point3f rotCenter, Vector3f rotAxis,
                               float degrees, float endDegrees, boolean isSpin,
                               BitSet bsSelected) {
    
    if (Float.isNaN(degrees) || degrees == 0)
      return;
    transformManager.rotateAxisAngleAtCenter(rotCenter, rotAxis, degrees,
        endDegrees, isSpin, bsSelected);
    refresh(-1, "rotateAxisAngleAtCenter");
  }

  void rotateAboutPointsInternal(Point3f point1, Point3f point2, float degrees,
                                 float endDegrees, boolean isSpin,
                                 BitSet bsSelected) {
    
    if (Float.isNaN(degrees) || degrees == 0)
      return;
    transformManager.rotateAboutPointsInternal(point1, point2, degrees,
        endDegrees, false, isSpin, bsSelected);
    refresh(-1, "rotateAxisAboutPointsInternal");
  }

  int getPickingSpinRate() {
    
    return global.pickingSpinRate;
  }

  public void startSpinningAxis(Point3f pt1, Point3f pt2, boolean isClockwise) {
    
    
    if (getSpinOn() || getNavOn()) {
      setSpinOn(false);
      setNavOn(false);
      return;
    }
    transformManager.rotateAboutPointsInternal(pt1, pt2,
        global.pickingSpinRate, Float.MAX_VALUE, isClockwise, true, null);
  }

  public Vector3f getModelDipole() {
    return modelSet.getModelDipole(getDisplayModelIndex());
  }

  public Vector3f calculateMolecularDipole() {
    return modelSet.calculateMolecularDipole(getDisplayModelIndex());
  }

  public float getDipoleScale() {
    return global.dipoleScale;
  }

  public void getAtomIdentityInfo(int atomIndex, Hashtable info) {
    modelSet.getAtomIdentityInfo(atomIndex, info);
  }

  void setDefaultLattice(Point3f ptLattice) {
    
    global.setDefaultLattice(ptLattice);
    global.setParameterValue("defaultLattice", Escape.escape(ptLattice));
  }

  Point3f getDefaultLattice() {
    return global.getDefaultLattice();
  }

  BitSet getTaintedAtoms(byte type) {
    return modelSet.getTaintedAtoms(type);
  }

  public void setTaintedAtoms(BitSet bs, byte type) {
    modelSet.setTaintedAtoms(bs, type);
  }

  public String getData(String atomExpression, String type) {
    String exp = "";
    if (type.toLowerCase().indexOf("property_") == 0)
      exp = "{selected}.label(\"%{" + type + "}\")";
    else if (type.equalsIgnoreCase("PDB"))
      
      exp = "{selected and not hetero}.label(\"ATOM  %5i %-4a%1A%3.3n %1c%4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          %2e  \").lines"
          + "+{selected and hetero}.label(\"HETATM%5i %-4a%1A%3.3n %1c%4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          %2e  \").lines";
    else if (type.equalsIgnoreCase("MOL"))
      exp = "\"line1\nline2\nline3\n\"+(\"\"+{selected}.size)%-3+(\"\"+{selected}.bonds.size)%-3+\"  0  0  0\n\""
          + "+{selected}.labels(\"%10.4x%10.4y%10.4z %-2e  0  0  0  0  0\").lines"
          + "+{selected}.bonds.labels(\"%3D1%3D2%3ORDER  0  0  0\").lines";
    else if (type.startsWith("USER:"))
      exp = "{selected}.label(\"" + type.substring(5) + "\").lines";
    else
      
      exp = "\"\" + {selected}.size + \"\n\n\"+{selected}.label(\"%-2e %10.5x %10.5y %10.5z\").lines";
    if (!atomExpression.equals("selected"))
      exp = TextFormat.simpleReplace(exp, "selected", atomExpression);
    return (String) evaluateExpression(exp);
  }

  public synchronized Object evaluateExpression(Object stringOrTokens) {
    return ScriptEvaluator.evaluateExpression(this, stringOrTokens);
  }

  Object getHelixData(BitSet bs, int tokType) {
    return modelSet.getHelixData(bs, tokType);
  }

  public String getPdbData(BitSet bs) {
    if (bs == null)
      bs = getSelectionSet();
    return modelSet.getPdbAtomData(bs);
  }

  String getPdbData(int modelIndex, String type) {
    return modelSet.getPdbData(modelIndex, type, selectionManager.bsSelection,
        false);
  }

  public boolean isJmolDataFrame(int modelIndex) {
    return modelSet.isJmolDataFrame(modelIndex);
  }

  public boolean isJmolDataFrame() {
    return modelSet.isJmolDataFrame(animationManager.currentModelIndex);
  }

  int getJmolDataFrameIndex(int modelIndex, String type) {
    return modelSet.getJmolDataFrameIndex(modelIndex, type);
  }

  void setJmolDataFrame(String type, int modelIndex, int dataIndex) {
    modelSet.setJmolDataFrame(type, modelIndex, dataIndex);
  }

  void setFrameTitle(String title) {
    loadShape(JmolConstants.SHAPE_ECHO);
    modelSet.setFrameTitle(animationManager.currentModelIndex, title);
  }

  public String getFrameTitle() {
    return modelSet.getFrameTitle(animationManager.currentModelIndex);
  }

  String getJmolFrameType(int modelIndex) {
    return modelSet.getJmolFrameType(modelIndex);
  }

  public int getJmolDataSourceFrame(int modelIndex) {
    return modelSet.getJmolDataSourceFrame(modelIndex);
  }

  void setAtomProperty(BitSet bs, int tok, int iValue, float fValue,
                       String sValue, float[] values, String[] list) {
    modelSet.setAtomProperty(bs, tok, iValue, fValue, sValue, values, list);
    switch (tok) {
    case Token.atomX:
    case Token.atomY:
    case Token.atomZ:
    case Token.fracX:
    case Token.fracY:
    case Token.fracZ:
    case Token.unitX:
    case Token.unitY:
    case Token.unitZ:
      refreshMeasures();
    }
  }

  public void setAtomCoord(int atomIndex, float x, float y, float z) {
    
    modelSet.setAtomCoord(atomIndex, x, y, z);
    
  }

  void setAtomCoord(BitSet bs, int tokType, Object xyzValues) {
    modelSet.setAtomCoord(bs, tokType, xyzValues);
    refreshMeasures();
  }

  public void setAtomCoordRelative(int atomIndex, float x, float y, float z) {
    modelSet.setAtomCoordRelative(atomIndex, x, y, z);
    
  }

  void setAtomCoordRelative(Point3f offset) {
    
    modelSet.setAtomCoordRelative(offset, selectionManager.bsSelection);
    refreshMeasures();
  }

  void setAllowRotateSelected(boolean TF) {
    global.allowRotateSelected = TF;
  }

  boolean allowRotateSelected() {
    return global.allowRotateSelected;
  }

  void invertSelected(Point3f pt, BitSet bs) {
    
    modelSet.invertSelected(pt, null, bs);
    refreshMeasures();
  }

  void invertSelected(Point3f pt, Point4f plane) {
    
    modelSet.invertSelected(pt, plane, selectionManager.bsSelection);
    refreshMeasures();
  }

  synchronized void moveSelected(int deltaX, int deltaY, int x, int y,
                                 boolean isTranslation) {
    if (isJmolDataFrame())
      return;
    BitSet bsSelected = selectionManager.bsSelection;
    if (deltaX == Integer.MIN_VALUE) {
      setSelectionHalos(true);
      refresh(3, "moveSelected");
      return;
    }
    if (deltaX == Integer.MAX_VALUE) {
      setSelectionHalos(false);
      refresh(3, "moveSelected");
      return;
    }
    if (isTranslation) {
      Point3f ptCenter = getAtomSetCenter(bsSelected);
      Point3i pti = transformPoint(ptCenter);
      Point3f pt = new Point3f(pti.x + deltaX, pti.y + deltaY, pti.z);
      unTransformPoint(pt, pt);
      pt.sub(ptCenter);
      modelSet.setAtomCoordRelative(pt, bsSelected);
    } else {
      transformManager.setRotateMolecule(true);
      transformManager.rotateXYBy(deltaX, deltaY, bsSelected);
      transformManager.setRotateMolecule(false);
    }
    refreshMeasures();
  }

  void rotateAtoms(Matrix3f mNew, Matrix3f matrixRotate, boolean fullMolecule,
                   Point3f center, boolean isInternal, BitSet bsAtoms) {
    modelSet.rotateAtoms(mNew, matrixRotate, bsAtoms, fullMolecule, center,
        isInternal);
    refreshMeasures();
  }

  public void refreshMeasures() {
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "refresh", null);
  }

  void setDynamicMeasurements(boolean TF) { 
    global.dynamicMeasurements = TF;
  }

  public boolean getDynamicMeasurements() {
    return global.dynamicMeasurements;
  }

  
  public float[][] functionXY(String functionName, int nX, int nY) {
    String data = null;
    if (functionName.indexOf("file:") == 0)
      data = getFileAsString(functionName.substring(5));
    else if (functionName.indexOf("data2d_") != 0)
      return statusManager.functionXY(functionName, nX, nY);
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    float[][] fdata;
    if (data == null) {
      fdata = getDataFloat2D(functionName);
      if (fdata != null)
        return fdata;
      data = "";
    }
    fdata = new float[nX][nY];
    float[] f = new float[nX * nY];
    Parser.parseFloatArray(data, null, f);
    for (int i = 0, n = 0; i < nX; i++)
      for (int j = 0; j < nY; j++)
        fdata[i][j] = f[n++];
    return fdata;
  }

  float[][][] functionXYZ(String functionName, int nX, int nY, int nZ) {
    String data = null;
    if (functionName.indexOf("file:") == 0)
      data = getFileAsString(functionName.substring(5));
    else if (functionName.indexOf("data3d_") != 0)
      return statusManager.functionXYZ(functionName, nX, nY, nZ);
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    nZ = Math.abs(nZ);
    float[][][] xyzdata;
    if (data == null) {
      xyzdata = getDataFloat3D(functionName);
      if (xyzdata != null)
        return xyzdata;
      data = "";
    }
    xyzdata = new float[nX][nY][nZ];
    float[] f = new float[nX * nY * nZ];
    Parser.parseFloatArray(data, null, f);
    for (int i = 0, n = 0; i < nX; i++)
      for (int j = 0; j < nY; j++)
        for (int k = 0; k < nZ; k++)
          xyzdata[i][j][k] = f[n++];
    return xyzdata;
  }

  void getHelp(String what) {
    if (what.length() > 0 && what.indexOf("?") != 0
        && global.helpPath.indexOf("?") < 0)
      what = "?search=" + what;
    showUrl(global.helpPath + what);
  }

  
  
  

  

  
  void addCommand(String command) {
    if (!autoExit)
      commandHistory.addCommand(TextFormat.replaceAllCharacters(command, "\r\n\t", " "));
  }

  
  String removeCommand() {
    return commandHistory.removeCommand();
  }

  
  public String getSetHistory(int howFarBack) {
    return commandHistory.getSetHistory(howFarBack);
  }

  
  
  

  public void writeTextFile(String fileName, String data) {
    createImage(fileName, "txt", data, Integer.MIN_VALUE, 0, 0);
  }

  
  public String clipImage(String text) {
    JmolImageCreatorInterface c;
    try {
      c = (JmolImageCreatorInterface) Interface
          .getOptionInterface("util.ImageCreator");
      c.setViewer(this);
      return c.clipImage(text);
    } catch (Error er) {
      
      return GT._("clipboard is not accessible -- use signed applet");
    }
  }

  
  String createImage(String fileName, String type, Object text_or_bytes,
                     int quality, int width, int height, BitSet bsFrames) {
    if (bsFrames == null)
      return createImage(fileName, type, text_or_bytes, quality, width, height);
    int modelCount = getModelCount();
    String info = "";
    int n = 0;
    int ptDot = fileName.indexOf(".");
    if (ptDot < 0)
      ptDot = fileName.length();

    String froot = fileName.substring(0, ptDot);
    String fext = fileName.substring(ptDot);
    for (int i = 0; i < modelCount; i++)
      if (bsFrames.get(i)) {
        setCurrentModelIndex(i);
        fileName = "0000" + (++n);
        fileName = froot + fileName.substring(fileName.length() - 4) + fext;
        String msg = createImage(fileName, type, text_or_bytes, quality, width,
            height);
        Logger.info(msg);
        info += msg + "\n";
        if (!msg.startsWith("OK"))
          return "ERROR WRITING FILE SET: \n" + info;
      }
    if (info.length() == 0)
      info = "OK\n";
    return info + "\n" + n + " files created";
  }

  private boolean creatingImage;

  
  public String createImage(String fileName, String type, Object text_or_bytes,
                            int quality, int width, int height) {

    

    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    if (quality != Integer.MIN_VALUE) {
      mustRender = true;
      resizeImage(width, height, true, false, false);
      setModelVisibility();
    }
    creatingImage = true;
    String err = null;
    try {
      if (fileName == null) {
        err = clipImage((String) text_or_bytes);
      } else {
        boolean useDialog = (fileName.indexOf("?") == 0);
        if (useDialog)
          fileName = fileName.substring(1);
        boolean forceDialog = (fileName.indexOf("?") == 0);
        if (forceDialog)
          fileName = fileName.substring(1);
        fileName = FileManager.setLocalPathForWritingFile(this, fileName);
        String[] aFileName = new String[] { (forceDialog || isApplet
            && useDialog ? "?" : "") + fileName };
        err = statusManager
            .createImage(aFileName, type, text_or_bytes, quality);
        if (!isApplet && err == null) {
          
          
          JmolImageCreatorInterface c = (JmolImageCreatorInterface) Interface
              .getOptionInterface("export.image.ImageCreator");
          c.setViewer(this);
          err = (String) c.createImage(aFileName[0], type, text_or_bytes,
              quality);
          statusManager.createImage(new String[] { err }, type, null, quality);
        }
      }
    } catch (Throwable er) {
      Logger.error(setErrorMessage(err = "ERROR creating image: " + er));
    }
    creatingImage = false;
    if (quality != Integer.MIN_VALUE) {
      resizeImage(saveWidth, saveHeight, true, false, true);
    }
    return ("CANCELED".equals(err) ? null : err);

  }

  private void setImageFontScaling(int width, int height) {
    float screenDimNew = (global.zoomLarge == (height > width) ? height : width);
    imageFontScaling = screenDimNew / getScreenDim();
  }

  private void setSyncTarget(int mode, boolean TF) {
    switch (mode) {
    case 0:
      statusManager.syncingMouse = TF;
      break;
    case 1:
      statusManager.syncingScripts = TF;
      break;
    case 2:
      statusManager.syncSend(TF ? SYNC_GRAPHICS_MESSAGE
          : SYNC_NO_GRAPHICS_MESSAGE, "*");
      if (Float.isNaN(transformManager.stereoDegrees))
        setFloatProperty("stereoDegrees",
            TransformManager.DEFAULT_STEREO_DEGREES);
      if (TF) {
        setBooleanProperty("syncMouse", false);
        setBooleanProperty("syncScript", false);
      }
      return;
    }
    
    if (!statusManager.syncingScripts && !statusManager.syncingMouse)
      refresh(-1, "set sync");
  }

  public final static String SYNC_GRAPHICS_MESSAGE = "GET_GRAPHICS";
  public final static String SYNC_NO_GRAPHICS_MESSAGE = "SET_GRAPHICS_OFF";

  public void syncScript(String script, String applet) {
    if (script.equalsIgnoreCase(SYNC_GRAPHICS_MESSAGE)) {
      statusManager.setSyncDriver(StatusManager.SYNC_STEREO);
      statusManager.syncSend(script, applet);
      setBooleanProperty("syncMouse", false);
      setBooleanProperty("syncScript", false);
      return;
    }
    
    
    
    
    boolean disableSend = "~".equals(applet);
    
    if (!disableSend && !".".equals(applet)) {
      statusManager.syncSend(script, applet);
      if (!"*".equals(applet))
        return;
    }
    if (script.equalsIgnoreCase("on")) {
      statusManager.setSyncDriver(StatusManager.SYNC_DRIVER);
      return;
    }
    if (script.equalsIgnoreCase("off")) {
      statusManager.setSyncDriver(StatusManager.SYNC_OFF);
      return;
    }
    if (script.equalsIgnoreCase("slave")) {
      statusManager.setSyncDriver(StatusManager.SYNC_SLAVE);
      return;
    }
    int syncMode = statusManager.getSyncMode();
    if (syncMode == StatusManager.SYNC_OFF)
      return;
    if (syncMode != StatusManager.SYNC_DRIVER)
      disableSend = false;
    if (Logger.debugging)
      Logger.debug(htmlName + " syncing with script: " + script);
    
    
    
    
    if (disableSend)
      statusManager.setSyncDriver(StatusManager.SYNC_DISABLE);
    if (script.indexOf("Mouse: ") != 0) {
      evalStringQuiet(script, true, false);
      return;
    }
    String[] tokens = Parser.getTokens(script);
    String key = tokens[1];
    switch (tokens.length) {
    case 3:
      if (key.equals("zoomByFactor"))
        zoomByFactor(Parser.parseFloat(tokens[2]));
      else if (key.equals("zoomBy"))
        zoomBy(Parser.parseInt(tokens[2]));
      else if (key.equals("rotateZBy"))
        rotateZBy(Parser.parseInt(tokens[2]));
      break;
    case 4:
      if (key.equals("rotateXYBy"))
        rotateXYBy(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]));
      else if (key.equals("translateXYBy"))
        translateXYBy(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]));
      else if (key.equals("rotateMolecule"))
        rotateMolecule(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]));
      break;
    }
    if (disableSend)
      setSyncDriver(StatusManager.SYNC_ENABLE);
  }

  void setSyncDriver(int mode) {
    statusManager.setSyncDriver(mode);
  }

  public float[] getPartialCharges() {
    return modelSet.getPartialCharges();
  }

  void setProteinType(byte iType, BitSet bs) {
    modelSet.setProteinType(bs == null ? selectionManager.bsSelection : bs,
        iType);
  }

  

  public Point3f getBondPoint3f1(int i) {
    
    return modelSet.getBondAtom1(i);
  }

  public Point3f getBondPoint3f2(int i) {
    
    return modelSet.getBondAtom2(i);
  }

  public Vector3f getVibrationVector(int atomIndex) {
    return modelSet.getVibrationVector(atomIndex, false);
  }

  public int getVanderwaalsMar(int i) {
    return (dataManager.defaultVdw == JmolConstants.VDW_USER ? dataManager.userVdwMars[i]
        : JmolConstants.getVanderwaalsMar(i, dataManager.defaultVdw));
  }

  public int getVanderwaalsMar(int i, int iMode) {
    if (iMode == JmolConstants.VDW_USER && dataManager.bsUserVdws == null)
      iMode = dataManager.defaultVdw;
    return (iMode == JmolConstants.VDW_USER ? dataManager.userVdwMars[i]
        : JmolConstants.getVanderwaalsMar(i, iMode));
  }

  void setDefaultVdw(String mode) {
    dataManager.setDefaultVdw(mode);
    global.setParameterValue("defaultVDW", getDefaultVdw(Integer.MIN_VALUE));
  }

  String getDefaultVdw(int iMode) {
    return dataManager.getDefaultVdw(iMode, null);
  }

  public int deleteAtoms(BitSet bs, boolean fullModels) {
    clearModelDependentObjects();
    if (!fullModels)
      return selectionManager.deleteAtoms(bs);
    fileManager.addLoadScript("zap " + Escape.escape(bs));
    setCurrentModelIndex(0, false);
    animationManager.setAnimationOn(false);
    BitSet bsDeleted = modelSet.deleteModels(bs);
    setAnimationRange(0, 0);
    eval.deleteAtomsInVariables(bsDeleted);
    repaintManager.clear();
    animationManager.clear();
    animationManager.initializePointers(1);
    if (getModelCount() > 1)
      setCurrentModelIndex(-1, true);
    hoverAtomIndex = -1;
    setFileLoadStatus(FILE_STATUS_MODELS_DELETED, null, null, null, null);
    refreshMeasures();
    return BitSetUtil.cardinalityOf(bsDeleted);
  }

  public void deleteModelAtoms(int firstAtomIndex, int nAtoms, BitSet bsDeleted) {
    
    dataManager.deleteModelAtoms(firstAtomIndex, nAtoms, bsDeleted);
  }

  public BitSet getDeletedAtoms() {
    return selectionManager.bsDeleted;
  }

  public char getQuaternionFrame() {
    return global.quaternionFrame.charAt(global.quaternionFrame.length() == 2 ? 1 : 0);
  }

  public int getHelixStep() {
    return global.helixStep;
  }

  String calculatePointGroup() {
    return modelSet.calculatePointGroup(selectionManager.bsSelection);
  }

  public Hashtable getPointGroupInfo(Object atomExpression) {
    return modelSet.getPointGroupInfo(getAtomBitSet(atomExpression));
  }

  public String getPointGroupAsString(boolean asDraw, String type, int index,
                                      float scale) {
    return modelSet.getPointGroupAsString(selectionManager.bsSelection, asDraw,
        type, index, scale);
  }

  public float getPointGroupTolerance(int type) {
    switch (type) {
    case 0:
      return global.pointGroupDistanceTolerance;
    case 1:
      return global.pointGroupLinearTolerance;
    }
    return 0;
  }

  Object getFileAsImage(String pathName, Hashtable htParams) {
    if (!haveDisplay)
      return "no display";
    Object obj = fileManager.getFileAsImage(pathName, htParams);
    if (obj instanceof String)
      return obj;
    Image image = (Image) obj;
    MediaTracker tracker = new MediaTracker(display);
    tracker.addImage(image, 0);
    try {
      tracker.waitForID(0);
    } catch (InterruptedException e) {
      
    }
    return image;
  }

  String cd(String dir) {
    if (dir == null) {
      dir = ".";
    } else if (dir.length() == 0) {
      setStringProperty("defaultDirectory", "");
      dir = ".";
    }
    dir = fileManager.getDefaultDirectory(dir
        + (dir.equals("=") || dir.endsWith("/") ? "" : "/X"));
    if (dir.length() > 0)
      setStringProperty("defaultDirectory", dir);
    String path = fileManager.getFullPath(dir + "/", true);
    if (path.startsWith("file:/"))
      FileManager.setLocalPath(this, dir, false);
    return dir;
  }

  

  private String errorMessage;
  private String errorMessageUntranslated;

  private String setErrorMessage(String errMsg) {
    return setErrorMessage(errMsg, null);
  }

  private String setErrorMessage(String errMsg, String errMsgUntranslated) {
    errorMessageUntranslated = errMsgUntranslated;
    
    
    return (errorMessage = errMsg);
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorMessageUntranslated() {
    
    
    return errorMessageUntranslated == null ? errorMessage
        : errorMessageUntranslated;
  }

  private int currentShapeID = -1;
  private String currentShapeState;
  
  public Shape getShape(int i) {
    return (modelSet == null ? null : modelSet.getShape(i));
  }

  public void setShapeErrorState(int shapeID, String state) {
    currentShapeID = shapeID;
    currentShapeState = state;
  }

  public String getShapeErrorState() {
    if (currentShapeID < 0)
      return "";
    if (modelSet != null)
      modelSet.releaseShape(currentShapeID);
    repaintManager.clear(currentShapeID);
    return JmolConstants.getShapeClassName(currentShapeID) + " "
        + currentShapeState;
  }

  public void handleError(Error er, boolean doClear) {
    
    try {
      if (doClear)
        zap("" + er); 
      Logger.error("viewer handling error condition: " + er);
      notifyError("Error", "doClear=" + doClear + "; " + er, "" + er);
    } catch (Throwable e1) {
      try {
        Logger.error("Could not notify error " + er + ": due to " + e1);
      } catch (Throwable er2) {
        
      }
    }
  }

  float[] getAtomicCharges() {
    return modelSet.getAtomicCharges();
  }

  
  
  
  ScriptFunction getFunction(String name) {
    return stateManager.getFunction(name);
  }

  void addFunction(ScriptFunction f) {
    stateManager.addFunction(f);
  }

  public void clearFunctions() {
    stateManager.clearFunctions();
  }

  public boolean isFunction(String name) {
    return stateManager.isFunction(name);
  }

  public String getFunctionCalls(String selectedFunction) {
    return stateManager.getFunctionCalls(selectedFunction);
  }

  void showMessage(String s) {
    if (!isPrintOnly)
      Logger.warn(s);
  }

  public String getMoInfo(int modelIndex) {
    return modelSet.getMoInfo(modelIndex);
  }

  boolean isRepaintPending() {
    return repaintManager.repaintPending;
  }

}
