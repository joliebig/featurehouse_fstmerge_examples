
package org.jmol.viewer;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;
import org.jmol.modelset.ModelSet;
import org.jmol.shape.Shape;
import org.jmol.shape.ShapeRenderer;
import org.jmol.util.Logger;

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

  void pushHoldRepaint() {
    ++holdRepaint;
    
  }

  void popHoldRepaint() {
    --holdRepaint;
    
    if (holdRepaint <= 0) {
      holdRepaint = 0;
      repaintPending = true;
      viewer.repaint();
    }
  }

  boolean refresh() {
    if (repaintPending)
      return false;
    repaintPending = true;
    if (holdRepaint == 0) {
      
      viewer.repaint();
    }
    return true;
  }

  synchronized void requestRepaintAndWait() {
    viewer.repaint();
    try {
      
      wait(1000); 
      if (repaintPending) {
        System.out.println("repaintManager requestRepaintAndWait timeout");
        repaintDone();
      }
    } catch (InterruptedException e) {
      
    }
    
  }

  synchronized void repaintDone() {
    repaintPending = false;
    
    notify(); 
  }

  void render(Graphics3D g3d, ModelSet modelSet) {
    if (!viewer.getRefreshing())
      return;
    
    render1(g3d, modelSet); 
    Rectangle band = viewer.getRubberBandSelection();
    if (band != null && g3d.setColix(viewer.getColixRubberband()))
      g3d.drawRect(band.x, band.y, 0, 0, band.width, band.height);
  }

  private boolean logTime;

  private void render1(Graphics3D g3d, ModelSet modelSet) { 

    if (modelSet == null || !viewer.mustRenderFlag())
      return;

    logTime = viewer.getTestFlag1();
    viewer.finalizeTransformParameters();
    if (logTime)
      Logger.startTimer();

    try {
      g3d.renderBackground();
      if (renderers ==  null)
        renderers = new ShapeRenderer[JmolConstants.SHAPE_MAX];
      for (int i = 0; i < JmolConstants.SHAPE_MAX && g3d.currentlyRendering(); ++i) {
        Shape shape = modelSet.getShape(i);
        if (shape == null)
          continue;
        getRenderer(i, g3d).render(g3d, modelSet, shape);
      }
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
    viewer.finalizeTransformParameters();
    g3dExport.renderBackground();
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = modelSet.getShape(i);
      if (shape != null)
        getRenderer(i, g3d).render(g3dExport, modelSet, shape);
    }
    return g3dExport.finalizeOutput();
  }
}
