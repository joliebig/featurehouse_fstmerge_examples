
package org.jmol.viewer;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;
import org.jmol.modelset.ModelSet;
import org.jmol.shape.Shape;
import org.jmol.shape.ShapeRenderer;
import org.jmol.util.Logger;

import java.awt.Component;
import java.awt.Rectangle;

class RepaintManager {

  private Viewer viewer;

  RepaintManager(Viewer viewer) {
    this.viewer = viewer;
  }

  void clear() {
    clear(-1);
  }

  private int holdRepaint = 0;
  boolean repaintPending;
  private boolean repaintInterrupted = false;

  void pushHoldRepaint() {
    ++holdRepaint;
  }

  void popHoldRepaint() {
    if (--holdRepaint <= 0)
      repaintDisplay();
  }

  boolean refresh() {
    if (repaintPending)
      return false;
    if (holdRepaint == 0)
      repaintDisplay();
    return true;
  }

  
  synchronized void requestRepaintAndWait() {
    repaintDisplay();
    try {
      wait();
    } catch (InterruptedException e) {
    }
  }

  private void repaintDisplay() {
    holdRepaint = 0;
    repaintPending = true;
    repaintInterrupted = false;
    Component display = viewer.getDisplay();
    if (display == null)
      return;
    display.repaint();
  }

  synchronized void cancelRendering() {
    if (!repaintPending || repaintInterrupted)
      return;
    repaintInterrupted = true;
    try {
      
      wait();
    } catch (InterruptedException e) {
    }
    repaintInterrupted = false;
    
  }

  synchronized void repaintDone() {
    repaintPending = false;
    notify(); 
  }

  void render(Graphics3D g3d, ModelSet modelSet) {
    if (!viewer.getRefreshing())
      return;
    try {
      render1(g3d, modelSet); 
    } catch (Exception e) {
      System.out.println("rendering Exception " + e.getMessage());
    }
  }

  private boolean logTime;

  private void render1(Graphics3D g3d, ModelSet modelSet) { 

    if (modelSet == null || !viewer.mustRenderFlag()
        || repaintInterrupted)
      return;

    logTime = viewer.getTestFlag1();

    viewer.finalizeTransformParameters();

    if (logTime)
      Logger.startTimer();

    try {
      g3d.renderBackground();
      if (renderers ==  null)
        renderers = new ShapeRenderer[JmolConstants.SHAPE_MAX];
      for (int i = 0; i < JmolConstants.SHAPE_MAX 
      && g3d.currentlyRendering(); ++i) {
        if (repaintInterrupted)
          return;
        Shape shape = modelSet.getShape(i);
        if (shape == null)
          continue;
        
        getRenderer(i, g3d).render(g3d, modelSet, shape);
      }
      Rectangle band = viewer.getRubberBandSelection();
      if (band != null && g3d.setColix(viewer.getColixRubberband()))
        g3d.drawRect(band.x, band.y, 0, 0, band.width, band.height);

    } catch (Exception e) {
      Logger
          .error("rendering error -- perhaps use \"set refreshing FALSE/TRUE\" ? ");
    }
    if (logTime)
      Logger.checkTimer("render time");
  }

  private ShapeRenderer[] renderers;
  
  void clear(int iShape) {
    if (renderers ==  null)
      return;
    if (iShape >= 0)
      renderers[iShape] = null;
    else
      for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i)
        renderers[i] = null;
  }

  private ShapeRenderer getRenderer(int shapeID, Graphics3D g3d) {
    if (renderers[shapeID] == null)
      renderers[shapeID] = allocateRenderer(shapeID, g3d);
    return renderers[shapeID];
  }

  private ShapeRenderer allocateRenderer(int shapeID, Graphics3D g3d) {
    String className = JmolConstants.getShapeClassName(shapeID) + "Renderer";
    try {
      Class shapeClass = Class.forName(className);
      ShapeRenderer renderer = (ShapeRenderer) shapeClass.newInstance();
      renderer.setViewerG3dShapeID(viewer, g3d, shapeID);
      return renderer;
    } catch (Exception e) {
      Logger.error("Could not instantiate renderer:" + className, e);
    }
    return null;
  }

  String generateOutput(String type, Graphics3D g3d, ModelSet modelSet,
                        String fileName) {

    viewer.finalizeTransformParameters();

    JmolRendererInterface g3dExport = null;
    Object output = null;
    boolean isOK = false;
    try {
      if (fileName == null) {
        output = new StringBuffer();
      } else {
        if (fileName.charAt(0) == '?')
          fileName = viewer.dialogAsk("save", fileName.substring(1));
        if (fileName == null)
          return null;
        output = fileName;
      }
      Class export3Dclass = Class.forName("org.jmol.export.Export3D");
      g3dExport = (JmolRendererInterface) export3Dclass.newInstance();
      isOK = g3dExport.initializeExporter(type, viewer, g3d, output);
    } catch (Exception e) {
    }
    if (!isOK) {
      Logger.error("Cannot export " + type);
      return null;
    }
    g3dExport.renderBackground();
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = modelSet.getShape(i);
      if (shape == null)
        continue;
      ShapeRenderer generator = getGenerator(i, g3d);
      generator.setGenerator(true);
      generator.render(g3dExport, modelSet, shape);
      generator.setGenerator(false);
    }
    return g3dExport.finalizeOutput();
  }

  private ShapeRenderer getGenerator(int shapeID, Graphics3D g3d) {
    String className = "org.jmol.export."
        + JmolConstants.getShapeClassName(~shapeID) + "Generator";
    try {
      Class shapeClass = Class.forName(className);
      ShapeRenderer renderer = (ShapeRenderer) shapeClass.newInstance();
      renderer.setViewerG3dShapeID(viewer, g3d, shapeID);
      return renderer;
    } catch (Exception e) {
      
      return getRenderer(shapeID, g3d);
    }
  }
}
