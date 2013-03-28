

package org.jmol.shape;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.ModelSet;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

public abstract class ShapeRenderer {

  
  
  
  
  protected Viewer viewer;
  protected JmolRendererInterface g3d;
  protected ModelSet modelSet;
  protected Shape shape;

  protected int myVisibilityFlag;
  protected int shapeID;
  
  
  protected short colix;
  protected short mad;
  protected short madBeg;
  protected short madMid;
  protected short madEnd;
  protected int exportType;

  public final void setViewerG3dShapeID(Viewer viewer, JmolRendererInterface g3d, int shapeID) {
    this.viewer = viewer;
    this.g3d = g3d;
    this.shapeID = shapeID;
    myVisibilityFlag = JmolConstants.getShapeVisibilityFlag(shapeID);
    initRenderer();
  }

  protected void initRenderer() {
  }

  public void render(JmolRendererInterface g3d, ModelSet modelSet, Shape shape) {
    this.g3d = g3d;
    this.modelSet = modelSet;
    this.shape = shape;
    exportType = g3d.getExportType();
    render();
    exportType = Graphics3D.EXPORT_NOT;
  }

  abstract protected void render();
  

}

