

package org.jmol.api;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.BitSet;
import java.util.Properties;
import java.util.Hashtable;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import org.jmol.viewer.Viewer;



abstract public class JmolViewer extends JmolSimpleViewer {

  
  static public JmolViewer allocateViewer(Component awtComponent,
                                          JmolAdapter jmolAdapter,
                                          String htmlName, URL documentBase, 
                                          URL codeBase,
                                          String commandOptions, 
                                          JmolStatusListener statusListener) {
    
    return Viewer.allocateViewer(awtComponent, jmolAdapter,
        htmlName, documentBase, codeBase, commandOptions, statusListener);
  }

  
  abstract public void renderScreenImage(Graphics gLeft, Graphics gRight, Dimension size,
                                         Rectangle clip);

  
  public static JmolViewer allocateViewer(Component awtComponent, JmolAdapter jmolAdapter) {
    return Viewer.allocateViewer(awtComponent, jmolAdapter, null, null, null, null, null);
  }
  
  static public String getJmolVersion() {
    return Viewer.getJmolVersion();
  }

  static public boolean checkOption(JmolViewer viewer, String option) {
    Object testFlag = viewer.getParameter(option);
    return (testFlag instanceof Boolean && ((Boolean) testFlag).booleanValue()
        || testFlag instanceof Integer && ((Integer) testFlag).intValue() != 0);
  }

  
  
  abstract public String generateOutput(String type, String fileName, int width, int height); 

  abstract public void setJmolCallbackListener(JmolCallbackListener jmolCallbackListener);

  abstract public void setJmolStatusListener(JmolStatusListener jmolStatusListener);

  abstract public void setAppletContext(String htmlName, URL documentBase, URL codeBase,
                                        String commandOptions);

  abstract public boolean checkHalt(String strCommand);
  abstract public void haltScriptExecution();

  abstract public boolean isJvm12orGreater();
  abstract public String getOperatingSystemName();
  abstract public String getJavaVersion();
  abstract public String getJavaVendor();

  abstract public boolean haveFrame();

  abstract public void pushHoldRepaint();
  abstract public void popHoldRepaint();

  
  abstract public String getData(String atomExpression, String type);


  
  abstract public void setScreenDimension(Dimension dim);
  abstract public int getScreenWidth();
  abstract public int getScreenHeight();

  abstract public Image getScreenImage();
  abstract public void releaseScreenImage();
  
  abstract public void writeTextFile(String string, String data);
  
  
  abstract public String clipImage(String text);

  
  abstract public String createImage(String fileName, String type, Object text_or_bytes, int quality,
                                   int width, int height);

  
  abstract public Object getImageAs(String type, int quality, int width, int height, String fileName, OutputStream os);

  abstract public boolean handleOldJvm10Event(Event e);

  abstract public int getMotionEventNumber();

  
   
  abstract public String openReader(String fullPathName, String fileName, Reader reader);
  
  
  abstract public void openClientFile(String fullPathName, String fileName,
                             Object clientFile);

  abstract public void showUrl(String urlString);


  abstract public int getMeasurementCount();
  abstract public String getMeasurementStringValue(int i);
  abstract public int[] getMeasurementCountPlusIndices(int i);

  abstract public Component getDisplay();

  abstract public BitSet getElementsPresentBitSet(int modelIndex);

  abstract public int getAnimationFps();

  abstract public int findNearestAtomIndex(int x, int y);

  abstract public String script(String script);
  abstract public Object scriptCheck(String script);
  abstract public String scriptWait(String script);
  abstract public Object scriptWaitStatus(String script, String statusList);
  abstract public String loadInline(String strModel);
  abstract public String loadInline(String strModel, boolean isMerge);
  abstract public String loadInline(String strModel, char newLine);
  abstract public String loadInline(String[] arrayModels);
  abstract public String loadInline(String[] arrayModels, boolean isMerge);

  abstract public String evalStringQuiet(String script);
  abstract public boolean isScriptExecuting();

  abstract public String getModelSetName();
  abstract public String getModelSetFileName();
  abstract public String getModelSetPathName();
  abstract public String getFileAsString(String filename);
  abstract public boolean getFileAsString(String[] data, int nBytesMax, boolean doSpecialLoad);
  abstract public Properties getModelSetProperties();
  abstract public Hashtable getModelSetAuxiliaryInfo();
  abstract public int getModelNumber(int modelIndex);
  abstract public String getModelName(int modelIndex);
  abstract public String getModelNumberDotted(int modelIndex);
  abstract public Properties getModelProperties(int modelIndex);
  abstract public String getModelProperty(int modelIndex, String propertyName);
  abstract public Hashtable getModelAuxiliaryInfo(int modelIndex);
  abstract public Object getModelAuxiliaryInfo(int modelIndex, String keyName);
  abstract public boolean modelHasVibrationVectors(int modelIndex);

  abstract public int getModelCount();
  abstract public int getDisplayModelIndex(); 
  abstract public int getAtomCount();
  abstract public int getBondCount(); 
  abstract public int getGroupCount();
  abstract public int getChainCount();
  abstract public int getPolymerCount();
  abstract public int getAtomCountInModel(int modelIndex);
  abstract public int getBondCountInModel(int modelIndex);  
  abstract public int getGroupCountInModel(int modelIndex);
  abstract public int getChainCountInModel(int modelIindex);
  abstract public int getPolymerCountInModel(int modelIndex);
  abstract public int getSelectionCount();

  abstract public void addSelectionListener(JmolSelectionListener listener);
  abstract public void removeSelectionListener(JmolSelectionListener listener);


  abstract public void homePosition();

  abstract public Hashtable getHeteroList(int modelIndex);


  abstract public boolean getPerspectiveDepth();
  abstract public boolean getShowHydrogens();
  abstract public boolean getShowMeasurements();
  abstract public boolean getShowAxes();
  abstract public boolean getShowBbcage();

  abstract public int getAtomNumber(int atomIndex);
  abstract public String getAtomName(int atomIndex);
  abstract public String getAtomInfo(int atomIndex); 

  abstract public float getRotationRadius();

  abstract public int getZoomPercent(); 
  abstract public float getZoomPercentFloat();
  abstract public Matrix4f getUnscaledTransformMatrix();

  abstract public int getBackgroundArgb();
  
  abstract public float getAtomRadius(int atomIndex);
  abstract public Point3f getAtomPoint3f(int atomIndex);
  abstract public int getAtomArgb(int atomIndex);
  abstract public int getAtomModelIndex(int atomIndex);

  abstract public float getBondRadius(int bondIndex);
  abstract public Point3f getBondPoint3f1(int bondIndex);
  abstract public Point3f getBondPoint3f2(int bondIndex);
  abstract public int getBondArgb1(int bondIndex);
  abstract public int getBondArgb2(int bondIndex);
  abstract public short getBondOrder(int bondIndex);
  abstract public int getBondModelIndex(int bondIndex);

  abstract public Point3f[] getPolymerLeadMidPoints(int modelIndex, int polymerIndex);
  
  abstract public boolean getAxesOrientationRasmol();
  abstract public int getPercentVdwAtom();

  abstract public boolean getAutoBond();

  abstract public short getMadBond();

  abstract public float getBondTolerance();

  abstract public void rebond();

  abstract public float getMinBondDistance();

  abstract public void refresh(int isOrientationChange, String strWhy);

  abstract public boolean showModelSetDownload();
  
  abstract public void repaintView();

  abstract public boolean getBooleanProperty(String propertyName);
  abstract public boolean getBooleanProperty(String key, boolean doICare);
  abstract public Object getParameter(String name);

  abstract public String getSetHistory(int howFarBack);
  
  abstract public boolean havePartialCharges();

  abstract public boolean isApplet();

  abstract public String getAltLocListInModel(int modelIndex);

  abstract public String getStateInfo();
  
  abstract public void syncScript(String script, String applet);  

  
  
  
  
  abstract public void setColorBackground(String colorName);
  abstract public void setShowAxes(boolean showAxes);
  abstract public void setShowBbcage(boolean showBbcage);
  abstract public void setJmolDefaults();
  abstract public void setRasmolDefaults();

  abstract public void setBooleanProperty(String propertyName, boolean value);
  abstract public void setIntProperty(String propertyName, int value);
  abstract public void setFloatProperty(String propertyName, float value);
  abstract public void setStringProperty(String propertyName, String value);

  abstract public void setModeMouse(int modeMouse); 

  abstract public void setShowHydrogens(boolean showHydrogens);
  abstract public void setShowMeasurements(boolean showMeasurements);
  abstract public void setPerspectiveDepth(boolean perspectiveDepth);
  abstract public void setAutoBond(boolean autoBond);
  abstract public void setMarBond(short marBond);
  abstract public void setBondTolerance(float bondTolerance);
  abstract public void setMinBondDistance(float minBondDistance);
  abstract public void setAxesOrientationRasmol(boolean axesMessedUp);
  abstract public void setPercentVdwAtom(int percentVdwAtom);
  
  
  abstract public void setAnimationFps(int framesPerSecond);
  
  abstract public void setFrankOn(boolean frankOn);
  
  abstract public void setDebugScript(boolean debugScript);
  
  
  abstract public void deleteMeasurement(int i);
  
  abstract public void clearMeasurements();
  
  abstract public void setVectorScale(float vectorScaleValue);
  
  abstract public void setVibrationScale(float vibrationScaleValue);
  
  abstract public void setVibrationPeriod(float vibrationPeriod);
  
  abstract public void selectAll();
  
  abstract public void clearSelection();
  
  
  abstract public void setSelectionSet(BitSet newSelection);
  
  abstract public void setSelectionHalos(boolean haloEnabled);
  
  abstract public void setCenterSelected(); 

  
  
  abstract public void rotateFront();
  
  
  
  abstract public void rotateX(int degrees);
  abstract public void rotateY(int degrees);
  abstract public void rotateX(float radians);
  abstract public void rotateY(float radians);
  abstract public void rotateZ(float radians);

  abstract public JmolAdapter getModelAdapter();

  abstract public void openFileAsynchronously(String fileName);
  abstract public Object getFileAsBytes(String fullPathName);

  abstract public String getErrorMessage();
  abstract public String getErrorMessageUntranslated();

}

